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

	public Restaurant(Context serverContext, String name) {
		mServerContext = serverContext;
		mName = name;
		mQueue = new ArrayDeque<Party>();
	}

	public String getName() {
		return mName;
	}

	public synchronized void addParty(Party party) {
		mQueue.add(party);
	}

	public synchronized void removeFromQueue(Party party) {
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
