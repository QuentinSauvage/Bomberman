package parser;

import java.awt.Point;

public class PlayersJson extends GameObjectJson {
	private int id;
	
	public PlayersJson(Point pos, int id) {
		super(pos);
		this.setId(id);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
