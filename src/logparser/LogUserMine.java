package logparser;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/* Class: LogGame
 * --------------
 * This is the class that will in a way store all of the information in the last four tables
 * for the fractions game.  
 * Each object will be an instantiation of one game.
 * Because of the log game will be storing a few things, from all of the tables, as well as a way to look
 * up the particular log, that we are looking for.
 */

public class LogUserMine implements SQLType{
	boolean clickedShow;
	ArrayList<String> qidsClickedShow, qidsIncorrect, qtriedids, qtriedattempt;
	ArrayList<LogUser> earlyQuestions, lateQuestions, earlyShadow, lateShadow;
	int numSessionsPlayed;
	int numQuestionsAnswered;
	int numWrongMoves;
	int uid, midtest, postest, star4th, midsub, postsub;
	private boolean hasLaxRules;
	
	public LogUserMine(String user, String s4th, String mqtot, String zqtot, String msub, String zsub, boolean hasLaxRules) {
		uid = Integer.parseInt(user);
		star4th = convertToInt(s4th);
		midtest = convertToInt(mqtot);
		postest = convertToInt(zqtot);
		midsub = convertToInt(msub);
		postsub = convertToInt(zsub);
		this.hasLaxRules = hasLaxRules;
	}
	
	public void setEarlyQuestions(ArrayList<LogUser> eqs) {
		earlyQuestions = eqs;
	}
	
	public void setLateQuestions(ArrayList<LogUser> lqs) {
		lateQuestions = lqs;
	}
	
	public void setEarlyShadowQs(ArrayList<LogUser> earlyULs) {
		earlyShadow = earlyULs;
	}
	
	public void setLateShadowQs(ArrayList<LogUser> lateULs) {
		lateShadow = lateULs;
	}

	public void setClickedShow(ArrayList<String> list) {
		qidsClickedShow = list;
	}
	
	public void setQidsIncorrect(ArrayList<String> qidsIncorrect) {
		this.qidsIncorrect = qidsIncorrect;
	}

	public void setQtriedids(ArrayList<String> qtriedids) {
		this.qtriedids = qtriedids;
	}

	public void setQtriedattempt(ArrayList<String> qtriedattempt) {
		this.qtriedattempt = qtriedattempt;
	}

	public void setNumSessionsPlayed(int numSessionsPlayed) {
		this.numSessionsPlayed = numSessionsPlayed;
	}

	public void setNumQuestionsAnswered(int numQuestionsAnswered) {
		this.numQuestionsAnswered = numQuestionsAnswered;
	}
	
	public void setNumWrongMoves(int nWrong) {
		this.numWrongMoves = nWrong;
	}

	private int convertToInt(String s) {
		if(s == null || s.length() == 0) {
			return -1;
		}
		return Integer.parseInt(s);
	}
	
	public int getNumIncorrect() {
		if(qidsIncorrect == null) return 0;
		return qidsIncorrect.size();
	}
	
	public int getUid() {
		return uid;
	}
		
	public boolean clickedShowHow() {
		if(qidsClickedShow == null) return false;
		return qidsClickedShow.size() > 0;
	}
	
	public static int numCorrectQuestions(ArrayList<LogUser> qs, boolean laxRules) {
		return totalQuestions(qs, laxRules) - numAnsweredIncorrectly(qs);
	}
	
	public static int totalQuestions(ArrayList<LogUser> qs, boolean laxRules) {
		HashSet<Integer> answeredquestion = new HashSet<Integer>();
		for(int i = 0; i < qs.size(); i++) {
			LogUser lu = qs.get(i);
			if(laxRules || lu.finishedQuestion()) {
				answeredquestion.add(lu.getQaid());
			}
		}
		return answeredquestion.size();
	}
	
	public static double getPercentWrong(ArrayList<LogUser> qs, boolean laxRules) {
		int wrong = numAnsweredIncorrectly(qs);
		int total = totalQuestions(qs, laxRules);
		return getPercentage(wrong, total);
	}
	
	public static double getPercentCorrect(ArrayList<LogUser> qs, boolean laxRules) {
		int correct = numCorrectQuestions(qs, laxRules);
		int total = totalQuestions(qs, laxRules);
		return getPercentage(correct, total);
	}
	
	public static double getAverageTimeSpent(ArrayList<LogUser> qs, boolean laxRules) {
		int total = totalQuestions(qs, laxRules);
		long secs = getSecondsSpent(qs);
		return getPercentage(secs, total);
	}
	
	public static double getPercentage(int num, int den) {
		return getPercentage((long) num, (long) den);
	}
	
	public static double getPercentage(long num, long den) {
		if(den == 0) return 0;
		return Double.valueOf(new DecimalFormat("#.##").format((double) num / den));		
	}
	
