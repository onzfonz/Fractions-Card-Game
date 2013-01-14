package pebblebag;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import basic.Constants;
import extras.Debug;


public class PebbleMover implements ActionListener {
	private PebblePanel pPanel;
	private int numTimesMoved;
	private Timer timer;
	private int desiredX;
	private int desiredY;
	private PebbleView chosenPebble;
	private boolean isOrange;
	
	public static final int VELOCITY = 0;
	public static final int MAX_MOVES = 50;
	
	public PebbleMover(PebblePanel p, boolean type) {
		pPanel = p;
		isOrange = type;
		numTimesMoved = 0;
	}
	
	public void setTimer(Timer t) {
		timer = t;
	}
	
	//@Override
	public void actionPerformed(ActionEvent e) {
		if(numTimesMoved == 0) {
			//pPanel.doneShakingTheBag(true);
			if(Constants.NETWORK_MODE) {
				chosenPebble = pPanel.oppoSelectedPebble(isOrange);
			}else {
				chosenPebble = pPanel.compSelectPebble();
			}
			desiredX = pPanel.getFuturePebbleX();
			desiredY = pPanel.getFuturePebbleY(desiredX);
			Debug.println("Moving " + chosenPebble.isOrange() + " to: " + desiredX + ", " + desiredY);
		}
		double curPebX = pPanel.getPebbleX();
		double curPebY = pPanel.getPebbleY();
		pPanel.movePebbleBy(increment(curPebX, desiredX), increment(curPebY, desiredY));
		pPanel.repaint();
		numTimesMoved++;
		if(numTimesMoved >= MAX_MOVES) {
			Debug.println("pebble " + chosenPebble.isOrange() + " ended at " + curPebX + ", " + curPebY);
			pPanel.revealPebble(chosenPebble);
			pPanel.repaint();
			numTimesMoved = 0;
			//stuff to have it finish completely
			timer.stop();
			if(pPanel.morePebblesNeedToBeRemoved()) {
				if(!Constants.NETWORK_MODE) {
					restartTimer();
				}else{
					pPanel.pebbleMoverDone();
				}
			}else{
				Debug.println("finished Timer without releasing");
				completelyFinishTimer();
			}
		}
	}
	
	public void choosePebbleDebug() {
		Debug.println("choosePebbleDebug: " + pPanel.oppoSelectedPebble(isOrange).isOrange());
	}
	
	private void completelyFinishTimer() {
		pPanel.turnOnUser();
	}
	
	private void restartTimer() {
		timer.setInitialDelay(Constants.BETWEEN_GAME_PAUSE/2);
		timer.start();
	}
	
	public double increment(double cur, int desired) {
		return Math.round((desired-cur)/(MAX_MOVES-numTimesMoved));
	}

}
