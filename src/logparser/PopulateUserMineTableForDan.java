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
 * Just scan through and then have the ids of the users
 * Not sure if there is an ingenious way of doing the user IDs.
 */

public class PopulateUserMineTableForDan
{
	private Connection conn;
	private ArrayList<Statement> statements;
	private PreparedStatement psInsert;
	private ResultSet rs;

	public static void main(String[] args)
	{
		//		ArrayList<String> qs = DBUtils.readFilesIntoList(DBUtils.USER_LOG_NAMES);
		//		DBUtils.printArrayList(teams);

		PopulateUserMineTableForDan db = new PopulateUserMineTableForDan();
		db.analyzeData();
		//      System.out.println("PopulateCardsTable finished");
	}

	public PopulateUserMineTableForDan() {
		DBUtils.loadDriver(DBUtils.DB_DRIVER);
		statements = new ArrayList<Statement>(); // list of Statements, PreparedStatements
	}

	public void analyzeData() {
		try {
			conn = DBUtils.getDBConnection(DBUtils.DB_SCHEMA, DBUtils.DB_NAME, DBUtils.DB_PROTOCOL);
			statements.add(conn.createStatement());

			PreparedStatement psSelect = DBUtils.prepareAStatement(conn, DBUtils.SQL_ALL_USERS, statements);
			PreparedStatement psShown = DBUtils.prepareAStatement(conn, DBUtils.SQL_SHOWN, statements);
			PreparedStatement psWrong = DBUtils.prepareAStatement(conn, DBUtils.SQL_WRONG_Q, statements);
			PreparedStatement psTried = DBUtils.prepareAStatement(conn, DBUtils.SQL_TRIED, statements);
			PreparedStatement psGames = DBUtils.prepareAStatement(conn, DBUtils.SQL_GAMES, statements);
			PreparedStatement psNumQs = DBUtils.prepareAStatement(conn, DBUtils.SQL_NUM_QS_ALT, statements);
			PreparedStatement psULogs = DBUtils.prepareAStatement(conn, DBUtils.SQL_WRONG_Q_FROM_GID, statements);
			PreparedStatement shadowlogs = DBUtils.prepareAStatement(conn, DBUtils.SQL_ULOGS_FROM_UIDGID, statements);

			System.out.println("uid, midtest, postest, midsub, postsub, camath, shown?, numshown, numwrong, numtries, numquestions, numsessions, earlynumcorrect, earlynumqs, latenumcorrect, latenumqs, earlynumpercent, latenumpercent, earlysecs, latesecs, earlysecsper, latesecsper, earlyshadtot, lateshadtot, earlyshadacc, lateshadacc, earlyshadavgtime, lateshadavgtime");
			assembleUserData(psSelect, psShown, psWrong, psTried, psGames, psNumQs, psULogs, shadowlogs, conn, DBUtils.SQL_TYPES_ULOGS);            
		}
		catch (SQLException sqle) {
			DBUtils.printSQLException(sqle);
		} finally {
			// release all open resources to avoid unnecessary memory usage
			DBUtils.cleanUp(rs, statements, conn);
		}
	}

	private void assembleUserData(PreparedStatement ps, PreparedStatement shown, PreparedStatement wrong, PreparedStatement tried, PreparedStatement games, PreparedStatement numQs, PreparedStatement uLogs, PreparedStatement shadowlogs, Connection conn, int[] types) throws SQLException {
		ArrayList<LogUserMine> usersData = DBUtils.getAllUsersMineableInfo(ps, true);
		for(LogUserMine lum: usersData) {
			DBUtils.setMineShownHow(lum, shown);
			DBUtils.setMineWrong(lum, wrong);
			DBUtils.setMineAttempts(lum, tried);
			DBUtils.setMineNumGames(lum, games);
			DBUtils.setMineQsAnswered(lum, numQs);
//			DBUtils.setMineEarlyLateQs(lum, uLogs);
			DBUtils.setMineEarlyLateWrong(lum, uLogs);
			DBUtils.setMineEarlyLateWrongAlt(lum, shadowlogs);
			System.out.println(Arrays.asList(lum.toSQLString()));
		}
	}
}
