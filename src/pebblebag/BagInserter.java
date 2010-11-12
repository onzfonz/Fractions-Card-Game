package pebblebag;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

import cards.CardGameConstants;

public class BagInserter implements ActionListener {
	private PebblePanel pPanel;
	private int numTimesMoved;
	private Timer timer;
	private int desiredX;
	private int desiredY;
	
	public static final int VELOCITY = 0;
	public static final int MAX_MOVES = 48;
	
	public BagInserter(PebblePanel p) {
		pPanel = p;
		numTimesMoved = 0;
	}
	
	public void setTimer(Timer t) {
		timer = t;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(numTimesMoved == 0) {
			desiredX = pPanel.getInsideBagX();
			desiredY = pPanel.getInsideBagY();
			if(CardGameConstants.DEBUG_MODE) {
				System.out.println("Moving to: " + desiredX + ", " + desiredY);
			}
			pPanel.makeAnOutsidePebbleCurrent();
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
			pPanel.repaint();
			numTimesMoved = 0;
			//stuff to have it finish completely
			timer.stop();
			if(pPanel.morePebblesNeedToBeMovedIn()) {
				restartTimer();
			}else{
				completelyFinishTimer();
			}
		}
	}
	
	private void completelyFinishTimer() {
		pPanel.resetBag();
		pPanel.repaint();
		pPanel.startInitialShaking();
	}
	
	private void restartTimer() {
		timer.setInitialDelay(CardGameConstants.MINI_GAME_PAUSE);
		timer.start();
	}
	
	public double increment(double cur, int desired) {
		return Math.round((desired-cur)/(MAX_MOVES-numTimesMoved));
	}

}
