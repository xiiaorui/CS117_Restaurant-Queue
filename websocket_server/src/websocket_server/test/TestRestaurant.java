package websocket_server.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import websocket_server.NotificationType;
import websocket_server.Server;

public class TestRestaurant extends WebSocketClient {

	private static Logger sLogger = Logger.getLogger(TestRestaurant.class.getName());

	public TestRestaurant(URI serverURI) {
		super(serverURI);
		connect();
	}

	@Override
	public void send(String message) {
		sLogger.info("Sending message: " + message);
		super.send(message);
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
		JSONObject obj = null;
		try {
			obj = new JSONObject(message);
		} catch (JSONException e) {
			// should not happen since server only sends JSON messages
			e.printStackTrace();
			return;
		}
		if (obj.has("notification")) {
			String notification = obj.getString("notification");
			if (notification.equals(NotificationType.ENTER_QUEUE.getValue())) {
				System.out.println("Received ENTER_QUEUE notification");
				int partyID = obj.getInt("party_id");
				// Call party some time later.
				(new CallPartyTask(partyID)).run();
			}
		}
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

	private class CallPartyTask implements Runnable {

		private final int mPartyID;

		public CallPartyTask(int partyID) {
			mPartyID = partyID;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(10 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// send call party request
			TestRestaurant.this.send(
				RequestFactory.callParty(mPartyID).toString()
			);
		}

	}

}
