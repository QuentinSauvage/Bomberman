package model;

import java.awt.Point;

import global.BombsEnum;
import parser.BombJson;

/**
 * bombClass
 * @author quentin sauvage
 *
 */
public class Bomb extends GameObject {
	private BombsEnum bombClass;
	
	public Bomb() {
		super();
	}
	
	public Bomb(BombsEnum bombClass, Point pos, int index) {
		super(pos, index);
		this.bombClass = bombClass;
	}
	
	public Bomb(BombJson bomb) {
		super(bomb.getPos());
		bombClass = bomb.getBombClass();
	}

	public BombsEnum getBombClass() {
		return bombClass;
	}

	public void setBombClass(BombsEnum bombClass) {
		this.bombClass = bombClass;
	}
}
