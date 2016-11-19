package websocket_server;

public class QueueStatus {

	private int mPosition;
	private int mWaitTime;	// estimated wait time

	public QueueStatus(int position, int waitTime) {
		mPosition = position;
		mWaitTime = waitTime;
	}

	public int getPosition() {
		return mPosition;
	}

	public int getWaitTime() {
		return mWaitTime;
	}

}
