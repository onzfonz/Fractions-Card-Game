package manipulatives;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Timer;

import basic.Constants;
import deck.DeckView;
import extras.Debug;
import extras.GraphicUtils;

public class ManipMover implements ActionListener {
	private ManipPanelListener manPanel;
	private int numTimesMoved;
	private Timer timer;
	private List<ManipInterface> manipulatives;
	private DeckView manipsDeck;
	
	public static final int VELOCITY = 0;
	public static final int MAX_MOVES = 50;
	
	public ManipMover(ManipPanelListener p, List<ManipInterface> manips, DeckView deck) {
		manPanel = p;
		numTimesMoved = 0;
		manipulatives = manips;
		manipsDeck = deck;
	}
	
	public void setTimer(Timer t) {
		timer = t;
	}
	
	//@Override
	public void actionPerformed(ActionEvent e) {
		double curManX = 0, curManY = 0;
		if(numTimesMoved == 0) {
			//gPanel.disableUser();
		}
		for(int i = 0; i < manipulatives.size(); i++) {
			ManipInterface chosenManip = manipulatives.get(i);
			if(!chosenManip.isShadow()) {
				curManX = chosenManip.getX();
				curManY = chosenManip.getY();
				chosenManip.moveBy((int) increment(curManX, chosenManip.getDesiredX()), (int) increment(curManY, chosenManip.getDesiredY()));
			}
		}
		manPanel.repaint();
		numTimesMoved++;
		if(numTimesMoved >= MAX_MOVES) {
			Debug.println("manipmover ended at " + curManX + ", " + curManY);
			Debug.println("manipmoverdone with num stinky as: " + numStinky());
			completelyFinishTimer();
		}
	}
	
	private void completelyFinishTimer() {
		manPanel.repaint();
		numTimesMoved = 0;
		//stuff to have it finish completely
		timer.stop();
		manPanel.fireAnimationDone(manipsDeck);
	}
	
	private void restartTimer() {
		timer.setInitialDelay(Constants.BETWEEN_GAME_PAUSE/2);
		timer.start();
	}
	
	public double increment(double cur, int desired) {
		return GraphicUtils.incrementalMove(cur, desired, MAX_MOVES-numTimesMoved);
	}
	
	private int numStinky() {
		int num = 0;
		for(ManipInterface man:manipulatives) {
			if(man.isStinky()) {
				num++;
			}
		}
		return num;
	}
}
