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

	public static final String CLIENT_RESOURCE_DESCRIPTOR = "client";
	public static final String SERVER_RESOURCE_DESCRIPTOR = "server";
	private static Server sServerInstance;
	private static Logger sLogger = Logger.getLogger(Server.class.getName());
	private HashMap<WebSocket, Context> mContextMap;
	private static HashMap<String, ServerAction> sServerActionMap;

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
		sLogger.info("New message: \"" + message + "\"");
		JSONObject messageJSON = null;
		JSONObject resp = null;
		try {
			messageJSON = new JSONObject(message);
		}
		catch (JSONException e) {
			// message was not a valid JSON object
			resp = new JSONObject();
			MessageHandlerUtil.setError(resp, ErrorCode.INVALID_JSON);
			conn.send(resp.toString());
			return;
		}
		Context context = getContext(conn);
		resp = context.getHandler().onMessage(messageJSON);
		conn.send(resp.toString());
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		String resourcePath = getResourcePath(conn.getResourceDescriptor());
		sLogger.info(
			"New connection. Resource: \"" + conn.getResourceDescriptor()
			+ "\" Resource path: \"" + resourcePath + "\" Address: "
			+ conn.getRemoteSocketAddress().getAddress().getHostAddress()
		);
		// generate DeviceType
		DeviceType type = null;	// default value
		if (resourcePath != null) {
			if (resourcePath.equals(CLIENT_RESOURCE_DESCRIPTOR)) {
				type = DeviceType.CLIENT;
			}
			else if (resourcePath.equals(SERVER_RESOURCE_DESCRIPTOR)) {
				type = DeviceType.SERVER;
			}
		}
		if (type == null) {
			// invalid resource descriptor, close connection
		}

		// make new Context for the connection
		Context context = new Context(conn, type);
		// add Context to context map
		mContextMap.put(conn, context);
		context.getHandler().onOpen();
	}

	public static ServerAction getServerActionFromString(String str) {
		return sServerActionMap.get(str);
	}

	private Context getContext(WebSocket conn) {
		return mContextMap.get(conn);
	}

	// resourceDescriptor looks like "/path:port"
	// tries to return just path
	private static String getResourcePath(String resourceDescriptor) {
		if ((resourceDescriptor == null) || (resourceDescriptor.isEmpty()))
			return null;
		if (resourceDescriptor.charAt(0) != '/')
			return null;
		int colonPos = resourceDescriptor.indexOf(':');
		if (colonPos == -1)
			return resourceDescriptor.substring(1);
		else
			return resourceDescriptor.substring(1, colonPos);
	}

	public static Server init(InetSocketAddress address) {
		if (sServerInstance != null) {
			throw new RuntimeException("Server already initialized");
		}
		sServerInstance = new Server(address);
		// initialize sServerActionMap
		sServerActionMap = new HashMap<>();
		for (ServerAction action : ServerAction.values()) {
			sServerActionMap.put(action.getValue(), action);
		}
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
