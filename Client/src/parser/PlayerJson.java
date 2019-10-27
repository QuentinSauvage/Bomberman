package parser;

import java.util.ArrayList;
import java.util.List;

public class PlayerJson {
	private int id;
	private int life;
	private int maxLife;
	private int speed;
	private int classic;
	private int remote;
	private int mine;
	int maxNbBomb;
	List<BonusJson> bonusList;
	
	public PlayerJson() {
		id = 0;
		life = 0;
		maxLife = 0;
		speed = 0;
		maxNbBomb = 0;
		classic = 0;
		mine = 0;
		remote = 0;
		bonusList = new ArrayList<BonusJson>();
	}
	
	public PlayerJson(int id, int life, int maxLife, int speed, int maxNbBomb, int classic, int mine, int remote) {
		this.id = id;
		this.life = life;
		this.maxLife = maxLife;
		this.speed = speed;
		this.maxNbBomb = maxNbBomb;
		this.classic = classic;
		this.mine = mine;
		this.remote = remote;
		bonusList = new ArrayList<BonusJson>();
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getLife() {
		return life;
	}
	
	public void setLife(int life) {
		this.life = life;
	}
	
	public int getMaxLife() {
		return maxLife;
	}
	
	public void setMaxLife(int maxLife) {
		this.maxLife = maxLife;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	public int getClassic() {
		return classic;
	}
	
	public void setClassic(int classic) {
		this.classic = classic;
	}
	
	public int getMine() {
		return mine;
	}
	
	public void setMine(int mine) {
		this.mine = mine;
	}
	
	public int getRemote() {
		return remote;
	}
	
	public void setRemote(int remote) {
		this.remote = remote;
	}
	
	public int getMaxNbBomb() {
		return maxNbBomb;
	}
	
	public void setMaxNbBomb(int maxNbBomb) {
		this.maxNbBomb = maxNbBomb;
	}
	
	public List<BonusJson> getBonusList() {
		return bonusList;
	}
	
	public void setBonusList(List<BonusJson> bonus) {
		this.bonusList = bonus;
	}
	
}
