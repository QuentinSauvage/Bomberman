package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import controller.MenuController;
import global.Binding;
import global.SpriteLoader;
import initialisation.GameHandler;

/**
 * The menu window.
 * @author quentin sauvage
 * @see MenuController
 */
public class MenuView extends AbstractView implements MenuObserver {
	private final static MenuView instance = new MenuView();
	private final static String JOIN_GAME = "Rejoindre partie";
	private final static String JOIN_SERVER = "Rejoindre serveur";
	private final static String GET_GAMES = "get games";
	private final static String GET_SERVERS = "get servers";
	private final static String UPDATE_BINDINGS = "update bindings";
	private final static String SERVERS_LIST = "Liste serveurs";
	private final static String REFRESH = "Actualiser";
	private final static String GAME_NAME = "Nom de la partie";
	private final static String CREATE_GAME = "Créer partie";
	private final static String AVAILABLE_GAMES = "Parties disponibles";
	private final static String BG_NAME = "menu_background.png";
	private static String MENU_TITLE = "Menu";
	private JPanel menuBox;
	private BufferedImage bg;
	private String[] menuOptions = {"Rechercher un serveur","Options","Configuration des touches","Quitter"};
	private String[] menuOptionsActions = {"searchServers", "showOptions", "showConfig", "closeMenu"};
	private JLabel choiceTitle = new JLabel("Title");
	private MenuButton retour = new MenuButton("Retour");
	private MenuController menuController;
	private GridBagConstraints gbc = new GridBagConstraints();
	private MenuButton joinButton = null;
	private String selectedServer;
	private String selectedGame;
	private JPanel bindings;
	private String keySelected = null;
	private List<MenuButton> bindList = new ArrayList<MenuButton>();
	private KeyListener keyListener;
	
	/**
	 * Constructor.
	 */
	private MenuView() {
		super(MENU_TITLE, 0, 0);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		menuController = null;
		retour.addActionListener(e -> returnBack());
		loadBackground();
		initComponents();
		choiceTitle.setHorizontalAlignment(JLabel.CENTER);
		choiceTitle.setForeground(Color.WHITE);
		choiceTitle.setPreferredSize(new Dimension(menuBox.getWidth(), 100));
		bindings = new JPanel(new GridLayout(9, 1));
		keyListener = new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {}

			@Override
			public void keyTyped(KeyEvent e) {
				menuController.controlBindings(keySelected, e.getKeyChar());
			}
			
		};
		for(Binding b : GameHandler.getInstance().getBindings()) {
			MenuButton bind = new MenuButton(b.getKeyName() + " : " + KeyEvent.getKeyText(b.getKeyCode()));
			bind.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					keySelected = b.getKeyName();
				}

