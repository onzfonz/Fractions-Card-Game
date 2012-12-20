package basic;
/*
 * File: Player.java
 * -----------------
 * File that holds the contents of the player's hand
 * Each player will hold an ArrayList of PlayerDecks
 */

import java.util.ArrayList;

import extras.CardGameUtils;
import extras.Debug;
import extras.GameUtils;
import extras.RandomGenerator;


public class ComputerPlayer extends Player {
	private RandomGenerator rgen;
	
	public ComputerPlayer(Dealer dlr) {
		super(dlr);
		computerSetup();
	}
	
	public ComputerPlayer(Dealer dlr, PlayerListener pl) {
		super(dlr, pl);
		computerSetup();
	}
	
	public ComputerPlayer(Dealer dlr, PlayerListener pl, boolean startRound) {
		super(dlr, pl, startRound, false);
		computerSetup();
	}
	
	private void computerSetup() {
		rgen = RandomGenerator.getInstance();
		if(Constants.DEBUG_MODE) {
			rgen.setSeed(1);
		}
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
		int randIndex = rgen.nextInt(0, allMoves.size()-1);
		return allMoves.get(randIndex);
	}
	
	protected ArrayList<PossibleMove> getAllPossibleMoves(Player opponent) {
		return GameUtils.getAllPossibleMoves(this, opponent);
	}	
}
