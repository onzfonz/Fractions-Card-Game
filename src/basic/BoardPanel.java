package basic;
import java.awt.*;

import javax.swing.*;

import deck.PlayDeck;

public class BoardPanel extends JPanel {
	private Game game;
	
	public BoardPanel(int w, int h) {
		setPreferredSize(new Dimension(w, h));
		setOpaque(true);
		setBackground(Color.gray);
		repaint();
		//game = new Game(this);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		for(int i = 1; i <= 2; i++) {
			Player p = game.getPlayer(i);
			//for(int j = 0; j < p.numDecks(); j++) {
//				PlayDeck pd = p.getPlayDeck(j);
//				ArrayList<Image> images = pd.getDeckImages();
//				for(int k = 0; k < images.size(); k++) {
//					Image img = images.get(k);
//					g.drawImage(img, j * img.getWidth(null), ((i-1) * (getHeight()/2) + k*img.getHeight(null)/5), null);
//				}
			//}
		}
	}
	
	public void newRound() {
		game.playOneRound();
		repaint();
	}
	
	public void newGame() {
		//game = new Game(this);
		repaint();
	}
	
	// Board Panel debug functions: Take Out Later! 
	
	public void addTrick(int i) {
		Player p = game.getPlayer(i);
		Dealer d = game.getDealer();
		PlayDeck pd = p.getPlayDeck(0);
		pd.addTrickCard(d.dealTrickCard());
		repaint();
	}
	
	public void addTeam(int i) {
		Player p = game.getPlayer(i);
		Dealer d = game.getDealer();
		p.addTeammateCard(d.dealTeammateCard());
		repaint();
	}
	
	public void printStack(int player, int deckN) {
		Player p = game.getPlayer(player);
		System.out.println(p.getPlayDeck(deckN));
	}
}
