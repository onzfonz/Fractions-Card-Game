package deck;
/*
 * PlayDeck.java
 * -------------
 * As of right now, PlayDeck is the main java file that
 * has the right properties for whether or not something can be applied to one of the decks.
 * It also has the logic that applies based on what trick cards are applied to a deck.
 */

import java.awt.Image;
import java.util.ArrayList;

import basic.PebbleBag;

import cards.Card;
import cards.CardView;
import cards.TeammateCard;
import cards.TrickCard;


import acm.util.RandomGenerator;

public class PlayDeck extends Deck {
	private TeammateCard team;
	private String errorMsg = "";

	public PlayDeck(TeammateCard l) {
		team = l;
		cards = new ArrayList<TrickCard>();
		createBackup();
	}

	/* This is our main workshorse method for making sure that we 
	 * can add a card to a specific playdeck, will add and return true;
	 */
	@SuppressWarnings("unchecked")
	public boolean addTrickCard(TrickCard c) {
		boolean couldAddTrick = couldAddTrickCard(c);
		if(couldAddTrick) {
			cards.add(c);
		}
		return couldAddTrick;
	}

	public boolean couldAddTrickCard(TrickCard c) {
		boolean noRemainder = false;
		if(c.isStink()) {
			noRemainder = canApplyStink(c);
		}else if(c.isAir()) {
			noRemainder = canApplyAir(c);
		}else if(c.isIceCream()) {
			noRemainder = true;
		}else if(c.isRadio()) {
			ArrayList<TrickCard> tCards = (ArrayList<TrickCard>) cards;
			for(TrickCard tc:tCards) {
				if(tc.isIceCream()) {
					return true;
				}
			}
			errorMsg = "You need to have Ice Cream Cards on this Deck to use radios";
			noRemainder = false;
		}
		return noRemainder;
	}

	public boolean removeTrickCard(TrickCard c) {
		boolean couldRemoveTrick = couldRemoveTrickCard(c);
		if(couldRemoveTrick) {
			cards.remove(c);
		}
		return couldRemoveTrick;
	}

	public boolean couldRemoveTrickCard(TrickCard c) {
		return c.isRadio() || c.isIceCream();
	}

	/* Will reset the error message to empty after it's been grabbed */
	public String getErrorMsg() {
		String tempMsg = errorMsg;
		errorMsg = "";
		return tempMsg;
	}

	public TeammateCard getTeammateCard() {
		return team;
	}

	public TrickCard getLastTrickPlaced() {
		if(cards.size() == 0) {
			return null;
		}
		return (TrickCard) cards.get(cards.size()-1);
	}

	public String generateQuestion(TrickCard tc, boolean useAllCards) {
		String prefix = "What is ";
		String fraction = tc.toFraction();
		String numPeople = "" + getNumForFraction(tc.isStink(), useAllCards);
		return prefix + fraction + " of " + numPeople + "?";
	}

	public int generateAnswer(TrickCard tc, boolean useAllCards) {
		int num = getNumForFraction(tc.isStink(), useAllCards);
		return applyFraction(num, tc);
	}

	public int initialDeckValue() {
		return team.getValue();
	}

	private int getNumForFraction(boolean isStink, boolean useAllCards) {
		int num = team.getValue();
		if(isStink){
			if(useAllCards) {
				num = calculateStinksAndAirs(cards, num);
			}else{
				num = calculateAlmostAllStinkAndAirs(cards, num);
			}
		}
		return num;
	}

