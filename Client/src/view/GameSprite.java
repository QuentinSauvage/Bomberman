package view;

import java.awt.image.BufferedImage;

import global.SpriteLoader;

public class GameSprite {
	private BufferedImage image;
	private String name;
	private int offset_x;
	private int offset_y;
	private static final int DIMENSION = 64;
	private static final String PLAYER = "player";
	
	public GameSprite(String name) {
		image = SpriteLoader.getInstance().load(name, true);
		this.offset_x = (DIMENSION - image.getWidth()) / 2;
		this.offset_y = (name.startsWith(PLAYER)) ? 0 : (DIMENSION - image.getHeight()) / 2;
	}
	
	public String getName() {
		return name;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public int getOffsetX() {
		return offset_x;
	}
	
	public int getOffsetY() {
		return offset_y;
	}
}
