package manipulatives;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Timer;

import extras.GameImages;


import basic.Constants;


public class PeopleRevealer implements ActionListener {
	private ManPanel mPanel;
	private int ticks;
	private Timer timer;
	private int alpha;
	private double alphaDx;
	
	private static int MAX_TICKS = 100;
	
	public PeopleRevealer(ManPanel p) {
		mPanel = p;
		ticks = 0;
		alpha = 255;
		alphaDx = alpha / (double) MAX_TICKS;
	}
	
	public void setTimer(Timer t) {
		timer = t;
	}
	
	//@Override
	public void actionPerformed(ActionEvent e) {
		if(ticks == 0) {
			
		}
		alpha = Math.max(alpha, 0);
		mPanel.setManipsAlpha(alpha);
		alpha -= alphaDx;
		mPanel.repaint();
		ticks++;
		//theta += delta;
		if(ticks >= MAX_TICKS || alpha < 0) {
			completelyFinishTimer();
		}
	}
	
	private void completelyFinishTimer() {
		ticks = 0;
		timer.stop();
		mPanel.repaint();
		mPanel.launchPeopleRevealedAnimation();
		//launch another Animation, like the stinky animation
	}
	
}
