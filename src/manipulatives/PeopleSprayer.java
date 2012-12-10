package manipulatives;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Timer;

import extras.GameImages;


import basic.Constants;


public class PeopleSprayer implements ActionListener {
	private ManPanel mPanel;
	private int ticks;
	private int regions;
	private Timer timer;
	private DoublePoint center;
	private double r;
	private double deltaR;
	private double theta;
	private double delta;
	private int ppl;
	private int numerator;
	private int answer;
	private boolean stink;
	private BufferedImage stinkBomb;
		
	public PeopleSprayer(ManPanel p, int total, int num, int den, int numAffected, boolean isStinky) {
		mPanel = p;
		ticks = 0;
		ppl = total;
		numerator = num;
		regions = den;
		answer = numAffected;
		stink = isStinky;
		stinkBomb = GameImages.getStinkOrAir(isStinky);
	}
	
	public void setTimer(Timer t) {
		timer = t;
	}
	
	//@Override
	public void actionPerformed(ActionEvent e) {
		if(ticks == 0) {
			mPanel.addItem(stink, stinkBomb);
			center = mPanel.getCenter();
			r = mPanel.calculateShortestLineLengthFromCenter();
			deltaR = r / (Constants.REVOLUTIONS * Constants.PARTS_PER_REVOLUTION);
			//theta = mPanel.calculateTheta(regions);
			delta = 360.0/Constants.PARTS_PER_REVOLUTION;
			theta = delta;
			r = 0;
		}
		mPanel.moveItem(ticks, theta, r, center);
		mPanel.repaint();
		ticks++;
		//theta += delta;
		r += deltaR;
		if(ticks >= Constants.REVOLUTIONS * Constants.PARTS_PER_REVOLUTION) {
			completelyFinishTimer();
		}
	}
	
	private void completelyFinishTimer() {
		ticks = 0;
		timer.stop();
		mPanel.removeItem();
		mPanel.repaint();
		//launch another Animation, like the stinky animation
		mPanel.launchPeopleTransformAnimation(ppl, regions, numerator, answer, stink);
	}
	
	private void restartTimer() {
		timer.setInitialDelay(Constants.MINI_GAME_PAUSE);
		timer.start();
	}
}
