package websocket_server;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

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

	public class Restaurant {

		public int mID;
		public Queue<Context> mQueue;

		public Restaurant() {
			mID = -1;
			mQueue = new ArrayDeque<Context>();
		}

	}
}
