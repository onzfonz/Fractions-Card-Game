package basic;
/*
 * File: Game.java
 * ---------------
 * This is the main file that deals with having the game engine.
 * This is not responsible for drawing any of the information, but rather is
 * the engine that drives the model for the data in the game.
 */


import cards.TrickCard;

public class Game {
	
	private Dealer d;
	private Player p1;
	private Player opponent;
	private GamePanel board;
	
	public Game(GamePanel p) {
		d = DealerFactory.getNewDealer();
		p1 = new Player(d);
		opponent = new Player(d);
		board = p;
		playOneRound();
	}
	
	public Game(GamePanel p, PlayerListener pl) {
		d = DealerFactory.getNewDealer();
		opponent = new ComputerPlayer(d, pl, true);
		p1 = new Player(d, pl, true, true);
	}
	
	public Game(GamePanel p, PlayerListener pl, boolean againstComputer) {
		d = DealerFactory.getNewDealer();
		if(againstComputer) {
			opponent = new ComputerPlayer(d, pl, againstComputer);
		}else{
			opponent = new Player(d, pl, againstComputer, false);
		}
		p1 = new Player(d, pl, againstComputer, true);
	}
	
	public void playOneRound() {
		distributeCards();
//		board.repaint();
//		selectCardsToPlay();
//		calculateScore();
	}
	
	public void clearTheRound() {
		p1.clearPlayerCards();
		opponent.clearPlayerCards();
	}
	
	private void distributeCards() {
		p1.newCardRound();
		opponent.newCardRound();
	}
	
	private void selectCardsToPlay() {
		while(true) {
			System.out.println("Player 1's turn");
			TrickCard tP1 = playTrickCard(p1, opponent);
			System.out.println("Player 2's turn");
			TrickCard tP2 = playTrickCard(opponent, p1);
			if(tP1 == null && tP2 == null) {
				break;
			}
		}
	}
	
	public String calculateScore() {
		int p1Score = getMyRound();
		int p2Score = getOppoRound();
		int difference = Math.abs(p1Score-p2Score);
		String prefix = "";
		String suffix = "";
		String postsuffix = "";
		String message = Constants.PARTS_HAD + difference + Constants.PARTS_MORE_TEAMMATE + ((difference == 1)?"":Constants.PARTS_PLURAL_SUFFIX) + Constants.PARTS_THAN;
		if(p1Score > p2Score) {
			p1.updatePoints(difference);
			prefix = Constants.PARTS_PREFIX_YOU;
			suffix = Constants.PARTS_SUFFIX_THEM;
			postsuffix = prefix;
		}else if(p2Score > p1Score) {
			opponent.updatePoints(difference);
			prefix = Constants.PARTS_PREFIX_THEM;
			suffix = Constants.PARTS_SUFFIX_YOU;
			postsuffix = Constants.PARTS_POST_THEY;
		}else{
			return Constants.PARTS_TIE;
		}
		return prefix + message + suffix + "  " + postsuffix + " scored " + difference + " point" + ((difference == 1)?"":"s") + "!  Let's start the next round!";
	}
	
	public int getMyRound() {
		return p1.totalTeammates();
	}
	
	public int getOppoRound() {
		return opponent.totalTeammates();
	}
	
	private TrickCard playTrickCard(Player p, Player otherP) {
		TrickCard tP = p.chooseTrickToPlay();
		if(tP != null) {
			otherP.addToPlayDeck(tP);
		}
		return tP;
	}
	
	public Player getPlayer(int i) {
		if(i == 1) {
			return p1;
		}
		return opponent;
	}
	
	public Player getHumanPlayer() {
		return p1;
	}
	
	public Player getOpposingPlayer() {
		return opponent;
	}
	
	public boolean isOpponent(Player p) {
		return p == opponent;
	}
	
	public Dealer getDealer() {
		return d;
	}
	
//	public static void main(String[] args) {
//		new Game();
//	}
}
