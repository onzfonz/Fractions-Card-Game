package logparser;

import java.util.HashMap;

import extras.GameUtils;

/* Class: LogGame
 * --------------
 * This is the class that will in a way store all of the information in the last four tables
 * for the fractions game.  
 * Each object will be an instantiation of one game.
 * Because of the log game will be storing a few things, from all of the tables, as well as a way to look
 * up the particular log, that we are looking for.
 */

public class LogState implements SQLType{
	int gid;
	boolean lastLogBeforeNewDeal;
	LogPair plog;
	HashMap<String, LogCard> cardMap;
	String[] pteamids;
	String[] pnumLeft;
	String[] pteamOrig;
	String[] ptrickids;
	String[] ptrickNames;
	//Might have a gamestate inserted in here.
	
	public LogState(LogPair pl, int game, String[] pteam, String[] pppl, String[] ptrick, HashMap<String, LogCard> cMap, String[] ptorig, String[] pNames) {
		plog = pl;
		gid = game;
		pteamids = pteam;
		pnumLeft = pppl;
		ptrickids = ptrick;
		pteamOrig = ptorig;
		ptrickNames = pNames;
		cardMap = cMap;
		lastLogBeforeNewDeal = false;
	}
	
	public LogState(int game, HashMap<String, LogCard> cMap) {
		gid = game;
		cardMap = cMap;
		plog = null;
		pteamids = new String[4];
		pnumLeft = new String[4];
		pteamOrig = new String[4];
		ptrickids = new String[8];
		ptrickNames = new String[8];
	}
	
	//This is for the log that we do when we have to change it into that state.
	public String toString() {
		return ""+gid+", "+plog.getPairLogId()+", "+stringArr(pteamids)+", "+stringArr(pnumLeft)+", "+stringArr(ptrickids);
	}
	
	public String[] toSQLString() {
		return DBUtils.convertCommaStrToArray(toString());
	}
	
	public String boolStr(boolean var) {
		if(var) {
			return "1";
		}
		return "0";
	}
	
	public void setAsLastLog() {
		lastLogBeforeNewDeal = true;
	}
	
	private String stringArr(String[] arr) {
		String result = "";
		for(int i = 0; i < arr.length-1; i++) {
			result += arr[i] + ", ";
		}
		return result + arr[arr.length-1];
	}
	
	//myTeam, myHand, move, radios, chip
	public LogState calculateLogStateFromPairAction(LogPair lp, int pairIndex) {
		String type = lp.getType();
		LogState newLs = deepCopy(this, lp);
		if(type.equals(DBUtils.VALID_MOVES[0])) {
			LogCard[] cids = extractCardIdsFromLog(lp.getContent());
			calcMyHand(cids, newLs, pairIndex, lp.getContent());
		}else if(type.equals(DBUtils.VALID_MOVES[1])) {
			LogCard[] cids = extractCardIdsFromLog(lp.getContent());
			calcMyTeam(cids, newLs, pairIndex);
		}else if(type.equals(DBUtils.VALID_MOVES[2])) { //move
			int trickIndex = getTrickIndex(lp);
			int teamIndex = getTeamIndex(lp);
			calcMove(newLs, trickIndex, teamIndex, pairIndex, lp);
		}else if(type.equals(DBUtils.VALID_MOVES[3])) { //radios
			int radiosToRemove = Integer.parseInt(lp.getContent());
			calcRemoveRadios(newLs, radiosToRemove, pairIndex);
		}else if(type.equals(DBUtils.VALID_MOVES[4])) { //chip ignore value kids must have run to truck if you get here
			calcIceCreamWorked(newLs, pairIndex);
		}
		return newLs;
	}
	
	private void calcMove(LogState newLs, int trickIndex, int teamIndex, int pairIndex, LogPair lp) {
		int indexSection = ptrickNames.length/2;
		LogCard lc = cardMap.get(ptrickNames[trickIndex + indexSection*pairIndex]);
		newLs.removeTrickCard(trickIndex, pairIndex);
		newLs.calcChangeToTeam(teamIndex, pairIndex, lc, lp);		
	}
	
	private int calcIndex(int index, int pairIndex, String[] arr) {
		int indexSection = arr.length/2;
		return index + (pairIndex*indexSection);
	}
	
	
	private void calcChangeToTeam(int teamIndex, int pairIndex, LogCard lc, LogPair lp) {
		int optIndex = -1;
		if(lc.isCombo()) {
			optIndex = Integer.parseInt(lp.getContent().charAt(2)+"");
		}
		if(lc.isIce(optIndex)) return;  //ignore doing anything to the ice cream card
		int num = lc.getNumerator();
		int den = lc.getDenominator();
		int index = calcIndex(teamIndex, pairIndex, pteamOrig);
		int ppl = 0;
		if(lc.isAttack(optIndex)) { //flip pairIndex if an Attack
			pairIndex = (pairIndex+1)%2;
			index = calcIndex(teamIndex, pairIndex, pteamOrig);
			ppl = Integer.parseInt(pnumLeft[index]);
		}else{
			ppl = Integer.parseInt(pteamOrig[index]);
		}
		int ans = GameUtils.solveEasyFraction(num, den, ppl);
		int result = ppl - ans;
		if(lc.isAttack(optIndex)) {
			result = Math.max(0, result);
		}else{
			//Here you can't do it on the original, it has to be the ones that are stinky...be careful about your stink, look back at the air fresheners.
			//just have to subtract the orig to the current number to get the number that are stinky, and then you have the value
			int numLeft = Integer.parseInt(pnumLeft[index]);
			result = numLeft + ans;
			result = Math.min(result, ppl);
		}
		pnumLeft[index] = ""+result;
	}

