package parser;

public class GameJson {
	private String name;
	private int nbPlayers;
	private String map;
	
	public GameJson(String name, int nbPlayers, String map) {
		this.name = name;
		this.nbPlayers = nbPlayers;
		this.map = map;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getNbPlayers() {
		return nbPlayers;
	}
	
	public void setNbPlayer(int nbPlayers) {
		this.nbPlayers = nbPlayers;
	}
	
	public String getMap() {
		return map;
	}
	
	public void setMap(String map) {
		this.map = map;
	}
}
