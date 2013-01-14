package tugstory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import manipulatives.ManipInterface;
import basic.Constants;
import basic.GamePanel;
import deck.DeckView;
import extras.Debug;
import extras.GraphicUtils;

public class TugPrepper implements ActionListener {
	private GamePanel manPanel;
	private int numTimesMoved;
	private Timer timer;
	private ArrayList<ManipInterface> myManips, theirManips;
	private List<DeckView> decks;
	private boolean firstTimeThrough;
	private boolean wasTold;
	
	public static final int VELOCITY = 0;
	public static final int MAX_MOVES = 100;
	
	public TugPrepper(GamePanel p, List<DeckView> allDecks, boolean toldTo) {
		manPanel = p;
		numTimesMoved = 0;
		firstTimeThrough = true;
		decks = allDecks;
		wasTold = toldTo;
		myManips = new ArrayList<ManipInterface>();
		theirManips = new ArrayList<ManipInterface>();
		setupManips();
		setupDesiredManipLocations();
		timer = new Timer(Constants.ANIMATION_MS_PAUSE, this);
		timer.setInitialDelay(Constants.BETWEEN_GAME_PAUSE);
		timer.start();
	}
	
	private void setupManips() {  //Only add manips that aren't stinky;
		for(DeckView dv: decks) {
			List<ManipInterface> manips = dv.getManipulatives();
			for(ManipInterface manip:manips) {
				if(!manip.isStinky()) {
					if(dv.getPlayer().isHuman()) {
						myManips.add(manip);
					}else{
						theirManips.add(manip);
					}
				}
			}
		}
	}
	
	private void setupDesiredManipLocations() {
		TugUtils.setDesiredAssetLocations(manPanel, myManips, theirManips);
	}
	
	public void setTimer(Timer t) {
		timer = t;
	}
	
	//@Override
	public void actionPerformed(ActionEvent e) {
		if(numTimesMoved == 0) {
			//gPanel.disableUser();
		}
		if(firstTimeThrough) {
			for(ManipInterface chosenManip: myManips) {
				chosenManip.moveBy((int) increment(chosenManip.getX(), chosenManip.getDesiredX()), (int) increment(chosenManip.getY(), chosenManip.getDesiredY()));
			}
			for(ManipInterface chosenManip: theirManips) {
				chosenManip.moveBy((int) increment(chosenManip.getX(), chosenManip.getDesiredX()), (int) increment(chosenManip.getY(), chosenManip.getDesiredY()));
			}
			manPanel.repaint();
		}
		numTimesMoved++;
		if(numTimesMoved >= MAX_MOVES) {
			if(firstTimeThrough) {
				firstTimeThrough = false;
				numTimesMoved = 0;
			}else{
				completelyFinishTimer();
			}
		}
	}
	
	private void completelyFinishTimer() {
		manPanel.repaint();
		numTimesMoved = 0;
		//stuff to have it finish completely
		timer.stop();
		Debug.println("Tug Prepper is finished");
		manPanel.manipsReadyToTug(wasTold);
	}
	
	public double increment(double cur, int desired) {
		return GraphicUtils.incrementalMove(cur, desired, MAX_MOVES-numTimesMoved);
	}
}
