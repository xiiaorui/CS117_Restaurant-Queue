package websocket_server;

import org.java_websocket.WebSocket;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {

	public static String getRemoteAddress(WebSocket conn) {
		return conn.getRemoteSocketAddress().getAddress().getHostAddress();
	}

	// Is obj[key] an integer?
	public static boolean isInt(JSONObject obj, String key) {
		try {
			obj.getInt(key);
			return true;
		} catch (JSONException e) {
			return false;
		}
	}

	public static int getInt(JSONObject obj, String key) {
		try {
			return obj.getInt(key);
		} catch (JSONException e) {
			throw new RuntimeException("obj[key] is not int");
		}
	}

	public static String getStr(JSONObject obj, String key) {
		try {
			return obj.getString(key);
		} catch (JSONException e) {
			throw new RuntimeException("obj[key] does not exist");
		}
	}

}
