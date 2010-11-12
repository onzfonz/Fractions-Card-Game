package extras;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import cards.CardViewOld;


public class CardVHandler extends TransferHandler {
	public int getSourceActions(JComponent c) {
		return MOVE;
	}
	
	public Transferable createTransferable(JComponent c) {
		if(c instanceof CardViewOld) {
			CardViewOld cv = (CardViewOld) c;
			return cv.getCard();
		}
		return null;
	}
	
	public void exportDone(JComponent c, Transferable t, int action) {
	    if (action == MOVE) {
	        //want to do something here where we can find where it is in the deck
	    }
	}

}
