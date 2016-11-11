package websocket_server;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.logging.Logger;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;

public class Server extends WebSocketServer {

	private static final String CLIENT_RESOURCE_DESCRIPTOR = "/client";
	private static final String SERVER_RESOURCE_DESCRIPTOR = "/server";
	private static Server sServerInstance;
	private static Logger sLogger = Logger.getLogger(Server.class.getName());
	private HashMap<WebSocket, Context> mContextMap;

	public Server(InetSocketAddress address) {
		super(address);
		mContextMap = new HashMap<>();
	}

	@Override
	public void onClose(WebSocket arg0, int arg1, String arg2, boolean arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(WebSocket arg0, Exception arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		Context context = getContext(conn);
		try {
			JSONObject json = new JSONObject(message);
			context.getHandler().onMessage(conn, context, json);
		}
		catch (JSONException e) {
			// message was not a valid JSON object
		}
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		sLogger.info("New connection.\nResource: " + conn.getResourceDescriptor()
				+ "\nAddress: " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
		// generate DeviceType
		DeviceType type = null;	// default value
		if (conn.getResourceDescriptor().equals(CLIENT_RESOURCE_DESCRIPTOR)) {
			type = DeviceType.CLIENT;
		}
		else if (conn.getResourceDescriptor().equals(SERVER_RESOURCE_DESCRIPTOR)) {
			type = DeviceType.SERVER;
		}
		else {
			// invalid resource descriptor, close connection
		}

		// make new Context for the connection
		Context context = new Context(conn, type);
		// add Context to context map
		mContextMap.put(conn, context);
	}

	private Context getContext(WebSocket conn) {
		return mContextMap.get(conn);
	}

	public static Server init(InetSocketAddress address) {
		if (sServerInstance != null) {
			throw new RuntimeException("Server already initialized");
		}
		sServerInstance = new Server(address);
		return sServerInstance;
	}

	public static Server get() {
		return sServerInstance;
	}

	public static void main(String[] args) {
		InetSocketAddress localAddress = new InetSocketAddress(80);
		Server.init(localAddress);
		Server.get().start();
		sLogger.info("Server started on port: " + Server.get().getPort());
	}

}
