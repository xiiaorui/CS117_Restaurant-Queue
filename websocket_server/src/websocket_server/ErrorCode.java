package websocket_server;

public enum ErrorCode {

	NO_ERROR(0, ""), INVALID_JSON(1, "invalid JSON"), INVALID_REQUEST(2, "invalid request"),
	DATABASE_ERROR(3, "database error"), RESTAURANT_NOT_OPEN(4, "restaurant is not open");

	private final int mValue;
	private final String mReason;

	ErrorCode(int value, String reason) {
		mValue = value;
		mReason = reason;
	}

	public int getValue() {
		return mValue;
	}

	public String getReason() {
		return mReason;
	}
}
