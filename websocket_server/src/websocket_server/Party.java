package websocket_server;

public class Party {

	private final Context mClientContext;
	private String mName;
	private int mSize;

	public Party(Context clientContext, String name, int size) {
		mClientContext = clientContext;
		mName = name;
		mSize = size;
	}

	public Context getClientContext() {
		return mClientContext;
	}

}
