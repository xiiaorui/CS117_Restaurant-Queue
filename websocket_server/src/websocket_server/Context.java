package websocket_server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.java_websocket.WebSocket;

// Each connection has an associated Context which contains
//   all state information.
public class Context {

	private final WebSocket mConnection;
	private Connection mDatabaseConnection;
	private DeviceType mDeviceType;
	private MessageHandler mHandler;
	private int mMessageID = 1;

	public Context(WebSocket conn, DeviceType type) {
		mConnection = conn;
		try {
			mDatabaseConnection = DriverManager.getConnection(
				DatabaseClient.URL,
				DatabaseClient.USER,
				DatabaseClient.PASS
			);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setType(type);
	}

	public WebSocket getConnection() {
		return mConnection;
	}

	public Connection getDatabaseConnection() {
		return mDatabaseConnection;
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
