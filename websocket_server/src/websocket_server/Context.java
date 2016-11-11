package websocket_server;

import org.java_websocket.WebSocket;

// Each connection has an associated Context which contains
//   all state information.
public class Context {

	private final WebSocket mConnection;
	private DeviceType mDeviceType;
	private MessageHandler mHandler;
	private int mMessageID = 1;

	public Context(WebSocket conn, DeviceType type) {
		mConnection = conn;
		setType(type);
	}

	public WebSocket getConnection() {
		return mConnection;
	}

	public DeviceType getType() {
		return mDeviceType;
	}

	public void setType(DeviceType type) {
		mDeviceType = type;
		if (type == null)
			throw new RuntimeException("DeviceType cannot be set to null");
		switch (type) {
		case CLIENT:
			mHandler = new ClientMessageHandler(this);
			break;
		case SERVER:
			mHandler = new ServerMessageHandler(this);
			break;
		}
	}

	public MessageHandler getHandler() {
		return mHandler;
	}

	public int getNewMessageID() {
		int newID = mMessageID;
		mMessageID += 2;
		return newID;
	}

}
