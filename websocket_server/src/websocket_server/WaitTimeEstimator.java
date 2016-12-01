package websocket_server;

import java.util.ArrayDeque;
import java.util.Queue;

public class WaitTimeEstimator {

	private final Queue<Node> mQueue;
	// The duration of time (in seconds) before current time to
	//   consider when estimating wait time.
	private final long mWindowDuration;
	private int mDurationAcc = 0;
	private double mStdDev;

	public WaitTimeEstimator(int windowDuration) {
		mQueue = new ArrayDeque<>();
		mWindowDuration = windowDuration;
	}

	public int add(Party party) {
		long timestamp = (System.currentTimeMillis() / 1000L);
		Node newNode = new Node();
		newNode.mTimestamp = timestamp;
		newNode.mDuration = (int) (timestamp - party.getTimestamp());
		clearOldNodes(timestamp);
		mQueue.add(newNode);
		updateStdDev();
		return getWaitTime(1);
	}

	public int getWaitTime(double relativePosition) {
		if (mQueue.isEmpty())
			return 0;
		return (int) (relativePosition * (mDurationAcc + mStdDev));
	}

	private void clearOldNodes(long timestamp) {
		while ((mQueue.peek() != null)) {
			Node node = mQueue.peek();
			if ((timestamp - node.mTimestamp) > mWindowDuration) {
				// remove node
				mQueue.poll();
				mDurationAcc -= node.mDuration;
			} else {
				break;
			}
		}
	}

	private void updateStdDev() {
		if (mQueue.isEmpty()) {
			mStdDev = 0;
		} else {
			double mean = ((double) mDurationAcc) / ((double) mQueue.size());
			double diff2acc = 0;
			for (Node node : mQueue) {
				diff2acc += Math.pow(node.mDuration - mean, 2);
			}
			mStdDev = Math.sqrt(diff2acc / mQueue.size());
		}
	}

	private class Node {
		public long mTimestamp;
		public int mDuration;
	}

}
