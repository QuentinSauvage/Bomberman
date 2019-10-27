package client;

import java.net.InetAddress;
import java.util.List;

import parser.JsonReader;
import parser.JsonWriter;

/**
 * Handles the actions between the server and the client.
 * @author quentin sauvage
 * @see TCPClient
 * @see UDPClientBroadcast
 * @see JsonReader
 * @see JsonWriter
 */
public class Client {
	private final static Client instance = new Client();
	private final static String BROADCAST = "255.255.255.255";
	private static JsonReader jsonReader = JsonReader.getInstance();
	private static JsonWriter jsonWriter = JsonWriter.getInstance();
	private UDPClientBroadcast clientUDP;
	private TCPClient clientTCP;
	
	/**
	 * Blank constructor.
	 */
	private Client() {}
	
	/**
	 * Getter for the TCPClient.
	 * @return the TCPClient.
	 */
	public TCPClient getClientTCP() {
		return clientTCP;
	}
	
	/**
	 * Getter for the instance.
	 * @return the single Client instance.
	 */
	public static Client getInstance() {
		return instance;
	}
	
	/**
	 * Initializes the UDP broadcast connection with all the bomberman servers.
	 * @return list of bomberman servers.
	 */
	public List<InetAddress> init() {
		clientUDP = new UDPClientBroadcast(BROADCAST);
		return clientUDP.searchServer();
	}
	
	/**
	 * Creates a TCP connexion between the client and the server.
	 * @param addr the address of the server to connect with.
	 */
	public void connectTCP(String addr) {
		clientTCP = new TCPClient(addr);
	}
	
	/**
	 * Asks the server the games list.
	 */
	public void askGamesList() {
		String s = clientTCP.sendJson(jsonWriter.writeGameList(), true);
		if(s != null) {
			jsonReader.readJson(s);
		}
	}
	
	/**
	 * Asks the server to join a game.
	 * @param name the name of the game
	 * @return boolean representing the success/failure of the joining process.
	 */
	public boolean joinGame(String name) {
		String res = clientTCP.sendJson(jsonWriter.writeJoin(name), true);
		if(res != null) {
			jsonReader.readJson(res);
			return true;
		}
		return false;
	}
	
	/**
	 * Asks the server to create a game.
	 * @param gameName the name of the new game.
	 * @param mapName the map name of the new game.
	 * @return boolean representing the success/failure of the creating process.
	 */
	public boolean createGame(String gameName, String mapName) {
		String res = clientTCP.sendJson(jsonWriter.writeCreate(gameName, mapName), true);
		if(res != null) {
			jsonReader.readJson(res);
			return true;
		}
		return false;
	}
	
	/**
	 * Informs the server that the users is moving.
	 * @param dir the direction in which is moving the player.
	 */
	public void move(String dir) {
		clientTCP.sendJson(jsonWriter.writeMove(dir), false);
	}
	
	/**
	 * Informs the server that the users is droping a bomb.
	 * @param x the x position of the bomb.
	 * @param y the y position of the bomb.
	 * @param bomb the type of bomb.
	 * @param bonus if the bomb is fire powered.
	 */
	public void dropBomb(int x, int y, String type, boolean bonus) {
		clientTCP.sendJson(jsonWriter.writeAttackBomb(x, y, type, bonus), false);
	}
	
	/**
	 * Informs the server that the users is collecting a bomb.
	 * @param type the type of bomb.
	 */
	public void collectBomb(String type) {
		clientTCP.sendJson(jsonWriter.writeGameObject("bomb", type), false);
	}
	
	/**
	 * Informs the server that the users is collecting a bonus.
	 * @param type the bonus type.
	 */
	public void collectBonus(String type) {
		clientTCP.sendJson(jsonWriter.writeGameObject("bonusMalus", type), false);
	}
	
	/**
	 * Informs the server that the remotes must explode.
	 */
	public void explodeRemotes() {
		clientTCP.sendJson(jsonWriter.writeRemote(), false);
	}
	
	/**
	 * Informs the server that the user left the game.
	 */
	public void disconnect() {
		clientTCP.sendJson(jsonWriter.writeDeconnexion(), false);
	}
}
