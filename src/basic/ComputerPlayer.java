package basic;
/*
 * File: Player.java
 * -----------------
 * File that holds the contents of the player's hand
 * Each player will hold an ArrayList of PlayerDecks
 */

import java.util.ArrayList;

import cards.CardView;
import cards.TrickCard;
import extras.CardGameUtils;
import extras.Debug;
import extras.GameUtils;


public class ComputerPlayer extends Player {
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
	
	public boolean chooseTrickCardToPlay(Player opponent, boolean isImmediate) {
		//I have a list of cards, each of those would need to be placed
		//I would need to keep track of whether the card could be placed
		ArrayList<PossibleMove> allMoves = getAllPossibleMoves(opponent);
		PossibleMove computerMove = chooseMoveToMake(allMoves);
		boolean couldMakeMove = computerMove != null;
		if(!isImmediate) {
			CardGameUtils.pause(Constants.BETWEEN_GAME_PAUSE/3);	
		}
		if(couldMakeMove) {
			fireCardAnimation(computerMove, opponent, Constants.THOUGHT_MOVE, isImmediate);
			//performMove(computerMove, opponent);
		}else{
			Debug.println("Couldn't make a turn");
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
		return GameUtils.getAllPossibleMoves(this, opponent);
	}	
}
