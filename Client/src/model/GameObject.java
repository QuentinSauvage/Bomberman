package model;

import java.awt.Point;

/**
 * Represents a game object.
 * @author quentin sauvage
 *
 */
public abstract class GameObject {
	private int index;
	private Point pos;
	
	public GameObject() {
		index = -1;
		pos = null;
	}
	
	public GameObject(Point pos) {
		this.pos = pos;
		this.index = -1;
	}
	
	public GameObject(Point pos, int index) {
		this.pos = pos;
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public Point getPos() {
		return pos;
	}
	
	public void setPos(Point pos) {
		this.pos = pos;
	}
}
