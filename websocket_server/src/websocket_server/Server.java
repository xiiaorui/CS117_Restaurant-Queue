package websocket_server;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;

public class Server extends WebSocketServer {

	public static final String CUSTOMER_RESOURCE_DESCRIPTOR = "customer";
	public static final String RESTAURANT_RESOURCE_DESCRIPTOR = "restaurant";
	private static Server sServerInstance;
	private static Logger sLogger = Logger.getLogger(Server.class.getName());
	private static HashMap<String, ServerAction> sServerActionMap;
	private Map<WebSocket, Context> mContextMap;

	private Server(InetSocketAddress address) {
		super(address);
		mContextMap = Collections.synchronizedMap(new HashMap<>());
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		sLogger.info(
			"onClose(address=" + Utility.getRemoteAddress(conn)
			+ ", code=" + code + ", reason=" + reason
			+ ", remote=" + remote + ")"
		);
		// remove from Context map if it is in there
		Context context = mContextMap.remove(conn);
		if (context != null) {
			// a context did exist for this connection
			context.getHandler().onClose();
		}
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
			+ Utility.getRemoteAddress(conn)
		);
		// generate DeviceType
		UserType type = null;	// default value
		if (resourcePath != null) {
			if (resourcePath.equals(CUSTOMER_RESOURCE_DESCRIPTOR)) {
				type = UserType.CUSTOMER;
			}
			else if (resourcePath.equals(RESTAURANT_RESOURCE_DESCRIPTOR)) {
				type = UserType.RESTAURANT;
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

	public static Server init(int port) {
		if (sServerInstance != null) {
			throw new RuntimeException("Server already initialized");
		}
		sServerInstance = new Server(new InetSocketAddress(port));
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
		Server.init(80);
		Server.get().start();
		sLogger.info("Server started on port: " + Server.get().getPort());
	}

}
