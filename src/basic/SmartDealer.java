package basic;
/*
 * File: SmartDealer.java
 * -----------------
 * This is a smarter dealer that uses the assumption of there only being constants.TEAM_HAND_SIZE and TRICK_HAND_SIZE
 * to make sure that the cards are going to the right person.
 * This also assumes that for you to deal a trick card in order to give you good trick cards, you must have dealt
 * the teammate cards previously. If not the program will crash.
 */

import java.util.ArrayList;

import cards.Card;
import cards.TeammateCard;
import cards.TrickCard;
import deck.Deck;
import deck.PlayDeck;
import deck.TeammatesDeck;
import deck.TricksDeck;


public class SmartDealer implements Dealer{
	private Deck tricks;
	private Deck teammates;
	private ArrayList<TeammateCard> teamList;
	private ArrayList<TrickCard> trickList;
	
	public static void main(String args[]) {
		SmartDealer d = new SmartDealer();
		int bball = 0;
		for(int i = 0; i < 100; i++) {
			TeammateCard tmCard = d.dealTeammateCard();
			TrickCard tkCard = d.dealTrickCard();
			if(tmCard.getValue() == 12) {
				bball++;
			}
		}
	}
	
	public SmartDealer() {
		this(Constants.FNAME_TRICK_DECK, Constants.FNAME_TEAM_DECK, Constants.FNAME_TEAM_DECK_ALT);
	}
	
	public SmartDealer(String tricksName, String teamsName, String teamsNameAlt) {
		teamList = new ArrayList<TeammateCard>();
		trickList = new ArrayList<TrickCard>();
		tricks = new TricksDeck(tricksName);
		teammates = new TeammatesDeck(teamsName, teamsNameAlt);
		tricks.shuffle();
		teammates.shuffle();
	}
	
	public TeammateCard dealTeammateCard() {
		TeammateCard tc = (TeammateCard) dealCard(teammates);
		while(tc.isShadowPlayer() && shadowPlayersInPile(teamList)) {
			tc = (TeammateCard) dealCard(teammates);
		}
		addToTeamHand(teamList, tc);
		return tc;
	}
	
	public TrickCard dealTrickCard() {
		TrickCard tc = (TrickCard) dealCard(tricks);
		if((trickList.size() + 1) % Constants.TRICK_HAND_SIZE == 0) {
			tc = dealLegalCard(tc, tricks);
		}
		addToTrickHand(trickList, tc);
		return tc;
	}
	
	private void addToTeamHand(ArrayList<TeammateCard> tcs, TeammateCard tc) {
		if(tcs.size() == 2 * Constants.TEAM_HAND_SIZE) {
			tcs.clear();
		}
		tcs.add(tc);
	}
	
	private void addToTrickHand(ArrayList<TrickCard> tcs, TrickCard tc) {
		if(tcs.size() == 2 * Constants.TRICK_HAND_SIZE) {
			tcs.clear();
		}
		tcs.add(tc);
	}
	
	private TrickCard dealLegalCard(TrickCard tc, Deck d) {
		ArrayList<TrickCard> handSoFar = extractHand(trickList);
		while(!goodCardInHand(tc, handSoFar)) {
			tc = (TrickCard) dealCard(d);
		}
		return tc;
	}
	
	private ArrayList<TrickCard> extractHand(ArrayList<TrickCard> list) {
		ArrayList<TrickCard> temp = new ArrayList<TrickCard>();
		for(int i = 0; i < Constants.TRICK_HAND_SIZE-1; i++) {
			temp.add(list.get(list.size()-(Constants.TRICK_HAND_SIZE) +(i+1)));
		}
		return temp;
	}
	
	private boolean goodCardInHand(TrickCard tc, ArrayList<TrickCard> hand) {
		hand.add(tc);
		boolean isGood = false;
		for(int i = 0; i < hand.size(); i++) {
			TrickCard t = hand.get(i);
			if(isGoodCard(t)) {
				isGood = true;
				break;
			}
		}
		hand.remove(hand.size()-1);
		return isGood;
	}
	
	private boolean isGoodCard(TrickCard tc) {
		if(tc.isCombo()) {
			return isGoodCard(tc.getFirstCard()) || isGoodCard(tc.getSecondCard());
		}
		if(trickList.size() < Constants.TRICK_HAND_SIZE) {
			return tc.isIceCream() || (tc.isStink() && tc.toFraction().indexOf("1/2") != -1);
		}
		return tc.isIceCream() || (tc.isStink() && applicableToCards(tc));
	}
	
	private boolean shadowPlayersInPile(ArrayList<TeammateCard> teamsDealtSoFar) {
		for(int i = 0; i < teamsDealtSoFar.size(); i++) {
			TeammateCard team = teamsDealtSoFar.get(i);
			if(team.isShadowPlayer()) {
				return true;
			}
		}
		return false;
	}
	
	private boolean applicableToCards(TrickCard stink) {
		for(int i = 0; i < Constants.TEAM_HAND_SIZE; i++) {
			TeammateCard teammate = teamList.get(i);
			PlayDeck teamDeck = new PlayDeck(teammate);
			if(teamDeck.couldAddTrickCard(stink)) {
				return true;
			}
		}
		return false;
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