				@Override
				public void mouseEntered(MouseEvent e) {}
				@Override
				public void mouseExited(MouseEvent e) {}
				@Override
				public void mousePressed(MouseEvent e) {}
				@Override
				public void mouseReleased(MouseEvent e) {}
			});
			bind.addKeyListener(keyListener);
			bindings.add(bind);
			bindList.add(bind);
		}
	}
	
	/**
	 * Getter for the menu controller.
	 * @return the menu controller.
	 */
	public MenuController getController() {
		return menuController;
	}
	
	/**
	 * Setter for the menu controller.
	 * @param menuController the new menu controller.
	 */
	public void setController(MenuController menuController) {
		this.menuController = menuController;
	}
	
	/**
	 * Getter for the instance.
	 * @return the single instance of MenuView.
	 */
	public static MenuView getInstance() {
		return instance;
	}

	/**
	 * Redraw the elements present in every menu panels.
	 */
	private void showRoutine() {
		gbc.gridx = 1;
	    gbc.gridy = 0;
		content.add(menuBox, gbc);
		window.validate();
		window.repaint();
	}
	
	/**
	 * Re-asks the games list.
	 */
	private void refreshGames() {
		content.removeAll();
		menuBox.removeAll();
		menuController.controlGamesList(selectedServer);
	}
	
	/**
	 * Re-asks the servers list.
	 */
	private void refreshList() {
		content.removeAll();
		menuBox.removeAll();
		menuController.controlServersList();
	}
	
	/**
	 * Visual informations to show which server/game is selected.
	 * @param buttonsList The list of games/servers buttons.
	 * @param selected the select button.
	 * @param server Whether it concerns the servers list or the games list.
	 */
	private void selectInList(List<MenuButton> buttonsList, MenuButton selected, boolean server) {
		for(JButton b : buttonsList) {
			b.setSelected(false);
		}
		selected.setSelected(true);
		if(server) {
			selectedServer = selected.getText();
		} else {
			selectedGame = selected.getText();
		}
		joinButton.setEnabled(true);
	}
	
	/**
	 * Add all the games buttons to the game panel.
	 */
	private void addGamesListButtons() {
		JPanel bottom = new JPanel(new GridLayout(1,4,0,0));
		joinButton = new MenuButton(JOIN_GAME);
		MenuButton serversList = new MenuButton(SERVERS_LIST);
		MenuButton refresh = new MenuButton(REFRESH);
		joinButton.addActionListener(e -> menuController.controlJoinGame(selectedGame));
		serversList.addActionListener(e -> searchServers(0));
		refresh.addActionListener(e -> refreshGames());
		joinButton.setEnabled(false);
		bottom.add(joinButton);
		bottom.add(refresh);
		bottom.add(serversList);
		bottom.add(retour);
		content.add(bottom, gbc);
	}
	
	/**
	 * Creates the panel containing the elements referring to the creation of a game.
	 * @param mapsList the list of maps available.
	 */
	private void createGamePanel(List<String> mapsList) {
		JPanel gameCreator = new JPanel();
		gameCreator.setBackground(Color.DARK_GRAY);
		JTextField gameName = new JTextField(GAME_NAME);
		MenuButton createGame = new MenuButton(CREATE_GAME);
		gameCreator.add(gameName);
		String[] mapNames = new String[mapsList.size()];
		int i = 0;
		for(String s : mapsList) {
			mapNames[i++] = s;
		}
		JComboBox<String> maps = new JComboBox<String>(mapNames);
		gameCreator.add(maps);
		createGame.addActionListener(e -> menuController.controlCreateGame(gameName.getText(), maps.getSelectedItem().toString()));
		gameCreator.add(createGame);
		menuBox.add(gameCreator, BorderLayout.SOUTH);
	}
	
	/**
	 * Creates the panel referring to the join-a-game process.
	 * @param games the list of games available.
	 * @param mapsList the list of maps available.
	 */
	private void selectGame(List<String> games, List<String> mapsList) {
		content.removeAll();
		menuBox.removeAll();
		choiceTitle.setText(AVAILABLE_GAMES);
		List<MenuButton> gamesList = new ArrayList<MenuButton>();
		JPanel gameListButtons = new JPanel();
		gameListButtons.setBackground(Color.BLACK);
		gameListButtons.setPreferredSize(new Dimension(menuBox.getWidth(), gamesList.size() * 30));
		for(String game : games) {
			MenuButton gameButton = new MenuButton(game);
			gameButton.setPreferredSize(new Dimension(menuBox.getWidth(), 30));
			gameButton.addActionListener(e -> selectInList(gamesList, gameButton, false));
			gameButton.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent event)
				{
					if (event.getClickCount() == 2) {
						menuController.controlJoinGame(selectedGame);
					}
				}
			});
			gameButton.setForeground(Color.WHITE);
			gameButton.setBackground(Color.DARK_GRAY);
			gameListButtons.add(gameButton);
			gamesList.add(gameButton);
		}
		JScrollPane js = new JScrollPane(gameListButtons, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		js.setBorder(BorderFactory.createEmptyBorder());
		menuBox.setLayout(new BorderLayout());
		menuBox.add(choiceTitle, BorderLayout.NORTH);
		menuBox.add(js, BorderLayout.CENTER);
		createGamePanel(mapsList);
		gbc.gridx = 1;
	    gbc.gridy = 1;
	    addGamesListButtons();
		showRoutine();
	}
	
	/**
	 * Add all the servers buttons to the server panel.
	 */
	private void addServersButtons() {
		JPanel bottom = new JPanel(new GridLayout(1,3,0,0));
		joinButton = new MenuButton(JOIN_SERVER);
		MenuButton refresh = new MenuButton(REFRESH);
		joinButton.addActionListener(e -> menuController.controlGamesList(selectedServer));
		refresh.addActionListener(e -> refreshList());
		joinButton.setEnabled(false);
		bottom.add(joinButton);
		bottom.add(refresh);
		bottom.add(retour);
		content.add(bottom, gbc);
	}
	
	/**
	 * Creates the panel referring to the select-a-server process.
	 * @param serverslist the list of servers available.
	 */
	private void showServers(List<String> serverslist) {
		menuBox.removeAll();
		List<MenuButton> serversList = new ArrayList<MenuButton>();
		JPanel hostListButtons = new JPanel();
		hostListButtons.setPreferredSize(new Dimension(menuBox.getWidth(), serversList.size() * 30));
		hostListButtons.setBackground(Color.BLACK);
		for(String addr : serverslist) {
			MenuButton hostButton = new MenuButton(addr);
			hostButton.setPreferredSize(new Dimension(menuBox.getWidth(), 30));
			hostButton.addActionListener(e -> selectInList(serversList, hostButton, true));
			hostButton.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent event)
				{
					if (event.getClickCount() == 2) {
						menuController.controlGamesList(selectedServer);
					}
				}
			});
			hostButton.setForeground(Color.WHITE);
			hostButton.setBackground(Color.DARK_GRAY);
			hostListButtons.add(hostButton);
			serversList.add(hostButton);
		}
		JScrollPane js = new JScrollPane(hostListButtons, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		js.setBorder(BorderFactory.createEmptyBorder());
		menuBox.setLayout(new BorderLayout());
		menuBox.add(choiceTitle, BorderLayout.NORTH);
		menuBox.add(js, BorderLayout.CENTER);
		gbc.gridx = 1;
	    gbc.gridy = 1;
	    addServersButtons();
		showRoutine();
	}

	/**
	 * Creates the panel referring to the options.
	 * @param i the title index.
	 */
	public void showOptions(int i) {
		menuBox.removeAll();
		choiceTitle.setText(menuOptions[i]);
		menuBox.add(choiceTitle);
		menuBox.add(retour);
		showRoutine();
	}
	
	/**
	 * Creates the panel referring to the configurations.
	 * @param i the title index.
	 */
	public void showConfig(int i) {
		menuBox.removeAll();
		menuBox.setLayout(new BorderLayout());
		choiceTitle.setText(menuOptions[i]);
		menuBox.add(choiceTitle, BorderLayout.NORTH);
		menuBox.add(bindings, BorderLayout.CENTER);
		menuBox.add(retour, BorderLayout.SOUTH);
		showRoutine();
	}
	
	/**
	 * Creates the panel referring to the servers selection and asks the controller to search for the list of servers available.
	 * @param i the title index.
	 */
	public void searchServers(int i) {
		content.removeAll();
		choiceTitle.setText(menuOptions[i]);
		menuController.controlServersList();
	}
	
	/**
	 * Creates the panel referring to the options.
	 * @param i the title index (not useful in this case because the window is closed).
	 */
	public void closeMenu(int i) {
		window.dispose();
	}
	
	/**
	 * Creates every action buttons needed to navigate in the menu.
	 * @param button the button to binds an action.
	 * @param i the title index.
	 */
	private void createOptionButton(JButton button, final int i) {
		Method m1 = null;
		try {
			m1 = this.getClass().getMethod(menuOptionsActions[i], int.class);
		} catch (NoSuchMethodException | SecurityException e1) {
			e1.printStackTrace();
		}
		final Method m2 = m1;
		button.addActionListener(e -> {
			try {
				m2.invoke(this, i);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
				e1.printStackTrace();
			}
		});
	}
	
	/**
	 * Create the buttons needed to navigate in the menu.
	 */
	private void createOptionsButtons() {
		MenuButton buttons[] = new MenuButton[4];
		for(int i = 0; i < 4; i++) {
			buttons[i] = new MenuButton(menuOptions[i]);
			createOptionButton(buttons[i], i);
			menuBox.add(buttons[i]);
		}
	}
	
	/**
	 * Goes back to the previous panel.
	 */
	private void returnBack() {
		keySelected = null;
		content.removeAll();
		cleanMenu();
		window.validate();
		window.repaint();
	}
	
	/**
	 * Goes back to the very first panel, as if the menu was launched for the first time.
	 */
	private void cleanMenu() {
		window.setContentPane(content);
		menuBox.removeAll();
		menuBox.setLayout(new GridLayout(4,1));
		createOptionsButtons();
		content.add(menuBox);
	}
	
	/**
	 * Initializes the menu window components.
	 */
	private void initComponents() {
		int w = bg.getWidth(), h = bg.getHeight();
		window.setContentPane(content);
		resize(w, h);
		menuBox = new JPanel(new GridLayout(4,1));
		menuBox.setPreferredSize(new Dimension(w/2, h/2));
		menuBox.setBackground(Color.BLACK);
		createOptionsButtons();
		content.add(menuBox);
	}
	
	/**
	 * Visually upgrades the bindings.
	 */
	public void updateBindings() {
		int i = 0;
		for(Binding b : GameHandler.getInstance().getBindings()) {
			bindList.get(i++).setText(b.getKeyName() + " : " + KeyEvent.getKeyText(b.getKeyCode()));
		}
	}
	
	/**
	 * Loads the menu window background.
	 */
	private void loadBackground() {
		bg = SpriteLoader.getInstance().load(BG_NAME, false);
		content = new JPanel(new GridBagLayout()) {
			private static final long serialVersionUID = 1L;

			@Override public void paintComponent(Graphics g) {
				g.drawImage(bg, 0, 0, null);
			}
		};
		gbc.fill = GridBagConstraints.BOTH;
		content.setPreferredSize(new Dimension(500, 500));
	}
	
	@Override
	public void update(List<String> list, List<String> mapsList, String type) {
		if(type.equals(GET_SERVERS)) {
			showServers(list);
		} else if(type.equals(GET_GAMES)) {
			selectGame(list, mapsList);
		} else if(type.equals(CREATE_GAME)) {
			window.dispose();
		} else if(type.equals(JOIN_GAME)) {
			window.dispose();
		} else if(type.equals(UPDATE_BINDINGS)) {
			updateBindings();
		}
	}
	
	class MenuButton extends JButton {
		private static final long serialVersionUID = -7612823048364468916L;

		public MenuButton(String name) {
			super(name);
		}
		
		public void paintComponent(Graphics g){
			Graphics2D g2d = (Graphics2D)g;
			GradientPaint gp;
			if(this.isSelected())
				gp = new GradientPaint(0, 0, Color.BLACK, 100, 100, Color.PINK, true);
			else if(this.isEnabled())
				gp = new GradientPaint(0, 0, Color.BLACK, 50, 50, Color.DARK_GRAY, true);
			else
				gp = new GradientPaint(0, 0, Color.DARK_GRAY, 50, 50, Color.LIGHT_GRAY, true);
			g2d.setPaint(gp);
			g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
			g2d.setColor(Color.WHITE);
			g2d.drawString(this.getText(), (this.getWidth() / 2) - (this.getText().length() * 4), this.getHeight() / 2 + 5);
		}
	}
}
