package websocket_server.test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import websocket_server.ServerAction;

public class InteractiveClientGUI extends JFrame implements ActionListener {

	InteractiveClient mClient = null;
	JTextField mURIInput;
	JTextField mInput;
	JTextArea mOutput;
	JScrollPane mScrollPane;
	JButton mConnectButton;
	JButton mSendButton;

	public InteractiveClientGUI() throws HeadlessException {
		super("WebSocket Client");

		Container pane = getContentPane();
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

		mURIInput = new JTextField("ws://localhost/server:80", 100);
		pane.add(mURIInput);

		mConnectButton = new JButton("Connect");
		mConnectButton.addActionListener(this);
		pane.add(mConnectButton);

		mOutput = new JTextArea(15, 1);
		mOutput.setLineWrap(true);
		mScrollPane = new JScrollPane(
			mOutput,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
		);
		pane.add(mScrollPane, BorderLayout.CENTER);

		mInput = new JTextField(60);
		pane.add(mInput);

		mSendButton = new JButton("Send");
		mSendButton.addActionListener(this);
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
			mConnectButton.setEnabled(false);
		}
		else if (event.getSource() == mSendButton) {
			String actionStr = mInput.getText();
			ServerAction action = getAction(actionStr);
			JSONObject req = null;
			if (action == null) {
				JOptionPane.showMessageDialog(null, "Unknown action.");
				return;
			}
			switch (action) {
			case GET_OPEN_RESTAURANTS:
				req = genGetOpenRestaurantsRequest();
				break;
			case OPEN_RESTAURANT:
				req = genOpenRestaurantRequest();
				break;
			default:
				JOptionPane.showMessageDialog(null, "Unimplemented action.");
			}
			if (req != null) {
				String reqStr = req.toString();
				outputAppend("Request:\n" + reqStr + "\n");
				mClient.send(reqStr);
				mInput.setText(null);
			}
		}
	}

	private JSONObject genGetOpenRestaurantsRequest() {
		return RequestFactory.openRestaurantsList();
	}

	private JSONObject genOpenRestaurantRequest() {
		int restaurantID = -1;
		boolean repeat;
		do {
			repeat = false;
			String idStr = JOptionPane.showInputDialog(null, "restaurant ID");
			try {
				restaurantID = Integer.parseInt(idStr);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Invalid restaurant ID.");
				repeat = true;
			}
		} while (repeat);
		return RequestFactory.openRestaurant(restaurantID);
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
		JScrollBar vertical = mScrollPane.getVerticalScrollBar();
		vertical.setValue(vertical.getMaximum());
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
		outputAppend("Successfully connected to server.\n");
	}

	public static void main(String[] args) {
		new InteractiveClientGUI();
	}

}
