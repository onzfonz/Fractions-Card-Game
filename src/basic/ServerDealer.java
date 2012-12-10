package basic;
/*
 * File: ServerDealer.java
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


public class ServerDealer{
	private Dealer bd;
	
	public static void main(String args[]) {
		ServerDealer d = new ServerDealer();
		for(int i = 0; i < 100; i++) {
			System.out.println(d.dealTeammateCard());
			System.out.println(d.dealTrickCard());
		}
	}
	
	public ServerDealer() {
		bd = DealerFactory.getNewDealer();
	}
	
	public ServerDealer(String tricksName, String teamsName, String teamsNameAlt) {
		bd = DealerFactory.getNewDealer(tricksName, teamsName, teamsNameAlt);
	}
	
	public String dealTeammateCard() {
		TeammateCard tc = bd.dealTeammateCard();
		return tc.toStream();
	}
	
	public String dealTrickCard() {
		TrickCard tc = bd.dealTrickCard();
		return tc.toStream();
	}
}
