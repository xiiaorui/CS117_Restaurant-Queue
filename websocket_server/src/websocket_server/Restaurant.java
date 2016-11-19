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
	// mPartyID is used to assign a unique (per restaurant) ID to each party
	//   so that restaurant can reference a specify party through that ID.
	private int mPartyID = 0;
	private boolean mIsClosed = false;

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
		if (!mIsClosed) {
			if (party.getClientContext() == null) {
				// A logic error occurred somewhere...
				throw new RuntimeException("party's context cannot be null");
			}
			party.setID(getNewPartyID());
			mContextMap.put(party.getClientContext(), party);
			mQueue.add(party);
			return true;
		}
		return false;
	}

	// Returns the associated Party if it exists
	public synchronized Party getParty(int partyID) {
		if (mIsClosed)
			return null;
		for (Party party : mQueue) {
			if (party.getID() == partyID) {
				return party;
			} else if (party.getID() > partyID) {
				// party is not in queue
				break;
			}
		}
		return null;
	}

	// Returns the party ID of the party associated with clientContext
	// Returns -1 if there was no party or restaurant closed
	public synchronized int removeFromQueue(Context clientContext) {
		if (mIsClosed)
			return -1;
		Party party = mContextMap.remove(clientContext);
		if (party == null) {
			// The client was not in the queue.
			return -1;
		}
		// clear party's context
		party.clearClientContext();
		// update queue
		mQueue.remove(party);
		return party.getID();
	}

	// Get up to count parties from front of queue
	public synchronized ArrayList<Party> getFrontOfQueue(int count) {
		ArrayList<Party> front = new ArrayList<>();
		Iterator<Party> iter = mQueue.iterator();
		while (iter.hasNext() && (front.size() < count)) {
			front.add(iter.next());
		}
		return front;
	}

	public synchronized QueueStatus getStatus(Context clientContext) {
		Party party = mContextMap.get(clientContext);
		if (party == null) {
			// Client is not in queue.
			return null;
		}
		// Find position.
		int position = 1;
		for (Party p : mQueue) {
			if (p == party) {
				break;
			}
			++position;
		}
		// TODO set wait time
		return (new QueueStatus(position, 0));
	}

	public synchronized Queue<Party> getQueueAndClose() {
		mIsClosed = true;
		return mQueue;
	}

	public synchronized void clear() {
		if (!mIsClosed) {
			throw new RuntimeException("cannot call clear() when still open");
		}
		mServerContext = null;
		mQueue = null;
		mContextMap = null;
	}

	private int getNewPartyID() {
		int id = mPartyID;
		mPartyID += 1;
		return id;
	}

}
