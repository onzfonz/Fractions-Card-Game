package combo;
/*
 * PebblePanel will hold a couple of things
 * it will have.
 * An image of a pebble bag
 * The pebbles that are supposed to be in the bag.
 * (This will be determined by the PebbleBag object which it will have one of);
 * The pebble bag will be divided into two states.
 * One state will be where one play can shake the bag
 * The other state will be where the player can draw out a pebble.
 * Both of these actions will occur using mouse listeners.
 * Each pebble will be drawn in the bag, and will be a one to one correspondence
 * However, there must be when they draw out from the bag, a way of not letting them
 * get past a threshold so that they cheat.
 * After a certain point, we will replace the image that they have with the image given
 * Once that happens, we can denote that the game the turn is over simply by looking at the
 * image that is currently loaded on the bag.
 * 
 * PebbleModel object will be one that has an ice cream Card followed by the number of Radios that it has.
 */
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import basic.Constants;
import cards.CardView;
import cards.ComboCardView;
import extras.Debug;
import extras.RandomGenerator;

public class ComboPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6965341647940551092L;
	private int origX, origY;
	private int chooseX, chooseY;
	private RandomGenerator rgen;
	private JLabel statusLine1;
	private JLabel statusLine2;
	private boolean playerIsShakingBag;
	private boolean playerShouldStartShaking;
	private CardView comboCard;
	private ArrayList<ComboListener> listeners;
	private ComboFrame combo;
	private int cpOption = -1;

	/* Need to have a good shake pebble method
	 * that will have the pebbles randomly move in their locations
	 * yet still stay inside the bag.
	 */
	public ComboPanel(CardView cv, ComboFrame cf) {
		setupPanel(cv, cf);
	}

	private void setupPanel(CardView cv, ComboFrame cf) {
		comboCard = cv;
		combo = cf;
		rgen = RandomGenerator.getInstance();
		listeners = new ArrayList<ComboListener>();
		chooseX = 0;
		chooseY = 0;
		setLayout(new BorderLayout());

		setPreferredSize(new Dimension(Constants.HUGE_CARD_WIDTH, Constants.HUGE_CARD_HEIGHT));

		addMouseListener( new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				origX = e.getX();
				origY = e.getY();
			}

			public void mouseReleased(MouseEvent e) {
				int lastX = e.getX();
				int lastY = e.getY();
				
				Point p1 = new Point(origX, origY);
				Point p2 = new Point(lastX, lastY);
				
				int comboLineX = (getWidth()-(Constants.HUGE_CARD_WIDTH/2));
				if(Math.abs(origX - comboLineX) > Constants.DRAG_THRESHOLD/2 && Math.abs(lastX - comboLineX) > Constants.DRAG_THRESHOLD/2) {
					if(cpOption != -1) {
						fireComboDone(cpOption);
					}
				}
			}
		});
		
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				int comboLineX = (getWidth()-(Constants.HUGE_CARD_WIDTH/2));
				if(e.getY() > Constants.HUGE_CARD_HEIGHT/5 && e.getY() < 4*(Constants.HUGE_CARD_HEIGHT/5)){
					cpOption = 0;
					if(e.getX() > comboLineX) {
						cpOption = 1;
					}
				}else{
					cpOption = -1;
				}
				repaint();
			}
			
			public void mouseDragged(MouseEvent e) {
				int lastX = e.getX();
				int lastY = e.getY();
				
				Point p1 = new Point(origX, origY);
				Point p2 = new Point(lastX, lastY);
				int comboLineX = (getWidth()-(Constants.HUGE_CARD_WIDTH/2));
				if(p1.distance(p2) > Constants.DRAG_THRESHOLD/2 || Math.abs(lastX - comboLineX) < Constants.DRAG_THRESHOLD/2) {
					cpOption = -1;
					repaint();
				}
			}
		});
	}
	
	public void addListener(ComboListener cl) {
		listeners.add(cl);
	}
	
	public void paintComponent(Graphics g) {
		//comboCard.drawBigCard(g, getWidth(), getHeight());
		comboCard.drawBigCard(g, Constants.HUGE_CARD_WIDTH, Constants.HUGE_CARD_HEIGHT);
		if(cpOption >= 0) {
			ComboCardView.drawACircle(g, getWidth()-Constants.HUGE_CARD_WIDTH, 0, Constants.HUGE_CARD_WIDTH, Constants.HUGE_CARD_HEIGHT, cpOption);
		}
	}

	public void fireComboDone(int option) {
		Debug.println(option);
		for(ComboListener l:listeners) {
			l.comboCardDone(combo, comboCard, option);
		}
	}
}
