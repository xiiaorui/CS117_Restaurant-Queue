package websocket_server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;

public class DatabaseClient {

	private static String sURL = "jdbc:mysql://localhost:3306/csm117?useSSL=false";
	private static String sUser = "app_user";
	private static String sPass = "2gvK4unu1(znGNL";

	public static Connection getNewClientConnection() throws SQLException {
		return DriverManager.getConnection(sURL, sUser, sPass);
	}

	public static Connection getNewServerConnection() throws SQLException {
		return DriverManager.getConnection(sURL, sUser, sPass);
	}

	// mainly for testing locally
	public static void setCredentials(String URL, String user, String pass) {
		sURL = URL;
		sUser = user;
		sPass = pass;
	}

	public static JSONArray getOpenRestaurants(Connection conn) {
		JSONArray rows = new JSONArray();
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String query = "SELECT * FROM restaurants WHERE open IS TRUE";
		ResultSet result = null;
		try {
			result = stmt.executeQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			while (result.next()) {
				JSONArray row = new JSONArray();
				int id = result.getInt("id");
				String name = result.getString("name");
				row.put(id);
				row.put(name);
				rows.put(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rows;
	}

	public static int openRestaurant(Connection conn, int restaurant_id) {
		String query = "UPDATE restaurants SET open=TRUE WHERE id=?";
		PreparedStatement prepStmt = null;
		int affectedRows = 0;
		try {
			prepStmt = conn.prepareStatement(query);
			prepStmt.setInt(1, restaurant_id);
			affectedRows = prepStmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			affectedRows = -1;
		}
		return affectedRows;
	}

}
