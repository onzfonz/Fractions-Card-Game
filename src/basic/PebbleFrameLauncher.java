package basic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import network.NetDelegate;
import pebblebag.IceCreamTruckView;
import deck.DeckView;
import extras.Debug;

public class PebbleFrameLauncher implements ActionListener {
	private int numTimesMoved = 0;
	private GamePanel gPanel;
	private Timer timer;
	private DeckView view;
	private NetDelegate netRep;
	
	public static final int VELOCITY = 0;
	public static final int MAX_MOVES = 0;
	
	public PebbleFrameLauncher(GamePanel gP, DeckView dv, NetDelegate nRep) {
		gPanel = gP;
		numTimesMoved = 0;
		view = dv;
		netRep = nRep;
	}
	
	public void setTimer(Timer t) {
		timer = t;
	}
	
	//@Override
	public void actionPerformed(ActionEvent e) {
		if(numTimesMoved == 0) {
			Debug.println("Launching Pebble Window");
		}
		Debug.println("numTimesMoved is: " + numTimesMoved);
		numTimesMoved++;
		if(numTimesMoved >= MAX_MOVES) {
			gPanel.repaint();
			numTimesMoved = 0;
			//stuff to have it finish completely
			timer.stop();
			completelyFinishTimer();
		}
	}
	
	private void completelyFinishTimer() {
		IceCreamTruckView pebWindow = new IceCreamTruckView(view, netRep, gPanel.parsePlayerTurnName());
		pebWindow.addBagListener(gPanel);
		gPanel.setPebbleWindow(pebWindow);
		gPanel.iceCreamTruckViewStarted(pebWindow);
	}
	
	private void restartTimer() {
		timer.setInitialDelay(Constants.BETWEEN_GAME_PAUSE/2);
		timer.start();
	}
	
	public double increment(double cur, int desired) {
		return Math.round((desired-cur)/(MAX_MOVES-numTimesMoved));
	}

}
