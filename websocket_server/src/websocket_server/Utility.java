package websocket_server;

import org.java_websocket.WebSocket;

public class Utility {

	public static String getRemoteAddress(WebSocket conn) {
		return conn.getRemoteSocketAddress().getAddress().getHostAddress();
	}

}
