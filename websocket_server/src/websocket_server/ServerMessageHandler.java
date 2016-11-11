package websocket_server;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

public class ServerMessageHandler implements MessageHandler {

	private final Context mContext;

	public ServerMessageHandler(Context context) {
		mContext = context;
	}

	@Override
	public void onMessage(WebSocket conn, Context context, JSONObject message) {
		// TODO Auto-generated method stub

	}

}
