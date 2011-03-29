package cards;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import manipulatives.ManFrame;
import network.NetHelper;
import basic.Constants;
import basic.GamePanel;
import basic.Player;
import extras.Debug;
import extras.PanelListener;
import extras.StringUtils;

public class CardFrame extends JFrame implements PanelListener, KeyListener {

	private static final long serialVersionUID = -2520131149153594608L;
	private GamePanel gamePanel;
	private ManFrame manipWindow;
	private CardFrame myFrame;
	private JButton newGame;
	private JLabel playerScore;
	private JLabel oppoScore;
	private JTextField textCommands;
	private ArrayList<JComponent> controls;

	public CardFrame() {
		setTitle(Constants.WINDOW_TITLE);
		manipWindow = null;
		myFrame = this;
		controls = new ArrayList<JComponent>();

		//if (file != null) boardPanel.open(file);

		JPanel box = new JPanel();
		box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
		add(box, BorderLayout.WEST);

		/*
		 Create the checkboxes and wire them to setters
		 on the ManPanel for each boolean feature.
		 */
		JButton newRound = new JButton(Constants.BTN_NEW_GAME);
		newRound.setToolTipText(Constants.TIP_START);
		newRound.setFont(Constants.FONT_TINY);
		newGame = newRound;
		box.add(newRound);
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
		box.add(newRound);
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(gamePanel.gameStarted()) {
					if(!gamePanel.computerMove()) {
						askToFinishRound();
					}else{
						NetHelper.sendNetworkCommand(Constants.CMD_NO_MOVES);
					}
				}else{
					JOptionPane.showMessageDialog(gamePanel, Constants.ERROR_NO_GAME_YET, "", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		newRound = new JButton(Constants.BTN_LAUNCH_MANIP);
		newRound.setToolTipText(Constants.TIP_MANIP);
		newRound.setFont(Constants.FONT_TINY);
		controls.add(newRound);
		box.add(newRound);
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(manipWindow == null) {
					manipWindow = new ManFrame(Constants.MAN_FRAME_DEFAULT_PLAY, null, null);
					manipWindow.setDefaultCloseOperation(HIDE_ON_CLOSE);
				}else {
					manipWindow.setVisible(true);
				}
			}
		});

		/*Debug Controls*/

		JLabel debugControls = new JLabel("Debug Options");
		debugControls.setVisible(Constants.DEBUG_MODE);
		box.add(debugControls);

		newRound = createDebugButton(box, "New Round");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.newRound();  
			}
		});

		newRound = createDebugButton(box, "Add P1 Teammate");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.addTeam(true);  
			}
		});

		newRound = createDebugButton(box, "Subtract P1 Teammate");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.removeLatestTeam(true);  
			}
		});

		newRound = createDebugButton(box, "Add P1 Trick");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.addTrick(true); 
			}
		});

		newRound = createDebugButton(box, "Subtract P1 Trick");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.eliminateLatestTrick(true); 
			}
		});

		newRound = createDebugButton(box, "Give Computer Set Tricks");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.setTrickHandDebug(false);
			}
		});

		newRound = createDebugButton(box, "Give Me Stinks");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.setTrickHandDebug(true);
			}
		});

		newRound = createDebugButton(box, "Give Me Airs");
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.setTrickHandToAirs(true);
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

		box.add(Box.createVerticalGlue());
		oppoScore = new JLabel(Constants.THEIR_INIT_SCORE + "0");
		oppoScore.setFont(new Font("Sans-serif", Font.BOLD, 32));
		box.add(oppoScore);

		for(int i = 0; i < 2; i++) {
			box.add(Box.createVerticalGlue());
		}
		playerScore = new JLabel(Constants.YOUR_INIT_SCORE + "0");
		playerScore.setFont(new Font("Sans-serif", Font.BOLD, 32));
		box.add(playerScore);

		for(int i = 0; i < 3; i++) {
			box.add(Box.createVerticalGlue());	
		}


		gamePanel = new GamePanel(1075, 775, this, null);
		add(gamePanel, BorderLayout.CENTER);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				System.exit(0);
			}
		});

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	public void beginGame() {
		if(!Constants.DEBUG_MODE) {
			JOptionPane.showMessageDialog(myFrame,
					Constants.INFO_START_GAME_HELP,
					"New Game",
					JOptionPane.INFORMATION_MESSAGE);
			newGame.setVisible(false);
		}
		gamePanel.startGame(true);
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

	public JButton createDebugButton(JPanel b, String label) {
		JButton temp = new JButton(label);
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
				JOptionPane.showMessageDialog(myFrame, Constants.INFO_SHUFFLING);
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
		oppoScore.setForeground(Color.black);
		playerScore.setForeground(Color.green);
	}

	public void opponentTurn() {
		oppoScore.setForeground(Color.green);
		playerScore.setForeground(Color.black);
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
			gamePanel.startGame(true);
		}else{
			System.exit(0);
		}
	}

	private void askToFinishRound() {
		int option = JOptionPane.showOptionDialog(myFrame, Constants.INFO_NO_MOVES, "", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, Constants.YES_NO, 0);
		if(option == 0) {
			NetHelper.sendNetworkCommand(Constants.CMD_CALC);
			calculateScore();
		}
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }

		new CardFrame();
	}

	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
			parseTextInput();
		}
	}

	//Try to build a parser that will take text and have the game perform something.
	//Text input for right now is cardindex , deckindex;
	private void parseTextInput() {
		String s = textCommands.getText();
		int colPos = s.indexOf(":");
		if(colPos == -1) {
			Debug.println(Constants.STATUS_NO_SUCH_CMD);
			gamePanel.updateStatus(Constants.STATUS_NO_SUCH_CMD);
			return;
		}
		String command = s.substring(0, colPos);
		String rest = s.substring(colPos+1);
		boolean isForSelf = command.contains(Constants.CMD_PART_ME);
		boolean shouldClear = command.contains(Constants.CMD_PART_CLEAR);
		decideCommand(command, rest, isForSelf, shouldClear);
	}
	
	private void decideCommand(String command, String rest, boolean isForSelf, boolean shouldClear) {
		if(command.equals(Constants.CMD_MOVE)) {
			parseMove(rest);
		}else if(command.equals(Constants.CMD_CALC)) {
			calculateScore();
		}else if(command.equals(Constants.CMD_START)) {
			beginGame();
		}else if(command.equals(Constants.CMD_NO_MOVES)) {
			askToFinishRound();
		}else if(command.contains(Constants.CMD_TURN)) {
			gamePanel.setTurn(isForSelf);
		}else if(command.contains(Constants.CMD_ADD_TRICK)) {
			if(shouldClear){
				clearHand(rest, isForSelf);
			}else{
				parseHand(rest, isForSelf);
			}
		}else if(command.contains(Constants.CMD_ADD_TEAM)) {
			if(shouldClear) {
				clearTeam(rest, isForSelf);
			}else{
				parseTeam(rest, isForSelf);
			}
		}else{
			gamePanel.updateStatus(Constants.STATUS_NO_SUCH_CMD);
		}
	}

	private void parseMove(String s) {
		int semiPos = s.indexOf(";");
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

	public void keyReleased(KeyEvent arg0) {

	}

	public void keyTyped(KeyEvent arg0) {

	}
}
