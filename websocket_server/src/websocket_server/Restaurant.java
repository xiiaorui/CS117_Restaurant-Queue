package websocket_server;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

public class Restaurant {

	private Context mServerContext;
	private Queue<Party> mQueue;
	// Maps a given client Context to its Party
	private Map<Context, Party> mMap;

	public Restaurant(Context serverContext) {
		mServerContext = serverContext;
		mQueue = new ArrayDeque<Party>();
		mMap = new HashMap<>();
	}

	public synchronized void addParty(Context clientContext, Party party) {
		mQueue.add(party);
		mMap.put(clientContext, party);
	}

	public synchronized void removeFromQueue(Context clientContext) {
		Party party = mMap.get(clientContext);
		if (party != null)
			mQueue.remove(party);
	}

	public synchronized ArrayList<Party> getFrontOfQueue() {
		ArrayList<Party> front = new ArrayList<>();
		Iterator<Party> iter = mQueue.iterator();
		while (iter.hasNext() && (front.size() < 10)) {
			front.add(iter.next());
		}
		return front;
	}

}
