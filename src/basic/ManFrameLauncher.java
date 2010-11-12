package basic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

import manipulatives.ManFrame;
import cards.CardGameConstants;
import cards.CardView;
import deck.DeckView;

public class ManFrameLauncher implements ActionListener {
	private String question;
	private ArrayList<ManFrame> frameList;
	private int numTimesMoved = 0;
	private GamePanel gPanel;
	private Timer timer;
	private DeckView view;
	private CardView card;
	
	public static final int VELOCITY = 0;
	public static final int MAX_MOVES = 0;
	
	public ManFrameLauncher(GamePanel gP, String q, ArrayList<ManFrame> manWindows, DeckView dv, CardView cv) {
		question = q;
		frameList = manWindows;
		gPanel = gP;
		numTimesMoved = 0;
		view = dv;
		card = cv;
	}
	
	public void setTimer(Timer t) {
		timer = t;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(numTimesMoved == 0) {
			if(CardGameConstants.DEBUG_MODE) {
				System.out.println("Launching Manipulation Window");
			}
		}
		if(CardGameConstants.DEBUG_MODE) {
			System.out.println("numTimesMoved is: " + numTimesMoved);
		}
		numTimesMoved++;
		if(numTimesMoved >= MAX_MOVES) {
			if(CardGameConstants.DEBUG_MODE) {
				System.out.println("Manipulation Window Finished");
			}
			gPanel.repaint();
			numTimesMoved = 0;
			//stuff to have it finish completely
			timer.stop();
			completelyFinishTimer();
		}
	}
	
	private void completelyFinishTimer() {
		ManFrame manWindow = new ManFrame(question, view, card);
		manWindow.requestFocus();
		frameList.add(manWindow);
		manWindow.addManListener(gPanel);
	}
	
	private void restartTimer() {
		timer.setInitialDelay(CardGameConstants.BETWEEN_GAME_PAUSE/2);
		timer.start();
	}
	
	public double increment(double cur, int desired) {
		return Math.round((desired-cur)/(MAX_MOVES-numTimesMoved));
	}

}
