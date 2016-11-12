package websocket_server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RestaurantManager {

	private static RestaurantManager sRestaurantManager;
	private Map<Integer, Restaurant> mRestaurantMap;

	private RestaurantManager() {
		mRestaurantMap = new HashMap<>();
	}

	private static void init() {
		sRestaurantManager = new RestaurantManager();
	}

	public static RestaurantManager get() {
		if (sRestaurantManager == null)
			init();
		return sRestaurantManager;
	}

	public void open(Context serverContext, int restaurantID) {
		Restaurant newRestaurant = new Restaurant(serverContext);
		mRestaurantMap.putIfAbsent(restaurantID, newRestaurant);
	}

	public void addParty(int restaurantID, Party party) {
		Restaurant restaurant = mRestaurantMap.get(restaurantID);
		restaurant.addParty(party);
		// TODO notify server
	}

	public ArrayList<Party> getFrontOfQueue(int restaurantID) {
		Restaurant restaurant = mRestaurantMap.get(restaurantID);
		if (restaurant == null)
			return null;
		return restaurant.getFrontOfQueue();
	}

}
