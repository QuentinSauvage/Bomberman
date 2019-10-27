package global;

/**
 * Controls of the game. Could be in a file but that would take more time to code
 * (handle if the file is here, if it is valid, etc).
 * @author quentin sauvage
 *
 */
public class Binding {
	
	private String keyName;
	private int keyCode;
	private final int dir;
	
	/**
	* Constructor.
	* @param key the string related to the key.
	*/
	public Binding(String keyName, int keyCode) {
		this.keyName = keyName;
		this.keyCode = keyCode;
		if(keyCode >= 37 && keyCode <= 40)
			dir = keyCode % 37;
		else
			dir = -1;
	}
	
	public String getKeyName() {
		return keyName;
	}
	
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	
	public int getKeyCode() {
		return keyCode;
	}
	
	public void setKeyCode(int keyCode) {
		this.keyCode = keyCode;
	}
	
	public int getDir() {
		return dir;
	}
}
