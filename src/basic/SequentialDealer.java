package basic;
/*
 * File: SmartDealer.java
 * -----------------
 * This is a smarter dealer that uses the assumption of there only being constants.TEAM_HAND_SIZE and TRICK_HAND_SIZE
 * to make sure that the cards are going to the right person.
 * This also assumes that for you to deal a trick card in order to give you good trick cards, you must have dealt
 * the teammate cards previously. If not the program will crash.
 */

import cards.Card;
import cards.TeammateCard;
import cards.TrickCard;
import deck.Deck;
import deck.TeammatesDeck;
import deck.TricksDeck;

public class SequentialDealer implements Dealer{
	private Deck tricks;
	private Deck teammates;
	
	public SequentialDealer() {
		this(Constants.FNAME_SEQ_PREFIX + Constants.FNAME_TRICK_DECK, Constants.FNAME_SEQ_PREFIX + Constants.FNAME_TEAM_DECK);
	}
	
	public SequentialDealer(String tricksName, String teamsName) {
		tricks = new TricksDeck(tricksName);
		teammates = new TeammatesDeck(teamsName);
	}
	
	public TeammateCard dealTeammateCard() {
		return (TeammateCard) dealCard(teammates);
	}
	
	public TrickCard dealTrickCard() {
		return (TrickCard) dealCard(tricks);
	}
	
	private Card dealCard(Deck d) {
		Card c = d.drawTopCard();
		if(c == null) {
			d.resetDeck();
			d.drawTopCard();
		}
		return c;
	}
}
