package parser;

import java.awt.Point;
import java.util.List;

/**
 * Contains all the methods referring to the json reading.
 * @author quentin sauvage
 * @see JsonReader
 */
public interface ReadJson {
	/**
	 * Reads the informations concerning the game
	 * @param game the game to read
	 */
	void readGame(String game);
	
	/**
	 * Reads the informations concerning the games list
	 * @param games the games list to read
	 */
	void readGames(String games);
	
	/**
	 * Reads a position
	 * @param pos the position to read
	 * @return
	 */
	Point readPos(String pos);
	
	/**
	 * Reads the informations concerning the map
	 * @param map the map to read
	 */
	void readMap(String map);
	
	/**
	 * Reads a bombs list.
	 * @param bomb the json name for the bomb list.
	 * @param type informs if the reader should read "class" or "type" for the bomb.
	 * @return
	 */
	List<BombJson> readBombList(String bomb, String type);
	
	/**
	 * Reads a bonus list.
	 * @param bonus the json name for the bonus list.
	 * @param type  informs if the reader should read "class" or "type" for the bonus.
	 * @return
	 */
	List<BombJson> readBombList2(String bonus, String type);
	
	/**
	 * Reads a bonus list.
	 * @param bonus the json name for the bonus list.
	 * @param type  informs if the reader should read "class" or "type" for the bonus.
	 * @return
	 */
	List<BonusJson> readBonusList(String bonus, String type);
	
	/**
	 * Reads the user player.
	 * @param player the user player.
	 */
	void readPlayer(String player);
	
	/**
	 * Reads all players informations.
	 * @param players the players list.
	 */
	void readPlayers(String players);
	
	/**
	 * Reads all maps.
	 * @param maps the maps list.
	 */
	void readMaps(String maps);
	
	/**
	 * Read the list action.
	 * @param json the message sent.
	 */
	void readList(String json);
	
	/**
	 * Read the create action informations.
	 * @param json the message sent.
	 */
	void readCreate(String json);
	
	/**
	 * Reads the join action informations.
	 * @param json the message sent.
	 */
	void readJoin(String json);
	
	/**
	 * Reads the new player informations.
	 * @param json the message sent.
	 */
	void readNewPlayer(String json);
	
	/**
	 * Reads a position update.
	 * @param json the message sent.
	 */
	void readPositionUpdate(String json);
	
	/**
	 * Reads the informations about the bomb deposited by the player.
	 * @param json the message sent.
	 */
	void readAttackBomb(String json);
	
	/**
	 * Reads the deposit of a bomb by another player.
	 * @param json the message sent.
	 */
	void readNewBomb(String json);
	
	/**
	 * Reads the informations about a bomb that exploded.
	 * @param json the message sent.
	 */
	void readExplodedBomb(String json);
	
	/**
	 * Reads the informations about an exploded player.
	 * @param json the message sent.
	 */
	void readAffect(String json);
	
	/**
	 * Reads the informations about a created object.
	 * @param json the message sent.
	 */
	void readObject(String json);
	
	/**
	 * Reads the disconnection action.
	 * @param json the message sent.
	 */
	void readDisconnection(String json);
}
