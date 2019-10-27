package model;

import java.awt.Point;

import global.BonusEnum;

/**
 * bonusClass
 * @author quentin sauvage
 *
 */
public class Bonus extends GameObject {
	private BonusEnum bonusClass;
	private int number = 1;
	
	public Bonus(BonusEnum bonusClass, Point pos, int index) {
		super(pos, index);
		this.bonusClass = bonusClass;
	}
	
	public Bonus(BonusEnum bonusClass, int number) {
		super();
		this.bonusClass = bonusClass;
		this.number = number;
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
