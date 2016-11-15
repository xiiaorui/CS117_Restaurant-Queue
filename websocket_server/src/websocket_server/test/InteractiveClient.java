package websocket_server.test;

import java.net.URI;
import java.util.logging.Logger;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class InteractiveClient extends WebSocketClient {

	private static final Logger sLogger = Logger.getLogger(InteractiveClient.class.getName());
	private final InteractiveClientGUI mGUI;

	public InteractiveClient(URI uri, InteractiveClientGUI gui) {
		super(uri);
		mGUI = gui;
	}

	@Override
	public void send(String message) {
		sLogger.info("Sending:\n" + message);
		super.send(message);
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		sLogger.info(
			"onClose(code=" + code + ", reason=" + reason
			+ ", remote=" + remote + ")"
		);
		mGUI.notifyOnClose(code, reason, remote);
	}

	@Override
	public void onError(Exception e) {
		sLogger.warning("onError(). Exception: " + e);
		mGUI.notifyOnError(e);
	}

	@Override
	public void onMessage(String message) {
		sLogger.info("Received: " + message);
		mGUI.notifyOnMessage(message);
	}

	@Override
	public void onOpen(ServerHandshake arg0) {
		sLogger.info("Connected to server.");
		mGUI.notifyOnOpen(arg0);
	}

}
