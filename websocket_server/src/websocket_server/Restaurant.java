package websocket_server;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

public class Restaurant {

	private Context mRestaurantContext;
	private final String mName;
	private Queue<Party> mQueue;
	// Maps a customer Context to its party
	private Map<Context, Party> mContextMap;
	private final WaitTimeEstimator mEstimator;
	// mPartyID is used to assign a unique (per restaurant) ID to each party
	//   so that restaurant can reference a specify party through that ID.
	private int mPartyID = 0;
	private boolean mIsClosed = false;

	public Restaurant(Context restaurantContext, String name) {
		mRestaurantContext = restaurantContext;
		mName = name;
		mQueue = new ArrayDeque<Party>();
		mContextMap = new HashMap<>();
		mEstimator = new WaitTimeEstimator(60 * 15);
	}

	public String getName() {
		return mName;
	}

	public Context getRestaurantContext() {
		return mRestaurantContext;
	}

	public synchronized QueueStatus addParty(Party party) {
		QueueStatus status = null;
		if (!mIsClosed) {
			if (party.getCustomerContext() == null) {
				// A logic error occurred somewhere...
				throw new RuntimeException("party's context cannot be null");
			}
			party.setID(getNewPartyID());
			party.setTimestamp(getTimestamp());
			mContextMap.put(party.getCustomerContext(), party);
			mQueue.add(party);
			// get status
			status = new QueueStatus(mQueue.size(), mEstimator.getWaitTime(1));
		}
		return status;
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

	// Returns the party ID of the party associated with custContext
	// Returns -1 if there was no party or restaurant closed
	// fromCall specifies if this call was made due to a call by restaurant.
	public synchronized int removeFromQueue(Context custContext, boolean fromCall) {
		if (mIsClosed)
			return -1;
		Party party = mContextMap.remove(custContext);
		if (party == null) {
			// The customer was not in the queue.
			return -1;
		}
		// clear party's context
		party.clearCustomerContext();
		// update queue
		mQueue.remove(party);
		if (fromCall) {
			// update wait time estimator
			mEstimator.add(party);
		}
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

	public synchronized QueueStatus getStatus(Context custContext) {
		Party party = mContextMap.get(custContext);
		if (party == null) {
			// Customer is not in queue.
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
		double relativePosition = ((double) position / mQueue.size());
		return (new QueueStatus(position, mEstimator.getWaitTime(relativePosition)));
	}

	public synchronized Queue<Party> getQueueAndClose() {
		mIsClosed = true;
		return mQueue;
	}

	public synchronized void clear() {
		if (!mIsClosed) {
			throw new RuntimeException("cannot call clear() when still open");
		}
		mRestaurantContext = null;
		mQueue = null;
		mContextMap = null;
	}

	private long getTimestamp() {
		return (System.currentTimeMillis() / 1000L);
	}

	private int getNewPartyID() {
		int id = mPartyID;
		mPartyID += 1;
		return id;
	}

}
