package parser;

import java.awt.Point;

import global.BombsEnum;

public class BombJson extends GameObjectJson {
	private BombsEnum bombClass;
	private boolean bonus;
	
	public BombJson() {
		super(new Point());
		bombClass = null;
		bonus = false;
	}
	
	public BombJson(Point pos, BombsEnum bombClass, boolean bonus) {
		super(pos);
		this.bombClass = bombClass;
		this.bonus = bonus;
	}
	
	public BombsEnum getBombClass() {
		return bombClass;
	}
	
	public void setBombClass(BombsEnum bombClass) {
		this.bombClass = bombClass;
	}
	
	public boolean isBonus() {
		return bonus;
	}
	
	public void setBonus(boolean bonus) {
		this.bonus = bonus;
	}
}
