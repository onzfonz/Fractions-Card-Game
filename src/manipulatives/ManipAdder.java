package manipulatives;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import basic.Constants;


public class ManipAdder implements ActionListener {
	private ManPanel mPanel;
	private int numPeopleAdded;
	private int numPeopleNeeded;
	private int sections;
	private Timer timer;
	private DoublePoint center;
	private double theta;
	private int answer;
	private int numerator;
	
	public ManipAdder(ManPanel p, int num, int den, int numer, int ans) {
		mPanel = p;
		numPeopleAdded = 0;
		numPeopleNeeded = num;
		sections = den;
		answer = ans;
		numerator = numer;
	}
	
	public void setTimer(Timer t) {
		timer = t;
	}
	
	//@Override
	public void actionPerformed(ActionEvent e) {
		if(numPeopleAdded == 0) {
			center = mPanel.getCenter();
			theta = mPanel.calculateTheta(sections);
		}
		mPanel.addAManip(numPeopleAdded, theta, center, sections);
		mPanel.repaint();
		numPeopleAdded++;
		if(numPeopleAdded % 8 == 0) {
			int delay = timer.getDelay();
			timer.setDelay((int) (delay * .8));
		}
		if(numPeopleAdded >= numPeopleNeeded) {
			completelyFinishTimer();
		}
	}
	
	private void completelyFinishTimer() {
		numPeopleAdded = 0;
		timer.stop();
		mPanel.repaint();
		mPanel.launchCirclerAnimation(numPeopleNeeded, sections, numerator, answer);
	}
	
	private void restartTimer() {
		timer.setInitialDelay(Constants.MINI_GAME_PAUSE);
		timer.start();
	}
}
