package websocket_server;

import org.json.JSONObject;

public class CustomerMessageHandler implements MessageHandler {

	private final Context mContext;

	public CustomerMessageHandler(Context context) {
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
		mContext.lock();
		try {
			RestaurantManager.get().leaveQueue(mContext);
		} finally {
			mContext.unlock();
		}
	}

	@Override
	public JSONObject onMessage(JSONObject message) {
		mContext.lock();
		try {
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
			case LEAVE_QUEUE:
				doLeaveQueue(message, resp);
				break;
			case QUEUE_STATUS:
				doQueueStatus(message, resp);
				break;
			case OPEN_RESTAURANT:
			case CREATE_RESTAURANT:
			case GET_PARTIES:
			case CALL_PARTY:
				// invalid action
				MessageHandlerUtil.setError(
					resp,
					ErrorCode.INVALID_REQUEST,
					"restaurant action requested"
				);
				break;
			}
			return resp;
		} finally {
			mContext.unlock();
		}
	}

	private void doQueue(JSONObject req, JSONObject resp) {
		if (!req.has("restaurant_id")) {
			MessageHandlerUtil.setError(resp, ErrorCode.INVALID_REQUEST, "missing restaurant_id");
			return;
		}
		if (!Utility.isInt(req, "restaurant_id")) {
			MessageHandlerUtil.setError(resp, ErrorCode.INVALID_REQUEST, "invalid restaurant_id");
			return;
		}
		if (!req.has("party_name")) {
			MessageHandlerUtil.setError(resp, ErrorCode.INVALID_REQUEST, "missing party_name");
			return;
		}
		if (!req.has("party_size")) {
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
		QueueStatus status = RestaurantManager.get().queue(restaurant_id, party);
		if (status == null) {
			// Party was not added to restaurant queue.
			// We assume this can only happen if the restaurant is not open.
			MessageHandlerUtil.setError(resp, ErrorCode.RESTAURANT_NOT_OPEN);
			return;
		}
		resp.put("position", status.getPosition());
		resp.put("wait_time", status.getWaitTime());
	}

	private void doLeaveQueue(JSONObject req, JSONObject resp) {
		RestaurantManager.get().leaveQueue(mContext);
	}

	private void doQueueStatus(JSONObject req, JSONObject resp) {
		QueueStatus status = RestaurantManager.get().getQueueStatus(mContext);
		if (status == null) {
			// Client was not in a queue.
			MessageHandlerUtil.setError(resp, ErrorCode.NOT_IN_QUEUE);
			return;
		}
		resp.put("position", status.getPosition());
		resp.put("wait_time", status.getWaitTime());
	}

}
