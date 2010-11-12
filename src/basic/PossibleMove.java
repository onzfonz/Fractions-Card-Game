/*
 * File: PossibleMove
 * ------------------
 * This is a simple struct that will allow us to hold more than one thing
 */
package basic;
import deck.*;
import cards.*;

public class PossibleMove {
	private DeckView deckToPlaceOn;
	private CardView cardThatWillWork;
	private int potentialDamage;
	
	public PossibleMove(DeckView dv, CardView tc, int damage) {
		deckToPlaceOn = dv;
		cardThatWillWork = tc;
		potentialDamage = damage;
	}

	public DeckView getDeck() {
		return deckToPlaceOn;
	}

	public CardView getTrickCard() {
		return cardThatWillWork;
	}

	public int getDamage() {
		return potentialDamage;
	}
}
