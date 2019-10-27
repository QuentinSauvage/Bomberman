package global;

/**
 * Lists all of the possible actions.
 * @author quentin sauvage
 *
 */
public enum Actions {
	List("GET game/list"),
	Create("POST game/create"),
	Join("POST game/join"),
	NewPlayer("POST game/newplayer"),
	PlayerMove("POST player/move"),
	PositionUpdate("POST player/position/update"),
	AttackBomb("POST attack/bomb"),
	NewBomb("POST attack/newbomb"),
	ExplodedBomb("POST attack/explose"),
	Remote("POST attack/remote/go"),
	Affect("POST attack/affect"),
	Object("POST object/new"),
	Disconnection("POST game/quit");
	
	private final String action;
	
	/**
	 * Constructor.
	 * @param action the string related to the action.
	 */
	private Actions(final String action) {
		this.action = action;
	}
	
	@Override
	public String toString() {
		return action;
	}
}
