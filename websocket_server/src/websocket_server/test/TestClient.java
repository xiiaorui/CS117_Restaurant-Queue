package websocket_server.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import websocket_server.Server;
import websocket_server.ServerAction;

// Test the server as a server device
// Currently, all it does is get open restaurants and then closes connection.
public class TestClient extends WebSocketClient {

	private static final Logger sLogger = Logger.getLogger(TestClient.class.getName());
	private int mMessageID = 0;

	public TestClient(URI uri) {
		super(uri);
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		sLogger.info(
			"onClose(code=" + code + ", reason=" + reason
			+ ", remote=" + remote + ")"
		);
	}

	@Override
	public void onError(Exception e) {
		sLogger.info("onError(). Exception: " + e);
	}

	@Override
	public void onMessage(String message) {
		sLogger.info("Received: " + message);
		sLogger.info("Closing connection.");
		try {
			closeBlocking();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onOpen(ServerHandshake arg0) {
		// TODO Auto-generated method stub
		sLogger.info("Connected to server.\nRequesting open restaurants.");
		JSONObject req = generateOpenRestaurantsRequest();
		sLogger.info("Request: " + req);
		send(req.toString());
	}

	private JSONObject generateOpenRestaurantsRequest() {
		JSONObject req = new JSONObject();
		req.put("id", getNewMessageID());
		req.put("action", ServerAction.GET_OPEN_RESTAURANTS.getValue());
		return req;
	}

	private int getNewMessageID() {
		int id = mMessageID;
		mMessageID += 2;
		return id;
	}

	public static void main(String[] args) {
		String uriStr = null;
		// optional argument uri
		if (args.length > 0) {
			uriStr = args[0];
		}
		else {
			uriStr = "ws://localhost/" + Server.SERVER_RESOURCE_DESCRIPTOR + ":80";
		}
		URI uri = null;
		try {
			uri = new URI(uriStr);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TestClient client = new TestClient(uri);
		try {
			if (!client.connectBlocking()) {
				sLogger.severe("Unable to connect.");
			}
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
