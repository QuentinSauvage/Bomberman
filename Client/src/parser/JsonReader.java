package parser;

import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import global.Actions;
import global.BombsEnum;
import global.BonusEnum;
import global.Direction;

/**
 * Reads every messages sent by the server.
 * @author quentin sauvage
 * @see ReadJson
 */
public class JsonReader implements ReadJson {
	private static JsonReader parser = new JsonReader();
	private static final int MIN_ERROR = 400;
	
	private String action;
	private BombJson bomb;
	private List<BombJson> bombsList;
	private List<BombJson> explodedBombsList;
	private List<BonusJson> bonusList;
	private Direction dir;
	private List<GameJson> games;
	private MapJson map;
	private List<String> maps;
	private String message;
	private int nb;
	private int disconnected;
	private PlayerJson player;
	private List<PlayersJson> players;
	private Point startPos;
	private int status;
	
	/**
	 * Constructor.
	 */
	private JsonReader() {
		bomb = new BombJson();
		bombsList = new ArrayList<BombJson>();
		bonusList = new ArrayList<BonusJson>();
		games = new ArrayList<GameJson>();
		new MapJson();
		maps = new ArrayList<String>();
		player = new PlayerJson();
		players = new ArrayList<PlayersJson>();
		startPos = new Point();
	}
	
	/**
	 * Getter for the instance.
	 * @return the single JsonReader instance.
	 */
	public static JsonReader getInstance() {
		return parser;
	}
	
	public String getAction() {
		return action;
	}
	
	public BombJson getBomb() {
		return bomb;
	}

	public List<BombJson> getBombsList() {
		return bombsList;
	}
	
	public List<BombJson> getExplodedBombsList() {
		return explodedBombsList;
	}

	public List<BonusJson> getBonusList() {
		return bonusList;
	}

	public Direction getDir() {
		return dir;
	}

	public List<GameJson> getGames() {
		return games;
	}

	public MapJson getMap() {
		return map;
	}

	public List<String> getMaps() {
		return maps;
	}

	public String getMessage() {
		return message;
	}

	public int getNb() {
		return nb;
	}
	
	public int getDisconnected() {
		return disconnected;
	}

	public PlayerJson getPlayer() {
		return player;
	}

	public List<PlayersJson> getPlayers() {
		return players;
	}

	public Point getStartPos() {
		return startPos;
	}

	public int getStatus() {
		return status;
	}
	
	/**
	 * Gets the informations associated to the json object in the message sent by the server.
	 * @param json the message sent by the server.
	 * @param objectName the name of the object requested.
	 * @param beginStr the string from which the search should start.
	 * @param endStr the string by which the search should end.
	 * @return
	 */
	private String getJsonObject(String json, String objectName, String beginStr, String endStr) {
		String objectInfo = null;
		int objectIndex = json.indexOf(objectName), beginIndex, endIndex;
		if(objectIndex >= 0) {
			beginIndex = json.indexOf(beginStr,objectIndex)+1;
			endIndex = json.indexOf(endStr, objectIndex);
			if(endIndex < 0) {
				objectInfo = json.substring(beginIndex, json.length());
			} else {
				objectInfo = json.substring(beginIndex,endIndex);
			}
		}
		return (objectInfo != null) ? objectInfo.trim() : null;
	}
	
	@Override
	public void readGame(String game) {
		String name = null, map = null;
		int nbPlayer = 0;
		name = getJsonObject(game, "\"name\"", ":", ",");
		nbPlayer = Integer.parseInt(getJsonObject(game, "\"nbPlayer\"", ":", ","));
		map = getJsonObject(game, "\"map\"", ":", ",");
		this.games.add(new GameJson(name, nbPlayer, map));
	}
	
	@Override
	public void readGames(String games) {
		if(games.length() == 0) {
			return;
		}
		String splitter[] = games.split("[\\{-\\}]");
		this.games.clear();
		for(String s : splitter) {
			if(s.length() > 1) {
				readGame(s);
			}
		}
	}
	
