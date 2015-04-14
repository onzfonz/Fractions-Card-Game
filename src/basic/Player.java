package basic;
/*
 * File: Player.java
 * -----------------
 * File that holds the contents of the player's hand
 * Each player will hold an ArrayList of PlayerDecks
 */

import java.util.ArrayList;
import java.util.Scanner;

import cards.Card;
import cards.CardView;
import cards.CardViewFactory;
import cards.TeammateCard;
import cards.TrickCard;
import deck.DeckView;
import deck.PlayDeck;
import extras.Debug;
import extras.GameUtils;


public class Player {
	protected ArrayList<CardView> trickHand;
	protected ArrayList<DeckView> decks;
	protected Dealer d;
	protected int points;
	private ArrayList<PlayerListener> listeners;
	private boolean isHuman;
	
	public Player(Dealer dlr) {
		this(dlr, null);
	}
	
	public Player(Dealer dlr, PlayerListener pl) {
		this(dlr, pl, false, true);
	}
	
	public Player(Dealer dlr, PlayerListener pl, boolean startRound, boolean human) {
		d = dlr;
		listeners = new ArrayList<PlayerListener>();
		if(pl != null) {
			addPlayerListener(pl);
		}
		trickHand = new ArrayList<CardView>();
		decks = new ArrayList<DeckView>();
		isHuman = human;
		setPoints(0);
		
		if(startRound) {
			newCardRound();
		}
	}
	
	public boolean isHuman() {
		return isHuman;
	}
	
	public void addPlayerListener(PlayerListener pl) {
		listeners.add(pl);
	}
	
	public void removePlayerListener(PlayerListener pl) {
		listeners.remove(pl);
	}
	
	public void addTrickCardToHand(TrickCard t) {
		CardView ct = null;
		if(isHuman()) {
			ct = CardViewFactory.createCard(t);
		}else{
			ct = CardViewFactory.createCard(t, false);
		}
		trickHand.add(ct);
		fireTrickCardAddedToHand(ct);
	}
	
	public void addTeammateCard(TeammateCard t) {
		PlayDeck newDeck = new PlayDeck(t);
		DeckView newDeckView = new DeckView(newDeck, this);
		decks.add(newDeckView);
		firePlayDeckAdded(newDeckView);
	}

	//This was an older version when we would have text input from the user
	//Commented out because it's not longer used and has an issue with findbugs
	//in specifying a charset on System.in in the scanner.
//	public void addToPlayDeck(TrickCard t) {
//		Scanner s = new Scanner(System.in);
//		printPlayDecks();
//		System.out.println("Which Deck? Choose a number between 0 and " + decks.size());
//		int i = s.nextInt();
//		while(i < 0 || i > decks.size()) {
//			System.out.println("invalid number please try again ");
//			i = s.nextInt();
//		}
//		if(i == 0) {
//			System.out.println("Cancelling option...");
//			s.close();
//			return;
//		}
//		DeckView curDeck = decks.get(i-1);
//		if(!curDeck.addTrickCard(t)) {
//			System.out.println("That trick card cannot be applied, Please wait for next turn");
//		}
//		firePlayDeckChanged();
//		s.close();
//	}
	
	private boolean addToPlayDeck(DeckView dv, CardView cv) {
		if(decks.indexOf(dv) == -1) {
			System.out.println("adding to some weird deck");
		}
		boolean couldBeAdded = dv.addTrickCard(cv);
		if(couldBeAdded) {
			firePlayDeckChanged();
		}
		return couldBeAdded;
	}
	
	private boolean couldAddToPlayDeck(DeckView dv, CardView cv) {
		return dv.couldAddTrickCard(cv);
	}
	
	private boolean couldAddToPlayDeck(DeckView dv, TrickCard tc) {
		return dv.couldAddTrickCard(tc);
	}
	
