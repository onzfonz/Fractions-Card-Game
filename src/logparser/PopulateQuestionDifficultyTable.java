package logparser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import extras.GameUtils;

/*
 * At this point in the GameUtils, as well as with the functions in the logtester, we can pretty much read any of the files
 * From that we can extract all the information needed for the question to be a decimal.  In the log, we can check to see
 * if that question is in the database.  If the question is in the database, we can use the id and move on.
 * Otherwise, we will place that question into the database.  Before placing all the numbers into the database, we can just
 * have it as storage first.  So we'll read all the files in at once, and before we start talking to the db, we'll
 * just hold all the questions as lines in the arraylist, we can store it as a HashMap.
 * Then when we get to a line that we will be reading in as a question.  We generate the id, which we'll store as a string
 * If the id exists, we won't put it into the database, that question has already been asked, or ready to be placed
 * If not, we'll put the string along with a line in there.  By this point we might have generated the answer as well,
 * or we can do that in the prepare stage instead.  Yeah, when reading in the files.  We'll just read them all in
 * and if it's a question add it to the arraylist.  We'll then later maybe parse it all to make it simpler on the file reading.  We'll see.
 * Then when we are ready to upload to the database after reading all the files.  We simply iterate through hashmap
 * and upload like usual.  We'll construct the file 
 */

public class PopulateQuestionDifficultyTable
{
	private Connection conn;
	private ArrayList<Statement> statements;
	private PreparedStatement psInsert;
	private ResultSet rs;

	public static void main(String[] args)
	{
//		ArrayList<String> qs = DBUtils.readFilesIntoList(DBUtils.USER_LOG_NAMES);
//		DBUtils.printArrayList(teams);

		PopulateQuestionDifficultyTable db = new PopulateQuestionDifficultyTable();
		db.analyzeData();
//      System.out.println("PopulateCardsTable finished");
	}

	public PopulateQuestionDifficultyTable() {
		DBUtils.loadDriver(DBUtils.DB_DRIVER);
		statements = new ArrayList<Statement>(); // list of Statements, PreparedStatements
	}

	public void analyzeData() {
		try {
			conn = DBUtils.getDBConnection("TUG", "tugofwar", DBUtils.DB_PROTOCOL);
			statements.add(conn.createStatement());

			// parameter 1 is num (int), parameter 2 is addr (varchar)
			PreparedStatement allQs = DBUtils.prepareAStatement(conn, DBUtils.SQL_GET_QIDS, statements);
			PreparedStatement allUlogs = DBUtils.prepareAStatement(conn, DBUtils.SQL_QID_ULOGS, statements);
			assembleQuestionData(conn, allQs, allUlogs);            
		}
		catch (SQLException sqle) {
			DBUtils.printSQLException(sqle);
		} finally {
			// release all open resources to avoid unnecessary memory usage
			DBUtils.cleanUp(rs, statements, conn);
		}
	}

	private void assembleQuestionData(Connection conn, PreparedStatement allqs, PreparedStatement allulogs) throws SQLException {
		ArrayList<Integer> qids = DBUtils.getSQLResultListInt(allqs);
		ArrayList<LogQMine> qData = new ArrayList<LogQMine>();
		System.out.println("qid, numTimesWrong, numTimesTotal, difficulty, timesShown, percentShown");
		for(int qid:qids) {
			allulogs.setInt(1, qid);
			ArrayList<LogUser> lu = DBUtils.constructUserLogs(allulogs);
			LogQMine lqm = new LogQMine(qid, lu);
			qData.add(lqm);
			System.out.println(Arrays.asList(lqm.toSQLString()));
		}
//		conn.commit();
	}
}
