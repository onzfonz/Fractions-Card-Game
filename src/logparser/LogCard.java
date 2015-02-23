package logparser;

/* Class: LogGame
 * --------------
 * This is the class that will in a way store all of the information in the last four tables
 * for the fractions game.  
 * Each object will be an instantiation of one game.
 * Because of the log game will be storing a few things, from all of the tables, as well as a way to look
 * up the particular log, that we are looking for.
 */

public class LogCard {
	private String[] realVals;
	private int cardId;
	
	public LogCard(int cid, String[] vals) {
		cardId = cid;
		realVals = vals;
	}
	
	@Override
	public String toString() {
		return ""+cardId+", "+stringArr(realVals);
	}
	
	private String stringArr(String[] arr) {
		String result = "";
		for(int i = 0; i < arr.length-1; i++) {
			result += arr[i] + ", ";
		}
		return result + arr[arr.length-1];
	}
	
	public int getCid() {
		return cardId;
	}
	
	public String getNumPpl() {
		return realVals[3];
	}
	
	public boolean isCombo() {
		return getCardType(-1).equals("combo");
	}
	
	public boolean isRadio() {
		return getCardType(-1).equals("radio");
	}
	
	public boolean isAttack(int i) {
		return isStink(i) || isIce(i);
	}
	
	public boolean isAir(int i) {
		return getCardType(i).equals("air");
	}
	
	public boolean isStink(int i) {
		return getCardType(i).equals("stink");
	}
	
	public boolean isIce(int i) {
		return getCardType(i).equals("ice");
	}
	
	public String getCardType(int i) {
		if(i == -1) {
			return realVals[1];
		}
		String s = realVals[2];
		int semiPos = s.indexOf(";")+1;
		int startPos = i*semiPos;
		int endPos = s.indexOf(":", startPos);
		return s.substring(startPos, endPos);
	}
	
	public int getNumerator() {
		return Integer.parseInt(realVals[3]);
	}
	
	public int getDenominator() {
		return Integer.parseInt(realVals[4]);
	}
}
