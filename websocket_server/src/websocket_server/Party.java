package websocket_server;

public class Party {

	private Context mClientContext;
	private String mName;
	private int mSize;
	private int mID = -1;	// assigned and used only by restaurant

	public Party(Context clientContext, String name, int size) {
		mClientContext = clientContext;
		mName = name;
		mSize = size;
	}

	public Context getClientContext() {
		return mClientContext;
	}

	public void clearClientContext() {
		mClientContext = null;
	}

	public int getID() {
		return mID;
	}

	// May be called only by the Restaurant associated with this Party.
	public void setID(int id) {
		mID = id;
	}

	public String getName() {
		return mName;
	}

	public int getSize() {
		return mSize;
	}

}
