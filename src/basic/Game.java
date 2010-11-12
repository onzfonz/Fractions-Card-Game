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
		d = new Dealer("Tricks1Deck.txt", "Team1Deck.txt");
		p1 = new Player(d);
		opponent = new Player(d);
		board = p;
		playOneRound();
	}
	
	public Game(GamePanel p, PlayerListener pl) {
		d = new Dealer("Tricks1Deck.txt", "Team1Deck.txt");
		opponent = new ComputerPlayer(d, pl, true);
		p1 = new Player(d, pl, true, true);
	}
	
	public void playOneRound() {
		distributeCards();
//		board.repaint();
//		selectCardsToPlay();
//		calculateScore();
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
		int p1Score = p1.totalTeammates();
		int p2Score = opponent.totalTeammates();
		int difference = Math.abs(p1Score-p2Score);
		String prefix = "";
		String suffix = "";
		String postsuffix = "";
		String message = " had " + difference + " more teammate" + ((difference == 1)?"":"s") + " than ";
		if(p1Score > p2Score) {
			p1.updatePoints(difference);
			prefix = "You";
			suffix = "your opponent.";
			postsuffix = prefix;
		}else if(p2Score > p1Score) {
			opponent.updatePoints(difference);
			prefix = "Your opponent";
			suffix = "you.";
			postsuffix = "They";
		}else{
			return "You tied that round.  No points were handed out. Let's go to the next one.";
		}
		return prefix + message + suffix + "  " + postsuffix + " scored " + difference + " point" + ((difference == 1)?"":"s") + "!  Let's start the next round!";
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
