package extras;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import basic.Dealer;
import basic.Game;
import basic.Player;
import deck.PlayDeck;

public class BoardPanel extends JPanel {
//	private Game game;
	
	public BoardPanel(int w, int h) {
		setPreferredSize(new Dimension(w, h));
		setOpaque(true);
		setBackground(Color.gray);
		repaint();
		//game = new Game(this);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
//		for(int i = 1; i <= 2; i++) {
//			Player p = game.getPlayer(i);
			//for(int j = 0; j < p.numDecks(); j++) {
//				PlayDeck pd = p.getPlayDeck(j);
//				ArrayList<Image> images = pd.getDeckImages();
//				for(int k = 0; k < images.size(); k++) {
//					Image img = images.get(k);
//					g.drawImage(img, j * img.getWidth(null), ((i-1) * (getHeight()/2) + k*img.getHeight(null)/5), null);
//				}
			//}
//		}
	}
	
	public void newGame() {
		repaint();
	}
}