	public boolean addToPlayDeck(DeckView dv, CardView cv, Player cvOwner) {
		TrickCard tc = (TrickCard) cv.getCard();
		if(cvOwner == this) {
			if(tc.isDefense()) {
				boolean addSuccessful = addToPlayDeck(dv, cv);
				if(addSuccessful && tc.isAir()) {
					fireAirFreshenerPlayed(dv, cvOwner);
				}else if(addSuccessful && tc.isRadio()) {
					fireRadioPlayed(dv, cvOwner);
				}
				return addSuccessful;
			}
			return false;
		}else{
			if(!tc.isDefense()) {
				boolean addSuccessful = addToPlayDeck(dv, cv);
				if(addSuccessful && tc.isIceCream()) {
					fireIceCreamTruckPlayed(dv, cvOwner);
				}else if(addSuccessful && tc.isStink()) {
					fireStinkBombPlayed(dv, cvOwner);
				}
				return addSuccessful;
			}
			return false;
		}
	}
	
	public boolean couldAddToPlayDeck(DeckView dv, CardView cv, Player cvOwner) {
		TrickCard tc = (TrickCard) cv.getCard();
		return couldAddToPlayDeck(dv, tc, cvOwner);
	}
	
	public boolean couldAddToPlayDeck(DeckView dv, TrickCard tc, Player cvOwner) {
		if(tc.isCombo()) {
			return couldAddToPlayDeck(dv, tc.getFirstCard(), cvOwner) || couldAddToPlayDeck(dv, tc.getSecondCard(), cvOwner);
		}
		Player p = dv.getPlayer();
		if(cvOwner == this) {
			if(tc.isDefense()) {
				return couldAddToPlayDeck(dv, tc);
			}
			return false;
		}else{
			if(!tc.isDefense()) {
				return couldAddToPlayDeck(dv, tc);
			}
			return false;
		}
	}
	
	public TrickCard getTrickFromHand(int i) {
		if(i >=0 && i < numTrickCards()) {
			CardView cv = trickHand.remove(i);
			Card c = cv.getCard();
			if(!c.isTrickCard()) {
				System.out.println("getTrickFromHand in Player.java is returning" + c.getName());
			}
			return (TrickCard) c;
		}else{
			return null;
		}
	}
	
	public void removeTrickFromHand(CardView cv) {
		if(trickHand.remove(cv)) {
			fireTrickCardRemovedFromHand(cv);
		}else{
			System.out.println("tried to remove" + cv.getCard() +" but that wasn't in the trickHand" + trickHand);
		}
	}
	
	public void removePlayDeckFromHand(DeckView dv) {
		if(decks.remove(dv)) {
			firePlayDeckRemoved(dv);
		}else{
			System.out.println("tried to remove" + dv.getTeammateCard() +" but that wasn't in the decks");
		}
	}
	
	public void removeIceRadiosFromDeck(DeckView dv) {
		ArrayList<CardView> deckHand = dv.getAllCards();
		for(int i = 1; i < deckHand.size(); i++) {
			CardView cv = deckHand.get(i);
			if(cv.isRadio() || cv.isIceCream()) {
				dv.removeTrickCard(cv);
			}
		}
		firePlayDeckChanged();
	}

	public boolean isCardInTrickHand(CardView cv) {
		return trickHand.indexOf(cv) != -1;
	}
	
	public int numTrickCards() {
		return trickHand.size();
	}
	
	/* Methods that the server would need to take care of */
	public void newCardRound() {
		clearPlayerCards();
		replenishTrickHand();
		replenishTeamHand();
	}
	
	public void clearPlayerCards() {
		clearTrickHand();
		clearDecks();
	}
	
	public void replenishTrickHand() {
		while(numTrickCards() < Constants.TRICK_HAND_SIZE) {
			addExtraTrickToHand();
		}
	}
	
	public void replenishTeamHand() {
		for(int i = 0; i < Constants.TEAM_HAND_SIZE; i++) {
			addExtraTeammate();
		}
	}
	
	public void addExtraTeammate() {
		TeammateCard tc = d.dealTeammateCard();
		addTeammateCard(tc);
	}
	
	public void addExtraTrickToHand() {
		addTrickCardToHand(d.dealTrickCard());
	}
	
	/* Eventually this method will have some type of input from the user
	 * but for now we'll make a very lame computer simulation
	 * of what they will pick from a card.
	 */
	//Also commented out due to lack of encoding
//	public TrickCard chooseTrickToPlay() {
//		Scanner s = new Scanner(System.in);
//		printTrickCards();
//		System.out.println("Choose a number between 0 and " + numTrickCards());
//		int i = s.nextInt();
//		while(i < 0 || i > numTrickCards()) {
//			System.out.println("invalid number please try again ");
//			i = s.nextInt();
//		}
//		TrickCard t = getTrickFromHand(i-1);
//		fireTrickHandChanged();
//		return t;
//	}
	
