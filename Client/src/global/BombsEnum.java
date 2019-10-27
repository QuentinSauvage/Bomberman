package global;

/**
 * Enumerates every bomb types.
 * @author quentin sauvage
 *
 */
public enum BombsEnum {
	CLASSIC("classic"),
	MINE("mine"),
	REMOTE("remote");
	
	private final String type;
	
	/**
	 * Constructor.
	 * @param bonus the string related to the bomb.
	 */
	private BombsEnum(final String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return type;
	}
}
