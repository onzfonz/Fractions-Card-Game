package cards;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import manipulatives.ManFrame;
import basic.GamePanel;
import basic.Player;
import extras.PanelListener;

public class CardFrame extends JFrame implements PanelListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2520131149153594608L;
	private GamePanel gamePanel;
	private JLabel status;
	private ManFrame manipWindow;
	private CardFrame myFrame;
	private JLabel playerScore;
	private JLabel oppoScore;
	private ArrayList<JComponent> controls;

	public CardFrame() {
		setTitle("Fractions Card Game");
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
		JButton newRound = new JButton("New Game");
		box.add(newRound);
		controls.add(newRound);
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.startGame();  
			}
		});

		newRound = new JButton("New Round");
		controls.add(newRound);
		box.add(newRound);
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(gamePanel.gameStarted()) {
					gamePanel.newRound();  
				}else{
					JOptionPane.showMessageDialog(gamePanel, "Please Click on New Game First", "", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		newRound = new JButton("Have Computer Take A Turn");
		controls.add(newRound);
		box.add(newRound);
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(gamePanel.gameStarted()) {
					gamePanel.computerMove();  
				}else{
					JOptionPane.showMessageDialog(gamePanel, "Please Click on New Game First", "", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		newRound = new JButton("Launch Manipulatives");
		controls.add(newRound);
		box.add(newRound);
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(manipWindow == null) {
					manipWindow = new ManFrame("Play Here", null, null);
					manipWindow.setDefaultCloseOperation(HIDE_ON_CLOSE);
				}else {
					manipWindow.setVisible(true);
				}
			}
		});

		newRound = new JButton("Calculate Score");
		box.add(newRound);
		controls.add(newRound);

		box.add(Box.createVerticalGlue());
		oppoScore = new JLabel("Them: 0");
		oppoScore.setFont(new Font("Sans-serif", Font.BOLD, 32));
		box.add(oppoScore);
		
		for(int i = 0; i < 2; i++) {
			box.add(Box.createVerticalGlue());
		}
		playerScore = new JLabel("You: 0");
		playerScore.setFont(new Font("Sans-serif", Font.BOLD, 32));
		box.add(playerScore);

		for(int i = 0; i < 3; i++) {
			box.add(Box.createVerticalGlue());	
		}
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(gamePanel.gameStarted()) {
					JOptionPane.showMessageDialog(myFrame, gamePanel.calculateScoreForRound());
					int oppoPoints = gamePanel.getOppositionPoints();
					int playerPoints = gamePanel.getPlayerPoints();
					String[] options = {"Yes", "No"};
					if(oppoPoints < CardGameConstants.SCORE_TO_WIN && playerPoints < CardGameConstants.SCORE_TO_WIN) {
						gamePanel.newRound();
					}else{
						String message = determineWinMessage(playerPoints, oppoPoints);
						int option = JOptionPane.showOptionDialog(myFrame, message, "", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, 0);
						handleWinScenario(option);
					}
				}else{
					JOptionPane.showMessageDialog(gamePanel, "Please Click on New Game First", "", JOptionPane.ERROR_MESSAGE);
				}
			}

			private String determineWinMessage(int playerPoints, int oppoPoints) {
				String message = ". Would you like to play again?";
				if(oppoPoints >= CardGameConstants.SCORE_TO_WIN) {
					message = "You lost" + message;
				}else{
					message = "You win" + message;
				}
				return message;
			}

			private void handleWinScenario(int option) {
				if(option == 0) {
					gamePanel.startGame();
				}else{
					System.exit(0);
				}

			}
		});
		
		gamePanel = new GamePanel(1200, 800, this, CardGameConstants.USER_GETS_GREEN_RECTANGLE);
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
	
	public void disableControls() {
		setAllControls(false);
	}
	
	public void enableControls() {
		setAllControls(true);
	}
	
	public void setAllControls(boolean enabled) {
		for(JComponent jc: controls) {
			jc.setEnabled(enabled);
		}
	}

	public void updateLabels(Player p) {
		int playerPts = p.getPoints();
		if(CardGameConstants.DEBUG_MODE) {
			System.out.println("Labels updating");
		}
		if(gamePanel.isOurUser(p)) {
			if(playerScore != null) {
				playerScore.setText("You: " + playerPts);
			}
		}else{
			if(oppoScore != null) {
				oppoScore.setText("Them: " + playerPts);
			}
		}
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }

		File fileToOpen = null;

		// Notice command line arg filename
		if (args.length != 0) {
			fileToOpen = new File(args[0]);
		}

		new CardFrame();
	}
}
