package pebblebag;
/*
 * File: PebbleFrame
 * This will be the container window that will have the buttons
 * and such to help manipulate the pebble window.  It will also have
 * a pebble panel.
 */
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import cards.TeammateCard;
import cards.TrickCard;
import deck.PlayDeck;

public class PebbleFrameDebug extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5148763924146523136L;
	private PebblePanel pPanel;
	private Timer bagTimer;
	private Timer pebbleTimer;
	private BagShaker bagS;
	private PebbleMover pebM;
	private int numShakes = 0;
	
	public PebbleFrameDebug() {
		setTitle("Ice Cream Truck Test");
		setLayout(new BorderLayout());
		
		/* To delete later */
		PlayDeck p1 = new PlayDeck(new TeammateCard("", "Johnson Family", "", 8));
		TrickCard two2Ice = new TrickCard("cards_ice.jpg", 2, 2, "Ice");
		TrickCard radio = new TrickCard("cards_radio.jpg", 1, 1, "Radio");
		TrickCard one2Ice = new TrickCard("cards_ice.jpg", 1, 2, "Ice");
		p1.addTrickCard(two2Ice);
		p1.addTrickCard(one2Ice);
		p1.addTrickCard(radio);
		
		/* end of deleting */
		pPanel = new PebblePanel(p1, 400, 600, true, this);
		
		bagS = new BagShaker(pPanel);
		bagTimer = new Timer(20, bagS);
		bagS.setTimer(bagTimer);
	
		pebM = new PebbleMover(pPanel);
		pebbleTimer = new Timer(20, pebM);
		pebM.setTimer(pebbleTimer);
		
		JPanel manBox = new JPanel();
		manBox.setLayout(new BorderLayout());
		manBox.add(pPanel, BorderLayout.CENTER);
		add(manBox, BorderLayout.CENTER);
		
		JPanel box = new JPanel();
		box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
		add(box, BorderLayout.WEST);
        
		JPanel statusBox = new JPanel();
		statusBox.setLayout(new BoxLayout(statusBox, BoxLayout.X_AXIS));
		manBox.add(statusBox, BorderLayout.SOUTH);
		
		/*
		 Create the checkboxes and wire them to setters
		 on the ManPanel for each boolean feature.
		*/
		final JButton relaunch = new JButton("ReLaunch");
		box.add(relaunch);
		relaunch.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pPanel.resetBag();
			}
		});
		
		final JButton shakeBag = new JButton("Shake Bag");
		box.add(shakeBag);
		shakeBag.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pPanel.beginShakingAnimation();
			}
		});
		
		final JButton movePebble = new JButton("Move Pebble");
		box.add(movePebble);
		movePebble.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pebbleTimer.start();
			}
		});
		
		final JButton insertPebble = new JButton("Insert Pebbles");
		box.add(insertPebble);
		insertPebble.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pPanel.beginInsertingAnimation();
			}
		});
		
		final JButton shakeNMovePebble = new JButton("Computer Shake");
		box.add(shakeNMovePebble);
		shakeNMovePebble.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bagTimer.start();
			}
		});
		
//		final JButton shakeNMovePebbleComp = new JButton("You Shake");
//		box.add(shakeNMovePebbleComp);
//		shakeNMovePebbleComp.addActionListener( new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				bagTimer.start();
//			}
//		});
//		
//		final JButton doneShaking = new JButton("Done Shaking");
//		box.add(doneShaking);
//		doneShaking.addActionListener( new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				bagTimer.start();
//			}
//		});
		
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }
		
		new PebbleFrameDebug();
        
	}
}
