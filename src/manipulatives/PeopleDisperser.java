package manipulatives;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Timer;

import pebblebag.TugImages;

import basic.Constants;


public class PeopleDisperser implements ActionListener {
	private ManPanel mPanel;
	private int ticks;
	private Timer timer;
	private boolean stink;
	
	public PeopleDisperser(ManPanel p, boolean isStinky) {
		mPanel = p;
		ticks = 0;
		stink = isStinky;
	}
	
	public void setTimer(Timer t) {
		timer = t;
	}
	
	//@Override
	public void actionPerformed(ActionEvent e) {
		if(ticks == 0) {
			//what to do at the start?
		}
		mPanel.moveAffectedPeople(stink);
		mPanel.repaint();
		ticks++;
		//theta += delta;
		if(ticks >= Constants.DISPERSER_TIMES) {
			completelyFinishTimer();
		}
	}
	
	private void completelyFinishTimer() {
		ticks = 0;
		timer.stop();
		mPanel.repaint();
		mPanel.fireAnimationDone();
		//launch another Animation, like the stinky animation
	}
	
}
