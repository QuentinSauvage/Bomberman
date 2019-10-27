package initialisation;

import java.util.ArrayList;
import java.util.List;

import com.sun.glass.events.KeyEvent;

import controller.GameController;
import controller.MenuController;
import global.Binding;
import model.Game;
import model.Menu;
import parser.JsonReader;
import parser.MapJson;
import view.GameGUI;
import view.GameObserver;
import view.MenuObserver;
import view.MenuView;

/**
 * Controls the MVCs of the application.
 * @author quentin sauvage
 *
 */
public class GameHandler {

	private final static String keyNames[] = {"Gauche", "Haut", "Droite", "Bas", "Classic", "Remote", "Mine","Espace", "Pause"};
	private final static int keyCodes[] = {KeyEvent.VK_LEFT,KeyEvent.VK_UP,KeyEvent.VK_RIGHT,KeyEvent.VK_DOWN,KeyEvent.VK_X,KeyEvent.VK_W,KeyEvent.VK_C,KeyEvent.VK_SPACE,KeyEvent.VK_ESCAPE};
	private static GameHandler gameHandler = new GameHandler();
	private static MenuView menuView = MenuView.getInstance();
	private static GameGUI gameView = GameGUI.getInstance();
	private final List<Binding> bindings = new ArrayList<Binding>();
	
	/**
	 * Blank constructor.
	 */
	private GameHandler() {
		int i = 0;
		for(String key : keyNames)
			bindings.add(new Binding(key, keyCodes[i++]));
	}

	/**
	 * Launches the menu.
	 */
	public void start() {
		launchMenu();
	}

	/**
	 * Launches the menu by creating the model, the view, and the controller.
	 * @see Menu
	 * @see MenuController
	 * @see MenuView
	 */
	private void launchMenu() {
		Menu menu = new Menu();
		MenuController menuController = new MenuController(menu);
		menuView.setController(menuController);
		menu.addObserver((MenuObserver) menuView);
		menuView.display();
	}

	/**
	 * Launches the game by creating the model, the view, and the controller, using the informations sent by the server.
	 * @see JsonReader
	 * @see MapJson
	 * @see Game
	 * @see GameController
	 * @see GameGUI
	 */
	public void launchGame() {
		MapJson map = JsonReader.getInstance().getMap();
		Game game = new Game(map);
		GameController gameController = new GameController(game);
		gameView.setController(gameController);
		game.addObserver((GameObserver) gameView);
		gameView.displayGame(map);
	}

	/**
	 * Getter for the GameHandler instance.
	 * @return the single GameHandler instance.
	 */
	public static GameHandler getInstance() {
		return gameHandler;
	}
	
	/**
	 * Getter for the list of bindings.
	 * @return the list of bindings.
	 */
	public List<Binding> getBindings() {
		return bindings;
	}
}
