package websocket_server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
		// TODO
	}

	public void queue(Context clientContext, int restaurantID) {
		boolean shouldUnlockClientMapMutex = true;
		mClientMapMutex.lock();
		try {
			Integer prevRestaurantID = mClientMap.put(clientContext, restaurantID);
			if (prevRestaurantID != null) {
				// The client was currently in a queue and has requested to join
				//   another queue.
				if (restaurantID == prevRestaurantID) {
					// The client wants to join the same queue it is already in.
					// Do nothing in this case.
				} else {
					// We notify the previous restaurant that the client has left
					//   the queue.
					shouldUnlockClientMapMutex = false;
					mClientMapMutex.unlock();
					notifyLeaveQueue(prevRestaurantID, clientContext);
				}
			}
		} finally {
			if (shouldUnlockClientMapMutex) {
				mClientMapMutex.unlock();
			}
		}
	}

	public void addParty(int restaurantID, Context clientContext, Party party) {
		Restaurant restaurant = mRestaurantMap.get(restaurantID);
		restaurant.addParty(party);
		// TODO notify server
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
		mRestaurantMapMutex.lock();
		Restaurant restaurant = mRestaurantMap.get(restaurantID);
		mRestaurantMapMutex.unlock();
		if (restaurant != null) {
			// TODO remove client from queue
		}
	}

	public ArrayList<Party> getFrontOfQueue(int restaurantID) {
		Restaurant restaurant = mRestaurantMap.get(restaurantID);
		if (restaurant == null)
			return null;
		return restaurant.getFrontOfQueue();
	}

}
