package parser;

import java.awt.Point;

import global.BonusEnum;

public class BonusJson extends GameObjectJson {
	private BonusEnum bonusClass;
	private int number;
	
	public BonusJson(Point pos, BonusEnum bonusClass, int number) {
		super(pos);
		this.setBonusClass(bonusClass);
		this.setNumber(number);
	}

	public BonusEnum getBonusClass() {
		return bonusClass;
	}

	public void setBonusClass(BonusEnum bonusClass) {
		this.bonusClass = bonusClass;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
}