	@Override
	public void readMaps(String maps) {
		if(maps.length() == 0) {
			return;
		}
		String splitter[] = maps.split(",");
		this.maps.clear();
		for(String s : splitter) {
			this.maps.add(s.replaceAll("\"", ""));
		}
	}
	
	@Override
	public Point readPos(String pos) {
		pos = pos.replaceAll("\"", "");
		Point p = null;
		String splitter[] = pos.split(",");
		if(splitter.length > 1) {
			p = new Point(Integer.parseInt(splitter[0]), Integer.parseInt(splitter[1]));
		}
		return p;
	}
	
	@Override
	public List<BombJson> readBombList(String bombs, String type) {
		List<BombJson> bombsList = new ArrayList<BombJson>();
		if(bombs.length() == 0) {
			return bombsList;
		}
		Point pos = null;
		String splitter[] = bombs.split("},"), bombClass = null;
		for(String s : splitter) {
			s = s.replaceAll("[\\{\\}\\[\\]]", "");
			bombClass = getJsonObject(s, type, ":", ",").replaceAll("\"", "").toUpperCase();
			for(BombsEnum b : BombsEnum.values()) {
				if(b.toString().equals(bombClass)) {
					pos = readPos(getJsonObject(s,"\"pos\"",":","\","));
					bombsList.add(new BombJson(pos, b, false));
					break;
				}
			}
		}
		return bombsList;
	}
	
	@Override
	public List<BonusJson> readBonusList(String bonus, String type) {
		List<BonusJson> bonusList = new ArrayList<BonusJson>();
		if(bonus == null || bonus.length() <= 1) {
			return bonusList;
		}
		Point pos = null;
		String splitter[] = bonus.split("},"), bonusClass = null, numberLine, posLine;
		int number = 0;
		for(String s : splitter) {
			s = s.replaceAll("[\\{\\}\\[\\]]", "");
			bonusClass = getJsonObject(s, type, ":", ",");
			if(bonusClass == null)
				return  null;
			bonusClass = bonusClass.replaceAll("\"", "");
			for(BonusEnum b : BonusEnum.values()) {
				if(b.toString().equals(bonusClass)) {
					numberLine = getJsonObject(s, "\"number\"", ":", ",");
					if(numberLine != null) {
						number = Integer.parseInt(numberLine);
					}
					posLine = getJsonObject(s,"\"pos\"",":","\",");
					if(posLine != null) {
						pos = readPos(getJsonObject(s,"\"pos\"",":","\","));
					}
					bonusList.add(new BonusJson(pos, b, number));
					break;
				}
			}
		}
		return bonusList;
	}
	
	@Override
	public List<BombJson> readBombList2(String bomb, String type) {
		List<BombJson> bombList = new ArrayList<BombJson>();
		if(bomb == null || bomb.length() <= 1) {
			return bombList;
		}
		Point pos = null;
		String splitter[] = bomb.split("},"), bombClass = null, posLine;
		for(String s : splitter) {
			s = s.replaceAll("[\\{\\}\\[\\]]", "");
			bombClass = getJsonObject(s, type, ":", ",");
			if(bombClass == null)
				return  null;
			bombClass = bombClass.replaceAll("\"", "");
			for(BombsEnum b : BombsEnum.values()) {
				if(b.toString().equals(bombClass)) {
					posLine = getJsonObject(s,"\"pos\"",":","\",");
					if(posLine != null) {
						pos = readPos(getJsonObject(s,"\"pos\"",":","\","));
					}
					bombList.add(new BombJson(pos, b, false));
					break;
				}
			}
		}
		return bombList;
	}
	
