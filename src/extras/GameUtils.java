package extras;

import java.util.ArrayList;

import basic.Player;
import basic.PossibleMove;
import cards.CardView;
import cards.TrickCard;
import deck.DeckView;

public class GameUtils {
	public static String formQuestion(int num, int den, int value) {
		return "What is " + num + "/" + den + " of " + value + "?";
	}
	
	public static String extractQuestion(String line) {
		int startPos = extractNumeratorPos(line);
		int endPos = extractPplEndPos(line);
		return "What is " + line.substring(startPos, endPos) + "?";
	}
	
	public static String determineIfDecimal(String val) {
		if(val.indexOf(".") != -1) {
			return "1";
		}
		return "0";
	}
	
	public static int generateAnswer(String question) {
		int num = GameUtils.extractNumerator(question);
		int den = GameUtils.extractDenominator(question);
		int ppl = GameUtils.extractPeople(question);
		return solveEasyFraction(num, den, ppl);
	}

	//Does not take into account simplifying the fraction first
	//Can do this later if need be.
	public static int solveEasyFraction(int num, int den, int value) {
		if(value % den != 0) {
			return -1;
		}
		return((value / den) * num);
	}
	
	/* Assumes that there is only one forward slash and that the
	 * numbers needed are:  num/den of ppl?  Also assumes spaces after fraction, but can handle if question
	 * starts before without a space.
	 */
	public static int extractNumerator(String question) {
		int pos = question.indexOf("/");
		if(pos == -1) {
			return -1;
		}
		int spacePos = extractNumeratorPos(question);
		try {
			return Integer.parseInt(question.substring(spacePos, pos));
		}catch(NumberFormatException e) {
			System.out.println("ERRROR! question:" + question + ", spacePos:" + spacePos + ", pos:" + pos);
		}
		return -1;
	}
	
	public static int extractNumeratorPos(String question) {
		int pos = question.indexOf("/");
		return question.lastIndexOf(" ", pos) + 1;
	}
	
	public static int extractDenominator(String question) {
		int pos = question.indexOf("/");
		int spacePos = question.indexOf(" ", pos);
		if(pos == -1 || spacePos == -1) {
			return -1;
		}
		return Integer.parseInt(question.substring(pos+1, spacePos));
	}
	
	public static int extractPeople(String question) {
		int pos = extractPplEndPos(question);	
		int spacePos = question.lastIndexOf(" ", pos);
		if(spacePos == -1) {
			return -1;
		}
		return Integer.parseInt(question.substring(spacePos+1, pos));
	}
	
	public static int extractPplEndPos(String question) {
		int pos = question.indexOf("?");
		if(pos == -1) {
			String temp = "of ";
			pos = question.indexOf(temp);
			pos += temp.length();
			while(pos < question.length() && Character.isDigit(question.charAt(pos))) {
				pos++;
			}
		}
		return pos;
	}
	
	public static int extractPplStartPos(String question) {
		return question.indexOf(" of ") + 4;
	}
	
	public static boolean legalHumanCardMove(DeckView deck, CardView card) {
		Player deckPlayer = deck.getPlayer();
		boolean humansDeck = deckPlayer.isHuman();
		if(card.isCombo()) {
			return legalHumanComboCardMove(deck, card, humansDeck);
		}
		if(!legalPlacementOnDeck(card, humansDeck)) return false;
		return deck.couldAddTrickCard(card);
	}
	
	public static boolean legalPlacementOnDeck(CardView cv, boolean humansDeck) {
		return legalPlacementOnDeck((TrickCard) cv.getCard(), humansDeck);
	}
	
	public static boolean legalPlacementOnDeck(TrickCard card, boolean humansDeck) {
		return (card.isAttack() && !humansDeck || card.isDefense() && humansDeck);
	}
	
	public static boolean legalHumanComboCardMove(DeckView deck, CardView card, boolean humansDeck) {
		assert(card.isComboCard());
		ArrayList<TrickCard> tcs = card.getCards();
		for(TrickCard tc:tcs) {
			Debug.println("playing: " + tc + " on " + deck.getPlayDeck());
			if(legalPlacementOnDeck(tc, humansDeck) && deck.couldAddTrickCard(tc)) {
				return true;
			}
		}
		return false;
	}
	
	public static ArrayList<PossibleMove> getAllPossibleMoves(Player p, Player opponent) {
		ArrayList<PossibleMove> allMoves = new ArrayList<PossibleMove>();
		ArrayList<CardView> cards = new ArrayList<CardView>(p.getTrickHand());
		for(CardView cv:cards) {
			TrickCard tc = (TrickCard) cv.getCard();
			if(tc.isCombo()) {
				buildCardMoves(allMoves, cv, tc.getFirstCard(), p, opponent);
				buildCardMoves(allMoves, cv, tc.getSecondCard(), p, opponent);
			}else{
				buildCardMoves(allMoves, cv, tc, p, opponent);
			}
		}
		Debug.println("Generated " + allMoves.size() + " moves for player's Hand");
		return allMoves;
	}
	
	private static void buildCardMoves(ArrayList<PossibleMove> movesSoFar, CardView cv, TrickCard tc, Player p, Player opponent) {
		ArrayList<DeckView> oppoDecks = opponent.getAllDecks();
		ArrayList<DeckView> decks = p.getAllDecks();
		ArrayList<PossibleMove> someMoves = null;
		if(tc.isAir() || tc.isRadio()) {
			someMoves = buildPossibleMoves(cv, tc, decks, p);
		}else{
			someMoves = buildPossibleMoves(cv, tc, oppoDecks, p);
		}
		movesSoFar.addAll(someMoves);
	}
	
	private static ArrayList<PossibleMove> buildPossibleMoves(CardView cv, TrickCard tc, ArrayList<DeckView> dvs, Player p) {
		ArrayList<PossibleMove> moves = new ArrayList<PossibleMove>();
		for(int i = 0; i < dvs.size(); i++) {
			DeckView dv = dvs.get(i);
			PossibleMove m = couldBePlayed(dv, cv, tc, p);
			if(m != null) {
				moves.add(m);
			}
		}
		return moves;
	}
	
	public static PossibleMove couldBePlayed(DeckView dv, CardView cv, TrickCard tc, Player p) {
		PossibleMove move = null;
		if(dv.getPlayer().couldAddToPlayDeck(dv, tc, p)) {
			int difference = dv.getPotentialScoreChange(cv);
			move = new PossibleMove(dv, cv, tc, difference);
		}
		return move;
	}
}
