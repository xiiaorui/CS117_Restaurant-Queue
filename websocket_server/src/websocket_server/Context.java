package websocket_server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

// Each connection has an associated Context which contains
//   all state information.
public class Context {

	private final WebSocket mConnection;
	private Connection mDatabaseConnection = null;
	private UserType mUserType;
	private MessageHandler mHandler;
	private Integer mMessageID = 1;
	// We want to process only one change of state at a time.
	// For example joining a queue, or being forcefully removed from
	//   a queue because the restaurant closed.
	private final Lock mLock = new ReentrantLock(true);

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

	public void lock() {
		mLock.lock();
	}

	public void unlock() {
		mLock.unlock();
	}

	public void setType(UserType type) throws SQLException {
		mUserType = type;
		if (type == null)
			throw new RuntimeException("UserType cannot be set to null");
		switch (type) {
		case CUSTOMER:
			mHandler = new CustomerMessageHandler(this);
			break;
		case RESTAURANT:
			mHandler = new RestaurantMessageHandler(this);
			mDatabaseConnection = DatabaseClient.getNewServerConnection();
			break;
		}
	}

	public MessageHandler getHandler() {
		return mHandler;
	}

	public void sendNotification(JSONObject obj) {
		obj.put("id", getNewMessageID());
		mConnection.send(obj.toString());
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
