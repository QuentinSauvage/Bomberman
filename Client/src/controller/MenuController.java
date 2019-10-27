package controller;

import initialisation.GameHandler;
import model.Menu;
import view.MenuView;

/**
 * Controls every events on the menu window.
 * @author quentin sauvage
 * @see MenuView
 * @see Menu
 *
 */
public class MenuController {
	private Menu menu;
	private GameHandler gameHandler = GameHandler.getInstance();
	
	/**
	 * Constructor.
	 * @param menu the menu handling the menu window logic.
	 */
	public MenuController(Menu menu) {
		this.menu = menu;
	}
	
	/**
	 * Asks the games list to the server, and add this list in the menu.
	 * @param host the host we want to get the games list.
	 */
	public void controlGamesList(String host) {
		menu.addGamesList(host);
		menu.notifyObserver();
	}
	
	/**
	 * Asks the menu to search for servers.
	 */
	public void controlServersList() {
		menu.getServers();
		menu.notifyObserver();
	}
	
	/**
	 * Asks the menu to inform the server which game the user wants to join, then asks the GameHandler to display the game window. The user will automatically join the game.
	 * @param game the game the user wants to join.
	 * @see GameHandler
	 */
	public void controlJoinGame(String game) {
		menu.updateGameName(game);
		menu.joinGame(game);
		gameHandler.launchGame();
		menu.notifyObserver();
	}
	
	/**
	 * Asks the menu to inform the server with the information of the game the user wants to create.
	 * @param gameName the name of the game the user wants to create.
	 * @param mapName the name of the map the user wants to create.
	 */
	public void controlCreateGame(String gameName, String mapName) {
		menu.createGame(gameName, mapName);
		gameHandler.launchGame();
		menu.notifyObserver();
	}
	
	/**
	 * Asks the menu to change the binding.
	 * @param bind the name of key changed.
	 * @param bindCode the code of the key changed.
	 */
	public void controlBindings(String bind, int bindCode) {
		menu.updateBindings(bind, bindCode);
		menu.notifyObserver();
	}
}
