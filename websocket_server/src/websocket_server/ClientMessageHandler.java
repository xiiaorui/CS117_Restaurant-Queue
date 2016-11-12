package websocket_server;

import org.json.JSONObject;

public class ClientMessageHandler implements MessageHandler {

	private final Context mContext;

	public ClientMessageHandler(Context context) {
		mContext = context;
	}

	@Override
	public void onOpen() {

	}

	@Override
	public void onClose() {

	}

	@Override
	public JSONObject onMessage(JSONObject message) {
		// TODO Auto-generated method stub
		return null;
	}

}
