package parser;

import java.awt.Point;

public abstract class GameObjectJson {
	private Point pos;
	
	public GameObjectJson(Point pos) {
		this.setPos(pos);
	}

	public Point getPos() {
		return pos;
	}

	public void setPos(Point pos) {
		this.pos = pos;
	}
}
