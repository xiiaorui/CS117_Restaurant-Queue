package websocket_server;

import org.json.JSONObject;

public interface MessageHandler {

	// Called after Server adds connection.
	void onOpen();

	// Called after Server removes the associated Context from its Context map
	void onClose();

	// Return object is response to message.
	// Called after Server receives a message.
	JSONObject onMessage(JSONObject message);

}
