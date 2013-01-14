package extras;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.Timer;

import manipulatives.AssetView;
import manipulatives.ManCardPanel;
import basic.Constants;
import cards.CardView;

public class ManPanelHelp implements ActionListener {
	private ManCardPanel panel;
	private Timer timer;
	private int numTimesMoved;
	private AssetView cursor;
	private int curDx;
	private int curDy;
	private ArrayList<Integer> desiredXs;
	private ArrayList<Integer> desiredYs;
	private ArrayList<Boolean> upDownState;
	private int desiredIndex;

	public static final int VELOCITY = 0;
	public static final int MAX_MOVES = 60;

	public ManPanelHelp(ManCardPanel p, AssetView asset) {
		panel = p;
		cursor = asset;
		panel.disableControls();
		desiredXs = new ArrayList<Integer>();
		desiredYs = new ArrayList<Integer>();
		int w = (int) (p.getPreferredSize().getWidth());
		int h = (int) (p.getPreferredSize().getHeight());
		desiredXs.addAll(Arrays.asList(fract(w, 1, 17), fract(w,1,1.45), 	0, 		fract(w,1,2), fract(w,1,2), fract(w,1,5), fract(w,1,5), 	0, 			fract(w,1,8), fract(w,1,7), fract(w,1,5), fract(w,1,4), fract(w,1,3), fract(w,1,3), fract(w,1,9),    fract(w,1,3),  fract(w,1,3),  fract(w,1,1.75),  fract(w,1,1.75), fract(w,1,1.25)));
		desiredYs.addAll(Arrays.asList(fract(h,1, 1.5), fract(h,1,8), fract(h,1,2), fract(h,1,2), fract(h,1,2), fract(h,1,5), fract(h,1,5), fract(h,2.8,4), fract(h,1,10), fract(h,1,6), fract(h,1,3), fract(h,1,3), fract(h,1,4), fract(h,1,10), fract(h,1,10), fract(h,1,17), fract(h,1,17), fract(h,1,17),    fract(h,1,17),   fract(h,1,17)));
		upDownState = new ArrayList<Boolean>();
		upDownState.addAll(Arrays.asList(true, false, true, true, false, true, false, true, true, false, false, false, false, false, false, true, true, true, true, true, true));
		asset.setDesiredLocation(desiredXs.get(0), desiredYs.get(0));
		desiredIndex = 1;
		panel.setInitialHelp();
		timer = new Timer(Constants.ANIMATION_MS_PAUSE, this);
		timer.setInitialDelay(Constants.BETWEEN_GAME_PAUSE);
		timer.start();
	}
	
	private int fract(int base, double num, double den) {
		return (int) (((base)*num)/den);
	}

	public void setTimer(Timer t) {
		timer = t;
	}

	//@Override
	public void actionPerformed(ActionEvent e) {
		if(numTimesMoved >= 0) {
			incrementalMove(cursor);
			inPanelState();
			panel.repaint();
		}
		numTimesMoved++;
		if(numTimesMoved >= MAX_MOVES) {
			Debug.printlnVerbose("cursor ended at " + cursor.getX() + ", " + cursor.getY());
			if(desiredIndex >= desiredXs.size()) {
				completelyFinishTimer();
			}else{
				numTimesMoved = -1 * (MAX_MOVES/2);
				Debug.printlnVerbose("Setting desired location to: " + desiredXs.get(desiredIndex) + ", " + desiredYs.get(desiredIndex));
				cursor.setDesiredLocation(desiredXs.get(desiredIndex), desiredYs.get(desiredIndex));
				possiblySetImage();
				changePanelState();
				desiredIndex++;
			}
		}
	}
	
	private void possiblySetImage() {
		if(upDownState.get(desiredIndex)) {
			cursor.setImage(GameImages.getMouseCursor());
		}else{
			cursor.setImage(GameImages.getMouseButtonDown());
		}
	}
	
	private void changePanelState() {
		switch(desiredIndex) {
			case 1: panel.startLine(cursor.getX(), cursor.getY()); break;
			case 2: panel.endLine(cursor.getX(), cursor.getY()); break;
			case 3: panel.setPplMode(); break;
			case 4: panel.addAManip(cursor.getX(), cursor.getY()); numTimesMoved = MAX_MOVES/2; break;
			case 6: panel.addAManip(cursor.getX(), cursor.getY()); numTimesMoved = MAX_MOVES/2; break;
			case 7: panel.showPencilHelp(); break;
			case 8: panel.setFreeDrawMode(); break;
			case 9: panel.startFreeDraw(cursor.getX(), cursor.getY());
			case 10: case 11: case 12: case 13: case 14: numTimesMoved = 0; break;
			case 15: panel.showAnswerSection(); break;
			case 17: panel.showNotAWholeNumberSection(); break;
			case 19: panel.endFreeDraw(cursor.getX(), cursor.getY()); break;
		}
	}
	
	private void inPanelState() {
		switch(desiredIndex) {
			case 0: break;
			case 2: panel.modifyLine(cursor.getX(), cursor.getY()); Debug.printlnVerbose("modifying line: " + cursor.getX() + ", " + cursor.getY()); break;
			case 3: break;
			case 10: case 11: case 12: case 13: case 14: case 15: panel.modifyFreeDraw(cursor.getX(), cursor.getY()); break;
		}
	}

	private void completelyFinishTimer() {
		panel.reset();
		panel.enableControls();
		panel.repaint();
		numTimesMoved = 0;
		timer.stop();
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
