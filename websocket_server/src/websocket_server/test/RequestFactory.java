package websocket_server.test;

import org.json.JSONObject;

import websocket_server.ServerAction;

public class RequestFactory {

	private static int sMessageID = 0;

	public static JSONObject openRestaurantsList() {
		return newRequest(ServerAction.GET_OPEN_RESTAURANTS);
	}

	public static JSONObject openRestaurant(int restaurantID) {
		JSONObject req = newRequest(ServerAction.OPEN_RESTAURANT);
		req.put("restaurant_id", restaurantID);
		return req;
	}

	public static JSONObject createRestaurant(String name) {
		JSONObject req = newRequest(ServerAction.CREATE_RESTAURANT);
		req.put("name", name);
		return req;
	}

	public static JSONObject queue(int restaurantID, String partyName, int partySize) {
		JSONObject req = newRequest(ServerAction.QUEUE);
		req.put("restaurant_id", restaurantID);
		req.put("party_name", partyName);
		req.put("party_size", partySize);
		return req;
	}

	public static JSONObject leaveQueue() {
		return newRequest(ServerAction.LEAVE_QUEUE);
	}

	private static JSONObject newRequest(ServerAction action) {
		JSONObject req = new JSONObject();
		req.put("id", getNewMessageID());
		req.put("action", action.getValue());
		return req;
	}

	private static int getNewMessageID() {
		int id = sMessageID;
		sMessageID += 2;
		return id;
	}

}
