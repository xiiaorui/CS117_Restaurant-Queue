package websocket_server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import websocket_server.schema.RestaurantsRow;

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

	public static RestaurantsRow getRestaurant(Connection conn, int restaurantID) {
		RestaurantsRow row = null;
		String query = "SELECT * FROM restaurants WHERE id = ?";
		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, restaurantID);
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				row = new RestaurantsRow();
				row.id = result.getInt(1);
				row.name = result.getString(2);
			}
			else {
				// Requested restaurant does not exist.
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return row;
	}

	public static int createRestaurant(Connection conn, String name) {
		String query = "INSERT INTO restaurants (name) VALUES (?)";
		int newRestaurantID = -1;
		try {
			PreparedStatement prepStmt = conn.prepareStatement(
				query,
				Statement.RETURN_GENERATED_KEYS
			);
			prepStmt.setString(1, name);
			int affectedRows = prepStmt.executeUpdate();
			if (affectedRows == 0) {
				throw new SQLException("inserting new restaurant failed, no rows affected");
			}
			ResultSet keys = prepStmt.getGeneratedKeys();
			if (keys.next()) {
				newRestaurantID = keys.getInt(1);
			} else {
				throw new SQLException("inserting new restaurant failed, no ID");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newRestaurantID;
	}

}
