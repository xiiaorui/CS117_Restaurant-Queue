package websocket_server;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.json.JSONArray;
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
		mContext.lock();
		try {
			if (mID != -1) {
				RestaurantManager.get().close(mID);
				// clear mID
				mID = -1;
			}
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
			case GET_PARTIES:
				doGetParties(message, resp);
				break;
			case CALL_PARTY:
				doCallParty(message, resp);
				break;
			case QUEUE:
			case LEAVE_QUEUE:
				// invalid action
				MessageHandlerUtil.setError(
					resp,
					ErrorCode.INVALID_REQUEST,
					"customer action requested"
				);
				break;
			}
			return resp;
		} finally {
			mContext.unlock();
		}
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
			MessageHandlerUtil.setError(resp, ErrorCode.INVALID_REQUEST, "missing restaurant_id");
			return;
		}
		if (restaurant_id < 0) {
			MessageHandlerUtil.setError(resp, ErrorCode.INVALID_REQUEST, "invalid restaurant_id");
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
			MessageHandlerUtil.setError(resp, ErrorCode.INVALID_REQUEST, "missing name");
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

	private void doGetParties(JSONObject req, JSONObject resp) {
		int num_parties = -1;
		// check if restaurant is open
		if (mID < 0) {
			MessageHandlerUtil.setError(resp, ErrorCode.INVALID_REQUEST, "not open");
			return;
		}
		// check if req contains num_parties
		if (!req.has("num_parties")) {
			MessageHandlerUtil.setError(resp, ErrorCode.INVALID_REQUEST, "missing num_parties");
			return;
		}
		if (!Utility.isInt(req, "num_parties") || (Utility.getInt(req, "num_parties") <= 0)) {
			MessageHandlerUtil.setError(resp, ErrorCode.INVALID_REQUEST, "invalid num_parties");
			return;
		}
		num_parties = Utility.getInt(req, "num_parties");
		// get the parties
		ArrayList<Party> parties = RestaurantManager.get().getFrontOfQueue(mID, num_parties);
		// generate JSON list
		JSONArray jsonParties = new JSONArray();
		for (Party p : parties) {
			JSONArray tmp = new JSONArray();
			tmp.put(p.getID());
			tmp.put(p.getName());
			tmp.put(p.getSize());
			jsonParties.put(tmp);
		}
		resp.put("list", jsonParties);
	}

	private void doCallParty(JSONObject req, JSONObject resp) {
		int party_id = -1;
		// check if restaurant is open
		if (mID < 0) {
			MessageHandlerUtil.setError(resp, ErrorCode.INVALID_REQUEST, "not open");
			return;
		}
		if (!req.has("party_id")) {
			MessageHandlerUtil.setError(resp, ErrorCode.INVALID_REQUEST, "missing party_id");
			return;
		}
		if (!Utility.isInt(req, "party_id") || (Utility.getInt(req, "party_id") < 0)) {
			MessageHandlerUtil.setError(resp, ErrorCode.INVALID_REQUEST, "invalid party_id");
			return;
		}
		party_id = Utility.getInt(req, "party_id");
		RestaurantManager.get().callParty(mID, party_id);
	}

}
