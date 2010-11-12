package basic;
/*
 * File:PebbleBag.java
 * -------------------
 * This is our virtual file that acts like a pebble bag that we can draw from.
 * Each pebble bag is initialized by giving it a trick card that has both a numerator and a denominator.
 * The numerator represents the orange chips, which have ice cream (true) and the denominator the purple chips
 * makes the kids not fall for the trick (false).
 */


import acm.util.*;

import java.util.*;

import cards.TrickCard;

/* in the pebble thing, true means the kids run to the ice
 * cream truck, false means they stay.
 */
public class PebbleBag {
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private ArrayList<Boolean> pebbles;
	private int yes;
	private int no;
	
	public PebbleBag(TrickCard ice) {
		no = ice.getNumerator();
		yes = ice.getDenominator();
		resetBag();
	}
	
	public boolean drawPebble() {
		if(pebbles.size() == 0) {
			return false;
		}
		return pebbles.remove(rgen.nextInt(0, pebbles.size()-1));
	}
	
	public int numPebbles() {
		return pebbles.size();
	}
	
	public int getnumOrangePebbles() {
		return no;
	}
	
	public int getnumPurplePebbles() {
		return yes;
	}
	
	public void resetBag() {
		pebbles = new ArrayList<Boolean>();
		for(int i = 0; i < yes; i++) {
			pebbles.add(true);
		}
		for(int i = 0; i < no; i++) {
			pebbles.add(false);
		}
	}
	
	public static void main (String args[]) {
		PebbleBag bag = new PebbleBag(new TrickCard("cards_ice.jpg", 2, 2, "ice"));
		int count = 0;
		for(int i = 0; i < 1000; i++) {
			if(bag.drawPebble()&&bag.drawPebble()) {
				count++;
			}
			bag.resetBag();
		}
		System.out.println(count);
		System.out.println(2.0/4 * 1.0/3);
	}
}
