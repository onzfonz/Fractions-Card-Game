package manipulatives;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Timer;

import basic.Constants;


public class PeopleTransformer implements ActionListener {
	private ManPanel mPanel;
	private int ticks;
	private Timer timer;
	private boolean stink;
	private List<ManModel> people;
	private int regions;
	private int numerator;
	private int answer;
	private int numTotal;
	
	public PeopleTransformer(ManPanel p, List<ManModel> ppl, int totalPeeps, int regions, int numerator, int answer, boolean isStinky) {
		mPanel = p;
		ticks = 0;
		stink = isStinky;
		people = ppl;
		numTotal = totalPeeps;
		this.regions = regions;
		this.numerator = numerator;
		this.answer = answer;
	}
	
	public void setTimer(Timer t) {
		timer = t;
	}
	
	//@Override
	public void actionPerformed(ActionEvent e) {
		if(ticks == 0) {
			//what to do at the start?
		}
		mPanel.transformAffectedPeople(stink);
		mPanel.repaint();
		ticks++;
		//theta += delta;
		if(ticks >= Constants.TRANSFORMER_TIMES) {
			completelyFinishTimer();
		}
	}
	
	private void completelyFinishTimer() {
		ticks = 0;
		timer.stop();
		mPanel.repaint();
		mPanel.launchPeopleResultAnimation(numTotal, regions, numerator, answer, stink);
		//launch another Animation, like the stinky animation
	}
	
}
