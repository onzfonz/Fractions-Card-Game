package logparser;

/* Class: LogGame
 * --------------
 * This is the class that will in a way store all of the information in the last four tables
 * for the fractions game.  
 * Each object will be an instantiation of one game.
 * Because of the log game will be storing a few things, from all of the tables, as well as a way to look
 * up the particular log, that we are looking for.
 */

import java.sql.*;

public class LogUserMove implements SQLType{
	int uid;
	String ulogtime, ulogaction, ulogcontent;
	String uwhole;
	Timestamp tstamp;
	
	public LogUserMove(int user, int qa, int order, int question, String time, String type, String attempt, String ppl, String lines, String marks, String shown, String whole) {
		uid = user;
		ulogtime = time;
		ulogaction = type;
		ulogcontent = attempt;
		uwhole = whole;
	}
	
	public LogUserMove(int user, int qa, int order, int question, Timestamp time, String type, String attempt, String ppl, String lines, String marks, String shown) {
		this(user, qa, order, question, time.toString(), type, attempt, ppl, lines, marks, shown, null);
		tstamp = time;
	}
	
	@Override
	public String toString() {
		return ""+uid+", "+ulogtime+", "+ulogaction+", "+ulogcontent;
	}
	
	public Timestamp getTimestamp() {
		return tstamp;
	}
	
	public String getLogType() {
		return ulogaction;
	}
		
	public String[] toSQLString() {
		return DBUtils.convertCommaStrToArray(toString());
	}
}
