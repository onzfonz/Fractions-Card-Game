package transform;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import cards.TrickCard;


public class TrickCardTransferable implements Transferable {
	public TrickCard card;
	
	public TrickCardTransferable(TrickCard c) {
		card = c;
	}
	
	@Override
	public Object getTransferData(DataFlavor arg0)
			throws UnsupportedFlavorException, IOException {
		// TODO Auto-generated method stub
		if(arg0.getDefaultRepresentationClass() == TrickCard.class){
			return new TrickCard(card);
		}
		return null;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		// TODO Auto-generated method stub
		DataFlavor[] flavas = new DataFlavor[1];
		flavas[0] = new DataFlavor(TrickCard.class, "Cards");
		return flavas;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		// TODO Auto-generated method stub
		if(flavor.getDefaultRepresentationClass() == TrickCard.class) {
			return true;
		}
		return false;
	}

}
