package websocket_server;

import org.json.JSONObject;

public interface MessageHandler {

	// Called after Server adds connection.
	void onOpen();

	// Return object is response to message.
	// Called after Server receives a message.
	JSONObject onMessage(JSONObject message);

}
