package websocket_server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;

public class DatabaseClient {

	public static final String URL = "jdbc:mysql://localhost:3306/csm117?useSSL=false";
	public static final String USER = "root";
	public static final String PASS = "";

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

}
