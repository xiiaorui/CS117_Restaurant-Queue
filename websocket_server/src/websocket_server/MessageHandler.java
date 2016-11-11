package websocket_server;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

public interface MessageHandler {

	void onMessage(WebSocket conn, Context context, JSONObject message);

}