	@Override
	public void readPlayer(String player) {
		int id = -1, life, maxLife, speed, classic, mine, remote, max;
		String idLine = getJsonObject(player, "\"id\"", ":", ",");
		if(idLine != null) {
			id = Integer.parseInt(idLine);
		}
		life = Integer.parseInt(getJsonObject(player, "\"life\"", ":", ","));
		maxLife = Integer.parseInt(getJsonObject(player, "\"maxLife\"", ":", ","));
		speed = Integer.parseInt(getJsonObject(player, "\"speed\"", ":", ","));
		classic = Integer.parseInt(getJsonObject(player, "\"currentNbClassicBomb\"", ":", ","));
		mine = Integer.parseInt(getJsonObject(player, "\"currentNbMine\"", ":", ","));
		remote = Integer.parseInt(getJsonObject(player, "\"currentNbRemoteBomb\"", ":", ","));
		max = Integer.parseInt(getJsonObject(player, "\"maxNbBomb\"", ":", ","));
		this.player = new PlayerJson(id,life,maxLife,speed,classic,mine,remote,max);
		this.player.setBonusList(readBonusList(getJsonObject(player, "\"bonusMalus\"", ":", "]"), "class"));
	}
	
	@Override
	public void readPlayers(String players) {
		int id;
		Point pos;
		this.players.clear();
		String splitter[] = players.split("},");
		for(String s : splitter) {
			id = Integer.parseInt(getJsonObject(s, "\"id\"", ":", ","));
			pos = readPos(getJsonObject(s,"\"pos\"",":","\","));
			this.players.add(new PlayersJson(pos, id));
		}
	}
	
	@Override
	public void readMap(String maps) {
		int width, height;
		String rawContent;
		width = Integer.parseInt(getJsonObject(maps, "\"width\"", ":", ","));
		height = Integer.parseInt(getJsonObject(maps, "\"height\"", ":", ","));
		rawContent = getJsonObject(maps, "\"content\"", ":", ",");
		if(rawContent != null) {
			map = new MapJson(width, height, rawContent.replaceAll("\"", "").toCharArray());
		}
	}
	
	@Override
	public void readList(String json) {
		String games = null;
		nb = Integer.parseInt(getJsonObject(json, "numberGameList", ":", ","));
		games = getJsonObject(json, "\"games\"", "[", "]");
		if(games != null) {
			readGames(games);
		}
		readMaps(getJsonObject(json, "\"maps\"", "[", "]"));
	}
	
	@Override
	public void readCreate(String json) {
		nb = Integer.parseInt(getJsonObject(json, "\"nbPlayers\"", ":", ","));
		startPos = readPos(getJsonObject(json, "\"startPos\"", ":", "\","));
		readPlayer(getJsonObject(json, "\"player\"", ":", "]"));
		readMap(getJsonObject(json, "\"map\"", ":", "}"));
	}

	@Override
	public void readJoin(String json) {
		nb = Integer.parseInt(getJsonObject(json, "\"nbPlayers\"", ":", ","));
		readPlayers(getJsonObject(json, "\"players\"", ":","]"));
		startPos = readPos(getJsonObject(json, "\"startPos\"", ":", "\","));
		readPlayer(getJsonObject(json, "\"player\"", ":", "]"));
		readMap(getJsonObject(json, "\"map\"", ":", "}"));
	}
	
	@Override
	public void readNewPlayer(String json) {
		readPlayers(json.substring(json.indexOf("{")+1, json.indexOf("}")));
	}

	@Override
	public void readPositionUpdate(String json) {
		String dir;
		dir = getJsonObject(json,"\"dir\"", ":", ",").replaceAll("[\"\\}]", "").toUpperCase();
		for(Direction d : Direction.values()) {
			if(d.toString().equals(dir)) {
				this.dir = d;
				nb = Integer.parseInt(getJsonObject(json, "\"player\"",":",","));
				break;
			}
		}
	}

	@Override
	public void readAttackBomb(String json) {
		readPlayer(getJsonObject(json, "\"player\"", ":", "]"));
	}

