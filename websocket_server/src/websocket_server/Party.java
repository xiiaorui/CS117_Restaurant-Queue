package websocket_server;

public class Party {

	private Context mCustomerContext;
	private String mName;
	private int mSize;
	// data members below this line are assigned and used only by restaurant
	private int mID = -1;
	private long mTimestamp = 0;

	public Party(Context customerContext, String name, int size) {
		mCustomerContext = customerContext;
		mName = name;
		mSize = size;
	}

	public Context getCustomerContext() {
		return mCustomerContext;
	}

	public void clearCustomerContext() {
		mCustomerContext = null;
	}

	public int getID() {
		return mID;
	}

	// May be called only by the Restaurant associated with this Party.
	public void setID(int id) {
		mID = id;
	}

	public void setTimestamp(long timestamp) {
		mTimestamp = timestamp;
	}

	public long getTimestamp() {
		return mTimestamp;
	}

	public String getName() {
		return mName;
	}

	public int getSize() {
		return mSize;
	}

}
