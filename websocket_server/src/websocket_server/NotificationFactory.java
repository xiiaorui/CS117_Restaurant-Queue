package websocket_server;

import org.json.JSONObject;

// Note: generated objects do not contain required id
public class NotificationFactory {

	public static JSONObject enterQueue() {
		JSONObject obj = new JSONObject();
		obj.put("notification", NotificationType.ENTER_QUEUE.getValue());
		return obj;
	}

}
