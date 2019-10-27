package parser;

/**
 * Lists all of the json attributes possible.
 * @author quentin sauvage
 */
public enum JsonObject {
	STATUT("status"),
	MESSAGE("message"),
	ACTION("action"),
	NUMBER_GAME_LIST("numberGameList"),
	GAMES("games"),
	NAME("name"),
	NB_PLAYERS("nbPlayers"),
	PLAYERS("players"),
	MAP("map"),
	WIDTH("width"),
	HEIGHT("height"),
	CONTENT("content"),
	MAPS("maps"),
	STARTPOS("startPos"),
	PLAYER("player"),
	ID("id"),
	LIFE("life"),
	MAXLIFE("maxLife"),
	SPEED("speed"),
	NB_CLASSIC("currentNbClassicBomb"),
	NB_MINE("currentNbMine"),
	NB_REMOTE("currentNbRemoteBomb"),
	MAX_BOMB("maxNbBomb"),
	MOVE("move"),
	DIR("dir"),
	BONUS_MALUS("bonusMalus"),
	CLASS("class"),
	NB_BONUS("number"),
	BONUS("bonus"),
	POS("pos"),
	TYPE("type"),
	BOMB("bomb");
	
	private final String objectName;
	
	/**
	 * Constructor.
	 * @param objectName the json string related to this object.
	 */
	JsonObject(final String objectName) {
		this.objectName = objectName;
	}
	
	@Override
	public String toString() {
		return "\""+objectName+"\"";
	}
}
