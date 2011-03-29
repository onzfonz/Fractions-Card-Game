package cards;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Scanner;

import basic.Constants;



public class TrickCard extends Card {
	protected int num;
	protected int den; 
	private String trickType;
	boolean isDecimal;
	
	public TrickCard(String img, int n, int d, String tType) {
		super(img);
		isDecimal = false;
		if(img.indexOf("Point") != -1) {
			isDecimal = true;
		}
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
	
	public boolean isCombo() {
		return containsCategory("combo");
	}
	
	public TrickCard getFirstCard() {
		if(isCombo()) {
			return getCard(0);
		}else{
			return this;
		}
	}
	
	public TrickCard getSecondCard() {
		if(isCombo()) {
			return getCard(1);
		}else{
			return this;
		}
	}
	
	private TrickCard getCard(int option) {
		String choice = extractChoiceInfo(option);
		int colonPos = choice.indexOf(":");
		String t = choice.substring(0, colonPos);
		int slashPos = choice.indexOf("/");
		String numerPart = choice.substring(colonPos+1, slashPos);
		int n = Integer.parseInt(numerPart);
		String denomPart = choice.substring(slashPos+1);
		int d = Integer.parseInt(denomPart);
		return new TrickCard(getImageName(), n, d, t);
	}
	
	//example of the string choice[air:1/2,stink:1/2]
	//example of the string choice[stink:2/2,ice:.5yes]
	//example of the string choice[ice:2/2, stink:1/2]
	private String extractChoiceInfo(int option) {
		int leftPos = 0;
		
		String s = getType();
		int rightPos = s.length();
		if(option == 0) {
			leftPos = s.indexOf("[");
			rightPos = s.indexOf(Constants.COMBO_FILE_DELIMITER);
		}else{
			leftPos = s.indexOf(Constants.COMBO_FILE_DELIMITER);
			rightPos = s.indexOf("]");
		}
		s = s.substring(leftPos+1, rightPos);
		return s.trim();
	}
	
	public String getType() {
		return trickType;
	}
	
	private boolean matchesCategory(String cat) {
		return trickType.equalsIgnoreCase(cat);
	}
	
	private boolean containsCategory(String cat) {
		return (trickType.indexOf(cat) != -1);
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
	
	public String toStream() {
		return trickType + ", " + imgName + ", " + num + ", " + den;
	}
	
	public String toFraction() {
		String s = num + "/" + den;
		if(isDecimal) {
			double dec = (double)num/den;
			String otherS = "" + dec;
			int pos = otherS.indexOf(".");
			s += " (" + otherS.substring(pos) + ") ";
		}
		return s;
	}
	
	private String toIce() {
		return num + "o:" + den + "p";
	}
	
	private void determineNameAndDescription() {
		if(isIceCream()) {
			setName(Constants.ICE_CREAM_NAME);
			setDescription(Constants.ICE_DESC);
		}else if(isStink()) {
			setName("Stink Bomb");
			setDescription(Constants.STINK_DESC);
		}else if(isAir()) {
			setName("Air Freshener");
			setDescription(Constants.AIR_DESC);
		}else if(isRadio()) {
			setName("Radio");
			setDescription(Constants.RADIO_DESC);
		}else if(isMoney()) {
			setName("Money");
			setDescription(Constants.MONEY_DESC);
		}
	}
	
	//@Override
	public Object getTransferData(DataFlavor arg0)
			throws UnsupportedFlavorException, IOException {
		// TODO Auto-generated method stub
		if(arg0.getDefaultRepresentationClass() == TrickCard.class){
			return new TrickCard(this);
		}
		return null;
	}

	//@Override
	public DataFlavor[] getTransferDataFlavors() {
		// TODO Auto-generated method stub
		DataFlavor[] flavas = new DataFlavor[1];
		flavas[0] = new DataFlavor(TrickCard.class, "Cards");
		return flavas;
	}

	//@Override
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
		TrickCard t1 = new TrickCard(Constants.HALF_STINK_FILENAME, 3, 5, "stink");
		System.out.println(t1.getValue());
		TrickCard t2 = new TrickCard("cards_Point.jpg", 1, 2, "air");
		System.out.println(t1.toFraction());
		System.out.println(t2.toFraction());
		TrickCard t3 = new TrickCard("cards_Point.jpg", 1, 4, "air");
		TrickCard t4 = new TrickCard("cards_Point.jpg", 3, 4, "air");
		System.out.println(t3.toFraction());
		System.out.println(t4.toFraction());
		TrickCard t5 = new TrickCard("cards.jpg", 3, 4, "combo[stink:1/2;air:1/2]");
		System.out.println(t5.isCombo());
	}
}
