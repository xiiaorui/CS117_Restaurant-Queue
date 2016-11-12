package websocket_server;

import java.util.logging.Logger;

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
			+ mContext.getConnection().getRemoteSocketAddress().getAddress().getHostAddress()
		);
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
				"resp",
				DatabaseClient.getOpenRestaurants(mContext.getDatabaseConnection())
			);
			break;
		}
		return resp;
	}

}
