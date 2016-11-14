package websocket_server.test;

import java.io.Console;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import websocket_server.DeviceType;
import websocket_server.Server;
import websocket_server.ServerAction;

public class InteractiveClient extends WebSocketClient {

	public static final String DEFAULT_SERVER_SCHEME = "ws";
	public static final String DEFAULT_SERVER_HOST = "localhost";
	public static final int DEFAULT_SERVER_PORT = 80;
	private static final Logger sLogger = Logger.getLogger(InteractiveClient.class.getName());
	private final Console mConsole;

	public InteractiveClient(URI uri, Console console) {
		super(uri);
		mConsole = console;
		try {
			connectBlocking();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		runLoop();
	}

	void runLoop() {
		ServerAction action;
		boolean running = true;
		while (running) {
			do {
				String actionStr = mConsole.readLine("Enter action: ");
				action = getAction(actionStr);
				if (action == null) {
					System.out.println("Unknown action.");
				}
			} while (action == null);
			JSONObject req = null;
			switch (action) {
			case GET_OPEN_RESTAURANTS:
				req = genGetOpenRestaurantsRequest();
				break;
			case OPEN_RESTAURANT:
				req = genOpenRestaurantRequest();
				break;
			default:
				throw new RuntimeException("Unhandled action.");
			}
			sLogger.info("Sending the request:\n" + req);
			send(req.toString());
		}

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
		sLogger.warning("onError(). Exception: " + e);
	}

	@Override
	public void onMessage(String message) {
		sLogger.info("Received: " + message);
	}

	@Override
	public void onOpen(ServerHandshake arg0) {
		sLogger.info("Connected to server.");
	}

	private JSONObject genGetOpenRestaurantsRequest() {
		return RequestFactory.openRestaurantsList();
	}

	private JSONObject genOpenRestaurantRequest() {
		int restaurantID = -1;
		do {
			String idStr = mConsole.readLine("restaurant ID: ");
			try {
				restaurantID = Integer.parseInt(idStr);
			} catch (NumberFormatException e) {
				System.out.println("Invalid restaurant ID.");
			}
		} while (restaurantID < 0);
		return RequestFactory.openRestaurant(restaurantID);
	}

	private static ServerAction getAction(String actionStr) {
		for (ServerAction action : ServerAction.values()) {
			if (action.getValue().equals(actionStr)) {
				return action;
			}
		}
		return null;
	}

	private static DeviceType getType(Console console) {
		String input = console.readLine("Device type (client or server): ");
		if (input.equals("client"))
			return DeviceType.CLIENT;
		else if (input.equals("server"))
			return DeviceType.SERVER;
		else
			return null;
	}

	public static String getURIString(String host, String resource, int port) {
		return (DEFAULT_SERVER_SCHEME + "://" + host + "/" + resource + ":" + port);
	}

	public static void main(String[] args) {
		Console console = System.console();
		DeviceType type;
		String uriHost = null;
		String uriResource = null;
		int uriPort = DEFAULT_SERVER_PORT;
		if (console == null)
			throw new RuntimeException("No console.");
		if (args.length >= 1)
			uriHost = args[0];
		else
			uriHost = DEFAULT_SERVER_HOST;
		do {
			type = getType(console);
		} while (type == null);
		if (type == DeviceType.CLIENT)
			uriResource = Server.CLIENT_RESOURCE_DESCRIPTOR;
		else if (type == DeviceType.SERVER)
			uriResource = Server.SERVER_RESOURCE_DESCRIPTOR;

		String uriStr = getURIString(uriHost, uriResource, uriPort);
		System.out.println("Server URI: " + uriStr);
		URI uri = null;
		try {
			uri = new URI(uriStr);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			System.exit(1);
		}
		new InteractiveClient(uri, console);
	}

}
