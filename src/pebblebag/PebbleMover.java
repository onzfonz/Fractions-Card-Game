package pebblebag;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import cards.CardGameConstants;

public class PebbleMover implements ActionListener {
	private PebblePanel pPanel;
	private int numTimesMoved;
	private Timer timer;
	private int desiredX;
	private int desiredY;
	private PebbleView chosenPebble;
	
	public static final int VELOCITY = 0;
	public static final int MAX_MOVES = 50;
	
	public PebbleMover(PebblePanel p) {
		pPanel = p;
		numTimesMoved = 0;
	}
	
	public void setTimer(Timer t) {
		timer = t;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(numTimesMoved == 0) {
			pPanel.doneShakingTheBag(true);
			chosenPebble = pPanel.compSelectPebble();
			desiredX = pPanel.getFuturePebbleX();
			desiredY = pPanel.getFuturePebbleY(desiredX);
			if(CardGameConstants.DEBUG_MODE) {
				System.out.println("Moving to: " + desiredX + ", " + desiredY);
			}
		}
		double curPebX = pPanel.getPebbleX();
		double curPebY = pPanel.getPebbleY();
		pPanel.movePebbleBy(increment(curPebX, desiredX), increment(curPebY, desiredY));
		pPanel.repaint();
		numTimesMoved++;
		if(numTimesMoved >= MAX_MOVES) {
			if(CardGameConstants.DEBUG_MODE) {
				System.out.println("pebble ended at " + curPebX + ", " + curPebY);
			}
			pPanel.revealPebble(chosenPebble);
			pPanel.repaint();
			numTimesMoved = 0;
			//stuff to have it finish completely
			timer.stop();
			if(pPanel.morePebblesNeedToBeRemoved()) {
				restartTimer();
			}else{
				completelyFinishTimer();
			}
		}
	}
	
	private void completelyFinishTimer() {
		pPanel.turnOnUser();
	}
	
	private void restartTimer() {
		timer.setInitialDelay(CardGameConstants.BETWEEN_GAME_PAUSE/2);
		timer.start();
	}
	
	public double increment(double cur, int desired) {
		return Math.round((desired-cur)/(MAX_MOVES-numTimesMoved));
	}

}
