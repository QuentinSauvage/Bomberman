package controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import client.Client;
import client.TCPClient;
import global.Binding;
import global.BombsEnum;
import initialisation.GameHandler;
import model.Game;
import model.Player;
import parser.JsonReader;
import view.GameGUI;

/**
 * Controls every events on the game window.
 * @author quentin sauvage
 * @see GameGUI
 * @see Game
 *
 */
public class GameController implements Runnable {
	private static final String PAUSE = "pause";
	private Game game;
	private JsonReader jsonReader;
	private Thread answerThread;
	private boolean gameOpen = true;
	private GameHandler gameHandler = GameHandler.getInstance();
	
	/**
	 * Constructor.
	 * @param game the game handling the game window logic.
	 */
	public GameController(Game game) {
		this.game = game;
		answerThread = new Thread(this);
		answerThread.start();
		jsonReader = JsonReader.getInstance();
	}
	
	/**
	 * Asks the games to refresh the window with all the elements initialized.
	 */
	public void controlInitBoard() {
		game.notifyObserver(false);
	}
	
	/**
	 * Calls different methods regarding the string it receives.
	 * @param str the action asked.
	 * @see Client
	 */
	public void control(String str) {
		if(str.equals("close") || str.equals("lose")) {
			gameOpen = false;
			game.notifyObserver(str);
			Client.getInstance().disconnect();
			try {
				answerThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Controls the inputs of the user.
	 * @param code The code of the key typed by the player.
	 */
	public void controlKeyBoard(int code) {
		int key, dir;
		String name;
		for(Binding b : gameHandler.getBindings()) {
			key = b.getKeyCode();
			if(code == key) {
				name = b.getKeyName();
				dir = b.getDir();
				if(dir >= 0)
					game.move(dir);
				else if(name.equals("Pause"))
					game.notifyObserver(PAUSE);
				else if(name.equals("Classic"))
					game.dropBomb(BombsEnum.CLASSIC.toString());
				else if(name.equals("Remote"))
					game.dropBomb(BombsEnum.REMOTE.toString());
				else if(name.equals("Mine"))
					game.dropBomb(BombsEnum.MINE.toString());
				else if(name.equals("Explode"))
					game.explodeRemotes();
				break;
			}
		}
	}
	
	/**
	 * Controls the position update action.
	 */
	public void controlPositionUpdate() {
		game.updatePosition();
		game.notifyObserver(true);
	}
	
	/**
	 * Controls the attack bomb action.
	 */
	public void controlAttackBomb() {
		if(game.validateBomb())
			game.notifyObserverBomb();
	}
	
	/**
	 * Controls the new bomb action.
	 */
	public void controlNewBomb() {
		if(game.validateBomb(jsonReader.getBomb()))
			game.notifyObserverBomb();
	}
	
	/**
	 * Controls the affect action.
	 */
	public void controlAffect() {
		game.update();
		game.notifyObserverLife();
		game.notifyObserver(true);
		if(game.getPlayer().getLife() <= 0) {
			control("lose");
		}
	}
	
	/**
	 * Controls the object action.
	 */
	public void controlObject() {
		game.updateObject();
		game.notifyObserverObject();
	}
	
	/**
	 * Controls the exploded bomb action.
	 */
	public void controlExplodedBomb() {
		game.validateExplosion();
		game.notifyObserverBomb();
		game.notifyObserverMap();
	}
	
	/**
	 * Controls the new player action.
	 */
	public void controlNewPlayer() {
		game.addNewPlayer();
		game.notifyObserver(false);
	}
	
	/**
	 * Controls the disconnection action.
	 */
	public void controlDisconnection() {
		Player p = game.removeLeavingPlayer();
		if(p != null)
			game.notifyObserver(p);
	}
	
	public void callControlMethod(String action) {
		Method method = null;
		try {
			method = this.getClass().getMethod("control"+action);
		//If the action doesn't exist, it means it is not a TCP action related to the game handling
		} catch (NoSuchMethodException e) {
		} catch (SecurityException e) {}
		try {
			if(method != null) {
				method.invoke(this);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {}
	}
	
	@Override
	public void run() {
		TCPClient tcpClient = Client.getInstance().getClientTCP();
		String ans = new String(), action;
		JsonReader reader = JsonReader.getInstance();
		int i;
		while(gameOpen) {
			ans = tcpClient.readAnswer();
			if(ans != null) {
				String splitter[] = ans.split("\n");
				if(splitter != null) {
					for(i = 0; i < splitter.length - 1; i++) {
						jsonReader.readJson(splitter[i]);
						action = reader.getAction();
						if(action != null) {
							callControlMethod(action);
						}
					}
				}
			}
		}
		control("leave");
	}
}
