package websocket_server;

import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import websocket_server.schema.RestaurantsRow;

public class ServerMessageHandler implements MessageHandler {

	private static final Logger sLogger = Logger.getLogger(ServerMessageHandler.class.getName());
	private final Context mContext;
	private int mID = -1;	// restaurant ID

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
		if (mID != -1) {
			RestaurantManager.get().close(mID);
			// clear mID
			mID = -1;
		}
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
				MessageHandlerUtil.getOpenRestaurants()
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
		// if restaurant is open, then mID is set
		if (mID != -1) {
			// Restaurant has requested to open after already opening.
			MessageHandlerUtil.setError(resp, ErrorCode.INVALID_REQUEST);
			return;
		}
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
		// query database
		RestaurantsRow restaurant = DatabaseClient.getRestaurant(
			mContext.getDatabaseConnection(),
			restaurant_id
		);
		if (restaurant == null) {
			MessageHandlerUtil.setError(resp, ErrorCode.DATABASE_ERROR);
			return;
		}
		// update mID
		mID = restaurant_id;
		// notify RestaurantManager
		RestaurantManager.get().open(mContext, restaurant);
	}

	private void doCreateRestaurant(JSONObject req, JSONObject resp) {
		// check if req contains name
		String restaurantName = null;
		try {
			restaurantName = req.getString("name");
		} catch (JSONException e) {
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
