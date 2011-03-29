package pebblebag;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import basic.Constants;

import extras.RandomGenerator;

public class PebbleView {
	private boolean kidsRunToTruck;
	private boolean isHidden;
	private int x;
	private int y;
	private RandomGenerator rgen;
	
	public static final int DEFAULT_MAX_VELOCITY = 30;
	public static final int DEFAULT_MIN_VELOCITY = 10;

	public PebbleView(boolean k) {
		kidsRunToTruck = k;
		isHidden = true;
		rgen = RandomGenerator.getInstance();
		randomlyAssignCoords();
	}

	public BufferedImage getPebbleImage() {
		if(isHidden){
			return PebbleImages.getHiddenPebble();
		}else{
			if(kidsRunToTruck) {
				return PebbleImages.getOrangePebble();
			}else{
				return PebbleImages.getPurplePebble();
			}
		}
	}
	
	public boolean isOrange() {
		return kidsRunToTruck;
	}
	
	public boolean kidsAreSafe() {
		if(isHidden) {
			return false;
		}
		return !isOrange();
	}
	
	public void setHidden(boolean hides) {
		isHidden = hides;
	}
	
	public boolean isHidden() {
		return isHidden;
	}
	
	public void randomlyAssignCoords() {
		int maxX = maxXCoord();
		int maxY = maxYCoord();
		x = rgen.nextInt(0, maxX);
		y = rgen.nextInt(minYCoord(), maxY);
	}
	
	public void randomlyMovePebble() {
		int xDirection = rgen.nextInt(0, 3);
		int yDirection = rgen.nextInt(0, 3);
		x = getNewPebbleCoord(x, minXCoord(), maxXCoord(), xDirection, DEFAULT_MIN_VELOCITY, DEFAULT_MAX_VELOCITY);
		y = getNewPebbleCoord(y, minYCoord(), maxYCoord(), yDirection, DEFAULT_MIN_VELOCITY, DEFAULT_MAX_VELOCITY);
		if(rgen.nextBoolean(.25)) {
			y = Math.min(y+(DEFAULT_MAX_VELOCITY-DEFAULT_MIN_VELOCITY)/4, maxYCoord());
		}
	}
	
	public void randomlyMovePebble(ArrayList<PebbleView> pebbles) {
		int origX = x;
		int origY = y;
		randomlyMovePebble();
		if(!pebblePlacedNicely(pebbles)) {
			x = origX;
			y = origY;
		}
	}
	
	private boolean pebblePlacedNicely(ArrayList<PebbleView> allPebbles) {
		return getOverlappingPebble(this, allPebbles) == null;
	}
	
	public static boolean pebbleOverlaps(PebbleView p1, PebbleView p2) {
		int distanceX = Math.abs(p1.getX()-p2.getX());
		int distanceY = Math.abs(p1.getY()-p2.getY());
		return pebbleOverlapsDistance(distanceX, distanceY);
	}
	
	public static boolean pebbleOverlapsDistance(int distX, int distY) {
		return (distX < Constants.PEBBLE_SIZE && distX >= 0) && (distY < Constants.PEBBLE_SIZE && distY >= 0);
	}
	
	public boolean contains(int x2, int y2) {
		int distX = x2 - getX();
		int distY = y2 - getY();
		return pebbleOverlapsDistance(distX, distY);
	}
	
	public boolean contains(PebbleView p2) {
		return contains(p2.getX(), p2.getY());
	}
	
	public static PebbleView getOverlappingPebble(PebbleView base, ArrayList<PebbleView> allPebbles) {
		for(PebbleView pv: allPebbles) {
			if(base != pv) {
				if(PebbleView.pebbleOverlaps(base, pv)) {
					return pv;
				}
			}
		}
		return null;
	}
	
	private int getNewPebbleCoord(int coord, int low, int high, int direction, int minV, int maxV) {
		return Math.min(high, Math.max(low, coord + getPebbleMove(direction, minV, maxV)));
	}
	
	private int maxXCoord() {
		return Constants.PEBBLE_BAG_SIZE-Constants.PEBBLE_SIZE-Constants.PEBBLE_BAG_MARGIN;
	}
	
	private int maxYCoord() {
		return Constants.PEBBLE_BAG_SIZE-Constants.PEBBLE_SIZE-Constants.PEBBLE_BAG_MARGIN;
	}
	
	private int minYCoord() {
		return Constants.PEBBLE_BAG_THRESHOLD;
	}
	
	private int minXCoord() {
		return Constants.PEBBLE_BAG_MARGIN;
	}
	
	public boolean insideOfBag() {
		return insideOfRange(getX(), getY(), minXCoord(), maxXCoord(), minYCoord(), maxYCoord());
	}
	
	public boolean completelyOutsideOfBag() {
		int minX = minXCoord()-Constants.PEBBLE_SIZE;
		int maxX = maxXCoord()+Constants.PEBBLE_SIZE;
		int minY = minYCoord()-Constants.PEBBLE_SIZE;
		int maxY = maxYCoord()+Constants.PEBBLE_SIZE;
		
		boolean in = insideOfRange(getX(), getY(), minX, maxX, minY, maxY);
		return !in;
	}
	
	public void moveBackNearEdges() {
		if(x > maxXCoord()) {
			x = maxXCoord();
		}else if(x < minXCoord()) {
			x = minXCoord();
		}
		if(y > maxYCoord()) {
			y = maxYCoord();
		}else if(y < minYCoord()) {
			y = minYCoord();
		}
	}
	
	public static boolean insideOfRange(int xcoord, int ycoord, int minX, int maxX, int minY, int maxY) {
		return xcoord >= minX && xcoord <= maxX && ycoord >= minY && ycoord <= maxY;
	}
	
	private int getPebbleMove(int direction) {
		return getPebbleMove(direction, DEFAULT_MIN_VELOCITY, DEFAULT_MAX_VELOCITY);
	}
	
	private int getPebbleMove(int direction, int minVelocity, int maxVelocity) {
		if(direction == 0) {
			return rgen.nextInt(minVelocity, maxVelocity);
		}else if(direction == 1) {
			return -1 * rgen.nextInt(minVelocity, maxVelocity);
		}else{
			return 0;
		}
	}
	
	public Point getLocation() {
		return new Point(x, y);
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void moveBy(int dx, int dy) {
		x += dx;
		y += dy;
	}
	
	public String toString() {
		return "" + kidsRunToTruck;
	}
}
