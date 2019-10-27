package global;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import view.GameSprite;

/**
 * This class is used to load a sprite from a file.
 * @author quentin sauvage
 *
 */
public class SpriteLoader {
	private final static String PATH = "src/resources/";
	private final static String SPRITES_DIR = "sprites/";
	private final static String PLAYER = "player";
	private final static String BOMB = "bomb";
	private static SpriteLoader spriteLoader = new SpriteLoader();
	private final static String BG_BEGIN = "bg";
	//Every player sprites should be like 'player[nb].png'.
	private static int firstPlayerIndex = -1;
	//Every bomb sprites should be like 'bomb[nb].png'.
	private static int firstBombIndex = -1;
	
	/**
	 * Blank constructor.
	 */
	private SpriteLoader() {}
	
	/**
	 * Getter for the instance of SpriteLoader.
	 * @return the single instance of SpriteLoader.
	 */
	public static SpriteLoader getInstance() {
		return spriteLoader;
	}
	
	/**
	 * Getter for the firstPlayerIndex in the sprites list.
	 * @return the index of the first player sprite in the sprites list.
	 */
	public int getFirstPlayerIndex() {
		return firstPlayerIndex;
	}
	
	/**
	 * Getter for the firstBombIndex in the sprites list.
	 * @return the index of the first bombsprite in the sprites list.
	 */
	public int getFirstBombIndex() {
		return firstBombIndex;
	}

	/**
	 * Loads and adds the sprites in the list, regarding their type (object or background).
	 * @param list the list in which will be added the sprites.
	 * @param sprite boolean refering to whether or not it should add the objects sprites.
	 * @see GameSprite
	 */
	public void loadSprites(List<GameSprite> list, boolean sprite) {
		File dir = new File(PATH + SPRITES_DIR);
		String files[] = dir.list();
		Arrays.sort(files);
		boolean start;
		int i = 0;
		for(String s : files) {
			start = s.startsWith(BG_BEGIN);
			if(firstPlayerIndex < 0 && s.startsWith(PLAYER)) {
				firstPlayerIndex = i;
			} else {
				if(firstBombIndex < 0 && s.startsWith(BOMB)) {
					firstBombIndex = i;
				}
			}
			if((sprite && !start) || (!sprite && start)) {
				list.add(new GameSprite(s));
				i++;
			}
		}
	}
	
	/**
	 * Loads a sprite from a file.
	 * @param path the sprite path
	 * @param sprite indicates if the sprite is in the sprite directory.
	 * @return a BufferedImage corresponding to the loaded sprite.
	 */
	public BufferedImage load(String path, boolean sprite) {
		BufferedImage img = null;
		try {
			File imgFile = (sprite) ? new File(PATH + SPRITES_DIR + path) : new File(PATH + path);
			if(imgFile != null) {
				img = ImageIO.read(imgFile);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}
}
