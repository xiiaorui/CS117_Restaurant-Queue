package websocket_server;

import org.json.JSONObject;

public class ClientMessageHandler implements MessageHandler {

	private final Context mContext;

	public ClientMessageHandler(Context context) {
		mContext = context;
	}

	@Override
	public void onOpen() {
		// Do nothing.
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
				MessageHandlerUtil.getOpenRestaurants()
			);
			break;
		case QUEUE:
			doQueue(message, resp);
			break;
		case OPEN_RESTAURANT:
		case CREATE_RESTAURANT:
			// invalid action
			MessageHandlerUtil.setError(resp, ErrorCode.INVALID_REQUEST, "server action requested");
			break;
		}
		return resp;
	}

	private void doQueue(JSONObject req, JSONObject resp) {
		Integer id = null;
		if (!Utility.hasKey(req, "restaurant_id")) {
			MessageHandlerUtil.setError(resp, ErrorCode.INVALID_REQUEST, "missing restaurant_id");
			return;
		}
		if (!Utility.isInt(req, "restaurant_id")) {
			MessageHandlerUtil.setError(resp, ErrorCode.INVALID_REQUEST, "invalid restaurant_id");
			return;
		}
		if (!Utility.hasKey(req, "party_name")) {
			MessageHandlerUtil.setError(resp, ErrorCode.INVALID_REQUEST, "missing party_name");
			return;
		}
		if (!Utility.hasKey(req, "party_size")) {
			MessageHandlerUtil.setError(resp, ErrorCode.INVALID_REQUEST, "missing party_size");
			return;
		}
		if (!Utility.isInt(req, "party_size") || (Utility.getInt(req, "party_size") <= 0)) {
			MessageHandlerUtil.setError(resp, ErrorCode.INVALID_REQUEST, "invalid party_size");
			return;
		}
		// valid request, process
		int restaurant_id = Utility.getInt(req, "restaurant_id");
		String party_name = Utility.getStr(req, "party_name");
		int party_size = Utility.getInt(req, "party_size");
		Party party = new Party(mContext, party_name, party_size);
		if (!RestaurantManager.get().queue(restaurant_id, party)) {
			// Party was not added to restaurant queue.
			// We assume this can only happen if the restaurant is not open.
			MessageHandlerUtil.setError(resp, ErrorCode.RESTAURANT_NOT_OPEN);
		}
		// TODO include position and wait time in response
	}

}
