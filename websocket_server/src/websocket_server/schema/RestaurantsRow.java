package websocket_server.schema;

public class RestaurantsRow {

	public int id;
	public String name;

	public RestaurantsRow() {
		id = -1;
		name = null;
	}

	public RestaurantsRow(int id, String name) {
		this.id = id;
		this.name = name;
	}

}
