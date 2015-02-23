package tugstory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import extras.Debug;

public class TugPuller implements ActionListener {
	private Timer timer;
	private int ticks;
	private TugPanel tPanel;
	private boolean stillAnimating;
	private int numTugs;
	private int disparity;
	private int tugTicksInterval;
	private int numToFall;
	private int maxTicks;
	private int travelDistance;
	
	public static final int MAX_TICKS = 20000 / TugPanel.TUG_TICK;
	public static final int ADDITIONAL_TICKS = MAX_TICKS / 5;
	public static final int NUM_ROPE_MOVEMENTS = 10;
	public static final int TICKS_BW_TUGS = MAX_TICKS / NUM_ROPE_MOVEMENTS;
	
	public TugPuller(TugPanel panel) {
		ticks = 0;
		tPanel = panel;
		stillAnimating = true;
		numToFall = tPanel.calcNumFall();
		disparity = tPanel.calcDifference();
		travelDistance = (int) tPanel.centerToWinDistance();
		disparity = (disparity == 0)? 1:disparity;
		numTugs = Math.min((travelDistance / disparity)+1, NUM_ROPE_MOVEMENTS);
		//ropeTicksInterval = maxTicks / numTugs;
		//numTugs = Math.max(numTugs, numToFall);
		if(numTugs > (travelDistance / TICKS_BW_TUGS)+1) {
			numTugs = Math.max(numTugs, numToFall);
			tugTicksInterval = (int) (((((double)travelDistance) / numTugs)/(NUM_ROPE_MOVEMENTS))*TICKS_BW_TUGS);
			maxTicks = (numTugs * tugTicksInterval)-1;
		}else{
			maxTicks = (TICKS_BW_TUGS * numTugs);
			tugTicksInterval = (maxTicks / numToFall);
//			maxTicks--;
		}
		//Here what i need to do is figure out how many times I'm going to move the flag
		//and make sure that is some interval of how many people are going to fall down.
		//then I make sure that on every beat, I check to see if it's time to either make
		//them fall down or move the flag over.
		//think of when you only have one person falling
		//versus having 10 people falling.
	}
	
	public void setTimer(Timer t) {
		timer = t;
	}
	
	//@Override
	public void actionPerformed(ActionEvent e) {
		if(ticks == 0) {
			tPanel.initializeTug();
		}
		ticks++;
		if(ticks > maxTicks) {
			tPanel.makeAllJump();
			if(ticks > maxTicks + ADDITIONAL_TICKS) {
				closeAnimation();
			}
			return;
		}else{
			tPanel.tugAction();
		}
		if(ticks % TICKS_BW_TUGS == 0) {
			tPanel.moveFlagNextTime();
		}
		if(ticks % tugTicksInterval == 0) {
			tPanel.makeOneSetFall();
//			Debug.println("one set down");
		}
		if((ticks >= maxTicks || tPanel.contestOver()) && stillAnimating) {
			stopPulling();
		}
	}
	
	public void closeAnimation() {
		tPanel.tugPanelDone();
		timer.stop();
		Debug.println("Animation Done in the Tug Panel");
	}
	
	public void stopPulling() {
//		timer.stop();
		Debug.println("no longer pulling");
		stillAnimating = false;
		tPanel.endTugOfWar(false);
		ticks = maxTicks;
//		timer.start();
	}
}
