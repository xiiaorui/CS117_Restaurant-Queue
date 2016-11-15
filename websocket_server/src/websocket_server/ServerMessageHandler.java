package websocket_server;

import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

public class ServerMessageHandler implements MessageHandler {

	private static final Logger sLogger = Logger.getLogger(ServerMessageHandler.class.getName());
	private final Context mContext;

	public ServerMessageHandler(Context context) {
		mContext = context;
	}

	@Override
	public void onOpen() {
		sLogger.info(
			"New server connection. Address: "
			+ Utility.getRemoteAddress(mContext.getConnection())
		);
	}

	@Override
	public void onClose() {
		// TODO notify RestaurantManager and all waiting parties
	}

	@Override
	public JSONObject onMessage(JSONObject message) {
		JSONObject resp = new JSONObject();
		if (MessageHandlerUtil.setDefaultResponse(message, resp)) {
			return resp;
		}
		switch (Server.getServerActionFromString(message.getString("action"))) {
		case GET_OPEN_RESTAURANTS:	// to be removed
			resp.put(
				"list",
				DatabaseClient.getOpenRestaurants(mContext.getDatabaseConnection())
			);
			break;
		case OPEN_RESTAURANT:
			doOpenRestaurant(message, resp);
			break;
		case CREATE_RESTAURANT:
			doCreateRestaurant(message, resp);
			break;
		}
		return resp;
	}

	private void doOpenRestaurant(JSONObject req, JSONObject resp) {
		// check if req contains restaurant_id
		int restaurant_id = -1;
		try {
			restaurant_id = req.getInt("restaurant_id");
		} catch (JSONException e) {
			MessageHandlerUtil.setError(resp, ErrorCode.INVALID_REQUEST);
			return;
		}
		if (restaurant_id < 0) {
			MessageHandlerUtil.setError(resp, ErrorCode.INVALID_REQUEST);
			return;
		}
		// update database
		int affectedRows = DatabaseClient.openRestaurant(
			mContext.getDatabaseConnection(),
			restaurant_id
		);
		if (affectedRows < 0) {
			MessageHandlerUtil.setError(resp, ErrorCode.DATABASE_ERROR);
			return;
		}
		// update RestaurantManager
		RestaurantManager.get().open(mContext, restaurant_id);
	}

	private void doCreateRestaurant(JSONObject req, JSONObject resp) {
		// check if req contains name
		String restaurantName = null;
		try {
			restaurantName = req.getString("name");
		} catch (JSONException e) {
			System.out.println("assdasda");
			MessageHandlerUtil.setError(resp, ErrorCode.INVALID_REQUEST);
			return;
		}
		int newRestaurantID = DatabaseClient.createRestaurant(
			mContext.getDatabaseConnection(),
			restaurantName
		);
		if (newRestaurantID == -1) {
			MessageHandlerUtil.setError(resp, ErrorCode.DATABASE_ERROR);
			return;
		}
		resp.put("restaurant_id", newRestaurantID);
	}

}
