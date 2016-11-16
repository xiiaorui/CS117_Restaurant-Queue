package websocket_server;

public enum NotificationType {
	ENTER_QUEUE("enter_queue");		// a new customer has entered a restaurant's queue

	private final String mValue;

	private NotificationType(String value) {
		mValue = value;
	}

	public String getValue() {
		return mValue;
	}
}
