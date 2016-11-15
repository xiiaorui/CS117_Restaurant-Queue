package websocket_server;

import org.json.JSONObject;

public class ClientMessageHandler implements MessageHandler {

	private final Context mContext;

	public ClientMessageHandler(Context context) {
		mContext = context;
	}

	@Override
	public void onOpen() {

	}

	@Override
	public void onClose() {
		// The client has disconnected.
		// Remove from queue if it is one.
		RestaurantManager.get().leaveQueue(mContext);
	}

	@Override
	public JSONObject onMessage(JSONObject message) {
		JSONObject resp = new JSONObject();
		if (MessageHandlerUtil.setDefaultResponse(message, resp)) {
			return resp;
		}
		switch (Server.getServerActionFromString(message.getString("action"))) {
		case GET_OPEN_RESTAURANTS:
			resp.put(
				"list",
				DatabaseClient.getOpenRestaurants(mContext.getDatabaseConnection())
			);
			break;
		case OPEN_RESTAURANT:
		case CREATE_RESTAURANT:
			// invalid action
			MessageHandlerUtil.setError(resp, ErrorCode.INVALID_REQUEST);
			break;
		}
		return resp;
	}

}