	@Override
	public void readNewBomb(String json) {
		Point pos = null;
		String bombClass = getJsonObject(json, "\"class\"", ":", "\",").replaceAll("[\"\\}]", "");
		for(BombsEnum b : BombsEnum.values()) {
			if(bombClass.equals(b.toString())) {
				pos = readPos(getJsonObject(json, "\"pos\"", ":", "\","));
				bomb = new BombJson(pos, b, false);
				break;
			}
		}
	}

	@Override
	public void readExplodedBomb(String json) {
		Point pos = null;
		String bombType = getJsonObject(json, "\"type\"", ":", "\",").replaceAll("[\"\\}]", "");
		for(BombsEnum b : BombsEnum.values()) {
			if(bombType.equals(b.toString())) {
				pos = readPos(getJsonObject(json, "\"pos\"", ":", "\","));
				bomb = new BombJson(pos, b, false);
				map.setContent(getJsonObject(json, "\"map\"", ":", ",").replaceAll("\"", "").toCharArray());
				bonusList = readBonusList(getJsonObject(json, "\"bonusMalus\"", ":", "}"), "type");
				bombsList = readBombList2(getJsonObject(json, "\"bomb\"", ":", "]"), "type");
				//explodedBombsList = readBombList(getJsonObject(json, "\"chain\"", ":", "]"), "\"class\"");
				break;
			}
		}
	}

	@Override
	public void readAffect(String json) {
		readPlayer(json);
	}

	@Override
	public void readObject(String json) {
		readPlayer(json);
	}
	
	@Override
	public void readDisconnection(String json) {
		String playerLine = getJsonObject(json, "\"player\"", ":", "\",");
		if(player != null) {
			disconnected = Integer.parseInt(playerLine.replaceAll("}", ""));
		}
	}
	
	/**
	 * Reads the action sent.
	 * @param header the first line of the json, containing the action.
	 * @return boolean informing if the action in the header.
	 */
	private boolean readAction(String header) {
		//action defined before bracket
		for(Actions a : Actions.values()) {
			if(a.toString().equals(header)) {
				action = a.name();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Reads the status, the message and the action (if it is not in the header).
	 * @param json the message sent by the server.
	 * @param actionFound boolean informing if the action is in the header or not.
	 */
	private void readAnswer(String json, boolean actionFound) {
		String statutLine = null;
		//initially, there is no error
		status = 200;
		if(!actionFound) {
			action = getJsonObject(json, "\"action\"", ":", ",");
			if(action != null) {
				for(Actions a : Actions.values()) {
					if(a.toString().contains(action.replaceAll("\"", ""))) {
						action = a.name();
						actionFound = true;
						break;
					}
				}
			}
		}
		message = getJsonObject(json, "\"message\"", ":", ",");
		if(message != null) {
			message = message.replaceAll("[\\{\\}]", "");
		}
		statutLine = getJsonObject(json, "\"status\"", ":", ",");
		if(statutLine != null) {
			status = Integer.parseInt(statutLine.replaceAll("[\\{\\}\"]", ""));
		}
		if(actionFound && status < MIN_ERROR) {
			callReadMethod(json);
		} else {
			System.out.println("Error #" + status + " : " + message);
		}
	}
	
	/**
	 * Call the method corresponding to the action present in the message sent.
	 * @param json the message sent by the server.
	 */
	public void callReadMethod(String json) {
		Method method = null;
		try {
			method = this.getClass().getMethod("read"+action, String.class);
		//If the method doesn't exist, then it means the json action is compared with a client-only action
		} catch (NoSuchMethodException e) {
			System.out.println("Error while calling the reading method");
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		try {
			method.invoke(this, json);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Call the reading method depending on whether or not the action is in the header.
	 * @param json
	 */
	public void readJson(String json) {
		if(json.charAt(0) != '{') {
			readAnswer(json, readAction(json.substring(0, json.indexOf('{'))));
		} else {
			readAnswer(json, false);
		}
	}

}
