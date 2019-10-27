package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * The JPanel representing a cell.
 * @author quentin sauvage
 */
public class Cell extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int DIMENSION = 64;
	private char content;
	private List<GameSprite> spritesList;
	GameSprite backgroundSprite = null;
	int life;
	
	/**
	 * Constructor
	 * @param content the char-representation of the cell background.
	 */
	public Cell(char content) {
		super();
		spritesList = new ArrayList<GameSprite>();
		this.content = content;
		this.setPreferredSize(new Dimension(DIMENSION,DIMENSION));
	}
	
	/**
	 * Getter for the cell content.
	 * @return the cell background.
	 */
	public char getContent() {
		return content;
	}
	
	/**
	 * Setter for the background sprite.
	 * @param backgroundSprite the new background sprite.
	 */
	public void setBackGroundSprite(GameSprite backgroundSprite) {
		this.backgroundSprite = backgroundSprite;
	}
	
	/**
	 * Adds a sprite to the sprites list.
	 * @param sprite the sprite to add.
	 */
	public void addSprite(GameSprite sprite) {
		spritesList.add(sprite);
	}
	
	/**
	 * Remove a sprite from the sprites list.
	 * @param sprite the sprite to remove.
	 */
	public void removeSprite(GameSprite sprite) {
		spritesList.remove(sprite);
	}
	
	public void setLife(int life) {
		this.life = life;
	}
	
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(backgroundSprite.getImage(), 0, 0, this);
        for(GameSprite sprite : spritesList) {
        	g2d.drawImage(sprite.getImage(), sprite.getOffsetX(), sprite.getOffsetY(), this);
        }
        if(life > 0) {
        	g2d.setColor(Color.BLACK);
        	g2d.fillRect(0, 0, 24, 10);
        	g2d.setColor(Color.WHITE);
        	g2d.drawString(Integer.toString(life), 0,10);
        }
	}
}
