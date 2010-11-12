package basic;
/*
 * File: Player.java
 * -----------------
 * File that holds the contents of the player's hand
 * Each player will hold an ArrayList of PlayerDecks
 */

import java.util.*;

import cards.*;
import deck.*;


public class ComputerPlayer extends Player implements CardGameConstants{
	public ComputerPlayer(Dealer dlr) {
		super(dlr);
	}
	
	public ComputerPlayer(Dealer dlr, PlayerListener pl) {
		super(dlr, pl);
	}
	
	public ComputerPlayer(Dealer dlr, PlayerListener pl, boolean startRound) {
		super(dlr, pl, startRound, false);
	}
	
	/*
	 * General strategy for this is to figure out all of the possible plays, and place
	 * them into an ArrayList.  Then as a default for a lame computer player
	 * we will have the computer player randomly pick one of the valid options in the
	 * arrayList.
	 */
	
	public boolean chooseTrickCardToPlay(Player opponent) {
		//I have a list of cards, each of those would need to be placed
		//I would need to keep track of whether the card could be placed
		ArrayList<PossibleMove> allMoves = getAllPossibleMoves(opponent);
		PossibleMove computerMove = chooseMoveToMake(allMoves);
		boolean couldMakeMove = computerMove != null;
		if(couldMakeMove) {
			fireCardAnimation(computerMove, opponent);
			//performMove(computerMove, opponent);
		}else{
			if(CardGameConstants.DEBUG_MODE) {
				System.out.println("Couldn't make a turn");
			}
		}
		return couldMakeMove;
	}
	
	protected PossibleMove chooseMoveToMake(ArrayList<PossibleMove> allMoves) {
		if(allMoves == null || allMoves.size() == 0) {
			return null;
		}
		int randIndex = (int) (Math.random()*allMoves.size());
		return allMoves.get(randIndex);
	}
	
	protected ArrayList<PossibleMove> getAllPossibleMoves(Player opponent) {
		ArrayList<DeckView> oppoDecks = opponent.getAllDecks();
		ArrayList<PossibleMove> allMoves = new ArrayList<PossibleMove>();
		for(CardView cv:trickHand) {
			TrickCard tc = (TrickCard) cv.getCard();
			ArrayList<PossibleMove> someMoves = null;
			if(tc.isAir() || tc.isRadio()) {
				someMoves = buildPossibleMoves(cv, decks);
			}else{
				someMoves = buildPossibleMoves(cv, oppoDecks);
			}
			allMoves.addAll(someMoves);
		}
		if(CardGameConstants.DEBUG_MODE){
			System.out.println("Generated " + allMoves.size() + " moves for Computer's Hand");
		}
		return allMoves;
	}	
}
