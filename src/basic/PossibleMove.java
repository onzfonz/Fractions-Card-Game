/*
 * File: PossibleMove
 * ------------------
 * This is a simple struct that will allow us to hold more than one thing
 */
package basic;
import java.util.ArrayList;

import cards.CardView;
import cards.TrickCard;
import deck.DeckView;
import deck.PlayDeck;

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
	
	public static PossibleMove getBestMove(ArrayList<PossibleMove> moves, TrickCard tc, DeckView dv) {
		PossibleMove bestPm = null;
		assert(moves != null && moves.size() > 0);
		for(PossibleMove pm: moves) {
			if(shouldBecomeBestMove(pm, bestPm, tc, dv)) {
				bestPm = pm;
			}
		}
		return bestPm;
	}
	
	public static PossibleMove getSpecifiedMove(ArrayList<PossibleMove> moves, TrickCard tc, DeckView dv) {
		PossibleMove myPm = null;
		assert(moves != null & moves.size() > 0);
		for(PossibleMove pm: moves) {
			if(isMyMove(pm, tc, dv)) {
				return pm;
			}
		}
		assert(myPm != null);
		return myPm;
	}
	
	private static boolean shouldBecomeBestMove(PossibleMove pm, PossibleMove bestPm, TrickCard tc, DeckView dv) {
		return bestPm == null || pm.getDamage() > bestPm.getDamage() || (pm.getDamage() == bestPm.getDamage() && pm.getTrickCard() == tc && pm.getDeck() == dv);
	}
	
	private static boolean isMyMove(PossibleMove pm, TrickCard tc, DeckView dv) {
		return pm != null && pm.getTrickCard().equals(tc) && pm.getDeck().getPlayDeck().equals(dv.getPlayDeck());
	}
	
	public String toString() {
		TrickCard tc = getTrickCard();
		String s = tc.toReadableStream() + " on ";
		PlayDeck pd = getDeck().getPlayDeck();
		return s + pd.calculateDeck()+"("+pd.initialDeckValue()+")";
	}
}
