package pebblebag;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Timer;

import manipulatives.AssetView;
import manipulatives.ManipInterface;
import manipulatives.ManipPanelListener;
import basic.Constants;
import cards.CardView;
import deck.DeckView;
import extras.Debug;
import extras.GraphicUtils;

public class TruckMover implements ActionListener {
	private ManipPanelListener manPanel;
	private int numTimesMoved;
	private Timer timer;
	private List<ManipInterface> manipulatives;
	private DeckView manipsDeck;
	private AssetView iceCreamTruck;
	private int xStart;

	public static final int VELOCITY = 0;
	public static final int MAX_MOVES = 120;

	public TruckMover(ManipPanelListener p, List<ManipInterface> manips, DeckView deck, AssetView truck) {
		manPanel = p;
		numTimesMoved = 0;
		manipulatives = manips;
		manipsDeck = deck;
		iceCreamTruck = truck;
		CardView team = manipsDeck.getTeammateCard();
		xStart = team.getX() + manipulatives.get(0).getX() + team.getWidth()*3;
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
		if(iceCreamTruck.getX() > xStart) {
			for(int i = 0; i < manipulatives.size(); i++) {
				ManipInterface chosenManip = manipulatives.get(i);
				if(!chosenManip.isShadow()) {
					curManX = chosenManip.getX();
					curManY = chosenManip.getY();
					chosenManip.moveBy((int) increment(curManX, chosenManip.getDesiredX()), (int) increment(curManY, chosenManip.getDesiredY()));
				}
			}
		}
		manPanel.tickPassedInTruckMover(manipsDeck, numTimesMoved);
		manPanel.repaint();
		numTimesMoved++;
		if(numTimesMoved >= MAX_MOVES) {
			Debug.println("truckmover ended at " + curManX + ", " + curManY);
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
}
