package manipulatives;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import basic.Constants;


public class PageDivider implements ActionListener {
	private ManPanel mPanel;
	private int numLinesDrawn;
	private int numLinesNeeded;
	private Timer timer;
	private DoublePoint center;
	private double r;
	private double theta;
	private int ppl;
	private int numerator;
	private int answer;
	
	public PageDivider(ManPanel p, int den, int ppl, int numer, int answer) {
		mPanel = p;
		numLinesDrawn = 0;
		numLinesNeeded = den;
		this.ppl = ppl;
		numerator = numer;
		this.answer = answer;
	}
	
	public void setTimer(Timer t) {
		timer = t;
	}
	
	//@Override
	public void actionPerformed(ActionEvent e) {
		if(numLinesDrawn == 0) {
			center = mPanel.getCenter();
			r = mPanel.calculateLineLength();
			theta = mPanel.calculateTheta(numLinesNeeded);
		}
		mPanel.addALine(numLinesDrawn, theta, r, center);
		mPanel.repaint();
		numLinesDrawn++;
		if(numLinesDrawn >= numLinesNeeded) {
			completelyFinishTimer();
		}
	}
	
	private void completelyFinishTimer() {
		numLinesDrawn = 0;
		timer.stop();
		mPanel.repaint();
		mPanel.launchPeopleAddAnimation(ppl, numLinesNeeded, numerator, answer);
	}
	
	private void restartTimer() {
		timer.setInitialDelay(Constants.MINI_GAME_PAUSE);
		timer.start();
	}
}
