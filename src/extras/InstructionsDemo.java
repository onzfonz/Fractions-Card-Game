package extras;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import manipulatives.AssetView;
import basic.Constants;
import basic.GamePanel;
import cards.CardView;
import deck.DeckView;

public class InstructionsDemo implements ActionListener {
	private GamePanel panel;
	private Timer timer;
	private int numTimesMoved;
	private AssetView cursor;
	private boolean clicked;
	private boolean extraCycle;
	private int origX;
	private int origY;
	private CardView cardMoved;
	private DeckView deckOn;
	private int curDx;
	private int curDy;
	private boolean isMyTurn;

	public static final int VELOCITY = 0;
	public static final int MAX_MOVES = 60;

	public InstructionsDemo(GamePanel p, AssetView asset, CardView card, boolean turn) {
		panel = p;
		cursor = asset;
		cardMoved = card;
		origX = card.getX();
		origY = card.getY();
		isMyTurn = turn;
		timer = new Timer(Constants.ANIMATION_MS_PAUSE, this);
		timer.setInitialDelay(Constants.BETWEEN_GAME_PAUSE);
		timer.start();
	}

	public void setTimer(Timer t) {
		timer = t;
	}

	//@Override
	public void actionPerformed(ActionEvent e) {
		double curManX = 0, curManY = 0;
		if(numTimesMoved >= 0) {
			incrementalMove(cursor);
			if(clicked) {
				incrementalMove(cardMoved, cursor);
			}
			panel.repaint();
		}
		if(numTimesMoved > ((MAX_MOVES/4)*3) && clicked) {
			deckOn.setHighlighted(true, true);
		}
		if(deckOn != null && deckOn.isHighlighted() && extraCycle) {
			extraCycle = false;  //extraCycle to slow down mouse animation at the end
		}else{
			numTimesMoved++;
			extraCycle = true;
		}
		if(numTimesMoved >= MAX_MOVES) {
			Debug.println("instructions demo ended at " + curManX + ", " + curManY);
			if(clicked) {
				completelyFinishTimer();
			}else{
				numTimesMoved = -1 * (MAX_MOVES/2);
				deckOn = panel.getAnOpponentDeck(cardMoved);
				int x = panel.getWidth()/2;
				int y = panel.getHeight()/4;
				if(deckOn != null) {
					x = deckOn.getX();
					y = deckOn.getY();
				}
				cardMoved.setHighlighted(true);
				cursor.setDesiredLocation(x, y);
				cursor.setImage(GameImages.getMouseButtonDown());
				clicked = true;
			}
		}
	}

	private void completelyFinishTimer() {
		panel.clearHelp();
		cardMoved.setLocation(origX, origY);
		cardMoved.setHighlighted(false);
		deckOn.setHighlighted(false, false);
		panel.repaint();
		numTimesMoved = 0;
		//stuff to have it finish completely
		timer.stop();
		panel.setTurn(isMyTurn);
	}

	private void restartTimer() {
		timer.setInitialDelay(Constants.BETWEEN_GAME_PAUSE/2);
		timer.start();
	}
	
	public void incrementalMove(AssetView asset) {
		curDx = (int) increment(asset.getX(), asset.getDesiredX());
		curDy = (int) increment(asset.getY(), asset.getDesiredY());
		asset.moveBy((int) increment(asset.getX(), asset.getDesiredX()), (int) increment(asset.getY(), asset.getDesiredY()));
	}
	
	public void incrementalMove(CardView cv, AssetView asset) {
		cv.moveBy(curDx, curDy);
	}

	public double increment(double cur, int desired) {
		return GraphicUtils.incrementalMove(cur, desired, MAX_MOVES-numTimesMoved);
	}
}
