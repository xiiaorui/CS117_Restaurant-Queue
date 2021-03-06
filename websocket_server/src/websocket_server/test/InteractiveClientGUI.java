package websocket_server.test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import websocket_server.Server;
import websocket_server.ServerAction;

public class InteractiveClientGUI extends JFrame implements ActionListener {

	InteractiveClient mClient = null;
	JTextField mURIInput;
	JComboBox<String> mInput;
	JTextArea mOutput;
	JScrollPane mScrollPane;
	JButton mConnectButton;
	JButton mSendButton;

	public InteractiveClientGUI() throws HeadlessException {
		super("WebSocket Client");

		Container pane = getContentPane();
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

		mURIInput = new JTextField(
			"ws://localhost/" + Server.RESTAURANT_RESOURCE_DESCRIPTOR + ":80",
			80
		);
		pane.add(mURIInput);

		mConnectButton = new JButton("Connect");
		mConnectButton.addActionListener(this);
		pane.add(mConnectButton);

		mOutput = new JTextArea(15, 1);
		mOutput.setLineWrap(true);
		DefaultCaret caret = (DefaultCaret) mOutput.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		mScrollPane = new JScrollPane(
			mOutput,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
		);
		pane.add(mScrollPane, BorderLayout.CENTER);

		Vector<String> comboItems = new Vector<>();
		for (ServerAction action : ServerAction.values()) {
			comboItems.add(action.getValue());
		}
		Collections.sort(comboItems);
		mInput = new JComboBox<String>(comboItems);
		pane.add(mInput);

		mSendButton = new JButton("Send");
		mSendButton.addActionListener(this);
		mSendButton.setEnabled(false);
		pane.add(mSendButton);

		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (mClient != null)
					mClient.close();
				dispose();
			}
		});

		pack();
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == mConnectButton) {
			String uriStr = mURIInput.getText();
			URI uri = null;
			try {
				uri = new URI(uriStr);
			} catch (URISyntaxException e) {
				JOptionPane.showMessageDialog(null, "Invalid URI: " + e);
				e.printStackTrace();
			}
			outputAppend("Attempting to connect to: " + uriStr);
			mClient = new InteractiveClient(uri, this);
			mClient.connect();
		}
		else if (event.getSource() == mSendButton) {
			String actionStr = (String) mInput.getSelectedItem();
			ServerAction action = getAction(actionStr);
			JSONObject req = null;
			if (action == null) {
				JOptionPane.showMessageDialog(null, "Unknown action.");
				return;
			}
			switch (action) {
			case GET_OPEN_RESTAURANTS:
				req = RequestFactory.openRestaurantsList();
				break;
			case OPEN_RESTAURANT:
				req = genOpenRestaurantRequest();
				break;
			case CREATE_RESTAURANT:
				req = genCreateRestaurantRequest();
				break;
			case QUEUE:
				req = genQueueRequest();
				break;
			case LEAVE_QUEUE:
				req = RequestFactory.leaveQueue();
				break;
			case GET_PARTIES:
				req = genGetPartiesRequest();
				break;
			case CALL_PARTY:
				req = genCallPartyRequest();
				break;
			case QUEUE_STATUS:
				req = RequestFactory.queueStatus();
				break;
			default:
				JOptionPane.showMessageDialog(null, "Unimplemented action.");
			}
			if (req != null) {
				String reqStr = req.toString();
				outputAppend("Request:\n" + reqStr + "\n");
				mClient.send(reqStr);
			}
		}
	}

	private JSONObject genOpenRestaurantRequest() {
		int restaurantID = -1;
		boolean repeat;
		do {
			repeat = false;
			String idStr = JOptionPane.showInputDialog(null, "restaurant ID");
			if (idStr == null)
				return null;
			try {
				restaurantID = Integer.parseInt(idStr);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Invalid restaurant ID.");
				repeat = true;
			}
		} while (repeat);
		return RequestFactory.openRestaurant(restaurantID);
	}

	private JSONObject genCreateRestaurantRequest() {
		String restaurantName = JOptionPane.showInputDialog(null, "restaurant name");
		if (restaurantName == null)
			return null;
		return RequestFactory.createRestaurant(restaurantName);
	}

	private JSONObject genQueueRequest() {
		int restaurantID = -1;
		String partyName;
		int partySize = -1;
		boolean repeat;
		do {
			repeat = false;
			String restaurantIDStr = JOptionPane.showInputDialog(null, "restaurant ID");
			if (restaurantIDStr == null)
				return null;
			try {
				restaurantID = Integer.parseInt(restaurantIDStr);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Invalid restaurant ID.");
				repeat = true;
			}
		} while (repeat);
		partyName = JOptionPane.showInputDialog(null, "party name");
		if (partyName == null)
			return null;
		do {
			repeat = false;
			String partySizeStr = JOptionPane.showInputDialog(null, "party size");
			if (partySizeStr == null)
				return null;
			try {
				partySize = Integer.parseInt(partySizeStr);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Invalid party size.");
				repeat = true;
			}
		} while (repeat);
		return RequestFactory.queue(restaurantID, partyName, partySize);
	}

	private JSONObject genGetPartiesRequest() {
		int numParties = -1;
		boolean repeat;
		do {
			repeat = false;
			String numPartiesStr = JOptionPane.showInputDialog(null, "num parties");
			if (numPartiesStr == null)
				return null;
			try {
				numParties = Integer.parseInt(numPartiesStr);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Invalid num parties");
				repeat = true;
			}
		} while (repeat);
		return RequestFactory.getParties(numParties);
	}

	private JSONObject genCallPartyRequest() {
		int partyID = -1;
		boolean repeat;
		do {
			repeat = false;
			String partyIDStr = JOptionPane.showInputDialog(null, "party id");
			if (partyIDStr == null)
				return null;
			try {
				partyID = Integer.parseInt(partyIDStr);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Invalid party ID.");
				repeat = true;
			}
		} while (repeat);
		return RequestFactory.callParty(partyID);
	}

	private static ServerAction getAction(String actionStr) {
		for (ServerAction action : ServerAction.values()) {
			if (action.getValue().equals(actionStr)) {
				return action;
			}
		}
		return null;
	}

	// always appends a newline after text
	private void outputAppend(String text) {
		mOutput.append(text + "\n");
	}

	public void notifyOnClose(int code, String reason, boolean remote) {
		outputAppend(
			"Connection closed.\ncode=" + code + "\nreason=" + reason
			+ "\nremote=" + remote + "\n"
		);
	}

	public void notifyOnError(Exception e) {
		outputAppend("Error:\n" + e + "\n\n");
	}

	public void notifyOnMessage(String message) {
		outputAppend("Response:\n" + message + "\n");
	}

	public void notifyOnOpen(ServerHandshake arg0) {
		mConnectButton.setEnabled(false);
		mSendButton.setEnabled(true);
		outputAppend("Successfully connected to server.\n");
	}

	public static void main(String[] args) {
		new InteractiveClientGUI();
	}

}
