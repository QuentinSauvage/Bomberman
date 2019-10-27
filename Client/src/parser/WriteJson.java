package parser;

/**
 * Contains all the methods referring to the json writing.
 * @author quentin sauvage
 * @see JsonWriter
 */
public interface WriteJson {
	/**
	 * Writes the game/list action.
	 * @return the message written.
	 */
	String writeGameList();
	
	/**
	 * Writes the game/create action.
	 * @param name the name of the game created.
	 * @param map the map of the game created.
	 * @return the message written.
	 */
	String writeCreate(String name, String map);
	
	/**
	 * Writes the game/join action.
	 * @param name The name of the gamed joined.
	 * @return the message written.
	 */
	String writeJoin(String name);
	
	/**
	 * Writes the player/move action
	 * @param move the direction of the player.
	 * @return the message written.
	 */
	String writeMove(String move);
	
	/**
	 * Writes the attack/bomb action.
	 * @param x the x pos.
	 * @param y the y pos.
	 * @param type the type of the bomb.
	 * @param bonus if the bomb is affected by a bonus or not.
	 * @return the message written.
	 */
	String writeAttackBomb(int x, int y, String type, boolean bonus);
	
	/**
	 * Writes the object/new action (bomb and bonus).
	 * @param type the type of the object.
	 * @param objectClass the name of the object.
	 * @return the message written.
	 */
	String writeGameObject(String type, String objectClass);
	
	/**
	 *  Writes the remote action.
	 * @return the message written.
	 */
	String writeRemote();
	
	/**
	 * Writes the disconnection action.
	 * @return the message written.
	 */
	String writeDeconnexion();
}
