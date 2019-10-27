package model;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import client.Client;
import global.Binding;
import initialisation.GameHandler;
import parser.GameJson;
import parser.JsonReader;
import view.MenuObserver;

/**
 * Contains all the menu logic.
 * @author quentin sauvage
 * @see MenuObserver
 * @see Client
 */
public class Menu implements MenuObservable {
	private final static String GET_SERVERS = "get servers";
	private final static String GET_GAMES = "get games";
	private final static String JOIN_GAME = "Rejoindre partie";
	private final static String CREATE_GAME = "Créer partie";
	private final static String UPDATE_BINDINGS = "update bindings";
	private List<MenuObserver> listObserver = new ArrayList<MenuObserver>();
	private List<String> list = new ArrayList<String>();
	private List<String>mapsList = new ArrayList<String>();
	private String action = null;
	private String gameName;
	private Client client = Client.getInstance();
	private GameHandler gameHandler = GameHandler.getInstance();
	
	/**
	 * Search for every bomberman servers.
	 */
	public void getServers() {
		action = GET_SERVERS;
		List<InetAddress> serversList = client.init();
		list.clear();
		for(InetAddress addr : serversList) {
			list.add(addr.getHostAddress());
		}
	}
	
	/**
	 * Getter for the list (games list or servers list).
	 * @return the games/servers list.
	 */
	public List<String> getList() {
		return list;
	}
	
	/**
	 * Search for the games related to this host.
	 * @param host the host the menu wants to ask the games list.
	 * @see JsonReader
	 */
	public void addGamesList(String host) {
		client.connectTCP(host);
		client.askGamesList();
		action = GET_GAMES;
		list.clear();
		for(GameJson game : JsonReader.getInstance().getGames()) {
			list.add("Nom : " + game.getName() + ", map : " + game.getMap() + ", joueurs : " + game.getNbPlayers());
		}
		this.mapsList.clear();
		this.mapsList.addAll(JsonReader.getInstance().getMaps());
	}
	
	/**
	 * Informs the server the user is joining the game.
	 * @param game the game the user want to join. If null, it means it created a new game, and will then automatically join it.
	 */
	public void joinGame(String game) {
		client.joinGame(gameName);
		if(game != null) {
			int firstIndex = game.indexOf('"');
			this.gameName = game.substring(firstIndex, game.indexOf(',', firstIndex));
		}
	}
	
	/**
	 * Update the game name if needed, used to call the joining process.
	 * @param game the game the user want to join. If null, it means it created a new game.
	 */
	public void updateGameName(String game) {
		if(game != null) {
			this.gameName = game.substring(game.indexOf("\""), game.indexOf(","));
			if(this.gameName != null) {
				this.gameName = this.gameName.replaceAll("\"", "");
			}
		}
		action = JOIN_GAME;
	}
	
	/**
	 * Informs the server of the creation of a game.
	 * @param gameName the name of the game the user wants to create.
	 * @param mapName the map name of the game the user wants to create.
	 */
	public void createGame(String gameName, String mapName) {
		if(client.createGame(gameName, mapName)) {
			list.clear();
			this.gameName = gameName;
			action = CREATE_GAME;
		}
	}
	
	/**
	 * Getter for the key code from a key char
	 * @param bindCode the key char.
	 * @return
	 */
	public int getBindCode(int bindCode) {
		int tmpCode = bindCode;
		for(Binding b : gameHandler.getBindings()) {
			int keyCode = b.getKeyCode();
			if(tmpCode >= 'a' && tmpCode <= 'z')
				tmpCode = tmpCode - 'a' + 'A';
			if(keyCode == tmpCode)
				return bindCode;
			else
				return tmpCode;
				
		}
		return bindCode;
	}
	
	/**
	 * Update the game bindings.
	 * @param bind the name of the bind modified.
	 * @param bindCode the code of the bind modified.
	 */
	public void updateBindings(String bind, int bindCode) {
		List<Binding> bindList = gameHandler.getBindings();
		int i, len = bindList.size();
		for(i = 0; i < len; i++) {
			Binding b = bindList.get(i);
			if(b.getKeyName().equals(bind)) {
				b.setKeyCode(getBindCode(bindCode));
				break;
			}
		}
		action = UPDATE_BINDINGS;
	}
	
	@Override
	public void addObserver(MenuObserver obs) {
		this.listObserver.add(obs);
	}

	@Override
	public void removeObserver() {
		listObserver = new ArrayList<MenuObserver>();
	}
	
	@Override
	public void notifyObserver() {
		for(MenuObserver obs : listObserver) {
			obs.update(list, mapsList, action);
		}
	}

}
