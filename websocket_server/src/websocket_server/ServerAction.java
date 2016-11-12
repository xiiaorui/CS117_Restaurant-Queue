package websocket_server;

public enum ServerAction {

	GET_OPEN_RESTAURANTS("get_restaurants");

	private final String mValue;

	ServerAction(String value) {
		mValue = value;
	}

	public String getValue() {
		return mValue;
	}
}
