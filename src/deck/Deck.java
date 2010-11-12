package deck;
import java.util.ArrayList;
import cards.Card;
import extras.*;

/*
 * File: Deck.java
 * ---------------
 * This will be where we have the two different types of decks.
 * For right now what we will do is have some simple methods where we can ask the deck for certain actions.
 * We'll be able to
 * 1) Create a deck, initializing it in some weird way.
 * 2) Shuffle the deck
 * 3) Draw a card from the deck.
 */
public abstract class Deck {
	protected ArrayList cards;
	protected RandomGenerator rgen = RandomGenerator.getInstance();
	protected ArrayList cardsCopy;
	
	protected void createBackup() {
		cardsCopy = (ArrayList) cards.clone();
	}
	
	public void reShuffle() {
		resetDeck();
		shuffle();
	}
	
	protected void resetDeck() {
		cards = (ArrayList) cardsCopy.clone();
	}
	
	protected Card drawRandomCard() {
		if(cardsExist()){
			return (Card) cards.get(rgen.nextInt(0, cards.size()-1));
		}
		return null;
	}
	
	public void shuffle() {
		for(int i = 0; i < cards.size(); i++) {
			swapCards(i, rgen.nextInt(0, cards.size()-1));
		}
	}
	
	private void swapCards(int pos1, int randPos) {
		Object c1 = cards.get(pos1);
		Object c2 = cards.get(randPos);
		cards.add(randPos+1, c1);
		cards.remove(randPos);
		cards.add(pos1+1, c2);
		cards.remove(pos1);		
	}
	
	protected void addCard(Card c) {
		cards.add(c);
	}
	
	public Card drawTopCard() {
		if(cardsExist()) {
			return (Card) cards.remove(0);
		}
		return null;
	}
	
	protected boolean cardsExist() {
		return cards != null && cards.size() > 1;
	}
	
	protected Card getCard(int index) {
		if(cardsExist() && index < cards.size()) {
			return (Card) cards.get(index);
		}
		return null;
	}
}
