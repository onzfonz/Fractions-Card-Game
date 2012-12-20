package cards;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

import basic.GamePanel;
import deck.DeckView;
import extras.Debug;
import extras.GraphicUtils;

public class CardDiscarder implements ActionListener {
	private GamePanel gamePanel;
	private int numTimesMoved;
	private Timer timer;
	private ArrayList<CardView> cardsTossed;
	private DeckView manipsDeck;
	
	public static final int VELOCITY = 0;
	public static final int MAX_MOVES = 50;
	
	public CardDiscarder(GamePanel p, ArrayList<CardView> cards, DeckView deck) {
		gamePanel = p;
		numTimesMoved = 0;
		cardsTossed = cards;
		manipsDeck = deck;
		if(cardsTossed == null) {
			cardsTossed = deck.getAllCards();
		}
	}
	
	public void setTimer(Timer t) {
		timer = t;
	}
	
	//@Override
	public void actionPerformed(ActionEvent e) {
		double curManX = 0, curManY = 0;
		if(numTimesMoved == 0) {

		}
		for(int i = 0; i < cardsTossed.size(); i++) {
			CardView card = cardsTossed.get(i);
			curManX = card.getX();
			curManY = card.getY();
			card.moveBy((int) increment(curManX, gamePanel.calcDiscardPileX(card.getWidth())), (int) increment(curManY, gamePanel.calcDiscardPileY(card.getHeight())));
		}
		gamePanel.repaint();
		numTimesMoved++;
		if(numTimesMoved >= MAX_MOVES) {
			Debug.println("pebble ended at " + curManX + ", " + curManY);
			completelyFinishTimer();
		}
	}
	
	private void completelyFinishTimer() {
		gamePanel.repaint();
		numTimesMoved = 0;
		//stuff to have it finish completely
		timer.stop();
		gamePanel.fireCardDiscardAnimationDone(cardsTossed, manipsDeck, cardsTossed.size() == manipsDeck.getAllCards().size());
	}
	
	public double increment(double cur, int desired) {
		return GraphicUtils.incrementalMove(cur, desired, MAX_MOVES-numTimesMoved);
	}
}
