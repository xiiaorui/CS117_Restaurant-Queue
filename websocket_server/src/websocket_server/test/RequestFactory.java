package websocket_server.test;

import org.json.JSONObject;

import websocket_server.ServerAction;

public class RequestFactory {

	private static int sMessageID = 0;

	public static JSONObject openRestaurantsList() {
		JSONObject req = new JSONObject();
		req.put("id", getNewMessageID());
		req.put("action", ServerAction.GET_OPEN_RESTAURANTS.getValue());
		return req;
	}

	public static JSONObject openRestaurant(int restaurantID) {
		JSONObject req = new JSONObject();
		req.put("id", getNewMessageID());
		req.put("action", ServerAction.OPEN_RESTAURANT.getValue());
		req.put("restaurant_id", restaurantID);
		return req;
	}

	private static int getNewMessageID() {
		int id = sMessageID;
		sMessageID += 2;
		return id;
	}

}
