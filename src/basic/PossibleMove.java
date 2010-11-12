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
	private CardView viewThatWillWork;
	private TrickCard underlyingCard;
	private int potentialDamage;
	
	public PossibleMove(DeckView dv, CardView cv, int damage) {
		this(dv, cv, (TrickCard) cv.getCard(), damage);
	}
	
	public PossibleMove(DeckView dv, CardView cv, TrickCard tc, int damage) {
		deckToPlaceOn = dv;
		viewThatWillWork = cv;
		potentialDamage = damage;
		underlyingCard = tc;
	}

	public DeckView getDeck() {
		return deckToPlaceOn;
	}

	public CardView getCardView() {
		return viewThatWillWork;
	}
	
	public TrickCard getTrickCard() {
		return underlyingCard;
	}

	public int getDamage() {
		return potentialDamage;
	}
}
