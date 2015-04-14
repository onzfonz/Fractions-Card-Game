package logparser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import extras.GameUtils;

/*
 * Just scan through and then have the ids of the users
 * Not sure if there is an ingenious way of doing the user IDs.
 */

public class PopulatePairsTable
{
	private Connection conn;
	private ArrayList<Statement> statements;
	private PreparedStatement psInsert;
//	private ResultSet rs;

	public static void main(String[] args)
	{
		ArrayList<String> qs = DBUtils.readFilesIntoList(DBUtils.USER_LOG_NAMES);
//		DBUtils.printArrayList(teams);

		PopulatePairsTable db = new PopulatePairsTable();
		db.uploadData(qs);
//      System.out.println("PopulateCardsTable finished");
	}

	public PopulatePairsTable() {
		DBUtils.loadDriver(DBUtils.DB_DRIVER);
		statements = new ArrayList<Statement>(); // list of Statements, PreparedStatements
	}

	public void uploadData(ArrayList<String> pairs) {
		try {
			conn = DBUtils.getDBConnection(DBUtils.DB_SCHEMA, DBUtils.DB_NAME, DBUtils.DB_PROTOCOL);
			conn.setAutoCommit(false);
			statements.add(conn.createStatement());

			// parameter 1 is num (int), parameter 2 is addr (varchar)
			psInsert = conn.prepareStatement(DBUtils.SQL_INSERT_PAIRS);
			statements.add(psInsert);
			
			PreparedStatement psSelect = conn.prepareStatement(DBUtils.SQL_UID_FROM_NAME);
			statements.add(psSelect);

			insertPairsIntoTable(pairs, psInsert, psSelect, conn, DBUtils.SQL_TYPES_PAIRS);            
		}
		catch (SQLException sqle) {
			DBUtils.printSQLException(sqle);
		} finally {
			// release all open resources to avoid unnecessary memory usage
			DBUtils.cleanUp(null, statements, conn);
		}
	}

	private void insertPairsIntoTable(ArrayList<String> lines, PreparedStatement psInsert, PreparedStatement ps, Connection conn, int[] types) throws SQLException {
		HashMap<String, String[]> pairs = new HashMap<String, String[]>();
		for(String l: lines) {
			ArrayList<String> elems = DBUtils.getParsedRegex(l);
			if(elems != null) {
				System.out.println("elem parsed is: " + elems.get(1));
				String[] realVals = DBUtils.getParsedNames(elems.get(1));
				String pairKey = realVals[0]+realVals[1];
				String oppoPairKey = realVals[1]+realVals[0];
//				System.out.println(pairKey);
				if(!pairs.containsKey(pairKey) && !pairs.containsKey(oppoPairKey)) {
					pairs.put(pairKey, realVals);
					String name1 = realVals[0];
					String name2 = realVals[1];
					cleanUpPairData(realVals, ps);
//					if(name1.equalsIgnoreCase("The") || name1.equalsIgnoreCase("A") || name1.equalsIgnoreCase("null")) {
//						System.out.println(elems.get(1));
//					}
//					System.out.println(name1 + ", " + name2 + ", " + realVals[0] + ", " + realVals[1] + " -- " + l);
//					String[] realVals = cleanUpPairData(userPair, types);
//					DBUtils.prepareSingleInsertIntoTable(realVals, psInsert, types, false);
				}
			}
		}
//		conn.commit();
	}
	
	private void cleanUpPairData(String[] pairNames, PreparedStatement ps) throws SQLException {
		for(int i = 0; i < pairNames.length; i++) {
			pairNames[i] = DBUtils.getUIDFromName(pairNames[i], ps);
		}
	}
}
