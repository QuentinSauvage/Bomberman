package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import client.Client;
import global.BombsEnum;
import global.BonusEnum;
import global.Direction;
import parser.BombJson;
import parser.BonusJson;
import parser.JsonReader;
import parser.MapJson;
import parser.PlayerJson;
import view.GameObserver;

/**
 * Contains all the game logic.
 * @author quentin sauvage
 * @see GameObserver
 * @see Player
 * @see MapJson
 *
 */
public class Game implements GameObservable {	
	private List<GameObserver> listObserver = new ArrayList<GameObserver>();
	private List<Player> playersList = new ArrayList<Player>();
	private char[][] board;
	private int width;
	private int height;
	private JsonReader reader = JsonReader.getInstance();
	private List<Bomb> bombsList;
	private List<Bomb> addedBombsList;
	private Bomb removedBomb = null;
	private List<Bonus> bonusList;
	private List<Bonus> addedBonusList;
	private Bonus removedBonus = null;
	private Player player;
	private Bomb bombObject = new Bomb();
	private Point objectPos = new Point();
	private boolean toAdd = false;

	/**
	 * Constructor
	 * @param map the map related to the actual game.
	 */
	public Game(MapJson map) {
		//mapName to mapId
		createBoard(map);
		createPlayers();
		bombsList = new ArrayList<Bomb>();
		bonusList = new ArrayList<Bonus>();
		addedBombsList = new ArrayList<Bomb>();
		addedBonusList = new ArrayList<Bonus>();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * Initializes the board with the map content.
	 * @param map the map sent by the server.
	 */
	public void createBoard(MapJson map) {
		width = map.getWidth();
		height = map.getHeight();
		board = new char[height][width];
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				board[i][j] = map.getContent()[i][j];
			}
		}
	}
	
	/**
	 * Gets all the players informations from the server, and add them in the logic.
	 * @see JsonReader
	 * @see PlayerJson
	 */
	public void createPlayers() {
		PlayerJson player = reader.getPlayer();
		int nbPlayers = reader.getNb();
		for(int i = 0; i < nbPlayers; i++) {
			if(player.getId() == i+1) {
				this.player = new Player(player);
				playersList.add(this.player);
				playersList.get(i).setPos(reader.getStartPos());
			} else {
				playersList.add(new Player(reader.getPlayers().get(i)));
			}
		}
	}
	
	/**
	 * Updates the player position.
	 */
	public void updatePosition() {
		Direction dir = reader.getDir();
		int d1, d2;
		d1 = dir.ordinal() / 2;
		d2 = dir.ordinal() % 2;
		if(d2 == 0) {
			d1 = (d1 > 0) ? 1 : -1;
		} else {
			d2 = (d1 > 0) ? 1 : -1;
			d1 = 0;
		}
		int id = reader.getNb();
		for(Player p : playersList) {
			if(p.getId() == id) {
				p.setMoved(true);
				p.setPos(d1, d2);
				if(id == player.getId()) {
					for(Bomb  bomb : bombsList) {
						if(bomb.getPos().equals(p.getPos())) {
							objectPos = bomb.getPos();
							Client.getInstance().collectBomb(bomb.getBombClass().toString());
							return;
						}
					}
					for(Bonus bonus : bonusList) {
						if(bonus.getPos().equals(p.getPos())) {
							objectPos = bonus.getPos();
							Client.getInstance().collectBonus(bonus.getBonusClass().toString());
							return;
						}
					}
				}
			}
		}
	}
	
	/**
	 * Informs the server of the direction pressed by the user.
	 * @param dir the direction chosen.
	 * @see Client
	 * @see Direction
	 */
	public void move(int dir) {
		Client.getInstance().move(Direction.values()[dir].toString().toLowerCase());
	}
	
	/**
	 * Informs the server the user wants to drop a bomb.
	 * @param type
	 */
	public void dropBomb(String type) {
		for(BombsEnum b : BombsEnum.values()) {
			if(b.toString().equals(type)) {
				bombObject.setBombClass(b);
				break;
			}
		}
		bombObject.setPos(player.getPos());
		boolean fire_power = false;
		for(Bonus b : player.getBonus()) {
			if(b.getBonusClass().toString().equals("fire_power")) {
				fire_power = true;
				break;
			}
		}
		Client.getInstance().dropBomb((int) player.getPos().getX(), (int) player.getPos().getY(), type, fire_power);
	}
	
	/**
	 * Asks the client to notify the server of the explose remotes action.
	 */
	public void explodeRemotes() {
		Client.getInstance().explodeRemotes();
	}
	
	/**
	 * Getter for the bomb sprite index.
	 * @param type the bomb type.
	 * @param decrease boolean to know if we should also decrease the player number of bombs (not used).
	 * @return
	 */
	public int getBombIndex(String type, boolean decrease) {
		int index = -1;
		if(type.equals(BombsEnum.CLASSIC.toString())) {
			if(decrease)
				player.setNbClassic(player.getNbClassic() - 1);
			index = 0;
		} else if(type.equals(BombsEnum.MINE.toString())) {
			if(decrease)
				player.setNbMine(player.getNbMine() - 1);
			index = 1;
		} else if(type.equals(BombsEnum.REMOTE.toString())) {
			if(decrease)
				player.setNbRemote(player.getNbRemote() - 1);
			index = 2;
		}
		return index;
	}
	
	/**
	 * Getter for the bonus sprite index.
	 * @param type the bonus type.
	 * @return
	 */
	public int getBonusIndex(String type) {
		for(BonusEnum bonusEnum : BonusEnum.values()) {
			if(bonusEnum.toString().equals(type))
				return bonusEnum.ordinal();
		}
		return 0;
	}
	
	/**
	 * Update the game with a bomb dropped by the player.
	 * @return boolean to know if this action is possible.
	 */
	public boolean validateBomb() {
		if(reader.getStatus() >= 400) return false;
		bombObject.setIndex(getBombIndex(bombObject.getBombClass().toString(), true));
		if(bombObject.getIndex() < 0) return false;
		toAdd = true;
		return true;
	}
	
	/**
	 * Updates the game with a bomb dropped by another player.
	 * @param bomb the bomb to add.
	 * @return boolean to know if this action is possible.
	 */
	public boolean validateBomb(BombJson bomb) {
		bombObject = new Bomb(bomb);
		bombObject.setIndex(getBombIndex(bomb.getBombClass().toString(), false));
		if(bombObject.getIndex() < 0) return false;
		bombObject.setPos(bomb.getPos());
		toAdd = true;
		return true;
	}
	
	/**
	 * Handles the explosion answer.
	 */
	public void validateExplosion() {
		bombObject = new Bomb(reader.getBomb());
		bombObject.setIndex(getBombIndex(bombObject.getBombClass().toString(), false));
		MapJson map = reader.getMap();
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				board[i][j] = map.getContent()[i][j];
			}
		}
		addedBombsList = new ArrayList<Bomb>();
		addedBonusList = new ArrayList<Bonus>();
		if(reader.getBombsList() != null) {
			for(BombJson bombJson : reader.getBombsList()) {
				Bomb b = new Bomb(bombJson.getBombClass(), bombJson.getPos(), getBombIndex(bombJson.getBombClass().toString(), false) + 3);
				addedBombsList.add(b);
				bombsList.add(b);
			}
		}
		if(reader.getBonusList() != null) {
			for(BonusJson bonusJson : reader.getBonusList()) {
				Bonus b = new Bonus(bonusJson.getBonusClass(), bonusJson.getPos(), getBonusIndex(bonusJson.getBonusClass().toString()));
				addedBonusList.add(b);
				bonusList.add(b);
			}
		}
		toAdd = false;
	}
	
	/**
	 * Adds a player to the game.
	 */
	public void addNewPlayer() {
		playersList.add(new Player(reader.getPlayers().get(0)));
	}
	
	/**
	 * Remove a player from the game.
	 * @return the player to remove.
	 */
	public Player removeLeavingPlayer() {
		for(Player p : playersList) {
			if(p.getId() == reader.getDisconnected()) {
				playersList.remove(p);
				return p;
			}
		}
		return null;
	}
	
	public void update() {
		player.update(reader.getPlayer());
	}
	
	public void updateObject() {
		update();
		for(Bomb bomb : bombsList) {
			if(bomb.getPos().equals(objectPos)) {
				removedBomb = bomb;
				bombsList.remove(bomb);
				return;
			}
		}
		for(Bonus bonus : bonusList) {
			if(bonus.getPos().equals(objectPos)) {
				removedBonus = bonus;
				bonusList.remove(bonus);
				return;
			}
		}
	}
	
	@Override
	public void addObserver(GameObserver obs) {
		this.listObserver.add(obs);
	}

	@Override
	public void removeObserver() {
		listObserver = new ArrayList<GameObserver>();
	}
	
	@Override
	public void notifyObserver(String str) {
		//notify the view about the changes
		for(GameObserver obs : listObserver)
			obs.update(str);
	}

	@Override
	public void notifyObserver(boolean check) {
		//notify the view about the changes
		for(GameObserver obs : listObserver)
			for(Player p : playersList)
				if(!check || p.getMoved()) {
					obs.update((int) p.getLastPos().getY(), (int) p.getLastPos().getX(), (int) p.getPos().getY(), (int) p.getPos().getX(), (p.getId()-1) % 4, p.getLife(), true);
					p.setMoved(false);
				}
	}
	
	
	@Override
	public void notifyObserverLife() {
		for(GameObserver obs : listObserver)
			obs.update((int) player.getLastPos().getY(), (int) player.getLastPos().getX(), (int) player.getPos().getY(), (int) player.getPos().getX(), (player.getId()-1) % 4, player.getLife(), false);
	}
	
	@Override
	public void notifyObserver(Player p) {
		//notify the view about the changes
		for(GameObserver obs : listObserver)
			obs.update((int) p.getPos().getY(), (int) p.getPos().getX(), (p.getId()-1) % 4);
	}
	
	@Override
	public void notifyObserverBomb() {
		for(GameObserver obs : listObserver) {
			obs.update((int) bombObject.getPos().getY(), (int) bombObject.getPos().getX(), bombObject.getIndex(), toAdd);
		}
	}
	
	@Override
	public void notifyObserverMap() {
		for(GameObserver obs : listObserver)
			obs.update(board, addedBombsList, addedBonusList);
	}
	
	@Override
	public void notifyObserverObject() {
		for(GameObserver obs : listObserver)
			obs.update(removedBomb, removedBonus);
		removedBomb = null;
		removedBonus = null;
	}

}
