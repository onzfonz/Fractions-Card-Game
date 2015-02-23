package logparser;

import java.util.ArrayList;

/* Class: LogGame
 * --------------
 * This is the class that will in a way store all of the information in the last four tables
 * for the fractions game.  
 * Each object will be an instantiation of one game.
 * Because of the log game will be storing a few things, from all of the tables, as well as a way to look
 * up the particular log, that we are looking for.
 */

public class LogPair implements SQLType {
	int plid, gid, pid, porder;
	String qaid;
	String plogtime, plogtype, plogcontent;
	
	private static int count;
	
	static {
		count = 0;
	}
	
	public LogPair(int game, int pair, int order, String qa, String time, String type, String content) {
		gid = game;
		pid = pair;
		porder = order;
		qaid = qa;
		plogtime = time;
		plogtype = type;
		plogcontent = content;
		count++;
		plid = count;
	}
	
	public boolean shouldBeRevised() {
		return plogtype.equals("move") && qaid == null;
	}
	
	public void setQaId(String qa) {
		qaid = qa;
	}
	
	public String getQaId() {
		return qaid;
	}
	
	@Override
	public String toString() {
		return ""+plid+", "+gid+", "+pid+", "+porder+", "+qaid+", "+plogtime+", "+plogtype;
	}
	
	public String[] toSQLString() {
		String[] soFar = DBUtils.convertCommaStrToArray(toString());
		String[] newArr = new String[soFar.length+1];
		for(int i = 0; i < soFar.length; i++) {
			newArr[i] = soFar[i];
		}
		newArr[soFar.length] = plogcontent;
		return newArr;
	}
	
	public int getPairLogId() {
		return plid;
	}
	
	public int getPairId() {
		return pid;
	}
	
	public String getType() {
		return plogtype;
	}
	
	public String getContent() {
		return plogcontent;
	}
	
	public boolean isRadio() {
		return plogtype.equals("radios");
	}
	
	public boolean hasShadow() {
		return numShadows() > 0;
	}
	
	public boolean isQAType() {
		return plogtype.equals(DBUtils.QATYPE);
	}
	
	private int shadowPos(int start) {
		return plogcontent.indexOf("Shadow Players", start);
	}
	
	private int numShadows() {
		return shadowPositions().size();
	}
	
	public ArrayList<String> getShadowQIDs() {
		if(plogcontent == null) return null;
		ArrayList<Integer> posList = shadowPositions();
		if(posList.size() == 0) {
			return null;
		}
		ArrayList<String> shadIDs = new ArrayList<String>();
		for(int pos:posList) {
			shadIDs.add(extractQID(pos));
		}
		return shadIDs;
	}
	
	private String extractQID(int pos) {
		int pstart = plogcontent.indexOf(",", pos);
		int pend = plogcontent.indexOf(",", pstart+1);
		String question = plogcontent.substring(pstart+1, pend);
		return DBUtils.getFractKeyFromQuestion(question);
	}
	
	private ArrayList<Integer> shadowPositions() {
		ArrayList<Integer> posList = new ArrayList<Integer>();
		int startPos = 0;
		int pos = shadowPos(startPos);
		while(pos != -1) {
			posList.add(pos);
			startPos = pos + 1;
			pos = shadowPos(startPos);
		}
		return posList;
	}
}
