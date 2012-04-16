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

public class PopulateQuestionsTable
{
	private Connection conn;
	private ArrayList<Statement> statements;
	private PreparedStatement psInsert;
	private ResultSet rs;

	public static void main(String[] args)
	{
		ArrayList<String> qs = DBUtils.readFilesIntoList(DBUtils.USER_LOG_NAMES);
//		DBUtils.printArrayList(teams);

		PopulateQuestionsTable db = new PopulateQuestionsTable();
		db.uploadData(qs);
//      System.out.println("PopulateCardsTable finished");
	}

	public PopulateQuestionsTable() {
		DBUtils.loadDriver(DBUtils.DB_DRIVER);
		statements = new ArrayList<Statement>(); // list of Statements, PreparedStatements
	}

	public void uploadData(ArrayList<String> questions) {
		try {
			conn = DBUtils.getDBConnection("TUG", "tugofwar", DBUtils.DB_PROTOCOL);
			conn.setAutoCommit(false);
			statements.add(conn.createStatement());

			// parameter 1 is num (int), parameter 2 is addr (varchar)
			psInsert = conn.prepareStatement(DBUtils.SQL_INSERT_QS);
			statements.add(psInsert);

			insertQuestionsIntoTable(questions, psInsert, conn, DBUtils.SQL_TYPES_QS);            
		}
		catch (SQLException sqle) {
			DBUtils.printSQLException(sqle);
		} finally {
			// release all open resources to avoid unnecessary memory usage
			DBUtils.cleanUp(rs, statements, conn);
		}
	}

	private void insertQuestionsIntoTable(ArrayList<String> lines, PreparedStatement ps, Connection conn, int[] types) throws SQLException {
		HashMap<String, String[]> questions = new HashMap<String, String[]>();
		for(String l: lines) {
			int pos = l.indexOf("Started");
			if(pos != -1) {
				String[] realVals = cleanUpQuestionsData(l, types, pos);
				String key = realVals[0];
				if(!questions.containsKey(key)) {
					questions.put(key, realVals);
//					System.out.println(Arrays.asList(realVals));
					DBUtils.prepareSingleInsertIntoTable(realVals, ps, types, false);
				}
			}
		}
		conn.commit();
	}

	//Find the max denominator, find the max value in cards.  Generate all possible questions that way.
	//110101 vs 1111
	//could do assume 3 or 4 digits and - vs not negative if the id is a decimal or not.

	private String[] cleanUpQuestionsData(String line, int[] types, int pos) {
		String[] content = new String[types.length];
		int subStrLength = "Started ".length();
		String question = line.substring(pos+subStrLength);
		content[3] = "" + GameUtils.extractNumerator(question);
		content[4] = "" + GameUtils.extractDenominator(question);
		content[5] = "" + GameUtils.extractPeople(question);
		content[1] = GameUtils.extractQuestion(line);
		content[2] = "" + GameUtils.generateAnswer(question);
		content[6] = GameUtils.determineIfDecimal(question);
		content[0] = DBUtils.convertFractToKey(content[3], content[4], content[5], content[6]);
		return content;
	}
}
