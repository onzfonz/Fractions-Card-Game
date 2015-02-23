package deck;
/*
 * PlayDeck.java
 * -------------
 * As of right now, PlayDeck is the main java file that
 * has the right properties for whether or not something can be applied to one of the decks.
 * It also has the logic that applies based on what trick cards are applied to a deck.
 */

import java.util.ArrayList;

import basic.Constants;
import basic.PebbleBag;

import cards.Card;
import cards.CardView;
import cards.TeammateCard;
import cards.TrickCard;


import extras.*;

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
	
	/**
	 * Note: This method is player agnostic, meaning that if the player tries to apply a stink bomb
	 * on itself, it would return true.  Also look at DeckView which calls this method using Delegation
	 * @param c a trick card
	 * @return a boolean telling you whether or not the card could be placed without adding it to the deck
	 */

	public boolean couldAddTrickCard(TrickCard c) {
		boolean noRemainder = false;
		if(c.isStink()) {
			noRemainder = canApplyStink(c);
		}else if(c.isAir()) {
			noRemainder = canApplyAir(c);
		}else if(c.isIceCream()) {
			noRemainder = true;
		}else if(c.isRadio()) {
			ArrayList<TrickCard> tCards = cards;
			for(TrickCard tc:tCards) {
				if(tc.isIceCream()) {
					return true;
				}
			}
			errorMsg = Constants.ERROR_PLACING_RADIO;
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
		String prefix = Constants.MAN_MSG_PREFIX;
		String fraction = tc.toFraction();
		String numPeople = "" + getNumForFraction(tc.isStink(), useAllCards);
		return prefix + fraction + Constants.MAN_MSG_OF + numPeople + "?";
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
		return getIceCreamCards(cards);
	}

	@SuppressWarnings("unchecked")
	private int numRadios() {
		return numRadios(cards);
	}

	@SuppressWarnings("unchecked")	
	private int numAirs() {
		return numAirs(cards);
	}

	@SuppressWarnings("unchecked")
	private int numStinks() {
		return numStinks(cards);
	}


	private boolean canApplyStink(TrickCard c) {
		int numStillLeft = calculateStinksAndAirs();
		boolean isDivisible = numStillLeft % c.getDenominator() == 0;
		if(!isDivisible) {
			if(numStinks() > 0) {
				//errorMsg = "You cannot use this " + c + "card because this deck now only has " + numStillLeft + " teammate" + ((numStillLeft != 1)? "s":"");
				errorMsg = Constants.ERROR_STINKS_LEFT_DIV;
			}else{
				//errorMsg = "A " + c + " card has to be evenly divisible to be used on the " + team;
				errorMsg = generateNotAWholeNumberMessage();
			}
		}
		return isDivisible;
	}
	
	private String generateNotAWholeNumberMessage() {
		return Constants.ERROR_NOT_WHOLE_NUM + Constants.ERROR_NOT_WHOLE_NUM_DIV;
	}

	private boolean canApplyAir(TrickCard c) {
		boolean isDivisible = team.getValue() % c.getDenominator() == 0;
		boolean someStinkies = (team.getValue() - calculateStinksAndAirs()) > 0;
		if(!isDivisible) {
			//errorMsg = "An " + c + " card has to be evenly divisible to be used on the " + team;
			errorMsg = generateNotAWholeNumberMessage();
		}else if(!someStinkies) {
			if(numStinks() > 0) {
				//errorMsg = "An " + c + " card has to have some stinky teammates before you can use it";
				errorMsg = Constants.ERROR_NO_STINKERS_LEFT;
			}else{
				//errorMsg = "There must be a stink card placed on this deck before you can use an " + c + "card";
				errorMsg = Constants.ERROR_NO_STINK_CARDS;
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
	
	public int[] calculateStinksAndAirsSeparately(boolean calcAllAirs) {
		return calculateStinksAndAirsSeparately(cards, team.getValue(), calcAllAirs);
	}

	public int calculateDeck() {
		return calculateStinksAndAirs();
		//		if(kidsRunToTruck()) {
		//			return 0;
		//		}else{
		//		}
	}
	
	public int calculateDeckMinusTop() {
		ArrayList<TrickCard> tcs = new ArrayList<TrickCard>();
		for(int i = 0; i < cards.size()-1; i++) {
			tcs.add((TrickCard) cards.get(i));
		}
		return calculateStinksAndAirs(tcs, team.getValue());
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

	/* expected as in the expectation after many tries.  so a 2:2 ice would be calculated as 1/2 */
	public static int calculateExpectedAfterIces(int totalLeft, ArrayList<TrickCard> ices) {
		double numLeft = totalLeft;
		for(TrickCard ice:ices) {
			numLeft = applyHypoIceFraction(numLeft, ice);
		}
		return (int) Math.round(numLeft);
	}

	private static int applyFraction(int teammates, TrickCard t) {
		return GameUtils.solveEasyFraction(t.getNumerator(), t.getDenominator(), teammates);
	}

	/* What we are calculating here is expected value after many many tries,
	 * We are not making a chance decision and then subtracting all of the units or keeping them.
	 */
	private static double applyHypoIceFraction(double teammates, TrickCard t) {
		return (teammates / ((t.getDenominator()+t.getNumerator()))* t.getDenominator());
	}

	public static int applyHypoNumbers(double teammates, int num, int den) {
		return (int) Math.round((teammates / ((num+den)))*den);
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
		return calculateStinksAndAirsSeparately(tcs, curPosse, false);
	}
	
	public static int[] calculateStinksAndAirsSeparately(ArrayList<TrickCard> tcs, int curPosse, boolean drawAllAirs) {
		int posseLeft = curPosse;
		int[] stinkAirs = new int[2];
		for(TrickCard tc: tcs) {
			posseLeft = playCardOnTeam(posseLeft, curPosse, tc, stinkAirs, drawAllAirs);
		}
		return stinkAirs;
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
		return playCardOnTeam(posse, maxPosse, tc, stinkAir, false);
	}
	
	public static int playCardOnTeam(int posse, int maxPosse, TrickCard tc, int[] stinkAir, boolean drawAllAirs) {
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
				stinkAir[1] = Math.min(stinkAir[1], maxPosse);
				if(!drawAllAirs) {
					stinkAir[1] = Math.min(stinkAir[1], oldStink);
				}
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
			TrickCard curTrick = tcs.get(i);
			if(curTrick.isStink()) {
				curPosse -= applyFraction(curPosse, curTrick);	
			}
		}
		return curPosse;
	}

	public static int numFreshened(ArrayList<TrickCard> tcs, int curPosse) {
		int numFreshened = 0;
		for(int i = 0; i < tcs.size(); i++) {
			TrickCard curTrick = tcs.get(i);
			if(curTrick.isAir()) {
				numFreshened += applyFraction(curPosse, curTrick);
			}
		}
		return Math.min(numFreshened, curPosse);
	}

	private static ArrayList<TrickCard> getIceCreamCards(ArrayList<TrickCard> tcs) {
		ArrayList<TrickCard> ices = new ArrayList<TrickCard>();
		for(int i = 0; i < tcs.size(); i++) {
			TrickCard curTrick = tcs.get(i);
			if(curTrick.isIceCream()) {
				ices.add(curTrick);
			}
		}
		return ices;
	}

	public static int numRadios(ArrayList<TrickCard> tcs) {
		int numRad = 0;
		for(int i = 0; i < tcs.size(); i++) {
			TrickCard curTrick = tcs.get(i);
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
			TrickCard curTrick = tcs.get(i);
			if(curTrick.isAir()) {
				numRad++;
			}
		}
		return numRad;
	}

	public static int numStinks(ArrayList<TrickCard> tcs) {
		int numRad = 0;
		for(int i = 0; i < tcs.size(); i++) {
			TrickCard curTrick = tcs.get(i);
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

	@Override
	public String toString() {
		String str = team.toString() + ", with ";
		str += cards;
		return str;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof PlayDeck)) return false;
		PlayDeck pd = (PlayDeck) o;
		return pd.calculateDeck() == calculateDeck();
	}

	public static void main(String[] args) {
		//quick test of the next boolean stuff
		TrickCard ice = new TrickCard(Constants.TWO_TWO_ICE_FILENAME, 1, 2, "ice");
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
