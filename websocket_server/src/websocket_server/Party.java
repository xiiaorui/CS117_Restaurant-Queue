package websocket_server;

public class Party {

	Context mClientContext;
	private String mName;
	private int mSize;

	public Party(String name, int size) {
		mName = name;
		mSize = size;
	}

	public Context getClientContext() {
		return mClientContext;
	}

}
