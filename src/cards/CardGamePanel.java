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
import java.util.concurrent.Semaphore;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
import network.NetDelegate;
import network.NetHelper;
import pebblebag.IceCreamTruckView;
import pebblebag.PebbleListener;
import tugstory.TugPanel;
import basic.Constants;
import basic.FYIMessage;
import basic.GamePanel;
import basic.Helpers;
import basic.Player;
import combo.ChooseComboCardPanel;
import extras.Debug;
import extras.PanelListener;
import extras.StringUtils;

public class CardGamePanel extends JPanel implements PanelListener, KeyListener {

	private static final long serialVersionUID = -2520131149153594608L;
	private GamePanel gamePanel;
//	private ManFrame manipWindow;
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
	private JButton doneWithTurnButton;
	private JComboBox sliderOption;
	private JPanel toolbox;

	private static final String GAME_PANEL = "Game Panel";
	private static final String MANIP_PANEL = "Manip Panel";
	private static final String COMBO_NAME = "Combo View";
	private static final String ICE_NAME = "Chance View";
	private static final String TUG_VIEW = "End-Of-Round View";

	public CardGamePanel(NetDelegate nRep) {
		//setTitle(Constants.WINDOW_TITLE);
//		manipWindow = null;
		myFrame = this;
		netRep = nRep;
		//if (file != null) boardPanel.open(file);
		setLayout(new BorderLayout());
		toolbox = new JPanel();
		toolbox.setLayout(new BoxLayout(toolbox, BoxLayout.Y_AXIS));
		toolbox.setBackground(Constants.TOOLBOX_BACKGROUND);
		add(toolbox, BorderLayout.WEST);

		manCardPanel = new ManCardPanel();
		controls = new ArrayList<JComponent>();
		gameArea = new JPanel(new CardLayout());
		resetPanel(false);
			
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
		doneWithTurnButton = new JButton(passBtnText);
		doneWithTurnButton.setToolTipText(Constants.TIP_DONE_TURN);
		doneWithTurnButton.setFont(Constants.FONT_TINY);
		final JButton doneTurn = doneWithTurnButton;
		controls.add(doneWithTurnButton);
		toolbox.add(doneWithTurnButton);
		manipButton = new JButton(Constants.BTN_LAUNCH_MANIP);
		manipButton.setToolTipText(Constants.TIP_MANIP);
		manipButton.setFont(Constants.FONT_TINY);
		manipButton.setVisible(Constants.HAVE_MANIP_BUTTON);
//		final JButton manipBtn = manipButton;
		//controls.add(newRound);
		toolbox.add(manipButton);
		manipButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleManipLayout();
			}
		});

		doneWithTurnButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doneTurn.setForeground(Constants.DEFAULT_BUTTON_TEXT_COLOR);
				doneTurn.setFont(Constants.FONT_TINY);
				repaint();
				if(gamePanel.gameStarted()) {
					/* one possibility is to remove this */
					if(!gamePanel.computerMove(true)) {
						askToFinishRound();
					}else{
						NetHelper.sendNetNoMoves(netRep);
					}
				}else{
					JOptionPane.showMessageDialog(gamePanel, Constants.ERROR_NO_GAME_YET, "", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		/*Debug Controls*/

		JLabel debugControls = new JLabel("Debug Options");
		debugControls.setVisible(Constants.DEBUG_MODE);
		toolbox.add(debugControls);

		newRound = Debug.createDebugButton(toolbox, "New Round");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.newRound();  
			}
		});

		newRound = Debug.createDebugButton(toolbox, "Add P1 Teammate");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.addTeam(true);  
			}
		});

		newRound = Debug.createDebugButton(toolbox, "Subtract P1 Teammate");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.removeLatestTeam(true);  
			}
		});

		newRound = Debug.createDebugButton(toolbox, "Add P1 Trick");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.addTrick(true); 
			}
		});

		newRound = Debug.createDebugButton(toolbox, "Subtract P1 Trick");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.eliminateLatestTrick(true); 
			}
		});

		newRound = Debug.createDebugButton(toolbox, "Give Computer Stinks");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.setTrickHandDebug(false);
			}
		});

		newRound = Debug.createDebugButton(toolbox, "Give Me Stinks");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.setTrickHandDebug(true);
			}
		});

		newRound = Debug.createDebugButton(toolbox, "Give Me Airs");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.setTrickHandToAirs(true);
			}
		});
		
		newRound = Debug.createDebugButton(toolbox, "Give Computer Airs");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.setTrickHandToAirs(false);
			}
		});
		
		newRound = Debug.createDebugButton(toolbox, "Give Me Ices");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.setTrickHandToIces(true);
			}
		});
		
		newRound = Debug.createDebugButton(toolbox, "Give Computer Ices");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.setTrickHandToIces(false);
			}
		});

		newRound = Debug.createDebugButton(toolbox, "End the Round");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.endOfRoundAnimation(false);
			}
		});

		final JLabel redLabel = Debug.createDebugLabel(toolbox, "r");
		final JSlider redSlider = Debug.createDebugSlider(toolbox, "Red", 0, 255, 128);
		redSlider.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				Color curColor = getSliderColor();
				int blue = curColor.getBlue();
				int green = curColor.getGreen();
				Color tempColor = new Color(redSlider.getValue(), green, blue);
				//gamePanel.setBackground(tempColor);
				setSliderColor(tempColor);
				redLabel.setText("" + redSlider.getValue());
			}
		});

		final JLabel greenLabel = Debug.createDebugLabel(toolbox, "g");
		final JSlider greenSlider = Debug.createDebugSlider(toolbox, "Green", 0, 255, 128);
		greenSlider.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				Color curColor = getSliderColor();
				int blue = curColor.getBlue();
				int red = curColor.getRed();
				Color tempColor = new Color(red, greenSlider.getValue(), blue);
				//gamePanel.setBackground(tempColor);
				setSliderColor(tempColor);
				greenLabel.setText("" + greenSlider.getValue());
			}
		});

		final JLabel blueLabel = Debug.createDebugLabel(toolbox, "b");
		final JSlider blueSlider = Debug.createDebugSlider(toolbox, "Blue", 0, 255, 128);
		blueSlider.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				Color curColor = getSliderColor();
				int red = curColor.getRed();
				int green = curColor.getGreen();
				Color tempColor = new Color(red, green, blueSlider.getValue());
				//gamePanel.setBackground(tempColor);
				setSliderColor(tempColor);
				blueLabel.setText("" + blueSlider.getValue());
			}
		});

		String[] colorOpts = {"Left", "Game", "Status-Back", "Status-Fore", "Suggestion"};
		sliderOption = new JComboBox(colorOpts);
		sliderOption.setVisible(Constants.DEBUG_MODE);
		toolbox.add(sliderOption);
		sliderOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Color c = getSliderColor();
				redSlider.setValue(c.getRed());
				greenSlider.setValue(c.getGreen());
				blueSlider.setValue(c.getBlue());
			}
		});

		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));

		final JCheckBox showComp = Debug.createDebugCheckBox(top, "Show Computer", Constants.SHOW_COMPUTER_CARDS);
		showComp.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Constants.SHOW_COMPUTER_CARDS = showComp.isSelected();
				repaint();
			}
		});

		final JCheckBox regMode = Debug.createDebugCheckBox(top, "Regular Mode", Constants.REGULAR_MODE);
		regMode.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Constants.REGULAR_MODE = regMode.isSelected();
				repaint();
			}
		});

		final JCheckBox showManips = Debug.createDebugCheckBox(top, "Show Manips", Constants.SHOW_DECK_MANIPS);
		showManips.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Constants.SHOW_DECK_MANIPS = showManips.isSelected();
				repaint();
			}
		});

		final JCheckBox askQs = Debug.createDebugCheckBox(top, "Ask User Questions", Constants.ASK_USERS_FRACTION_QS);
		askQs.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Constants.ASK_USERS_FRACTION_QS = askQs.isSelected();
				repaint();
			}
		});

		textCommands = Debug.createDebugTextField(top, "Computer");
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

		add(gameArea, BorderLayout.CENTER);
	}

	public void resetPanel(boolean isReset) {
		String title = "";
		if(isReset) {
			gameArea.removeAll();
			title = gamePanel.getTitle();
		}
		gamePanel = new GamePanel(Constants.PANEL_WIDTH, Constants.PANEL_HEIGHT, this, netRep);
		manCardPanel.addManListener(gamePanel);

		if(isReset) {
			cardMapping.clear();
			cardPanelNames.clear();
			gamePanel.titleUpdated(title);
		}else{
			cardPanelNames = new ArrayList<String>();
			cardMapping = new HashMap<String, ManCardPanel>();
		}

		if(!isReset) {
			gameArea = new JPanel(new CardLayout());
		}
		currentLayout = GAME_PANEL;
		addLayout(gamePanel, GAME_PANEL);
		addLayout(manCardPanel, MANIP_PANEL);
	}
	
	public boolean possibleComputerTurn() {
		if(!Constants.NETWORK_MODE && !gamePanel.isMyTurn()) {
			return doAComputerOpponentMove();
		}
		return false;
	}
	
	public void doComputerTurn() {
		doAComputerOpponentMove();
	}
	
	private boolean doAComputerOpponentMove() {
		if(!gamePanel.computerMove(false)) {
			askToFinishRound();
			return true;
		}
		return false;
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
		Debug.println("disabling controls");
	}

	public void enableControls() {
		setAllControls(true);
		userTurn();
	}

	public void setAllControls(boolean enabled) {
		for(JComponent jc: controls) {
			jc.setEnabled(enabled);
		}
	}
	
	public void suggestDoneWithTurn() {
		doneWithTurnButton.setForeground(Constants.LOUD_BUTTON_TEXT_COLOR);
		doneWithTurnButton.setFont(Constants.FONT_SMALL);
		gamePanel.updateStatus(Constants.STATUS_SUGGEST_DONE_W_TURN);
		repaint();
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

	private void calculateScore(boolean wasTold) {  //This is the part that I want to look into for calculating the score, which we'll swap out
		if(gamePanel.gameStarted()) {
			String score = gamePanel.calculateScoreForRound();
			int oppoPoints = gamePanel.getOppositionPoints();
			int playerPoints = gamePanel.getPlayerPoints();
			if(oppoPoints < Constants.SCORE_TO_WIN && playerPoints < Constants.SCORE_TO_WIN) {
				if(!Constants.DEBUG_MODE) {
					shufflin = new FYIMessage(myFrame, Constants.INFO_SHUFFLING);
				}
				gamePanel.newRound();
			}else{
				String message = determineWinMessage(playerPoints, oppoPoints);
				int option = JOptionPane.showOptionDialog(myFrame, message, "", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, Constants.YES_NO, 0);
				handleWinScenario(option);
			}
			JOptionPane.showMessageDialog(myFrame, score);
		}else{
			JOptionPane.showMessageDialog(gamePanel, Constants.ERROR_NO_GAME_YET, "", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void userTurn() {
		oppoScore.setForeground(Constants.TOOLBOX_FOREGROUND);
		playerScore.setForeground(Constants.TOOLBOX_FOREGROUND_LOUD);
		manipButton.setEnabled(false);
	}

	public void opponentTurn() {
		oppoScore.setForeground(Constants.TOOLBOX_FOREGROUND_LOUD);
		playerScore.setForeground(Constants.TOOLBOX_FOREGROUND);
		manipButton.setEnabled(true);
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
		if(option == JOptionPane.YES_OPTION || option == JOptionPane.CLOSED_OPTION) {
			gamePanel.startGame(!Constants.NETWORK_MODE);
		}else{
			System.exit(0);
		}
	}

	private void askToFinishRound() {
		String[] optsArray = {"No", "Yes"};
		int option = JOptionPane.showOptionDialog(myFrame, Constants.INFO_NO_MOVES, "", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, optsArray, 0);
		gamePanel.systemNotify("askToFinishRound");
		if(option == 1) {
			NetHelper.sendNetCalc(netRep); //send this out first so that the animation doesn't take over...
			//calculateScore();
			gamePanel.endOfRoundAnimation(false);
		}else if(option == 0 || option == JOptionPane.CLOSED_OPTION) {
			forceUserTurn(true);
		}
	}

	private void forceUserTurn(boolean isForSelf) {
		if(gamePanel.shouldLaunchHelp(isForSelf)) {
			return;
		}
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
			textCommands.setText("");
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
			Debug.println("received the move command");
			gamePanel.systemWait("received move cmd");
			Debug.println("passed the wait part");
			parseMove(rest);
			Debug.println("finished parsing move cmd");
		}else if(command.equals(Constants.CMD_CALC)) {
			gamePanel.endOfRoundAnimation(true);
			//calculateScore();
		}else if(command.equals(Constants.CMD_START)) {
			beginGame();
		}else if(command.equals(Constants.CMD_NO_MOVES)) {
			gamePanel.systemWait("received nomoves cmd");
			askToFinishRound();
		}else if(command.contains(Constants.CMD_TURN)) {
			forceUserTurn(isForSelf);
		}else if(command.contains(Constants.CMD_RADIOS)) {
			gamePanel.oppoUsedRadios(Integer.parseInt(rest));
		}else if(command.contains(Constants.CMD_CHIP)) {
			chipObserver.chipDrawn(Boolean.parseBoolean(rest));
		}else if(command.contains(Constants.CMD_SHAKED)) {
			gamePanel.timeToDrawPebbles();
		} else if(command.contains(Constants.CMD_SHAKING)) {
			gamePanel.timeToMoveBagAround(rest);
		} else if(command.contains(Constants.CMD_ADD_TRICK)) {
			Helpers.acquireCardDist();
			if(shouldClear){
				clearHand(rest, isForSelf);
			}else{
				parseHand(rest, isForSelf);
			}
			Helpers.releaseCardDist();
		}else if(command.contains(Constants.CMD_ADD_TEAM)) {
			Helpers.acquireCardDist();
			if(shouldClear) {
				clearTeam(rest, isForSelf);
			}else{
				parseTeam(rest, isForSelf);
			}
			Helpers.releaseCardDist();
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
		String deckStr;
		int spacePos = s.indexOf(" ");
		if(spacePos == -1) {
			deckStr = s.substring(semiPos+1);
		}else{
			deckStr = s.substring(semiPos+1, spacePos);
		}
		int deckIndex = Integer.parseInt(deckStr);
		gamePanel.systemNotify(" parsed moved");
		gamePanel.handleNetworkMove(cardIndex, deckIndex);
	}

	private void parseHand(String s, boolean isPlayer) {
		ArrayList<String> ts = generateCardStrings(s);
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
		ArrayList<String> ts = generateCardStrings(s);
		if(ts.size() > 1) {
			gamePanel.addTeamsToHand(ts, isPlayer);
		}else{
			gamePanel.addTeamToHand(ts.get(0), isPlayer);
		}
	}
	
	private ArrayList<String> generateCardStrings(String s) {
		s = StringUtils.within(s, Constants.CMD_ARG_START, Constants.CMD_ARG_END);
		return new ArrayList<String>(Arrays.asList(s.split(Constants.CMD_CARD_DELIMITER)));
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
			NetHelper.logMessage(netRep, gamePanel.parsePlayerTurnName(), "Closed the scratch paper");
			switchToLayout(GAME_PANEL);
		}else{
			NetHelper.logMessage(netRep, gamePanel.parsePlayerTurnName(), "Opened the scratch paper");
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
		mPanel.askForFocus();
	}

	public boolean manViewDone(ManCardPanel mPanel) {
		String name = mPanel.getQuestion();
		removeLayout(mPanel, name);
		cardMapping.remove(name);
		//TODO: fix but where it is not showing that it is there. string is not the same
		return switchToGameOrManipPanel();
	}

	private boolean switchToGameOrManipPanel() {
		if(cardPanelNames.size() > 2) {
			switchToAskManipLayout();
		}else{
			showGameLayout();
		}
		return cardPanelNames.size() <= 2;
	}

	public void comboViewCreated(ChooseComboCardPanel cPanel) {
		panelViewCreated(cPanel, COMBO_NAME);
	}

	public boolean comboViewDone(ChooseComboCardPanel cPanel) {
		return panelViewDone(cPanel, COMBO_NAME);
	}

	public void iceViewCreated(IceCreamTruckView iPanel) {
		panelViewCreated(iPanel, ICE_NAME);
	}

	public boolean iceViewDone(IceCreamTruckView iPanel) {
		panelViewDone(iPanel, ICE_NAME);
		return switchToGameOrManipPanel();
	}
	
	public void tugViewCreated(TugPanel tPanel) {
		panelViewCreated(tPanel, TUG_VIEW);
	}
	
	public boolean tugViewDone(TugPanel tPanel) {
		boolean b = panelViewDone(tPanel, TUG_VIEW);
		Debug.println("told? " + tPanel.wasTold());
		calculateScore(tPanel.wasTold());
		if(Constants.NETWORK_MODE && tPanel.wasTold()) {
			NetHelper.sendNetNewRound(netRep);
		}
		return b;
	}

	private void panelViewCreated(JPanel p, String name) {
		addLayout(p, name);
		manipButton.setEnabled(false);
		switchToLayout(name);		
	}

	private boolean panelViewDone(JPanel p, String name) {
		removeLayout(p, name);
		showGameLayout();
		return true;
	}

	public void toggleManipView() {
		// TODO Auto-generated method stub
		toggleManipLayout();
	}

	private Color getSliderColor() {
		int opt = sliderOption.getSelectedIndex();
		switch(opt) {
		case 0: return toolbox.getBackground();
		case 1: return gamePanel.getBackground();
		case 2: return gamePanel.getStatusArea().getBackground();
		case 3: return gamePanel.getStatusBox().getForeground();
		case 4: return gamePanel.getSuggestionOverlay();
		}
		return null;
	}

	private void setSliderColor(Color c) {
		int opt = sliderOption.getSelectedIndex();
		switch(opt) {
		case 0: toolbox.setBackground(c); break;
		case 1: gamePanel.setBackground(c); break;
		case 2: gamePanel.getStatusArea().setBackground(c); break;
		case 3: gamePanel.getStatusBox().setForeground(c); break;
		case 4: gamePanel.setSuggestionOverlay(c); break;
		}
	}
}
