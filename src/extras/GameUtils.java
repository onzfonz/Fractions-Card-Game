package extras;

import java.util.ArrayList;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

import basic.Constants;
import basic.Player;
import basic.PossibleMove;
import cards.CardView;
import cards.TrickCard;
import deck.DeckView;

public class GameUtils {
	private static final String TRUE_STR = "1";
	private static final String FALSE_STR = "0";
	
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
			return TRUE_STR;
		}
		return FALSE_STR;
	}
	
	public static boolean isQuestionDecimalQuestion(String question) {
		return determineIfDecimal(question).equals(TRUE_STR);
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
		return question.indexOf(Constants.MAN_MSG_OF) + 4;
	}
	
	public static boolean legalHumanCardMove(DeckView deck, CardView card) {
		return legalHumanCardMoveExplained(deck, card) == Constants.MOVE_OK;
	}
	
	//this is pretty much legalHumanCardMove but with 3 different results
	//returning -1 means it's not being legal at all (tried to place a stink on yourself
	//0 means it's legal game wise, but illegal via the fraction
	//1 means it's both legal game wise and legal via the fraction
	public static int legalHumanCardMoveExplained(DeckView deck, CardView card) {
		Player deckPlayer = deck.getPlayer();
		boolean humansDeck = deckPlayer.isHuman();
		if(card.isCombo()) {
			return legalHumanComboCardMoveExplained(deck, card, humansDeck);
		}
		if(!legalPlacementOnDeck(card, deck, humansDeck)) return Constants.MOVE_ILLEGAL;
		if(!deck.couldAddTrickCard(card)) {
			return Constants.MOVE_BAD_FRACTION;
		}
		return Constants.MOVE_OK;
	}
	
	public static boolean legalPlacementOnDeck(CardView cv, DeckView dv, boolean humansDeck) {
		return legalPlacementOnDeck((TrickCard) cv.getCard(), dv, humansDeck);
	}
	
	public static boolean legalPlacementOnDeck(TrickCard card, DeckView dv, boolean humansDeck) {
		if(card.isCombo()) return ((legalPlacementOnDeck(card.getFirstCard(), dv, humansDeck) || (legalPlacementOnDeck(card.getSecondCard(), dv, humansDeck))));
		if (!(card.isAttack() && !humansDeck || card.isDefense() && humansDeck)) return false;
		if(card.isRadio() || card.isAir() && !dv.hasStinkyPpl()) return false; 
		return true;
	}
	
	public static boolean legalHumanComboCardMove(DeckView deck, CardView card, boolean humansDeck) {
		return legalHumanComboCardMoveExplained(deck, card, humansDeck) == Constants.MOVE_OK;
	}
	
	public static int legalHumanComboCardMoveExplained(DeckView deck, CardView card, boolean humansDeck) {
		assert(card.isComboCard());
		ArrayList<TrickCard> tcs = card.getCards();
		int bestMoveSoFar = Constants.MOVE_ILLEGAL;
		for(TrickCard tc:tcs) {
			Debug.println("playing: " + tc + " on " + deck.getPlayDeck());
			if(legalPlacementOnDeck(tc, deck, humansDeck)) {
				bestMoveSoFar = Constants.MOVE_BAD_FRACTION;
				if(deck.couldAddTrickCard(tc)) {
					return Constants.MOVE_OK;
				}
			}
		}
		return bestMoveSoFar;
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