	//might make this private later.
	public void clearTrickHand() {
		trickHand.clear();
		fireTrickCardsTossed();
	}
	
	public void clearDecks() {
		decks.clear();
		firePlayDecksTossed();
	}
	
	protected void printTrickCards() {
		System.out.println("Your Trick Hand");
		for(int i = 0; i < trickHand.size(); i++) {
			int iplus = i+1;
			System.out.println(iplus + ") " +trickHand.get(i));
		}
	}
	
	protected void printPlayDecks() {
		System.out.println("Decks");
		for(int i = 0; i < decks.size(); i++) {
			int iplus = i+1;
			System.out.println(iplus+") " + ((decks.get(i)).getPlayDeck()).getTeammateCard());
		}
	}
	
	protected CardView getARadio() {
		for(CardView cv:trickHand) {
			if(cv.isRadio()) {
				return cv;
			}
		}
		return null;
	}
	
	//Assumes that there is an IceCream Truck ready to be placed here
	public void playARadio(DeckView dv) {
		CardView radio = getARadio();
		PossibleMove move = couldBePlayed(dv, radio);
		if(move != null) {
			fireCardAnimation(move, null, Constants.STATUS_PLAYED_RADIO, true);
		}
	}
	
	//Want something that would check to see if it was possible to add the card
	protected PossibleMove couldBePlayed(DeckView dv, CardView cv) {
		return GameUtils.couldBePlayed(dv, cv, (TrickCard) cv.getCard(), this);
	}
	
	public void assembleMove(String cardIndex, int deckIndex, Player opponent) {
		CardView cv = extractCard(cardIndex);
		TrickCard tc = extractTrick(cv, cardIndex);
		Player p = opponent;
		if(tc.isDefense()) {
			p = this;
		}
		DeckView dv = p.getDeckView(deckIndex);
		PossibleMove pm = new PossibleMove(dv, cv, tc, -1);
		fireCardAnimation(pm, opponent, Constants.DECIDED_MOVE, true);
	}
	
	private CardView extractCard(String cardIndex) {
		int commaPos = cardIndex.indexOf("c");
		if(commaPos != -1) {
			cardIndex = cardIndex.substring(0, commaPos);
		}
		return trickHand.get(Integer.parseInt(cardIndex));
	}
	
	private TrickCard extractTrick(CardView cv, String cardIndex) {
		if(!cv.isCombo()) {
			return (TrickCard) cv.getCard();
		}
		int commaPos = cardIndex.indexOf("c");
		int option = Integer.parseInt(cardIndex.substring(commaPos+1));
		return cv.setOptionChosen(option);
	}
	
	//Assumes move and player are both non-null;
	protected void performMove(PossibleMove move, Player opponent) {
		CardView cv = move.getCardView();
		TrickCard tc = move.getTrickCard();
		Player p = null;
		Player cvOwner = this;
		if(tc.isAir() || tc.isRadio()) {
			p = this;
		}else{
			p = opponent;
		}
		removeTrickFromHand(cv);
		cv.setFaceUp(true);
		Debug.println("Going to play " + cv.getCard() + " on the deck with teammate " + move.getDeck().getTeammateCard().getCard() + " causing damage " + move.getDamage());
		if(cv.isCombo()) {
			cv.setOptionChosen(extractOption(cv, tc));
		}
		cv.setCard(tc);  //need to set the card now that it's been picked as the real TrickCard
		p.addToPlayDeck(move.getDeck(), cv, cvOwner);
	}
	
	private int extractOption(CardView cv, TrickCard tc) {
		TrickCard oldCard = (TrickCard) cv.getCard();
		String oldType = oldCard.getType();
		String newType = tc.getType();
		int colonPos = oldType.indexOf(";");
		int typePos = oldType.indexOf(newType);
		if(colonPos > typePos) {
			return 0;
		}
		return 1;
	}
	
	public int indexOfDeck(DeckView dv) {
		return decks.indexOf(dv);
	}
	
