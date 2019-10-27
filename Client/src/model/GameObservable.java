package model;

import view.GameObserver;

/**
 * Defines the methods used by the game model.
 * @author quentin sauvage
 * @see Game
 */
public interface GameObservable {
	/**
	 * Adds the observer to the game model.
	 * @param obs the observer to add.
	 */
	public void addObserver(GameObserver obs);
	
	/**
	 * Remove every observers.
	 */
	public void removeObserver();
	
	/**
	 * Notifies the observers with the action.
	 * @param str the string representing the action.
	 */
	public void notifyObserver(String str);
	
	/**
	 * Notifies the observers.
	 * @param check to know if we need to redraw.
	 */
	public void notifyObserver(boolean check);
	
	/**
	 * Notifies the observers with the player that has left the game.
	 * @param p the player that has left the game.
	 */
	public void notifyObserver(Player p);
	
	/**
	 * Notifies the observers with the bomb to display.
	 */
	public void notifyObserverBomb();
	
	/**
	 * Notifies the observers with the new map.
	 */
	public void notifyObserverMap();
	
	/**
	 * Notifies the observers with the object collected.
	 */
	public void notifyObserverObject();
	
	/**
	 * Notifies the observers with the life update.
	 */
	public void notifyObserverLife();
}
