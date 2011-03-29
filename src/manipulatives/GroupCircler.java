package manipulatives;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import basic.Constants;


public class GroupCircler implements ActionListener {
	private ManPanel mPanel;
	private int numShapesAdded;
	private int numShapesNeeded;
	private int sections;
	private Timer timer;
	private DoublePoint center;
	private double theta;
	private int answer;
	private int people;
	private double r;
	
	public GroupCircler(ManPanel p, int ppl, int den, int numer, int ans) {
		mPanel = p;
		numShapesAdded = 0;
		numShapesNeeded = numer;
		sections = den;
		answer = ans;
		people = ppl;
	}
	
	public void setTimer(Timer t) {
		timer = t;
	}
	
	//@Override
	public void actionPerformed(ActionEvent e) {
		if(numShapesAdded == 0) {
			center = mPanel.getCenter();
			r = mPanel.calculateLineLength();
			theta = mPanel.calculateTheta(sections);
			if(answer == -1) {
				completelyFinishTimer(false);
				return;
			}
		}
		mPanel.addACircle(numShapesAdded, theta, r, center, sections);
		mPanel.repaint();
		numShapesAdded++;
		if(numShapesAdded >= numShapesNeeded) {
			completelyFinishTimer(true);
		}
	}
	
	private void completelyFinishTimer(boolean gaveAnswer) {
		numShapesAdded = 0;
		timer.stop();
		if(gaveAnswer) {
			mPanel.displayMessage(Constants.MAN_MSG_HOW_MANY);
		}
		mPanel.repaint();
		mPanel.enableControls();
	}
	
	private void restartTimer() {
		timer.setInitialDelay(Constants.MINI_GAME_PAUSE);
		timer.start();
	}
}
