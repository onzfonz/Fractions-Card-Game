package manipulatives;

import java.awt.Graphics;
import javax.swing.JPanel;

import cards.CardView;
import deck.DeckView;

public class ManDeckViewPanel extends JPanel {
	private DeckView view;
	private CardView cardPlayed;
	
	public ManDeckViewPanel() {
		super();
	}
	
	public ManDeckViewPanel(DeckView dv, CardView cp) {
		super();
		view = dv;
		cardPlayed = cp;
	}
	
	public void setDeckView(DeckView dv) {
		view = dv;
	}
	
	public DeckView getDeckView() {
		return view;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(view != null) {
			view.drawAbsoluteDeck(g, cardPlayed, getWidth(), getHeight());
		}
	}
}
