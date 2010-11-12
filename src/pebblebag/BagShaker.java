package pebblebag;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class BagShaker implements ActionListener {
	private Timer timer;
	private int numShakes;
	private PebblePanel pPanel;
	
	public static final int MAX_SHAKES = 100;
	
	public BagShaker(PebblePanel panel) {
		numShakes = 0;
		pPanel = panel;
	}
	
	public void setTimer(Timer t) {
		timer = t;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(numShakes == 0) {
			pPanel.setChooseCoords();
		}
		pPanel.compShakeBag();
		numShakes++;
		if(numShakes > MAX_SHAKES) {
			numShakes = 0;
			pPanel.doneShakingTheBag(false);
			pPanel.turnOnUser();
			timer.stop();
		}
	}
}
