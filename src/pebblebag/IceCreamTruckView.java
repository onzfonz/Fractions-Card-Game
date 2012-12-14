package pebblebag;
/*
 * File: IceCreamTruckView
 * This will be the container window that will have the buttons
 * and such to help manipulate the pebble window.  It will also have
 * a pebble panel.
 */
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import manipulatives.ManDeckViewPanel;
import network.NetDelegate;
import network.NetHelper;
import basic.Constants;
import deck.DeckView;

public class IceCreamTruckView extends JPanel implements PebblePanelListener {
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
	private NetDelegate netRep;
	private JButton movePebble;
	
	public IceCreamTruckView(DeckView dv, NetDelegate nRep, String p) {
		setLayout(new BorderLayout());
		
		JPanel manBox = new JPanel();
		manBox.setLayout(new BorderLayout());
		
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
		movePebble = new JButton(Constants.BTN_PEBBLE_DONE_SHAKING);
		movePebble.setToolTipText(Constants.TIP_DONE_SHAKING);
		movePebble.setFont(Constants.FONT_REG);
		box.add(movePebble);
		movePebble.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Constants.NETWORK_MODE) {
					pPanel.tellOppoToChoosePebbles();
				}else{
					pPanel.beginPebbleAnimation(true);
				}
			}
		});
		movePebble.setVisible(false);
		
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
		
		pPanel = new PebblePanel(dv, 1000, 600, !dv.getPlayer().isHuman(), this, nRep, this, p);
		netRep = nRep;
		
		manBox.add(pPanel, BorderLayout.CENTER);
		add(manBox, BorderLayout.WEST);
		
		ManDeckViewPanel deckViewShown = new ManDeckViewPanel(dv, null);
		deckViewShown.setPreferredSize(new Dimension(Constants.ORIG_CARD_WIDTH, getHeight()));
		add(deckViewShown, BorderLayout.EAST);
		
		setVisible(true);
	}
	
	public void addBagListener(IceWindowListener l) {
		pPanel.addBagListener(l);
	}
	
	public void pebbleDrawingTime() {
		pPanel.stopShakingAnimation();
	}
	
	public void forceABagShake(int x, int y) {
		pPanel.doAShake(x, y);
	}

	public void bagShakingDone() {
		// TODO Auto-generated method stub
		viewPebbleButton(false);
	}

	public void bagShakingStarted() {
		// TODO Auto-generated method stub
		viewPebbleButton(true);
	}
	
	private void viewPebbleButton(boolean visible) {
		movePebble.setVisible(visible);
		repaint();
	}
}
