package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import parser.BonusJson;
import parser.PlayerJson;
import parser.PlayersJson;

/**
 * The player logic.
 * @author quentin sauvage
 */
public class Player {
	private int id;
	private int life = -1;
	private int speed = 1;
	private int nbClassic = 0;
	private int nbMine = 0;
	private int nbRemote = 0;
	private int maxBombs = -1;
	private int maxLife = -1;
	private List<Bonus> bonusList = null;
	private Point pos;
	private Point lastPos = new Point();
	public boolean moved = true;
	/**
	 * Constructor.
	 * @param player the player informations sent by the server.
	 * @see PlayerJson
	 */
	public Player(PlayerJson player) {
		this.id = player.getId();
		this.life = player.getLife();
		this.maxLife = player.getMaxLife();
		this.speed = player.getSpeed();
		nbClassic = player.getClassic();
		nbMine = player.getMine();
		nbRemote = player.getRemote();
		this.maxBombs = player.getMaxNbBomb();
		this.bonusList = new ArrayList<Bonus>();
		for(BonusJson bonus : player.getBonusList()) {
			this.bonusList.add(new Bonus(bonus.getBonusClass(), bonus.getNumber()));
		}
	}
	
	/**
	 * Constructor.
	 * @param players the players (not the one related to the user) informations sent by the server.
	 * @see PlayersJson
	 */
	public Player(PlayersJson players) {
		this.id = players.getId();
		this.pos = players.getPos();
	}
	
	/**
	 * Getter for the player ID.
	 * @return the player ID.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Setter for the player ID.
	 * @param id the new player ID.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Getter for the player actual life.
	 * @return the player actual life.
	 */
	public int getLife() {
		return life;
	}

	/**
	 * Setter for the player actual life.
	 * @param life the new player actual life.
	 */
	public void setLife(int life) {
		this.life = life;
	}

	/**
	 * Getter for the player speed.
	 * @return the player speed.
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * Setter for the player speed.
	 * @param speed the new player speed.
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}

	/**
	 * Getter for the number of classic bombs of the player.
	 * @return the number of classic bombs of the player.
	 */
	public int getNbClassic() {
		return nbClassic;
	}

	/**
	 * Setter for the number of classic bombs of the player.
	 * @param nbClassic the new number of classic bombs of the player.
	 */
	public void setNbClassic(int nbClassic) {
		this.nbClassic = nbClassic;
	}
	
	/**
	 * Getter for the number of mine bombs of the player.
	 * @return the number of mine bombs of the player.
	 */
	public int getNbMine() {
		return nbMine;
	}

	/**
	 * Setter for the number of mine bombs of the player.
	 * @param nbMine the new number of mine bombs of the player.
	 */
	public void setNbMine(int nbMine) {
		this.nbMine = nbMine;
	}
	
	/**
	 * Getter for the number of remote bombs of the player.
	 * @return the number of remote bombs of the player.
	 */
	public int getNbRemote() {
		return nbRemote;
	}

	/**
	 * Setter for the number of remote bombs of the player.
	 * @param nbRemote the new number of remote bombs of the player.
	 */
	public void setNbRemote(int nbRemote) {
		this.nbRemote = nbRemote;
	}

	/**
	 * Getter for the maximum life of the player.
	 * @return the maximum life of the player.
	 */
	public int getMaxLife() {
		return maxLife;
	}

	/**
	 * Setter for the maximum life of the player.
	 * @param maxLife the new maximum life of the player.
	 */
	public void setMaxLife(int maxLife) {
		this.maxLife = maxLife;
	}
	
	/**
	 * Getter for the maximum number of bombs of the player.
	 * @return the maximum number of bombs of the player.
	 */
	public int getMaxBombs() {
		return maxBombs;
	}

	/**
	 * Setter for the maximum number of bombs of the player.
	 * @param maxBombs the new maximum number of bombs of the player.
	 */
	public void setMaxBombs(int maxBombs) {
		this.maxBombs = maxBombs;
	}

	/**
	 * Getter for the bonus list of the player.
	 * @return the bonus list of the player.
	 */
	public List<Bonus> getBonus() {
		return bonusList;
	}

	/**
	 * Setter for the bonus list of the player.
	 * @param bonus the new bonus list of the player.
	 */
	public void setBonus(List<Bonus> bonus) {
		this.bonusList = bonus;
	}
	
	/**
	 * Getter for the player moved boolean.
	 * return moved the boolean to know if the player has moved.
	 */
	public boolean getMoved() {
		return moved;
	}
	
	/**
	 * Setter for the player moved boolean.
	 * @param lastPos the boolean to know if the player has moved.
	 */
	public void setMoved(boolean moved) {
		this.moved = moved;
	}
	
	/**
	 * Getter for the player position.
	 * return pos the position of the player.
	 */
	public Point getPos() {
		return pos;
	}
	
	/**
	 * Setter for the player position.
	 * @param pos the new position of the player.
	 */
	public void setPos(Point pos) {
		this.pos = pos;
	}
	
	/**
	 * Getter for the player last position.
	 * return lastPos the last position of the player.
	 */
	public Point getLastPos() {
		return lastPos;
	}
	
	/**
	 * Setter for the player last position.
	 * @param lastPos the new last position of the player.
	 */
	public void setLastPos(Point lastPos) {
		this.lastPos = lastPos;
	}
	
	/**
	 * Setter for the player position.
	 * @param x the new position x of the player.
	 * @param x the new position y of the player.
	 */
	public void setPos(int x, int y) {
		lastPos.setLocation(pos);
		pos.setLocation(pos.getX() + x, pos.getY() + y);
	}
	
	/**
	 * Updates a player with the player in the server answer.
	 * @param player the player read in the server answer.
	 */
	public void update(PlayerJson player) {
		this.life = player.getLife();
		this.maxLife = player.getMaxLife();
		this.speed = player.getSpeed();
		nbClassic = player.getClassic();
		nbMine = player.getMine();
		nbRemote = player.getRemote();
		this.maxBombs = player.getMaxNbBomb();
		for(BonusJson bonus : player.getBonusList()) {
			this.bonusList.add(new Bonus(bonus.getBonusClass(), bonus.getNumber()));
		}
	}
}
