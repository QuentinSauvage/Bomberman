package global;

/**
 * Lists all the cell types.
 * @author quentin sauvage
 *
 */
public enum CellEnum {
	STAR('*'),
	DASH('-'),
	UNDERSCORE('_');
	
	private final char name;
	
	/**
	 * Constructor
	 * @param name the char related to the cell.
	 */
	private CellEnum(final char name) {
		this.name = name;
	}
	
	/**
	 * Getter for the name attribute.
	 * @return the attribute name.
	 */
	public char getName() {
		return name;
	}
}
