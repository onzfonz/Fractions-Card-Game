package extras;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;

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
import cards.CardGameConstants;

public class CardFrameDebug extends JFrame implements PanelListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2520131149153594608L;
	private GamePanel gamePanel;
	private JLabel status;
	private ManFrame manipWindow;
	private CardFrameDebug myFrame;
	private JLabel playerScore;
	private JLabel oppoScore;
	private ArrayList<JComponent> controls;

	public CardFrameDebug() {
		setTitle("Fractions Card Game Debug");
		
		gamePanel = new GamePanel(1200, 800, this, CardGameConstants.USER_GETS_GREEN_RECTANGLE);
		manipWindow = null;
		myFrame = this;
		//if (file != null) boardPanel.open(file);
		add(gamePanel, BorderLayout.CENTER);
		controls = new ArrayList<JComponent>();

		JPanel box = new JPanel();
		box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
		add(box, BorderLayout.WEST);

		/*
		 Create the checkboxes and wire them to setters
		 on the ManPanel for each boolean feature.
		 */
		JButton newRound = new JButton("New Game");
		box.add(newRound);
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.startGame();  
			}
		});

		newRound = new JButton("New Round");
		box.add(newRound);
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.newRound();  
			}
		});

		newRound = new JButton("Add P1 Teammate");
		box.add(newRound);
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.addTeam(true);  
			}
		});

		newRound = new JButton("Subtract P1 Teammate");
		box.add(newRound);
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.removeLatestTeam(true);  
			}
		});

		newRound = new JButton("Add P1 Trick");
		box.add(newRound);
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.addTrick(true); 
			}
		});

		newRound = new JButton("Subtract P1 Trick");
		box.add(newRound);
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.eliminateLatestTrick(true); 
			}
		});
		
		newRound = new JButton("Have Computer Take a Turn");
		box.add(newRound);
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.computerMove();  
			}
		});
		
		newRound = new JButton("Add Computer Trick");
		box.add(newRound);
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.addTrick(false);  //false lets gamepanel know it's the computer
			}
		});
		
		newRound = new JButton("Give Computer Set Tricks");
		box.add(newRound);
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.setTrickHandDebug(false);
			}
		});
		
		newRound = new JButton("Give Me Stinks");
		box.add(newRound);
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.setTrickHandDebug(true);
			}
		});
		
		newRound = new JButton("Give Me Airs");
		box.add(newRound);
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.setTrickHandToAirs(true);
			}
		});
		
		newRound = new JButton("Launch Manipulatives");
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
		
		
		oppoScore = new JLabel("Them: 0");
		oppoScore.setFont(new Font("Sans-serif", Font.BOLD, 32));
		box.add(oppoScore);
		
		playerScore = new JLabel("You: 0");
		playerScore.setFont(new Font("Sans-serif", Font.BOLD, 32));
		box.add(playerScore);
		
		newRound.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
		System.out.println("Labels updating");
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

		new CardFrameDebug();
	}


}