	public static int numAnsweredIncorrectly(ArrayList<LogUser> qs) {
		HashSet<Integer> answeredincorrectly = new HashSet<Integer>();
		for(int i = 0; i < qs.size(); i++) {
			LogUser lu = qs.get(i);
			if(lu.isAttempt()) {
				answeredincorrectly.add(lu.getQaid());
			}
		}
		return answeredincorrectly.size();
	}
	
	public static int numTimesShown(ArrayList<LogUser> qs) {
		HashSet<Integer> answeredincorrectly = new HashSet<Integer>();
		for(int i = 0; i < qs.size(); i++) {
			LogUser lu = qs.get(i);
			if(lu.wasShown()) {
				answeredincorrectly.add(lu.getQaid());
			}
		}
		return answeredincorrectly.size();
	}
	
	public static String getTimeSpent(ArrayList<LogUser> qs) {
		long timeSpentSoFar = getMillisSpent(qs);
		return String.format("%d min and %d sec", TimeUnit.MILLISECONDS.toMinutes(timeSpentSoFar), TimeUnit.MILLISECONDS.toSeconds(timeSpentSoFar) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeSpentSoFar)));
	}
	
	public static long getSecondsSpent(ArrayList<LogUser> qs) {
		long timeSpent = getMillisSpent(qs);
		return TimeUnit.MILLISECONDS.toSeconds(timeSpent);
	}
	
	public static long getMillisSpent(ArrayList<LogUser> qs) {
		long timeSpentSoFar = 0;
		HashSet<Integer> questionsCalced = new HashSet<Integer>();
		for(int i = 0; i < qs.size(); i++) {
			LogUser lu = qs.get(i);
			int qaid = lu.getQaid();
			if(!questionsCalced.contains(qaid)) {
				assert(lu.getLogType().equals("QStarted"));
				long interval = calculateInterval(lu, i, qs);
				timeSpentSoFar += interval;
				questionsCalced.add(qaid);
			}
		}
		return timeSpentSoFar;
	}
	
	public static long calculateInterval(LogUser lu, int index, ArrayList<LogUser> qs) {
		int i = index+1;
		if(i >= qs.size()) return 0;
		LogUser nextLu = qs.get(i);
		long origmillis = lu.getTimestamp().getTime();
		while(!(i >= qs.size()-1 || (nextLu.getLogType().equals("QDone") && lu.getQaid() == nextLu.getQaid()))) {
			i++;
			nextLu = qs.get(i);
			if(nextLu.getLogType().equals("QDone") && lu.getQaid() != nextLu.getQaid()) {
				origmillis = nextLu.getTimestamp().getTime();
			}
		}
		if(i >= qs.size()) return 0;
		long donemillis = nextLu.getTimestamp().getTime();
		return donemillis - origmillis;
	}
	
	public String[] toSQLString() {
		int earlytot = totalQuestions(earlyQuestions, hasLaxRules);
		int earlyright = numCorrectQuestions(earlyQuestions, hasLaxRules);
		int latetot = totalQuestions(lateQuestions, hasLaxRules);
		int lateright = numCorrectQuestions(lateQuestions, hasLaxRules);
		int earlyshadtot = totalQuestions(earlyShadow, hasLaxRules);
		int lateshadtot = totalQuestions(lateShadow, hasLaxRules);
		
		double earlyacc = getPercentCorrect(earlyQuestions, hasLaxRules);
		double lateacc = getPercentCorrect(lateQuestions, hasLaxRules);
		double earlysecsper = getAverageTimeSpent(earlyQuestions, hasLaxRules);
		double latesecsper = getAverageTimeSpent(lateQuestions, hasLaxRules);
		double earlyshad = getPercentCorrect(earlyShadow, hasLaxRules);
		double lateshad = getPercentCorrect(lateShadow, hasLaxRules);
		double earlyshadtime = getAverageTimeSpent(earlyShadow, hasLaxRules);
		double lateshadtime = getAverageTimeSpent(lateShadow, hasLaxRules);
		
		String[] sql = {""+uid, toStr(midtest), toStr(postest), toStr(midsub), toStr(postsub), toStr(star4th), ""+clickedShowHow(), ""+qidsClickedShow.size(), ""+qidsIncorrect.size(), ""+qtriedids.size(), ""+numQuestionsAnswered, ""+numSessionsPlayed, ""+earlyright, ""+earlytot, ""+lateright, ""+latetot, ""+earlyacc, ""+lateacc, ""+getSecondsSpent(earlyQuestions), ""+getSecondsSpent(lateQuestions), ""+earlysecsper, ""+latesecsper, ""+earlyshadtot, ""+lateshadtot, ""+earlyshad, ""+lateshad, ""+earlyshadtime, ""+lateshadtime, ""+numQuestionsAnswered};
		return sql;
	}
	
	private String toStr(int val) {
		if(val == -1) {
			return null;
		}
		return ""+val;
	}
}