	public int totalTeammates() {
		int score = 0;
		for(int i = 0; i < decks.size(); i++) {
			PlayDeck curDeck = decks.get(i).getPlayDeck();
			score += curDeck.calculateDeck();
		}
		return score;
	}
	
	public int numRadiosInHand() {
		return PlayDeck.numRadios(PlayDeck.convertListToTrickCards(trickHand));
	}
	
	public ArrayList<CardView> getTrickHand() {
		return trickHand;
	}
	
	public ArrayList<DeckView> getAllDecks() {
		return decks;
	}
	
	public void updatePoints(int p) {
		setPoints(points + p);
	}
	
	public int getPoints() {
		return points;
	}
	
	public void setPoints(int p) {
		points = p;
		firePlayerScoreChanged();
	}

	public PlayDeck getPlayDeck(int i) {
		DeckView dv = getDeckView(i);
		if(dv == null) {
			return null;
		}
		return dv.getPlayDeck();
	}
	
	private DeckView getDeckView(int i) {
		if(i < 0 || i > decks.size()-1) {
			return null;
		}
		return decks.get(i);
	}
	
	public int numPlayDecks() {
		return decks.size();
	}
	
	/* My current debate is whether or not to pass in the trick card so that I know exactly what to add or remove
	 * I feel like usually in mvc, we just want to call it and have the whole thing update itself, but maybe
	 * that's not really what we want in this case.  Passing it in would allow you to have it be quicker, but 
	 * it wouldn't be in the spirit of MVC, since you are telling it exactly what to remove or add.  You need to look at CardView
	 * to see what it does to make your decision.
	 */
	
	public void fireDeckRepaint(DeckView dv) {
		for(PlayerListener l:listeners) {
			l.deckNeedsRepaint(this, dv);
		}
	}
	
	private void firePlayDeckChanged() {
		for(PlayerListener l:listeners) {
			l.playDeckChanged(this);
		}
	}
	
	private void fireTrickHandChanged() {
		for(PlayerListener l:listeners) {
			l.trickDeckChanged(this);
		}
	}
	
	private void fireTrickCardAddedToHand(CardView cv) {
		for(PlayerListener l:listeners) {
			l.trickCardAdded(this, cv);
		}
	}
	
	private void fireTrickCardRemovedFromHand(CardView cv) {
		for(PlayerListener l:listeners) {
			l.trickCardRemoved(this, cv);
		}
	}
	
	private void fireTrickCardsTossed() {
		for(PlayerListener l:listeners) {
			l.trickCardsThrownAway(this);
		}
	}
	
	private void firePlayDeckAdded(DeckView p) {
		for(PlayerListener l:listeners) {
			l.playDeckAdded(this, p);
		}
	}
	
	private void firePlayDeckRemoved(DeckView p) {
		for(PlayerListener l:listeners) {
			l.playDeckRemoved(this, p);
		}
	}
	
	private void firePlayDecksTossed() {
		for(PlayerListener l:listeners) {
			l.playDecksTossed(this);
		}
	}
	
	private void fireIceCreamTruckPlayed(DeckView p, Player cvOwner) {
		assert(cvOwner != this);
		for(PlayerListener l:listeners) {
			l.iceCreamPlayed(this, p, cvOwner);
		}
	}
	
	private void fireAirFreshenerPlayed(DeckView p, Player cvOwner) {
		assert(cvOwner == this);
		for(PlayerListener l:listeners) {
			l.airFreshenerPlayed(this, p, cvOwner);
		}
	}
	
	private void fireStinkBombPlayed(DeckView p, Player cvOwner) {
		assert(cvOwner != this);
		for(PlayerListener l:listeners) {
			l.stinkBombPlayed(this, p, cvOwner);
		}
	}
	
	private void fireRadioPlayed(DeckView p, Player cvOwner) {
		assert(cvOwner == this);
		for(PlayerListener l:listeners) {
			l.radioPlayed(this, p, cvOwner);
		}
	}
	
	private void firePlayerScoreChanged() {
		for(PlayerListener l:listeners) {
			l.scoreUpdated(this);
		}
	}
	
	protected void fireCardAnimation
	(PossibleMove cMove, Player opponent, String message, boolean isImmediate) {
		for(PlayerListener l:listeners) {
			l.cardAnimationLaunched(cMove, this, opponent, message, isImmediate);
		}
	}
}
