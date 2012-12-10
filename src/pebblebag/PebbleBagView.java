package pebblebag;

import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import basic.Constants;
import basic.PebbleBag;
import cards.TrickCard;
import extras.GameImages;
import extras.RandomGenerator;

public class PebbleBagView {
	private PebbleBag bag;
	private int numTurns;
	private int numRadios;
	private ArrayList<PebbleView> pebbles;
	private int curIndex;
	private int x;
	private int y;
	private boolean bagNeedsToBeShaked;
	private RandomGenerator rgen;
	private JPanel outerPanel;
	
	public PebbleBagView(TrickCard t, int nRadios, JPanel p) {
		this(t, nRadios, 0, 0, p);
	}
	
	public PebbleBagView(TrickCard t, int nRadios, int xCoord, int yCoord, JPanel outerP) {
		bag = new PebbleBag(t);
		numRadios = nRadios;
		numTurns = maxTurns();
		pebbles = new ArrayList<PebbleView>();
		bagNeedsToBeShaked = true;
		x = xCoord;
		y = yCoord;
		rgen = RandomGenerator.getInstance();
		outerPanel = outerP;
		curIndex = pebbles.size()-1;
		makeNPebbleViews(true, bag.getnumOrangePebbles());
		makeNPebbleViews(false, bag.getnumPurplePebbles());
		//reAssignLocations();
		placePebblesOutside();
	}
	
	public int numPebbles() {
		return bag.numPebbles();
	}
	
	private void makeNPebbleViews(boolean isOrange, int num) {
		for(int i = 0; i < num; i++) {
			pebbles.add(new PebbleView(isOrange));
		}
	}
	
	public void shuffleBag() {
		movePebblesRandomly();
	}
	
	public PebbleView nextPebble() {
		curIndex++;
		if(curIndex >= pebbles.size()) {
			curIndex = 0;
		}
		return pebbles.get(curIndex);
	}
	
	public PebbleView getOutsidePebble() {
		for(PebbleView pv:pebbles) {
			if(!pv.insideOfBag()) {
				return pv;
			}
		}
		return null;
	}
	
	public void resetBag() {
		randomlyAssignPebblesCoords();
		reAssignLocations();
		hidePebbles();
		numTurns = maxTurns();
	}
	
	public boolean bagNeedsShaking() {
		return bagNeedsToBeShaked;
	}
	
	public boolean timeToDrawAPebble() {
		return !bagNeedsToBeShaked && numTurns > 0;
	}
	
	public void doneShakingTheBag() {
		bagNeedsToBeShaked = false;
	}
	
	public boolean updateTurnCount() {
		if(pebblesStillNeedToBeDrawn()) {
			numTurns--;
		}
		return pebblesStillNeedToBeDrawn();
	}
	
	public boolean pebblesStillNeedToBeDrawn() {
		return numTurns > 0;
	}
	
	public boolean allPebblesInside() {
		return getOutsidePebble() == null;
	}
	
	public int maxTurns() {
		return numRadios+1;
	}
	
	public int numTurnsLeft() {
		return numTurns;
	}
	
	public PebbleView choosePebbleFromBag() {
		if(numHidden() == 0) {
			return null;
		}
		ArrayList<PebbleView> hidPebs = getPebblesStillHidden();
		return drawRandomPebble(hidPebs);
	}
	
	public PebbleView choosePebbleFromBag(boolean isOrange) {
		if(numLeft(isOrange) == 0) {
			return null;
		}
		ArrayList<PebbleView> hidPebs = getPebblesStillHidden(isOrange);
		return drawRandomPebble(hidPebs);
	}
	
	public PebbleView drawRandomPebble(ArrayList<PebbleView> pebs) {
		return pebs.get(rgen.nextInt(0, pebs.size()-1));
	}
	
	/*Assumes method will not be called if pebbles have not been placed outside*/
	public boolean didIceCreamTruckWork() {
		boolean didRun = true;
		for(PebbleView pv:pebbles) {
			//Debug.println("Pebble: " + pv.kidsAreSafe());
			if(pv.kidsAreSafe()) {
				didRun = false;
			}
		}
		return didRun && pebblesHaveBeenDrawn();
	}
	
	private boolean pebblesHaveBeenDrawn() {
		return pebbles.size() != numHidden();
	}
	
	private int numHidden() {
		ArrayList<PebbleView> hidPebs = getPebblesStillHidden();
		return hidPebs.size();
	}
	
	private int numLeft(boolean isOrange) {
		ArrayList<PebbleView> hidPebs = getPebblesStillHidden(isOrange);
		return hidPebs.size();
	}
	
