package logparser;

/* Class: LogGame
 * --------------
 * This is the class that will in a way store all of the information in the last four tables
 * for the fractions game.  
 * Each object will be an instantiation of one game.
 * Because of the log game will be storing a few things, from all of the tables, as well as a way to look
 * up the particular log, that we are looking for.
 */

public class LogUser implements SQLType{
	int uid, qaid, uorder, qid;
	String ulogtime, ulogtype, ulogattempt;
	String ulogppl, uloglines, ulogmarks, ulogshown;
	String uwhole;
	
	public LogUser(int user, int qa, int order, int question, String time, String type, String attempt, String ppl, String lines, String marks, String shown, String whole) {
		uid = user;
		qaid = qa;
		uorder = order;
		qid = question;
		ulogtime = time;
		ulogtype = type;
		ulogppl = ppl;
		ulogattempt = attempt;
		uloglines = lines;
		ulogmarks = marks;
		if(shown == null) {
			ulogshown = null;
		}else if(shown.equals("n")) {
			ulogshown = "0";
		}else{
			ulogshown = "1";
		}
		uwhole = whole;
	}
	
	public String toString() {
		return ""+uid+", "+qaid+", "+uorder+", "+qid+", "+ulogtime+", "+ulogtype+", "+ulogattempt+", "+ulogppl+", "+uloglines+", "+ulogmarks+", "+ulogshown;
	}
	
	public String[] toSQLString() {
		return DBUtils.convertCommaStrToArray(toString());
	}
}
