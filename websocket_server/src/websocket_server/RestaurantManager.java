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
	// Key is customer context, value is ID of the restaurant that customer
	//   is in queue for.
	// When a customer joins the queue of a restaurant, it is added to this map.
	private Map<Context, Integer> mCustomerMap;
	private final Lock mCustomerMapMutex = new ReentrantLock(true);

	private RestaurantManager() {
		mRestaurantMap = new HashMap<>();
		mCustomerMap = new HashMap<>();
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
	public void open(Context restContext, RestaurantsRow restaurant) {
		Restaurant newRestaurant = new Restaurant(restContext, restaurant.name);
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
			party.getCustomerContext().sendNotification(notification);
		}
	}

	// Returns QueueStatus if party joins restaurant queue or is already part of that queue.
	// Returns null otherwise.
	public QueueStatus queue(int restaurantID, Party party) {
		QueueStatus status = null;
		Integer prevRestaurantID = null;
		mCustomerMapMutex.lock();
		try {
			prevRestaurantID = mCustomerMap.put(party.getCustomerContext(), restaurantID);
		} finally {
			mCustomerMapMutex.unlock();
		}
		if (prevRestaurantID != null) {
			// The customer was currently in a queue and has requested to join
			//   another queue.
			if (restaurantID == prevRestaurantID) {
				// The customer wants to join the same queue it is already in.
				// Do nothing in this case.
				Restaurant restaurant = getRestaurant(restaurantID);
				if (restaurant == null) {
					// The restaurant must have just closed.
					// Return null status.
				} else {
					status = restaurant.getStatus(party.getCustomerContext());
				}
			} else {
				// We notify the previous restaurant that the customer has left
				//   the queue.
				notifyLeaveQueue(prevRestaurantID, party.getCustomerContext(), false);
				// We now add party to requested restaurant queue.
				status = joinQueue(restaurantID, party);
			}
		} else {
			// This is the most common case: queuing into a restaurant
			//   when customer is not currently in a queue.
			// Get the associated restaurant.
			status = joinQueue(restaurantID, party);
		}
		return status;
	}

	// Returns QueueStatus if party was able to join queue.
	// Returns null otherwise.
	private QueueStatus joinQueue(int restaurantID, Party party) {
		Restaurant restaurant = getRestaurant(restaurantID);
		QueueStatus status = null;
		if (restaurant == null) {
			// The restaurant is not open.
			return null;
		}
		status = restaurant.addParty(party);
		if (status == null) {
			// The restaurant is not accepting new parties.
			// This is the case where restaurant is closing at
			//   same time that party wants to join queue.
			return null;
		}
		// Successfully added party to queue.
		// Notify restaurant.
		restaurant.getRestaurantContext().sendNotification(
			NotificationFactory.enterQueue(
				party.getID(),
				party.getName(),
				party.getSize()
			)
		);
		return status;
	}

	// The customer leaves the queue it is in.
	public void leaveQueue(Context custContext) {
		int restaurantID = -1;	// the ID of restaurant to be notified
		mCustomerMapMutex.lock();
		try {
			Integer restaurantIDInteger = mCustomerMap.remove(custContext);
			if (restaurantIDInteger != null) {
				// The request is valid.
				restaurantID = restaurantIDInteger;
			}
		} finally {
			mCustomerMapMutex.unlock();
		}
		if (restaurantID != -1) {
			// Notify restaurant that customer has left.
			notifyLeaveQueue(restaurantID, custContext, false);
		}
	}

	// Notify the restaurant that customer is leaving its queue.
	// fromCall specifies if leaving queue initiated by restaurant call.
	private void notifyLeaveQueue(int restaurantID, Context custContext, boolean fromCall) {
		Restaurant restaurant = getRestaurant(restaurantID);
		if (restaurant != null) {
			int partyID = restaurant.removeFromQueue(custContext, fromCall);
			if (partyID < 0) {
				// Customer was not in queue.
				return;
			}
			// Send notification to restaurant that customer has left.
			restaurant.getRestaurantContext().sendNotification(
				NotificationFactory.leaveQueue(partyID)
			);
		}
	}

	// get up to count parties at front of queue
	public ArrayList<Party> getFrontOfQueue(int restaurantID, int count) {
		Restaurant restaurant = getRestaurant(restaurantID);
		if (restaurant == null) {
			// The restaurant is not open.
			// This is impossible if restaurant is the one that called
			//   this method.
			return null;
		}
		return restaurant.getFrontOfQueue(count);
	}

	public QueueStatus getQueueStatus(Context custContext) {
		Integer restaurantID = null;
		Restaurant restaurant = null;
		mCustomerMapMutex.lock();
		try {
			restaurantID = mCustomerMap.get(custContext);
		} finally {
			mCustomerMapMutex.unlock();
		}
		if (restaurantID == null) {
			// Not in any queue.
			return null;
		}
		restaurant = getRestaurant(restaurantID);
		if (restaurant == null) {
			// The restaurant must have just closed.
			// Remove from mCustomerMap
			mCustomerMapMutex.lock();
			try {
				mCustomerMap.remove(custContext);
			} finally {
				mCustomerMapMutex.unlock();
			}
			return null;
		}
		return restaurant.getStatus(custContext);
	}

	public void callParty(int restaurantID, int partyID) {
		Restaurant restaurant = getRestaurant(restaurantID);
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
		// Since we are updating state of customer context, we should lock it.
		Context custContext = party.getCustomerContext();
		custContext.lock();
		try {
			removedPartyID = restaurant.removeFromQueue(custContext, true);
			// We also remove customer from mCustomerMap
			mCustomerMapMutex.lock();
			try {
				mCustomerMap.remove(custContext);
			} finally {
				mCustomerMapMutex.unlock();
			}

		} finally {
			custContext.unlock();
		}
		if (removedPartyID < 0) {
			// The party was not actually removed from queue from this call.
			// However, the party was not in queue when removeFromQueue() was called.
			// This can occur if customer removes itself from queue
			//   after getParty() call but before removeFromQueue() call.
			// In this case, we do not notify customer.
			return;
		}
		// Send notification to party
		custContext.sendNotification(
			NotificationFactory.call()
		);
	}

	private Restaurant getRestaurant(int restaurantID) {
		Restaurant restaurant = null;
		mRestaurantMapMutex.lock();
		try {
			restaurant = mRestaurantMap.get(restaurantID);
		} finally {
			mRestaurantMapMutex.unlock();
		}
		return restaurant;
	}

}
