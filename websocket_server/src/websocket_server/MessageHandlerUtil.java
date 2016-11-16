package websocket_server;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import websocket_server.schema.RestaurantsRow;

public class MessageHandlerUtil {

	public static JSONArray getOpenRestaurants() {
		JSONArray list = new JSONArray();
		ArrayList<RestaurantsRow> restaurants = RestaurantManager.get().getOpenRestaurants();
		for (RestaurantsRow restaurant : restaurants) {
			JSONArray sublist = new JSONArray();
			sublist.put(restaurant.id);
			sublist.put(restaurant.name);
			list.put(sublist);
		}
		return list;
	}

	// sets the default values of response
	// returns whether there was an error
	public static boolean setDefaultResponse(JSONObject req, JSONObject resp) {
		resp.put("error", 0);	// default error code (no error)
		try {
			int id = req.getInt("id");
			resp.put("id", id);
			String actionStr = req.getString("action");
			ServerAction action = Server.getServerActionFromString(actionStr);
			if (action == null) {
				setError(resp, ErrorCode.INVALID_REQUEST);
				return true;
			}
		} catch (JSONException e) {
			setError(resp, ErrorCode.INVALID_REQUEST);
			return true;
		}

		return false;
	}

	public static void setError(JSONObject obj, ErrorCode error) {
		obj.put("error", error.getValue());
		obj.put("error_reason", error.getReason());
	}

}