	//Will be an obsolete method in GUI version since it gets taken right away
	private boolean kidsRunToTruck() {
		ArrayList <TrickCard> ices = getIceCreamCards();
		if(ices.size() == 0) {
			return false;
		}
		int nRadioCards = numRadios();
		for(int i = 0; i < ices.size(); i++) {
			PebbleBag bag = new PebbleBag(ices.get(i));
			if(nRadioCards > 0) {
				if(bag.drawPebble()&&bag.drawPebble()) {
					return true;
				}
				nRadioCards--;
			}else{
				if(bag.drawPebble()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean playOneIceCreamTruck(TrickCard ice) {
		return rgen.nextBoolean(ice.getValue());
	}

	@SuppressWarnings("unchecked")
	private ArrayList<TrickCard> getIceCreamCards() {
		return getIceCreamCards((ArrayList<TrickCard>) cards);
	}

	@SuppressWarnings("unchecked")
	private int numRadios() {
		return numRadios((ArrayList<TrickCard>) cards);
	}

	@SuppressWarnings("unchecked")	
	private int numAirs() {
		return numAirs((ArrayList<TrickCard>) cards);
	}

	@SuppressWarnings("unchecked")
	private int numStinks() {
		return numStinks((ArrayList<TrickCard>) cards);
	}


	private boolean canApplyStink(TrickCard c) {
		int numStillLeft = calculateStinksAndAirs();
		boolean isDivisible = numStillLeft % c.getDenominator() == 0;
		if(!isDivisible) {
			if(numStinks() > 0) {
				errorMsg = "You cannot use this " + c + "card because this deck now only has " + numStillLeft + " teammate" + ((numStillLeft != 1)? "s":"");
			}else{
				errorMsg = "A " + c + " card has to be evenly divisible to be used on the " + team;
			}
		}
		return isDivisible;
	}

	private boolean canApplyAir(TrickCard c) {
		boolean isDivisible = team.getValue() % c.getDenominator() == 0;
		boolean someStinkies = (team.getValue() - calculateStinksAndAirs()) > 0;
		if(!isDivisible) {
			errorMsg = "A " + c + " card has to be evenly divisible to be used on the " + team;
		}else if(!someStinkies) {
			if(numStinks() > 0) {
				errorMsg = "A " + c + " card has to have some stinky teammates before you can use it";
			}else{
				errorMsg = "There must be a stink card placed on this deck before you can use a" + c + "card";
			}
		}
		return (isDivisible && someStinkies);
	}

	public int numLeftAfterStink() {
		int curPosse = team.getValue();
		return numLeftAfterStink(cards, curPosse);
	}

	private int numFreshened() {
		int curPosse = team.getValue();
		return numFreshened(cards, curPosse);
	}

	public int calculateStinksAndAirs() {
		return calculateStinksAndAirs(cards, team.getValue());
	}

	public int[] calculateStinksAndAirsSeparately() {
		return calculateStinksAndAirsSeparately(cards, team.getValue());
	}

	public int calculateDeck() {
		int totalLeft = calculateStinksAndAirs();
		//		if(kidsRunToTruck()) {
		//			return 0;
		//		}else{
		return totalLeft;
		//		}
	}

	public int calculatePotentialScore() {
		return calculatePotentialScore(cards, team.getValue());
	}

	public int calculatePotentialScore(TrickCard tc) {
		ArrayList<TrickCard> tcs = new ArrayList<TrickCard>();
		tcs.addAll(cards);
		tcs.add(tc);
		return calculatePotentialScore(tcs, team.getValue());

	}

	public static int calculateExpectedAfterIces(int totalLeft, ArrayList<TrickCard> ices) {
		double numLeft = totalLeft;
		for(TrickCard ice:ices) {
			numLeft = applyHypoIceFraction(numLeft, ice);
		}
		return (int) Math.round(numLeft);
	}

	private static int applyFraction(int teammates, TrickCard t) {
		if(teammates % t.getDenominator() != 0) {
			return -1;
		}
		return ((teammates / t.getDenominator())*t.getNumerator());
	}

	private static double applyHypoIceFraction(double teammates, TrickCard t) {
		return (teammates / ((double) (t.getDenominator()+t.getNumerator()))* t.getDenominator());
	}

	public static int applyHypoNumbers(double teammates, int num, int den) {
		return (int) Math.round((teammates / ((double) (num+den)))*den);
	}

	public static int calculateStinksAndAirsOld(ArrayList<TrickCard> tcs, int curPosse) {
		int stinkers = curPosse - numLeftAfterStink(tcs, curPosse);
		int smellGood = numFreshened(tcs, curPosse);
		stinkers = Math.max(0, stinkers-smellGood);
		return curPosse - stinkers;
	}

	public static int calculateStinksAndAirs(ArrayList<TrickCard> tcs, int curPosse) {
		int posseLeft = curPosse;
		for(TrickCard tc: tcs) {
			posseLeft = playCardOnTeam(posseLeft, curPosse, tc);
		}
		return posseLeft;
	}

	public static int[] calculateStinksAndAirsSeparately(ArrayList<TrickCard> tcs, int curPosse) {
		int posseLeft = curPosse;
		int[] stinksAirs = new int[2];
		for(TrickCard tc: tcs) {
			posseLeft = playCardOnTeam(posseLeft, curPosse, tc, stinksAirs);
		}
		return stinksAirs;
	}

	public static int calculateAlmostAllStinkAndAirs(ArrayList<TrickCard> tcs, int curPosse) {
		int posseLeft = curPosse;
		for(int i = 0; i < tcs.size()-1; i++) {
			TrickCard tc = tcs.get(i);
			posseLeft = playCardOnTeam(posseLeft, curPosse, tc);
		}
		return posseLeft;
	}

	public static int playCardOnTeam(int posse, int maxPosse, TrickCard tc) {
		return playCardOnTeam(posse, maxPosse, tc, null);
	}

	public static int playCardOnTeam(int posse, int maxPosse, TrickCard tc, int[] stinkAir) {
		if(tc.isStink()) {
			int result = applyFraction(posse, tc);
			if(stinkAir != null) {
				stinkAir[0] += result;
				stinkAir[1] -= result;
				stinkAir[1] = Math.max(0, stinkAir[1]);
				stinkAir[0] = Math.min(stinkAir[0], maxPosse);
			}
			posse -= result;
		}else if(tc.isAir()) {
			int result = applyFraction(maxPosse, tc);
			if(stinkAir != null) {
				int oldStink = stinkAir[0];
				stinkAir[0] -= result;
				stinkAir[1] += result;
				stinkAir[0] = Math.max(0, stinkAir[0]);
				stinkAir[1] = Math.min(stinkAir[1], oldStink);
				
			}
			posse += result;
			posse = Math.min(maxPosse, posse);
		}
		return posse;
	}

	//Calculate Potential Score is how many people would you theoretically be left with.
	public static int calculatePotentialScore(ArrayList<TrickCard> tcs, int curPosse) {
		int totalLeft = calculateStinksAndAirs(tcs, curPosse);
		ArrayList<TrickCard> ices = getIceCreamCards(tcs);
		totalLeft = calculateExpectedAfterIces(totalLeft, ices);
		return totalLeft;
	}


	public static int numLeftAfterStink(ArrayList<TrickCard> tcs, int curPosse) {
		for(int i = 0; i < tcs.size(); i++) {
			TrickCard curTrick = (TrickCard) tcs.get(i);
			if(curTrick.isStink()) {
				curPosse -= applyFraction(curPosse, curTrick);	
			}
		}
		return curPosse;
	}

	public static int numFreshened(ArrayList<TrickCard> tcs, int curPosse) {
		int numFreshened = 0;
		for(int i = 0; i < tcs.size(); i++) {
			TrickCard curTrick = (TrickCard) tcs.get(i);
			if(curTrick.isAir()) {
				numFreshened += applyFraction(curPosse, curTrick);
			}
		}
		return Math.min(numFreshened, curPosse);
	}

	private static ArrayList<TrickCard> getIceCreamCards(ArrayList<TrickCard> tcs) {
		ArrayList<TrickCard> ices = new ArrayList<TrickCard>();
		for(int i = 0; i < tcs.size(); i++) {
			TrickCard curTrick = (TrickCard) tcs.get(i);
			if(curTrick.isIceCream()) {
				ices.add((TrickCard) curTrick);
			}
		}
		return ices;
	}

	public static int numRadios(ArrayList<TrickCard> tcs) {
		int numRad = 0;
		for(int i = 0; i < tcs.size(); i++) {
			TrickCard curTrick = (TrickCard) tcs.get(i);
			if(curTrick.isRadio()) {
				numRad++;
			}
		}
		return numRad;
	}

	public static ArrayList<TrickCard> convertListToTrickCards(ArrayList<CardView> cvs) {
		if(cvs == null) {
			return null;
		}
		ArrayList<TrickCard> tcs = new ArrayList<TrickCard>();
		for(CardView cv:cvs) {
			tcs.add((TrickCard)cv.getCard());
		}
		return tcs;
	}

	public static int numAirs(ArrayList<TrickCard> tcs) {
		int numRad = 0;
		for(int i = 0; i < tcs.size(); i++) {
			TrickCard curTrick = (TrickCard) tcs.get(i);
			if(curTrick.isAir()) {
				numRad++;
			}
		}
		return numRad;
	}

	public static int numStinks(ArrayList<TrickCard> tcs) {
		int numRad = 0;
		for(int i = 0; i < tcs.size(); i++) {
			TrickCard curTrick = (TrickCard) tcs.get(i);
			if(curTrick.isStink()) {
				numRad++;
			}
		}
		return numRad;
	}

	public ArrayList<Card> getAllCards() {
		ArrayList<Card> allCards = new ArrayList<Card>();
		allCards.add(team);
		for(int i = 0; i < cards.size(); i++) {
			allCards.add((Card) cards.get(i));
		}
		return allCards;
	}

	public ArrayList<TrickCard> getTrickCards() {
		return (ArrayList<TrickCard>) cards.clone();
	}

	public String toString() {
		String str = team.toString() + ", with ";
		str += cards;
		return str;
	}

	public static void main(String[] args) {
		//quick test of the next boolean stuff
		TrickCard ice = new TrickCard("cards_ice.jpg", 1, 2, "ice");
		RandomGenerator rgen = RandomGenerator.getInstance();
		int count = 0;
		for(int i = 0; i < 1000; i++) {
			if(rgen.nextBoolean(ice.getValue())&&rgen.nextBoolean(ice.getValue())) {
				count++;
			}
		}
		System.out.println(count);
		System.out.println(2.0/4 * 1.0/3);
	}

}
