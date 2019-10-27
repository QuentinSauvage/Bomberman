package model;

import view.MenuObserver;

/**
 * Defines the methods used by the menu model.
 * @author quentin sauvage
 * @see Menu
 */
public interface MenuObservable {
	/**
	 * Adds the observer to the menu model.
	 * @param obs the observer to add.
	 */
	public void addObserver(MenuObserver obs);
	
	/**
	 * Remove every observers.
	 */
	public void removeObserver();
	
	/**
	 * Notifies the observers.
	 */
	public void notifyObserver();
}
