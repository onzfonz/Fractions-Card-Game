package basic;
/*
 * File: BasicDealer.java
 * -----------------
 * This class is the one responsible for creating and shuffling the two decks of cards
 * The teammate deck and the tricks deck.
 * Another file asks the dealer to deal or draw a card from one of the two decks.
 */

import cards.Card;
import cards.TeammateCard;
import cards.TrickCard;
import deck.Deck;
import deck.TeammatesDeck;
import deck.TricksDeck;


public class BasicDealer implements Dealer{
	private Deck tricks;
	private Deck teammates;
	
	public static void main(String args[]) {
		BasicDealer d = new BasicDealer();
		int bball = 0;
		for(int i = 0; i < 100; i++) {
			TeammateCard tmCard = d.dealTeammateCard();
			TrickCard tkCard = d.dealTrickCard();
			if(tmCard.getValue() == 12) {
				bball++;
			}
		}
	}
	
	public BasicDealer() {
		this("Tricks1Deck.txt", "Team1Deck.txt");
	}
	
	public BasicDealer(String tricksName, String teamsName) {
		tricks = new TricksDeck(tricksName);
		teammates = new TeammatesDeck(teamsName);
		tricks.shuffle();
		teammates.shuffle();
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
			d.reShuffle();
			c = d.drawTopCard();
		}
		return c;
	}
}
