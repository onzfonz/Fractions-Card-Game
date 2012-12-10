package deck;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import basic.Constants;
import basic.Player;
import extras.Debug;

public class FreshPoofer implements ActionListener {
	private int numTicks;
	private Timer timer;
	private DeckView chosenDeck;
	private Player player;
	private int timesDrawn;
	
	public static final int VELOCITY = 0;
	public static final int MAX_MOVES = 100;
	
	public FreshPoofer(Player p, DeckView dv) {
		chosenDeck = dv;
		player = p;
		numTicks = 0;
		timesDrawn = 0;
	}
	
	public void setTimer(Timer t) {
		timer = t;
	}
	
	//@Override
	public void actionPerformed(ActionEvent e) {
		if(numTicks == 0) {
			///player.fireAnimationStarted();
			Debug.println("fresh poof launched");
//			Debug.println("Moving to: " + desiredX + ", " + desiredY);
			chosenDeck.beginFreshenedGraphic(MAX_MOVES);
		}
		if(numTicks > timesDrawn) return;
		numTicks++;
		chosenDeck.advanceFreshenedGraphic();
		player.fireDeckRepaint(chosenDeck);
		if(numTicks >= MAX_MOVES) {
//			Debug.println("pebble ended at " + curCardX + ", " + curCardY);
			numTicks = 0;
			//stuff to have it finish completely
			timer.stop();
			completelyFinishTimer();
		}
	}
	
	public void graphicDrawn() {
		timesDrawn++;
	}
	
	private void completelyFinishTimer() {
		//player.fireAnimationEnded();
		chosenDeck.endFreshenedGraphic();
	}
	
	private void restartTimer() {
		timer.setInitialDelay(Constants.BETWEEN_GAME_PAUSE/2);
		timer.start();
	}
}
