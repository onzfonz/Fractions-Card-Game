package logparser;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class LogQMine {
	private int numTimesGotWrong;
	private int numTimesAsked;
	private int numTimesShown;
	private ArrayList<LogUser> ulogsForQuestion;
	private int qid;
	
	public LogQMine(int qid, ArrayList<LogUser> ulogs) {
		this.qid = qid;
		setULogs(ulogs);
	}
	
	public void setULogs(ArrayList<LogUser> ulogs) {
		ulogsForQuestion = ulogs;
		recalculate();
	}
	
	private void recalculate() {
		numTimesAsked = LogUserMine.totalQuestions(ulogsForQuestion);
		numTimesGotWrong = LogUserMine.numAnsweredIncorrectly(ulogsForQuestion);
		numTimesShown = LogUserMine.numTimesShown(ulogsForQuestion);
	}
	
	public double getDifficulty() {
		return divide(numTimesGotWrong, numTimesAsked);
	}
	
	public double getPercentageShown() {
		return divide(numTimesShown, numTimesAsked);
	}
	
	public double divide(int numerator, int denominator) {
		double quotient = numerator / (double) denominator;
		return Double.valueOf(new DecimalFormat("#.##").format(quotient));
	}
	
	public String[] toSQLString() {
		String[] sql = {""+qid, ""+numTimesGotWrong, ""+numTimesAsked, ""+getDifficulty(), ""+numTimesShown, ""+getPercentageShown()};
		return sql;
	}
}
