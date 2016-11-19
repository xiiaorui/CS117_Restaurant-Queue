package websocket_server;

import org.json.JSONObject;

// Note: generated objects do not contain required id
public class NotificationFactory {

	public static JSONObject enterQueue() {
		return makeDefaultObject(NotificationType.ENTER_QUEUE);
	}

	public static JSONObject leaveQueue(int partyID) {
		JSONObject obj = makeDefaultObject(NotificationType.LEAVE_QUEUE);
		obj.put("party_id", partyID);
		return obj;
	}

	private static JSONObject makeDefaultObject(NotificationType type) {
		JSONObject obj = new JSONObject();
		obj.put("notification", type.getValue());
		return obj;
	}

}
