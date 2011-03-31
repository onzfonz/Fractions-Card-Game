package cards;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import manipulatives.ManCardPanel;
import manipulatives.ManFrame;
import network.NetDelegate;
import network.NetHelper;
import pebblebag.PebbleListener;
import basic.Constants;
import basic.FYIMessage;
import basic.GamePanel;
import basic.Helpers;
import basic.Player;
import extras.Debug;
import extras.PanelListener;
import extras.StringUtils;

public class CardGamePanel extends JPanel implements PanelListener, KeyListener {

	private static final long serialVersionUID = -2520131149153594608L;
	private GamePanel gamePanel;
	private ManFrame manipWindow;
	private CardGamePanel myFrame;
	private JButton newGame;
	private JLabel playerScore;
	private JLabel oppoScore;
	private JTextField textCommands;
	private ArrayList<JComponent> controls;
	private ArrayList<String> cardPanelNames;
	private HashMap<String, ManCardPanel> cardMapping;
	private NetDelegate netRep;
	private PebbleListener chipObserver;
	private JPanel gameArea;
	private ManCardPanel manCardPanel;
	private FYIMessage shufflin;
	private String currentLayout;
	private JButton manipButton;
	
	private static final String GAME_PANEL = "Game Panel";
	private static final String MANIP_PANEL = "Manip Panel";
	private static final String MANIP_CALC_PANEL = "Manip Calc";
	
