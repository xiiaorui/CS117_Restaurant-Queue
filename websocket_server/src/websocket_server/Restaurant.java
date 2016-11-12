package websocket_server;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;

public class Restaurant {

	private Context mServerContext;
	private Queue<Party> mQueue;

	public Restaurant(Context serverContext) {
		mServerContext = serverContext;
		mQueue = new ArrayDeque<Party>();
	}

	public void addParty(Party party) {
		synchronized(mQueue) {
			mQueue.add(party);
		}
	}

	public ArrayList<Party> getFrontOfQueue() {
		ArrayList<Party> front = new ArrayList<>();
		synchronized(mQueue) {
			Iterator<Party> iter = mQueue.iterator();
			while (iter.hasNext() && (front.size() < 10)) {
				front.add(iter.next());
			}
		}
		return front;
	}

}
