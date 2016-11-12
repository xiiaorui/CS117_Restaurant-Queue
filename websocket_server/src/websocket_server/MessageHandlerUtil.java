package websocket_server;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageHandlerUtil {

	// sets the default values of response
	// returns whether there was an error
	public static boolean setDefaultResponse(JSONObject req, JSONObject resp) {
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
