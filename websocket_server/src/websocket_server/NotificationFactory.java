package websocket_server;

import org.json.JSONObject;

// Note: generated objects do not contain required id
public class NotificationFactory {

	public static JSONObject enterQueue(int partyID, String partyName, int partySize) {
		JSONObject obj = makeDefaultObject(NotificationType.ENTER_QUEUE);
		obj.put("party_id", partyID);
		obj.put("party_name", partyName);
		obj.put("party_size", partySize);
		return obj;
	}

	public static JSONObject leaveQueue(int partyID) {
		JSONObject obj = makeDefaultObject(NotificationType.LEAVE_QUEUE);
		obj.put("party_id", partyID);
		return obj;
	}

	public static JSONObject close() {
		return makeDefaultObject(NotificationType.CLOSE);
	}

	public static JSONObject call() {
		return makeDefaultObject(NotificationType.CALL);
	}

	private static JSONObject makeDefaultObject(NotificationType type) {
		JSONObject obj = new JSONObject();
		obj.put("notification", type.getValue());
		return obj;
	}

}