	private ArrayList<PebbleView> getPebblesStillHidden() {
		ArrayList<PebbleView> hidPebs = new ArrayList<PebbleView>();
		for(PebbleView pv:pebbles) {
			if(pv.isHidden()) {
				hidPebs.add(pv);
			}
		}
		return hidPebs;
	}
	
	private ArrayList<PebbleView> getPebblesStillHidden(boolean isOrange) {
		ArrayList<PebbleView> hidPebs = new ArrayList<PebbleView>();
		for(PebbleView pv:pebbles) {
			if(pv.isOrange() == isOrange && pv.isHidden()) {
				hidPebs.add(pv);
			}
		}
		System.out.println(hidPebs);
		return hidPebs;
	}
	
	public void moveBy(int dx, int dy) {
		x += dx;
		y += dy;
	}
	
	public int getFutureX() {
		return getFutureX(outerPanel);
	}
	
	public int getFutureX(JPanel p) {
		if(inLeftHalf(p)) {
			return x + getWidth() + Constants.PEBBLE_BAG_MARGIN;
		}else{
			return x - Constants.PEBBLE_SIZE - Constants.PEBBLE_BAG_MARGIN;
		}
	}
	
	public int getFutureY(int xCoord) {
		return getFutureY(outerPanel, xCoord);
	}
	
	public int getFutureY(JPanel p, int xCoord) {
		int potY = 0;
		if(inTopHalf(p)) {
			potY = y + getHeight() + Constants.PEBBLE_BAG_MARGIN;
			while(findPebble(xCoord + 5, potY + 5) != null) {
				potY += Constants.PEBBLE_SIZE + Constants.PEBBLE_BAG_MARGIN;
			}
		}else{
			potY = y /*- Constants.PEBBLE_SIZE*/ - Constants.PEBBLE_BAG_MARGIN;
			while(findPebble(xCoord + 5, potY + 5) != null) {
				potY -= (Constants.PEBBLE_SIZE + Constants.PEBBLE_BAG_MARGIN);
			}
		}
		return potY;
	}
	
	public boolean inLeftHalf(JPanel p) {
		int midPX = p.getWidth()/2;
		int midBagX = x+getWidth()/2;
		return midBagX < midPX;
	}
	
	public boolean inTopHalf(JPanel p) {
		int midPY = p.getHeight()/2;
		int midBagY = y + getHeight()/2;
		return midBagY < midPY;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return Constants.PEBBLE_BAG_SIZE;
	}
	
	public int getHeight() {
		return Constants.PEBBLE_BAG_SIZE;
	}
	
	private void movePebblesRandomly() {
		for(PebbleView pv: pebbles) {
			pv.randomlyMovePebble(pebbles);
		}
	}
	
	private void randomlyAssignPebblesCoords(){
		for(PebbleView pv: pebbles) {
			pv.randomlyAssignCoords();
		}
	}
	
	private void placePebblesOutside() {
		for(PebbleView pv: pebbles) {
			int desiredX = getFutureX();
			int desiredY = getFutureY(desiredX);
			int actualX = getX() + pv.getX();
			int actualY = getY() + pv.getY();
			pv.moveBy(desiredX-actualX, desiredY-actualY);
			pv.setHidden(false);
		}
	}
	
	private void reAssignLocations() {
		while(!pebblesPlacedNicely()) {
			PebbleView overlap = findOverlappingPebble();
			overlap.randomlyAssignCoords();
		}
	}
	
	private void hidePebbles() {
		setAllHidden(true);
	}
	
	public void showPebbles() {
		setAllHidden(false);
	}
	
	private void setAllHidden(boolean hide) {
		for(PebbleView pv:pebbles) {
			pv.setHidden(hide);
		}
	}
	
	public void drawBag(Graphics g) {
		g.drawImage(GameImages.getPebbleBag(), x, y, null);
		for(PebbleView pv:pebbles) {
			g.drawImage(pv.getPebbleImage(), x + pv.getX(), y + pv.getY(), null);
		}
	}
	
	private boolean pebblesPlacedNicely() {
		return findOverlappingPebble() == null;
	}
	
	private PebbleView findOverlappingPebble() {
		if(!shouldResize(pebbles)) {
			return null;
		}
		for(PebbleView pv: pebbles) {
			PebbleView overlap = PebbleView.getOverlappingPebble(pv, pebbles);
			if(overlap != null) {
				return overlap;
			}
		}
		return null;
	}
	
	public PebbleView findPebble(int xCoord, int yCoord) {
		for(PebbleView pv: pebbles) {
			if(pv.contains(xCoord-x, yCoord-y)) {
				return pv;
			}
		}
		return null;
	}
	
	public static boolean shouldResize(ArrayList<PebbleView> allPebbles) {
		if(allPebbles.size() > Constants.MAX_PEBBLES_FOR_RESIZING) {
			return false;
		}
		return true;
	}
}
