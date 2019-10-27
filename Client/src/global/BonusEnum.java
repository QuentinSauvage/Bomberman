package global;

/**
 * Lists all the bonus types.
 * @author quentin sauvage
 *
 */
public enum BonusEnum {
	BOMB_UP("bomb_up"),
	BOMB_DOWN("bomb_down"),
	FIRE_POWER("fire_power"),
	SCOOTER("scooter"),
	BROKEN_LEGS("broken_legs"),
	MAJOR("major"),
	LIFE_UP("life_up"),
	LIFE_MAX("life_max");
	
	private final String bonus;
	
	/**
	 * Constructor.
	 * @param bonus the string related to the bonus.
	 */
	private BonusEnum(final String bonus) {
		this.bonus = bonus;
	}
	
	@Override
	public String toString() {
		return bonus;
	}
}