	public int getTrickIndex(LogPair lp) {
		String content = lp.getContent();
		return Integer.parseInt(content.substring(0, 1));
	}
	
	public int getTeamIndex(LogPair lp) {
		String content = lp.getContent();
		int semiPos = content.indexOf(";");
		return Integer.parseInt(content.substring(semiPos+1));
	}
	
	public LogState deepCopy(LogState ls, LogPair lp) {
		LogState temp = new LogState(lp, gid, DBUtils.copyArray(pteamids), DBUtils.copyArray(pnumLeft), DBUtils.copyArray(ptrickids), cardMap, DBUtils.copyArray(pteamOrig), DBUtils.copyArray(ptrickNames));
		return temp;
	}
	
	public void setTeams(String[] tids) {
		pteamids = tids;
	}
	
	public void setTrickHand(String[] tids, String[] tNames) {
		ptrickids = tids;
		ptrickNames = tNames;
	}
	
	public void setTeamNums(String[] nums) {
		pnumLeft = nums;
		pteamOrig = DBUtils.copyArray(nums);
	}
	
	private void calcMyHand(LogCard[] cids, LogState ls, int pairIndex, String content) {
		//convert logs to cids and get ready to copy them into logstate using the pairIndex
		//generate new array copy as a new method that takes in logcard arr and pairindex
		String[] cardNames = extractTrickHand(content);
		cardNames = copyValuesIntoBiggerArray(cardNames, ptrickNames, pairIndex);
		String[] temp = newCardArray(cids, ptrickids, pairIndex);
		ls.setTrickHand(temp, cardNames);
	}
	
	private String[] copyValuesIntoBiggerArray(String[] cardNames, String[] tNames, int pairIndex) {
		int indexSection = tNames.length/2;
		String[] newArr = DBUtils.copyArray(tNames);
		for(int i = 0; i < indexSection; i++) {
			newArr[indexSection*pairIndex+i] = ""+cardNames[i];
		}
		return newArr;
	}

	private void calcMyTeam(LogCard[] cids, LogState ls, int pairIndex) {
		ls.setTeams(newCardArray(cids, pteamids, pairIndex));
		ls.setTeamNums(newCardNumLeft(cids, pnumLeft, pairIndex));
	}

	private void calcRemoveRadios(LogState newLs, int radiosToRemove, int pairIndex) {
		while(radiosToRemove > 0) {
			int index = newLs.findRadioIndex(pairIndex);
			newLs.removeTrickCard(index, pairIndex);
			radiosToRemove--;
		}
	}
	
	private void calcIceCreamWorked(LogState newLs, int pairIndex) {
		int index = getTeamIndex(plog);
		newLs.removeTeamCard(index, pairIndex);
	}
	
	public void removeTeamCard(int index, int pairIndex) {
		int indexSection = pteamids.length/2;
		index = indexSection*pairIndex+index;
		pteamids = DBUtils.copyAndRemove(pteamids, index, pairIndex);
		pnumLeft = DBUtils.copyAndRemove(pnumLeft, index, pairIndex);
		pteamOrig = DBUtils.copyAndRemove(pteamOrig, index, pairIndex);
	}

	public void removeTrickCard(int index, int pairIndex) {
		int indexSection = ptrickids.length/2;
		index = indexSection*pairIndex+index;
		ptrickids = DBUtils.copyAndRemove(ptrickids, index, pairIndex);
		ptrickNames = DBUtils.copyAndRemove(ptrickNames, index, pairIndex);
	}

	public int findRadioIndex(int pairIndex) {
		int indexSection = ptrickids.length/2;
		for(int i = 0; i < indexSection; i++) {
			int index = indexSection*pairIndex+i;
			LogCard lc = cardMap.get(ptrickNames[index]);
			if(lc != null && lc.isRadio()) {
				return index;
			}
		}
		return -1;
	}

	private String[] newCardArray(LogCard[] cids, String[] arr, int pairIndex) {
		int indexSection = arr.length/2;
		String[] newArr = DBUtils.copyArray(arr);
		for(int i = 0; i < indexSection; i++) {
			newArr[indexSection*pairIndex+i] = ""+cids[i].getCid();
		}
		return newArr;
	}
	
	private String[] newCardNumLeft(LogCard[] cids, String[] arr, int pairIndex) {
		int indexSection = arr.length/2;
		String[] newArr = DBUtils.copyArray(arr);
		for(int i = 0; i < indexSection; i++) {
			String num = "" + cids[i].getNumPpl();
			newArr[indexSection*pairIndex+i] = num;
		}
		return newArr;
	}
	
	private LogCard[] extractCardIdsFromLog(String content) {
		String[] cards = extractTrickHand(content);
		LogCard[] lcs = new LogCard[cards.length];
		for(int i = 0; i < cards.length; i++) {
			LogCard lc = cardMap.get(cards[i]);
			if(lc == null) {
				System.out.println("uh oh");
			}
			lcs[i] = lc;
		}
		return lcs;
	}
	
	private String[] extractTrickHand(String content) {
		if(content.charAt(0) != '{') {
			System.out.println("earlier uh oh");
		}
		content = content.substring(1, content.length()-1);
		return content.split("_");
	}
}
