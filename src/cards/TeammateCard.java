package cards;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;



public class TeammateCard extends Card {
	private int value;
	
	public TeammateCard(String ci, String n, String d, int v) {
		super(ci);
		setName(n);
		description = d;
		value = v;
	}
	
	public TeammateCard(TeammateCard tc) {
		this(tc.getImageName(), tc.getName(), tc.getDescription(), tc.getValue());
	}
	
	public int getValue() {
		return value;
	}
	
	public String toString() {
		if(!isShadowPlayer()) {
			return name + ":" + value;
		}else{
			return name + ":" + description;
		}
	}
	
	public String toReadableText() {
		return toString();
	}
	
	public String toStream() {
		return name + ", " + description + ", " + imgName + ", " + value;
	}
	
	public boolean isShadowPlayer() {
		return name.equalsIgnoreCase("Shadow Players");
	}
	
	//@Override
	public Object getTransferData(DataFlavor arg0)
			throws UnsupportedFlavorException, IOException {
		// TODO Auto-generated method stub
		if(arg0.getDefaultRepresentationClass() == TeammateCard.class){
			return this;
		}
		return null;
	}

	//@Override
	public DataFlavor[] getTransferDataFlavors() {
		// TODO Auto-generated method stub
		DataFlavor[] flavas = new DataFlavor[1];
		flavas[0] = new DataFlavor(TeammateCard.class, "Cards");
		return flavas;
	}

	//@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		// TODO Auto-generated method stub
		if(flavor.getDefaultRepresentationClass() == TeammateCard.class) {
			return true;
		}
		return false;
	}
}
