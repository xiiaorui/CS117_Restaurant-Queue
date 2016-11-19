package websocket_server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONObject;

import websocket_server.schema.RestaurantsRow;

public class RestaurantManager {

	private static RestaurantManager sRestaurantManager;
	// Key is restaurant ID, value is state of the associated restaurant.
	// When a restaurant is opened, it is added to this map.
	private Map<Integer, Restaurant> mRestaurantMap;
	private final Lock mRestaurantMapMutex = new ReentrantLock(true);
	// Key is client context, value is ID of the restaurant that client
	//   is in queue for.
	// When a client joins the queue of a restaurant, it is added to this map.
	private Map<Context, Integer> mClientMap;
	private final Lock mClientMapMutex = new ReentrantLock(true);

	private RestaurantManager() {
		mRestaurantMap = new HashMap<>();
		mClientMap = new HashMap<>();
	}

	private static void init() {
		sRestaurantManager = new RestaurantManager();
	}

	public static RestaurantManager get() {
		if (sRestaurantManager == null)
			init();
		return sRestaurantManager;
	}

	public ArrayList<RestaurantsRow> getOpenRestaurants() {
		ArrayList<RestaurantsRow> list = new ArrayList<>();
		mRestaurantMapMutex.lock();
		try {
			for (Map.Entry<Integer, Restaurant> entry : mRestaurantMap.entrySet()) {
				list.add(new RestaurantsRow(
					entry.getKey(),
					entry.getValue().getName()
				));
			}
		} finally {
			mRestaurantMapMutex.unlock();
		}
		return list;
	}

	// Open a restaurant.
	public void open(Context serverContext, RestaurantsRow restaurant) {
		Restaurant newRestaurant = new Restaurant(serverContext, restaurant.name);
		mRestaurantMapMutex.lock();
		try {
			mRestaurantMap.putIfAbsent(restaurant.id, newRestaurant);
		} finally {
			mRestaurantMapMutex.unlock();
		}
	}

	// Close a restaurant.
	public void close(int restaurantID) {
		Restaurant restaurant = null;
		if (restaurantID < 0)
			return;	// invalid ID
		mRestaurantMapMutex.lock();
		try {
			restaurant = mRestaurantMap.remove(restaurantID);
		} finally {
			mRestaurantMapMutex.unlock();
		}
		if (restaurant == null) {
			// The restaurant was not open.
			return;
		}
		// Notify customers that restaurant has closed.
		Queue<Party> queue = restaurant.getQueueAndClose();
		restaurant.clear();
		JSONObject notification = NotificationFactory.close();
		while (!queue.isEmpty()) {
			Party party = queue.poll();
			party.getClientContext().sendNotification(notification);
		}
	}

	// Returns true if party joins restaurant queue or if it is already part of that queue.
	public boolean queue(int restaurantID, Party party) {
		boolean shouldUnlockClientMapMutex = true;
		boolean retVal = false;
		mClientMapMutex.lock();
		try {
			Integer prevRestaurantID = mClientMap.put(party.getClientContext(), restaurantID);
			if (prevRestaurantID != null) {
				// The client was currently in a queue and has requested to join
				//   another queue.
				if (restaurantID == prevRestaurantID) {
					// The client wants to join the same queue it is already in.
					// Do nothing in this case.
					retVal = true;
				} else {
					// We notify the previous restaurant that the client has left
					//   the queue.
					shouldUnlockClientMapMutex = false;
					mClientMapMutex.unlock();
					notifyLeaveQueue(prevRestaurantID, party.getClientContext());
					// We now add party to requested restaurant queue.
					retVal = joinQueue(restaurantID, party);
				}
			} else {
				// This is the most common case: queuing into a restaurant
				//   when client is not currently in a queue.
				// Get the associated restaurant.
				shouldUnlockClientMapMutex = false;
				mClientMapMutex.unlock();
				retVal = joinQueue(restaurantID, party);
			}
		} finally {
			if (shouldUnlockClientMapMutex) {
				mClientMapMutex.unlock();
			}
		}
		return retVal;
	}

	private boolean joinQueue(int restaurantID, Party party) {
		Restaurant restaurant = null;
		mRestaurantMapMutex.lock();
		try {
			restaurant = mRestaurantMap.get(restaurantID);
		} finally {
			mRestaurantMapMutex.unlock();
		}
		if (restaurant == null) {
			// The restaurant is not open.
			return false;
		}
		if (restaurant.addParty(party)) {
			// Successfully added party to queue.
			// Notify restaurant.
			restaurant.getServerContext().sendNotification(
				NotificationFactory.enterQueue()
			);
			return true;
		} else {
			// The restaurant is not accepting new parties.
			// This is the case where restaurant is closing at
			//   same time that party wants to join queue.
			return false;
		}
	}

	// The client leaves the queue it is in.
	public void leaveQueue(Context clientContext) {
		int restaurantID = -1;	// the ID of restaurant to be notified
		mClientMapMutex.lock();
		try {
			Integer restaurantIDInteger = mClientMap.remove(clientContext);
			if (restaurantIDInteger != null) {
				// The request is valid.
				restaurantID = restaurantIDInteger;
			}
		} finally {
			mClientMapMutex.unlock();
		}
		if (restaurantID != -1) {
			// Notify restaurant that client has left.
			notifyLeaveQueue(restaurantID, clientContext);
		}
	}

	// Notify the restaurant that client is leaving its queue.
	private void notifyLeaveQueue(int restaurantID, Context clientContext) {
		Restaurant restaurant = null;
		mRestaurantMapMutex.lock();
		try {
			restaurant = mRestaurantMap.get(restaurantID);
		} finally {
			mRestaurantMapMutex.unlock();
		}
		if (restaurant != null) {
			int partyID = restaurant.removeFromQueue(clientContext);
			if (partyID < 0) {
				// Client was not in queue.
				return;
			}
			// Send notification to restaurant that client has left.
			restaurant.getServerContext().sendNotification(
				NotificationFactory.leaveQueue(partyID)
			);
		}
	}

	// get up to count parties at front of queue
	public ArrayList<Party> getFrontOfQueue(int restaurantID, int count) {
		Restaurant restaurant = null;
		mRestaurantMapMutex.lock();
		try {
			restaurant = mRestaurantMap.get(restaurantID);
		} finally {
			mRestaurantMapMutex.unlock();
		}
		if (restaurant == null) {
			// The restaurant is not open.
			// This is impossible if restaurant is the one that called
			//   this method.
			return null;
		}
		return restaurant.getFrontOfQueue(count);
	}

	public void callParty(int restaurantID, int partyID) {
		Restaurant restaurant = null;
		mRestaurantMapMutex.lock();
		try {
			restaurant = mRestaurantMap.get(restaurantID);
		} finally {
			mRestaurantMapMutex.unlock();
		}
		if (restaurant == null) {
			// Restaurant is closed or does not exist.
			return;
		}
		Party party = restaurant.getParty(partyID);
		if (party == null) {
			// Party is not in queue.
			return;
		}
		// Remove party from queue.
		int removedPartyID = -1;
		// Since we are updating state of client context, we should lock it.
		Context clientContext = party.getClientContext();
		clientContext.lock();
		try {
			removedPartyID = restaurant.removeFromQueue(clientContext);
		} finally {
			clientContext.unlock();
		}
		if (removedPartyID < 0) {
			// The party was not actually removed from queue from this call.
			// However, the party was not in queue when removeFromQueue() was called.
			// This can occur if customer removes itself from queue
			//   after getParty() call but before removeFromQueue() call.
			// In this case, we do not notify customer.
			return;
		}
		// TODO send notification to party
	}

}
