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
import javax.swing.Timer;
import deck.DeckView;

public class PebbleFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4850780253187289854L;
	private PebblePanel pPanel;
	private Timer bagTimer;
	private Timer pebbleTimer;
	private BagShaker bagS;
	private PebbleMover pebM;
	private int numShakes = 0;
	private boolean animationStarted;
	
	public PebbleFrame(DeckView dv) {
		setTitle("Ice Cream Truck Test");
		setLayout(new BorderLayout());
		
		pPanel = new PebblePanel(dv, 400, 600, !dv.getPlayer().isHuman(), this);
	
		
		
		JPanel manBox = new JPanel();
		manBox.setLayout(new BorderLayout());
		manBox.add(pPanel, BorderLayout.CENTER);
		add(manBox, BorderLayout.CENTER);
		
		JPanel box = new JPanel();
		box.setLayout(new BoxLayout(box, BoxLayout.X_AXIS));
		add(box, BorderLayout.NORTH);
        
		JPanel statusBox = new JPanel();
		statusBox.setLayout(new BoxLayout(statusBox, BoxLayout.X_AXIS));
		manBox.add(statusBox, BorderLayout.SOUTH);
		
		animationStarted = false;
		
		/*
		 Create the checkboxes and wire them to setters
		 on the ManPanel for each boolean feature.
		*/
		/*final JButton relaunch = new JButton("ReLaunch");
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
		*/
		final JButton movePebble = new JButton("Done Shaking");
		box.add(movePebble);
		movePebble.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pPanel.beginPebbleAnimation();
			}
		});
		
		/*final JButton shakeNMovePebble = new JButton("Computer Shake");
		box.add(shakeNMovePebble);
		shakeNMovePebble.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bagTimer.start();
			}
		});*/
		
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
		
		
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		pack();
		setVisible(true);
	}
	
	public void addBagListener(BagListener l) {
		pPanel.addBagListener(l);
	}
	
}
