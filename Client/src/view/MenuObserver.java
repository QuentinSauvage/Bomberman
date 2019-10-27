package view;

import java.util.List;

/**
 * Methods used by the game view.
 * @author quentin sauvage
 * @see GameGUI
 */
public interface MenuObserver {
	/**
	 * Update the window depending on the user action performed.
	 * @param list1 the games or servers list.
	 * @param list2 the maps list, used if list1 is the games list.
	 * @param type the action type performed.
	 */
	public void update(List<String> list1, List<String> list2, String type);
}
