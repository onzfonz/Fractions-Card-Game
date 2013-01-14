package basic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

import manipulatives.ManCardPanel;
import network.NetDelegate;
import cards.CardView;
import deck.DeckView;
import extras.Debug;
import extras.PanelListener;

public class ManFrameLauncher implements ActionListener {
	private String question;
	private ArrayList<ManCardPanel> frameList;
	private int numTimesMoved = 0;
	private GamePanel gPanel;
	private Timer timer;
	private DeckView view;
	private CardView card;
	private NetDelegate netRep;
	private boolean firstTime;
	
	public static final int VELOCITY = 0;
	public static final int MAX_MOVES = 0;
	
	public ManFrameLauncher(GamePanel gP, String q, ArrayList<ManCardPanel> manWindows, DeckView dv, CardView cv, NetDelegate nr, boolean firstTimeLaunched) {
		question = q;
		frameList = manWindows;
		frameList.add(null);
		gPanel = gP;
		numTimesMoved = 0;
		view = dv;
		card = cv;
		netRep = nr;
		firstTime = firstTimeLaunched;
	}
	
	public void setTimer(Timer t) {
		timer = t;
	}
	
	//@Override
	public void actionPerformed(ActionEvent e) {
		if(numTimesMoved == 0) {
			Debug.println("Launching Manipulation Window");
		}
		Debug.println("numTimesMoved is: " + numTimesMoved);
		numTimesMoved++;
		if(numTimesMoved >= MAX_MOVES) {
			Debug.println("Manipulation Window Finished");
			gPanel.repaint();
			numTimesMoved = 0;
			//stuff to have it finish completely
			timer.stop();
			completelyFinishTimer();
		}
	}
	
	private void completelyFinishTimer() {
		ManCardPanel manWindow = new ManCardPanel(question, view, card, netRep, gPanel, firstTime);
		PanelListener pl = gPanel.getPanelListener();
		pl.manViewCreated(manWindow);
		frameList.remove(null);
		frameList.add(manWindow);
		manWindow.addManListener(gPanel);
	}
	
	private void restartTimer() {
		timer.setInitialDelay(Constants.BETWEEN_GAME_PAUSE/2);
		timer.start();
	}
	
	public double increment(double cur, int desired) {
		return Math.round((desired-cur)/(MAX_MOVES-numTimesMoved));
	}

}
