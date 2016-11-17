package websocket_server;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

public class Restaurant {

	private Context mServerContext;
	private final String mName;
	private Queue<Party> mQueue;
	// Maps a client Context to its party
	private Map<Context, Party> mContextMap;
	private boolean mAcceptingNewParty = true;

	public Restaurant(Context serverContext, String name) {
		mServerContext = serverContext;
		mName = name;
		mQueue = new ArrayDeque<Party>();
		mContextMap = new HashMap<>();
	}

	public String getName() {
		return mName;
	}

	public Context getServerContext() {
		return mServerContext;
	}

	public synchronized boolean addParty(Party party) {
		if (mAcceptingNewParty) {
			if (party.getClientContext() == null) {
				// A logic error occurred somewhere...
				throw new RuntimeException("party's context cannot be null");
			}
			mContextMap.put(party.getClientContext(), party);
			mQueue.add(party);
			return true;
		}
		return false;
	}

	public synchronized void removeFromQueue(Context clientContext) {
		Party party = mContextMap.remove(clientContext);
		if (party == null) {
			// The client was not in the queue.
			return;
		}
		// update queue
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

	public synchronized void close() {
		mAcceptingNewParty = false;
	}

}
