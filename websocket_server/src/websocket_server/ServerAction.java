package websocket_server;

public enum ServerAction {

	GET_OPEN_RESTAURANTS("get_restaurants"),	// get list of all open restaurants
	OPEN_RESTAURANT("open_restaurant"),			// open a restaurant
	CREATE_RESTAURANT("create_restaurant");		// create a new restaurant

	private final String mValue;

	ServerAction(String value) {
		mValue = value;
	}

	public String getValue() {
		return mValue;
	}
}
