package websocket_server;

import java.sql.Connection;
import java.sql.SQLException;

import org.java_websocket.WebSocket;

// Each connection has an associated Context which contains
//   all state information.
public class Context {

	private final WebSocket mConnection;
	private Connection mDatabaseConnection = null;
	private UserType mUserType;
	private MessageHandler mHandler;
	private Integer mMessageID = 1;

	public Context(WebSocket conn, UserType type) {
		mConnection = conn;
		try {
			setType(type);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public WebSocket getConnection() {
		return mConnection;
	}

	public Connection getDatabaseConnection() {
		return mDatabaseConnection;
	}

	public void setType(UserType type) throws SQLException {
		mUserType = type;
		if (type == null)
			throw new RuntimeException("UserType cannot be set to null");
		switch (type) {
		case CUSTOMER:
			mHandler = new ClientMessageHandler(this);
			break;
		case RESTAURANT:
			mHandler = new ServerMessageHandler(this);
			mDatabaseConnection = DatabaseClient.getNewServerConnection();
			break;
		}
	}

	public MessageHandler getHandler() {
		return mHandler;
	}

	public int getNewMessageID() {
		int newID;
		synchronized(mMessageID) {
			newID = mMessageID;
			mMessageID += 2;
		}
		return newID;
	}

}