	public CardGamePanel(NetDelegate nRep) {
		//setTitle(Constants.WINDOW_TITLE);
		manipWindow = null;
		myFrame = this;
		controls = new ArrayList<JComponent>();
		cardPanelNames = new ArrayList<String>();
		cardMapping = new HashMap<String, ManCardPanel>();
		netRep = nRep;
		//if (file != null) boardPanel.open(file);
		setLayout(new BorderLayout());
		final JPanel toolbox = new JPanel();
		toolbox.setLayout(new BoxLayout(toolbox, BoxLayout.Y_AXIS));
		toolbox.setBackground(Constants.TOOLBOX_BACKGROUND);
		add(toolbox, BorderLayout.WEST);

		/*
		 Create the checkboxes and wire them to setters
		 on the ManPanel for each boolean feature.
		 */
		JButton newRound = new JButton(Constants.BTN_NEW_GAME);
		newRound.setToolTipText(Constants.TIP_START);
		newRound.setFont(Constants.FONT_TINY);
		newGame = newRound;
		toolbox.add(newRound);
		controls.add(newRound);
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				beginGame();
			}
		});

		//		newRound = new JButton("New Round");
		//		controls.add(newRound);
		//		box.add(newRound);
		//		newRound.addActionListener( new ActionListener() {
		//			public void actionPerformed(ActionEvent e) {
		//				if(gamePanel.gameStarted()) {
		//					gamePanel.newRound();  
		//				}else{
		//					JOptionPane.showMessageDialog(gamePanel, Constants.ERROR_NO_GAME_YET, "", JOptionPane.ERROR_MESSAGE);
		//				}
		//			}
		//		});

		String passBtnText = Constants.BTN_DONE_TURN;
		if(Constants.NETWORK_MODE){
			passBtnText = Constants.BTN_PASS;
		}
		newRound = new JButton(passBtnText);
		newRound.setToolTipText(Constants.TIP_DONE_TURN);
		newRound.setFont(Constants.FONT_TINY);
		final JButton doneTurn = newRound;
		controls.add(newRound);
		toolbox.add(newRound);
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(gamePanel.gameStarted()) {
					/* one possibility is to remove this */
					if(!gamePanel.computerMove()) {
						askToFinishRound();
					}else{
						NetHelper.sendNetNoMoves(netRep);
					}
				}else{
					JOptionPane.showMessageDialog(gamePanel, Constants.ERROR_NO_GAME_YET, "", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		manipButton = new JButton(Constants.BTN_LAUNCH_MANIP);
		manipButton.setToolTipText(Constants.TIP_MANIP);
		manipButton.setFont(Constants.FONT_TINY);
		manipButton.setVisible(Constants.HAVE_MANIP_BUTTON);
			//controls.add(newRound);
		toolbox.add(manipButton);
		manipButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleManipLayout();
			}
		});

		/*Debug Controls*/

		JLabel debugControls = new JLabel("Debug Options");
		debugControls.setVisible(Constants.DEBUG_MODE);
		toolbox.add(debugControls);

		newRound = createDebugButton(toolbox, "New Round");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.newRound();  
			}
		});

		newRound = createDebugButton(toolbox, "Add P1 Teammate");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.addTeam(true);  
			}
		});

		newRound = createDebugButton(toolbox, "Subtract P1 Teammate");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.removeLatestTeam(true);  
			}
		});

		newRound = createDebugButton(toolbox, "Add P1 Trick");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.addTrick(true); 
			}
		});

		newRound = createDebugButton(toolbox, "Subtract P1 Trick");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.eliminateLatestTrick(true); 
			}
		});

		newRound = createDebugButton(toolbox, "Give Computer Set Tricks");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.setTrickHandDebug(false);
			}
		});

		newRound = createDebugButton(toolbox, "Give Me Stinks");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.setTrickHandDebug(true);
			}
		});

		newRound = createDebugButton(toolbox, "Give Me Airs");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.setTrickHandToAirs(true);
			}
		});
		
		final JLabel redLabel = createDebugLabel(toolbox, "r");
		final JSlider redSlider = createDebugSlider(toolbox, "Red", 0, 255, 128);
		redSlider.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				Color curColor = toolbox.getBackground();
				int blue = curColor.getBlue();
				int green = curColor.getGreen();
				Color tempColor = new Color(redSlider.getValue(), green, blue);
				//gamePanel.setBackground(tempColor);
				toolbox.setBackground(tempColor);
				redLabel.setText("" + redSlider.getValue());
			}
		});

		final JLabel greenLabel = createDebugLabel(toolbox, "g");
		final JSlider greenSlider = createDebugSlider(toolbox, "Green", 0, 255, 128);
		greenSlider.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				Color curColor = toolbox.getBackground();
				int blue = curColor.getBlue();
				int red = curColor.getRed();
				Color tempColor = new Color(red, greenSlider.getValue(), blue);
				//gamePanel.setBackground(tempColor);
				toolbox.setBackground(tempColor);
				greenLabel.setText("" + greenSlider.getValue());
			}
		});

		final JLabel blueLabel = createDebugLabel(toolbox, "b");
		final JSlider blueSlider = createDebugSlider(toolbox, "Blue", 0, 255, 128);
		blueSlider.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				Color curColor = toolbox.getBackground();
				int red = curColor.getRed();
				int green = curColor.getGreen();
				Color tempColor = new Color(red, green, blueSlider.getValue());
				//gamePanel.setBackground(tempColor);
				toolbox.setBackground(tempColor);
				blueLabel.setText("" + blueSlider.getValue());
			}
		});


		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));

		final JCheckBox showComp = createDebugCheckBox(top, "Show Computer", Constants.SHOW_COMPUTER_CARDS);
		showComp.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Constants.SHOW_COMPUTER_CARDS = showComp.isSelected();
				repaint();
			}
		});

		final JCheckBox regMode = createDebugCheckBox(top, "Regular Mode", Constants.REGULAR_MODE);
		regMode.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Constants.REGULAR_MODE = regMode.isSelected();
				repaint();
			}
		});

		final JCheckBox showManips = createDebugCheckBox(top, "Show Manips", Constants.SHOW_DECK_MANIPS);
		showManips.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Constants.SHOW_DECK_MANIPS = showManips.isSelected();
				repaint();
			}
		});

		final JCheckBox askQs = createDebugCheckBox(top, "Ask User Questions", Constants.ASK_USERS_FRACTION_QS);
		askQs.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Constants.ASK_USERS_FRACTION_QS = askQs.isSelected();
				repaint();
			}
		});

		textCommands = createDebugTextField(top, "Computer");
		textCommands.addKeyListener(this);

		add(top, BorderLayout.NORTH);

		/* End of Debug Controls */

		toolbox.add(Box.createVerticalGlue());
		oppoScore = new JLabel(Constants.THEIR_INIT_SCORE + "0");
		oppoScore.setFont(new Font("Sans-serif", Font.BOLD, 32));
		toolbox.add(oppoScore);

		for(int i = 0; i < 2; i++) {
			toolbox.add(Box.createVerticalGlue());
		}
		playerScore = new JLabel(Constants.YOUR_INIT_SCORE + "0");
		playerScore.setFont(new Font("Sans-serif", Font.BOLD, 32));
		toolbox.add(playerScore);

		for(int i = 0; i < 3; i++) {
			toolbox.add(Box.createVerticalGlue());	
		}

		gameArea = new JPanel(new CardLayout());
		manCardPanel = new ManCardPanel();
		gamePanel = new GamePanel(Constants.PANEL_WIDTH, Constants.PANEL_HEIGHT, this, nRep);
		manCardPanel.addManListener(gamePanel);
		currentLayout = GAME_PANEL;
		addLayout(gamePanel, GAME_PANEL);
		addLayout(manCardPanel, MANIP_PANEL);
		add(gameArea, BorderLayout.CENTER);
	}

	public void beginGame() {
		if(!Constants.DEBUG_MODE) {
			JOptionPane.showMessageDialog(myFrame,
					Constants.INFO_START_GAME_HELP,
					"New Game",
					JOptionPane.INFORMATION_MESSAGE);
			newGame.setVisible(false);
		}
		gamePanel.startGame(!Constants.NETWORK_MODE);
	}

	public void disableControls() {
		setAllControls(false);
	}

	public void enableControls() {
		userTurn();
		setAllControls(true);
	}

	public void setAllControls(boolean enabled) {
		for(JComponent jc: controls) {
			jc.setEnabled(enabled);
		}
	}
	
	public JLabel createDebugLabel(JPanel b, String label) {
		JLabel l = new JLabel(label);
		l.setVisible(Constants.DEBUG_MODE);
		b.add(l);
		return l;
	}

	public JButton createDebugButton(JPanel b, String label) {
		JButton temp = new JButton(label);
		b.add(temp);
		temp.setVisible(Constants.DEBUG_MODE);
		return temp;
	}

	public JSlider createDebugSlider(JPanel b, String title, int low, int high, int reg) {
		JLabel sliderTitle = new JLabel(title);
		sliderTitle.setVisible(Constants.DEBUG_MODE);
		b.add(sliderTitle);
		JSlider temp = new JSlider(low, high, reg);
		b.add(temp);
		temp.setVisible(Constants.DEBUG_MODE);
		return temp;
	}

	public JCheckBox createDebugCheckBox(JPanel p, String label, boolean initValue) {
		JCheckBox temp = new JCheckBox(label);
		temp.setSelected(initValue);
		p.add(temp);
		temp.setVisible(Constants.DEBUG_MODE);
		return temp;
	}

	public JTextField createDebugTextField(JPanel p, String label) {
		JLabel l = new JLabel(label);
		JTextField temp = new JTextField();
		p.add(temp);
		p.add(l);
		p.setVisible(Constants.DEBUG_MODE);
		temp.setVisible(Constants.DEBUG_MODE);
		return temp;
	}

	public void updateLabels(Player p) {
		int playerPts = p.getPoints();
		Debug.println("Labels updating");
		if(gamePanel.isOurUser(p)) {
			if(playerScore != null) {
				playerScore.setText(Constants.YOUR_INIT_SCORE + playerPts);
			}
		}else{
			if(oppoScore != null) {
				oppoScore.setText(Constants.THEIR_INIT_SCORE + playerPts);
			}
		}
	}

	private void calculateScore() {
		if(gamePanel.gameStarted()) {
			JOptionPane.showMessageDialog(myFrame, gamePanel.calculateScoreForRound());
			int oppoPoints = gamePanel.getOppositionPoints();
			int playerPoints = gamePanel.getPlayerPoints();
			if(oppoPoints < Constants.SCORE_TO_WIN && playerPoints < Constants.SCORE_TO_WIN) {
				shufflin = new FYIMessage(myFrame, Constants.INFO_SHUFFLING);
				gamePanel.newRound();
			}else{
				String message = determineWinMessage(playerPoints, oppoPoints);
				int option = JOptionPane.showOptionDialog(myFrame, message, "", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, Constants.YES_NO, 0);
				handleWinScenario(option);
			}
		}else{
			JOptionPane.showMessageDialog(gamePanel, Constants.ERROR_NO_GAME_YET, "", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void userTurn() {
		oppoScore.setForeground(Constants.TOOLBOX_FOREGROUND);
		playerScore.setForeground(Constants.TOOLBOX_FOREGROUND_LOUD);
	}

	public void opponentTurn() {
		oppoScore.setForeground(Constants.TOOLBOX_FOREGROUND_LOUD);
		playerScore.setForeground(Constants.TOOLBOX_FOREGROUND);
	}

	private String determineWinMessage(int playerPoints, int oppoPoints) {
		String message = " " + Constants.INFO_PLAY_AGAIN;
		if(oppoPoints >= Constants.SCORE_TO_WIN) {
			message = Constants.YOU_LOST + message;
		}else{
			message = Constants.YOU_WON + message;
		}
		return message;
	}

	private void handleWinScenario(int option) {
		if(option == 0) {
			gamePanel.startGame(!Constants.NETWORK_MODE);
		}else{
			System.exit(0);
		}
	}

	private void askToFinishRound() {
		int option = JOptionPane.showOptionDialog(myFrame, Constants.INFO_NO_MOVES, "", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, Constants.YES_NO, 0);
		if(option == 0) {
			calculateScore();
			NetHelper.sendNetCalc(netRep);
		}else if(option == 1) {
			forceUserTurn(true);
		}
	}

	private void forceUserTurn(boolean isForSelf) {
		gamePanel.setTurn(isForSelf);
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }

		new CardGamePanel(null);
	}

	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
			parseTextInput(textCommands.getText());
		}
	}

	//Try to build a parser that will take text and have the game perform something.
	//Text input for right now is cardindex , deckindex;
	public void parseTextInput(String cmd) {
		if(shufflin != null) {
			shufflin.killMessage();
			shufflin = null;
		}
		int colPos = cmd.indexOf(":");
		if(colPos == -1) {
			Debug.println(Constants.STATUS_NO_SUCH_CMD);
			gamePanel.updateStatus(Constants.STATUS_NO_SUCH_CMD);
			return;
		}
		String command = cmd.substring(0, colPos);
		String rest = cmd.substring(colPos+1);
		boolean isForSelf = command.contains(Constants.CMD_PART_ME);
		boolean shouldClear = command.contains(Constants.CMD_PART_CLEAR);
		decideCommand(command, rest, isForSelf, shouldClear);
	}
	
	private boolean isGamePanelCommand(String command) {
		return !(command.equals(Constants.CMD_SHAKED) ||
			command.equals(Constants.CMD_CHIP));
	}

	private void decideCommand(String command, String rest, boolean isForSelf, boolean shouldClear) {
		if(isGamePanelCommand(command) && currentLayout.equals(MANIP_PANEL)) {
			showGameLayout();
		}
		if(command.equals(Constants.CMD_MOVE)) {
			parseMove(rest);
		}else if(command.equals(Constants.CMD_CALC)) {
			calculateScore();
			NetHelper.sendNetNewRound(netRep);
		}else if(command.equals(Constants.CMD_START)) {
			beginGame();
		}else if(command.equals(Constants.CMD_NO_MOVES)) {
			askToFinishRound();
		}else if(command.contains(Constants.CMD_TURN)) {
			forceUserTurn(isForSelf);
		}else if(command.contains(Constants.CMD_RADIOS)) {
			gamePanel.oppoUsedRadios(Integer.parseInt(rest));
		}else if(command.contains(Constants.CMD_CHIP)) {
			chipObserver.chipDrawn(Boolean.parseBoolean(rest));
		}else if(command.contains(Constants.CMD_SHAKED)) {
			gamePanel.timeToDrawPebbles();
		}else if(command.contains(Constants.CMD_ADD_TRICK)) {
			Helpers.acquireSem(Helpers.cardDist);
			if(shouldClear){
				clearHand(rest, isForSelf);
			}else{
				parseHand(rest, isForSelf);
			}
			Helpers.releaseSem(Helpers.cardDist);
		}else if(command.contains(Constants.CMD_ADD_TEAM)) {
			Helpers.acquireSem(Helpers.cardDist);
			if(shouldClear) {
				clearTeam(rest, isForSelf);
			}else{
				parseTeam(rest, isForSelf);
			}
			Helpers.releaseSem(Helpers.cardDist);
		}else{
			gamePanel.updateStatus(Constants.STATUS_NO_SUCH_CMD);
		}
	}

	private void parseMove(String s) {
		int semiPos = s.indexOf(Constants.CMD_MOVE_SEP);
		if(semiPos == -1) {
			Debug.println("Relinquishing control");
			//TODO: Add some type of status counter here so you know that the other person gave up
			return;
		}
		String cardIndex = s.substring(0, semiPos);
		int deckIndex = Integer.parseInt(s.substring(semiPos+1));
		gamePanel.handleNetworkMove(cardIndex, deckIndex);
	}

	public static final String CARD_DELIMITER = "_";

	private void parseHand(String s, boolean isPlayer) {
		s = StringUtils.within(s, Constants.CMD_ARG_START, Constants.CMD_ARG_END);
		Debug.println(s);
		ArrayList<String> ts = new ArrayList<String>(Arrays.asList(s.split(CARD_DELIMITER)));
		Debug.println(ts);
		if(ts.size() > 1) {
			gamePanel.addTricksToHand(ts, isPlayer);
		}else{
			gamePanel.addTrickToHand(ts.get(0), isPlayer);
		}
	}

	private void clearHand(String s, boolean isPlayer) {
		//For now just clear the hand...don't think you'll need to remove specific cards
		gamePanel.clearHand(isPlayer);
	}

	private void clearTeam(String s, boolean isPlayer) {
		gamePanel.clearTeam(isPlayer);
	}

	private void parseTeam(String s, boolean isPlayer) {
		s = StringUtils.within(s, "{", "}");
		ArrayList<String> ts = new ArrayList<String>(Arrays.asList(s.split(CARD_DELIMITER)));
		if(ts.size() > 1) {
			gamePanel.addTeamsToHand(ts, isPlayer);
		}else{
			gamePanel.addTeamToHand(ts.get(0), isPlayer);
		}
	}

	public void titleUpdated(String title) {
		gamePanel.titleUpdated(title);
	}

	public void setChipObserver(PebbleListener l) {
		chipObserver = l;
	}
	
	private void switchToLayout(String layoutName) {
    	CardLayout cl = (CardLayout) gameArea.getLayout();
    	cl.show(gameArea, layoutName);
    	currentLayout = layoutName;
    	repaint();
    }
	
	private void switchToAskManipLayout() {
		String name = cardPanelNames.get(2);
		switchToLayout(name);
		ManCardPanel mcp = cardMapping.get(name);
		mcp.askForFocus();
	}
	
	private boolean addLayout(JPanel layout, String name) {
		if(cardPanelNames.contains(name)) {
			return false;
		}
		gameArea.add(layout, name);
		cardPanelNames.add(name);
		return true;
	}
	
	private void removeLayout(JPanel mPanel, String name) {
		assert(cardPanelNames.contains(name));
		gameArea.remove(mPanel);
		cardPanelNames.remove(name);
	}
	
	private void toggleManipLayout() {
		if(!currentLayout.equals(GAME_PANEL)) {
			switchToLayout(GAME_PANEL);
		}else{
			switchToLayout(MANIP_PANEL);
		}
	}
	
	private void showGameLayout() {
		switchToLayout(GAME_PANEL);
		manipButton.setEnabled(true);
	}
	
	private boolean inLayout(String layoutName) {
    	return currentLayout.equalsIgnoreCase(layoutName);
    }

	public void keyReleased(KeyEvent arg0) {

	}

	public void keyTyped(KeyEvent arg0) {

	}

	public void manViewCreated(ManCardPanel mPanel) {
		String name = mPanel.getQuestion();
		addLayout(mPanel, name);
		cardMapping.put(name, mPanel);
		manipButton.setEnabled(false);
		switchToLayout(name);
	}

	public boolean manViewDone(ManCardPanel mPanel) {
		String name = mPanel.getQuestion();
		removeLayout(mPanel, name);
		cardMapping.remove(name);
		//TODO: fix but where it is not showing that it is there. string is not the same
		if(cardPanelNames.size() > 2) {
			switchToAskManipLayout();
		}else{
			showGameLayout();
		}
		return cardPanelNames.size() <= 2;
	}

	public void toggleManipView() {
		// TODO Auto-generated method stub
		toggleManipLayout();
	}
}
