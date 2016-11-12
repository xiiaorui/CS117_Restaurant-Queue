package websocket_server;

public enum ServerAction {

	GET_OPEN_RESTAURANTS("get_restaurants"),	// get list of all open restaurants
	OPEN_RESTAURANT("open");					// open a restaurant

	private final String mValue;

	ServerAction(String value) {
		mValue = value;
	}

	public String getValue() {
		return mValue;
	}
}
