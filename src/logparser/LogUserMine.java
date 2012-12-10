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
	int uid, midtest, postest, star3rd, star4th, midsub, postsub;
	
	public LogUserMine(String user, String s3rd, String s4th, String mqtot, String zqtot, String msub, String zsub) {
		uid = Integer.parseInt(user);
		star3rd = convertToInt(s3rd);
		star4th = convertToInt(s4th);
		midtest = convertToInt(mqtot);
		postest = convertToInt(zqtot);
		midsub = convertToInt(msub);
		postsub = convertToInt(zsub);
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
	
	public static int numCorrectQuestions(ArrayList<LogUser> qs) {
		return totalQuestions(qs) - numAnsweredIncorrectly(qs);
	}
	
	public static int totalQuestions(ArrayList<LogUser> qs) {
		HashSet<Integer> answeredquestion = new HashSet<Integer>();
		for(int i = 0; i < qs.size(); i++) {
			LogUser lu = qs.get(i);
			if(lu.finishedQuestion()) {
				answeredquestion.add(lu.getQaid());
			}
		}
		return answeredquestion.size();
	}
	
	public static double getPercentWrong(ArrayList<LogUser> qs) {
		int wrong = numAnsweredIncorrectly(qs);
		int total = totalQuestions(qs);
		return getPercentage(wrong, total);
	}
	
	public static double getPercentCorrect(ArrayList<LogUser> qs) {
		int correct = numCorrectQuestions(qs);
		int total = totalQuestions(qs);
		return getPercentage(correct, total);
	}
	
	public static double getAverageTimeSpent(ArrayList<LogUser> qs) {
		int total = totalQuestions(qs);
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
		int earlytot = totalQuestions(earlyQuestions);
		int earlyright = numCorrectQuestions(earlyQuestions);
		int latetot = totalQuestions(lateQuestions);
		int lateright = numCorrectQuestions(lateQuestions);
		int earlyshadtot = totalQuestions(earlyShadow);
		int lateshadtot = totalQuestions(lateShadow);
		
		double earlyacc = getPercentCorrect(earlyQuestions);
		double lateacc = getPercentCorrect(lateQuestions);
		double earlysecsper = getAverageTimeSpent(earlyQuestions);
		double latesecsper = getAverageTimeSpent(lateQuestions);
		double earlyshad = getPercentCorrect(earlyShadow);
		double lateshad = getPercentCorrect(lateShadow);
		double earlyshadtime = getAverageTimeSpent(earlyShadow);
		double lateshadtime = getAverageTimeSpent(lateShadow);
		
		String[] sql = {""+uid, toStr(midtest), toStr(postest), toStr(midsub), toStr(postsub), toStr(star3rd), toStr(star4th), ""+clickedShowHow(), ""+qidsClickedShow.size(), ""+qidsIncorrect.size(), ""+qtriedids.size(), ""+numQuestionsAnswered, ""+numSessionsPlayed, ""+earlyright, ""+earlytot, ""+lateright, ""+latetot, ""+earlyacc, ""+lateacc, ""+getSecondsSpent(earlyQuestions), ""+getSecondsSpent(lateQuestions), ""+earlysecsper, ""+latesecsper, ""+earlyshadtot, ""+lateshadtot, ""+earlyshad, ""+lateshad, ""+earlyshadtime, ""+lateshadtime};
		return sql;
	}
	
	private String toStr(int val) {
		if(val == -1) {
			return null;
		}
		return ""+val;
	}
}
