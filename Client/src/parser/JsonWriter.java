package parser;

import global.Actions;

/**
 * Writes every messages to send to the server.
 * @author quentin sauvage
 * @see Actions
 * @see WriteJson
 */
public class JsonWriter implements WriteJson {
	private static JsonWriter parser = new JsonWriter();
	
	/**
	 * Blank constructor.
	 */
	private JsonWriter() {}
	
	/**
	 * Getter for the instance.
	 * @return the single instance of JsonWriter.
	 */
	public static JsonWriter getInstance() {
		return parser;
	}
	
	@Override
	public String writeGameList() {
		return Actions.List.toString();
	}

	@Override
	public String writeCreate(String name, String map) {
		return Actions.Create.toString()+"{"+JsonObject.NAME+":\""+name
				+"\","+JsonObject.MAP+":\""+map+"\"}";
	}

	@Override
	public String writeJoin(String name) {
		return Actions.Join.toString()+"{"+JsonObject.NAME+":\""+name+"\"}";
	}

	@Override
	public String writeMove(String move) {
		return Actions.PlayerMove.toString()+"{"+JsonObject.MOVE+":\""+move+"\"}";
	}

	@Override
	public String writeAttackBomb(int x, int y, String bombClass, boolean bonus) {
		return Actions.AttackBomb.toString()+"{"+JsonObject.POS+":\""+x+','+y
				+"\","+JsonObject.CLASS+":\""+bombClass+
				"\","+""+JsonObject.BONUS+":\""+bonus+"\"}";
	}

	@Override
	public String writeGameObject(String type, String objectClass) {
		return Actions.Object.toString()+"{"+JsonObject.TYPE+":\""+type
				+"\","+JsonObject.CLASS+":\""+objectClass+"\"}";
	}

	@Override
	public String writeRemote() {
		return Actions.Remote.toString();
	}
	
	@Override
	public String writeDeconnexion() {
		return Actions.Disconnection.toString();
	}

}
