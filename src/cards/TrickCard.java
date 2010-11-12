package cards;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import cards.*;



public class TrickCard extends Card {
	private int num;
	private int den; 
	private String trickType;
	
	public TrickCard(String img, int n, int d, String tType) {
		super(img);
		num = n;
		den = d;
		trickType = tType;
		determineNameAndDescription();
	}
	
	public TrickCard(TrickCard tc) {
		this(tc.getImageName(), tc.getNumerator(), tc.getDenominator(), tc.getType());
	}
	
	public boolean isStink() {
		return matchesCategory("stink");
	}
	
	public boolean isAir() {
		return matchesCategory("air");
	}
	
	public boolean isDefense() {
		return isAir() || isRadio();
	}
	
	public boolean isMoney() {
		return matchesCategory("money");
	}
	
	public boolean isIceCream() {
		return matchesCategory("ice");
	}
	
	public boolean isRadio() {
		return matchesCategory("radio");
	}
	
	public String getType() {
		return trickType;
	}
	
	private boolean matchesCategory(String cat) {
		return trickType.equalsIgnoreCase(cat);
	}
	public double getValue() {
		return ((double) num)/den;
	}
	
	public int getDenominator() {
		return den;
	}
	
	public int getNumerator() {
		return num;
	}
	
	public String getOperation() {
		return trickType;
	}
	
	public String toString() {
		if(!isIceCream()) {
			return trickType + ":" + toFraction();
		}else{
			return trickType + "-" + toIce();
		}
		
	}
	
	public String toFraction() {
		return num + "/" + den;
	}
	
	private String toIce() {
		return num + "o:" + den + "p";
	}
	
	private void determineNameAndDescription() {
		if(isIceCream()) {
			setName("Ice Cream Truck");
			setDescription("Choose one of the other player's groups.  Put the pebbles in the bag.  If the other player chooses one of the bad pebbles, then the kids will run to the ice cream truck.");
		}else if(isStink()) {
			setName("Stink Bomb");
			setDescription("Choose one of the other player's groups.  Remove a fraction of teammates from that group");
		}else if(isAir()) {
			setName("Air Freshener");
			setDescription("Choose one of the other player's groups.  Protect a fraction of the teammates from that group");
		}else if(isRadio()) {
			setName("Radio");
			setDescription("Choose one your teammate cards that has an Ice Cream Truck On It.  That card now gets to remove two pebbles from the bag!");
		}else if(isMoney()) {
			setName("Money");
			setDescription("Use this card and combine it with other cards to buy more trick cards or teammates!");
		}
	}
	
	@Override
	public Object getTransferData(DataFlavor arg0)
			throws UnsupportedFlavorException, IOException {
		// TODO Auto-generated method stub
		if(arg0.getDefaultRepresentationClass() == TrickCard.class){
			return new TrickCard(this);
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
	
	/**
	 * @param args
	 */
	public static void main(String args[]) {
		TrickCard t1 = new TrickCard("cards_stink.jpg", 3, 5, "stink");
		System.out.println(t1.getValue());
	}
}
