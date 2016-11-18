package websocket_server.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import websocket_server.Server;

public class TestRestaurant extends WebSocketClient {

	private static Logger sLogger = Logger.getLogger(TestRestaurant.class.getName());

	public TestRestaurant(URI serverURI) {
		super(serverURI);
		connect();
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		sLogger.info("Restaurant closed.");
		try {
			Thread.sleep(10 * 1000);	// wait 10s
		} catch (InterruptedException e) {
		}
		System.exit(0);
	}

	@Override
	public void onError(Exception e) {
		sLogger.info("onError() Exception: " + e);
	}

	@Override
	public void onMessage(String message) {
		sLogger.info("onMessage() message: " + message);
	}

	@Override
	public void onOpen(ServerHandshake arg0) {
		sLogger.info("Restaurant connected. Sending open request.");
		JSONObject req = RequestFactory.openRestaurant(1);
		send(req.toString());
	}

	public static void main(String[] args) {
		String uriStr = "ws://localhost/" + Server.RESTAURANT_RESOURCE_DESCRIPTOR + ":80";
		URI uri = null;
		try {
			uri = new URI(uriStr);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		new TestRestaurant(uri);
	}

}
