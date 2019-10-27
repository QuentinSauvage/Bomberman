package view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import controller.GameController;
import global.CellEnum;
import global.SpriteLoader;
import model.Bomb;
import model.Bonus;
import parser.MapJson;

/**
 * The game window.
 * @author quentin sauvage
 * @see GameController
 * @see Cell
 * @see SpriteLoader
 * @see PauseView
 */
public class GameGUI extends AbstractView implements GameObserver {
	private final static GameGUI instance = new GameGUI();
	private static String GAME_TITLE = "Bomberstudent";
	private static String CLOSE = "close";
	private static String PAUSE = "pause";
	private static String LOSE = "lose";
	private final static int CELLSIZE = 64;
	private static int width = 500;
	private static int height = 500;
	private GameController gameController;
	private Cell[][] gameCells = null;
	private List<GameSprite> spritesList;
	private List<GameSprite> bgList;
	private SpriteLoader spriteLoader = SpriteLoader.getInstance();
	private PauseView pause = new PauseView();
	private GameOver gameOver;
	
	/**
	 * Constructor.
	 */
	private GameGUI() {
		super(GAME_TITLE, width, height);
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(new WindowAdapter() {
			@Override
		    public void windowClosing(WindowEvent e) {
				window.dispose();
				pause.closePause();
		    }
		});
		this.gameController = null;
		spritesList = new ArrayList<GameSprite>();
		bgList = new ArrayList<GameSprite>();
		spriteLoader.loadSprites(spritesList, true);
		spriteLoader.loadSprites(bgList, false);
	}
	
	/**
	 * Getter for the game controller.
	 * @return the game controller.
	 */
	public GameController getController() {
		return gameController;
	}
	
	/**
	 * Setter for the game controller.
	 * @param gameController the new game controller.
	 */
	public void setController(GameController gameController) {
		this.gameController = gameController;
	}
	
	/**
	 * Getter for the instance.
	 * @return the single GameGUI instance.
	 */
	public static GameGUI getInstance() {
		return instance;
	}
	
