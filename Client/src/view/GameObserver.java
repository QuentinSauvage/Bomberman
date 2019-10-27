package view;

import java.util.List;

import model.Bomb;
import model.Bonus;

/**
 * Methods used by the game view.
 * @author quentin sauvage
 * @see GameGUI
 */
public interface GameObserver {
	/**
	 * Updates the window depending on the action.
	 * @param str the action
	 */
	public void update(String str);
	
	/**
	 * Updates the corresponding cell with the index sprite in the sprites list to remove.
	 * @param posX the column number.
	 * @param posY the row number.
	 * @param index the index of the sprite in the list.
	 */
	public void update(int posX, int posY, int index);
	
	/**
	 * Updates the corresponding cell with the index sprite in the sprites list to remove.
	 * @param lastX the last column number.
	 * @param lastY the last row number.
	 * @param newX the new column number.
	 * @param newY the new row number.
	 * @param index the index of the sprite in the list.
	 * @param life of the player to draw.
	 * @param add to know if the player sprite should be redrawn.
	 */
	public void update(int lastX, int lastY, int newX, int newY, int index, int life, boolean add);
	
	/**
	 * Updates the corresponding cell with the index sprite in the sprites list to remove.
	 * @param posX the column number.
	 * @param posY the row number.
	 * @param add boolean to know if we're adding or removing.
	 */
	public void update(int posX, int posY, int index, boolean add);
	
	/**
	 * Update the map background and bombs added by explosion.
	 * @param map The new map state.
	 * @param bombsList the list of bombs to add.
	 * @param bonusList the list of bonus to add.
	 */
	public void update(char[][] map, List<Bomb> bombsList, List<Bonus> bonusList);
	
	/**
	 * Update the map when player collect an object.
	 * @param removedBomb the possible bomb to remove..
	 * @param removedBonus The possible bonus to remove.
	 */
	public void update(Bomb removedBomb, Bonus removedBonus);
}
