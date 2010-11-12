package pebblebag;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import deck.DeckView;

public class PebbleCloser implements ActionListener {
	private PebblePanel pPanel;
	private DeckView deck;
	private boolean kidsRun;
	
	public PebbleCloser(PebblePanel pp, DeckView dv, boolean kidsR) {
		pPanel = pp;
		deck = dv;
		kidsRun = kidsR;
	}
	//@Override
	public void actionPerformed(ActionEvent arg0) {
		pPanel.fireIceCreamTruckDone(deck, kidsRun);
	}

}