	/**
	 * Display the game with the informations contained in the map.
	 * @param map the map sent by the server.
	 * @see MapJson
	 */
	public void displayGame(MapJson map) {
		super.display();
		width = map.getWidth();
		height = map.getHeight();
		content = new JPanel(new GridLayout(height, width));
		window.setContentPane(content);
		content.setBackground(Color.PINK);
		resize(width * CELLSIZE, height * CELLSIZE);
		gameOver = new GameOver();
		gameCells = new Cell[height][width];
		addAllSprites(map);
		window.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {
				gameController.controlKeyBoard(e.getKeyCode());
			}
		});
		window.pack();
		gameController.controlInitBoard();
	}
	
	/**
	 * Initializes the cells with their corresponding background.
	 * @param map the map sent by the server.
	 */
	public void addAllSprites(MapJson map) {
		char c;
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				c = map.getContent()[i][j];
				Cell cell = new Cell(c);
				for(CellEnum cellEnum : CellEnum.values()) {
					if(cellEnum.getName() == c) {
						cell.setBackGroundSprite(bgList.get(cellEnum.ordinal()));
					}
				}
				content.add(cell);
				gameCells[i][j] = cell;
			}
		}
	}
	
	@Override
	public void update(String str) {
		if(str.equals(CLOSE)) {
			window.dispose();
		} else if(str.equals(PAUSE)) {
			pause.display();
		} else if(str.equals(LOSE)) {
			window.dispose();
			gameOver.display();
		}
	}
	
	@Override
	public void update(int posX, int posY, int index) {
		gameCells[posX][posY].removeSprite(spritesList.get(spriteLoader.getFirstPlayerIndex() + index));
		gameCells[posX][posY].setLife(0);
		window.repaint();
	}
	
	@Override
	public void update(int lastX, int lastY, int newX, int newY, int index, int life, boolean add) {
		gameCells[lastX][lastY].removeSprite(spritesList.get(spriteLoader.getFirstPlayerIndex() + index));
		if(add)
			gameCells[newX][newY].addSprite(spritesList.get(spriteLoader.getFirstPlayerIndex() + index));
		gameCells[newX][newY].setLife(life);
		gameCells[lastX][lastY].setLife(0);
		window.repaint();
	}
	
	@Override
	public void update(int posX, int posY, int index, boolean add) {
		if(add)
			gameCells[posX][posY].addSprite(spritesList.get(spriteLoader.getFirstBombIndex() + index));
		else {
			gameCells[posX][posY].removeSprite(spritesList.get(spriteLoader.getFirstBombIndex() + index));
		}
		window.repaint();
	}
	
	@Override
	public void update(char map[][], List<Bomb> bombsList, List<Bonus> bonusList) {
		char c;
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				c = map[i][j];
				for(CellEnum cellEnum : CellEnum.values()) {
					if(cellEnum.getName() == c) {
						gameCells[i][j].setBackGroundSprite(bgList.get(cellEnum.ordinal()));
					}
				}
			}
		}
		for(Bomb bomb : bombsList) {
			gameCells[(int) bomb.getPos().getY()][(int) bomb.getPos().getX()].addSprite(spritesList.get(spriteLoader.getFirstBombIndex() + bomb.getIndex()));
		}
		for(Bonus bonus : bonusList) {
			gameCells[(int) bonus.getPos().getY()][(int) bonus.getPos().getX()].addSprite(spritesList.get(bonus.getIndex()));
		}
		
		window.repaint();
	}
	
	@Override
	public void update(Bomb removedBomb, Bonus removedBonus) {
		if(removedBomb != null) {
			gameCells[(int) removedBomb.getPos().getY()][(int) removedBomb.getPos().getX()].removeSprite(spritesList.get(spriteLoader.getFirstBombIndex() + removedBomb.getIndex()));
		} else if(removedBonus != null) {
			gameCells[(int) removedBonus.getPos().getY()][(int) removedBonus.getPos().getX()].removeSprite(spritesList.get(removedBonus.getIndex()));
		}
	}
	
	/**
	 * Sub-class representing the pause window.
	 * @author quentin sauvage
	 */
	class PauseView extends AbstractView {
		private static final String TITLE = "Pause";
		private static final String RESUME = "Reprendre";
		private static final String LEAVE = "Quitter";
		private static final int HEIGHT = 100;
		private static final int WIDTH = 150;
		
		/**
		 * Constructor.
		 */
		public PauseView() {
			super(TITLE, WIDTH, HEIGHT);
			resize(WIDTH, HEIGHT);
			window.setFocusableWindowState(false);
			JPanel content = new JPanel(new GridLayout(2,1));
			content.setBackground(Color.BLACK);
			JButton resume = new JButton(RESUME);
			resume.addActionListener(e -> display());
			JButton leave = new JButton(LEAVE);
			leave.addActionListener(e -> closePause());
			content.add(resume);
			content.add(leave);
			window.setContentPane(content);
			window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			window.addWindowListener(new WindowAdapter() {
				@Override
			    public void windowClosing(WindowEvent e) {
					window.setVisible(false);
			    }
			});
		}
		
		/**
		 * Closes the pause window and send the disconnect information.
		 */
		private void closePause() {
			window.dispose();
			disconnect();
		}
	}
	
	/**
	 * Sub-class representing the pause window.
	 * @author quentin sauvage
	 */
	class GameOver extends AbstractView {
		private static final String TITLE = "Game Over";
		private static final String LEAVE = "Quitter";
		private static final int HEIGHT = 75;
		private static final int WIDTH = 100;
		
		/**
		 * Constructor.
		 */
		public GameOver() {
			super(TITLE, WIDTH, HEIGHT);
			window.toFront();
			window.setFocusableWindowState(false);
			JPanel content = new JPanel();
			content.setBackground(Color.BLACK);
			JButton leave = new JButton(LEAVE);
			leave.addActionListener(e -> display());
			content.add(leave);
			window.setContentPane(content);
			window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			window.addWindowListener(new WindowAdapter() {
				@Override
			    public void windowClosing(WindowEvent e) {
					window.setVisible(false);
			    }
			});
		}
	}
	
	/**
	 * Informs the game controller that the player is leaving the game.
	 */
	public void disconnect() {
		gameController.control(CLOSE);
	}
}
