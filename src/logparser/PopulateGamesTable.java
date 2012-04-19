package logparser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import basic.Constants;

import extras.GameUtils;

/*
 * Just scan through and then have the ids of the users
 * Not sure if there is an ingenious way of doing the user IDs.
 */

public class PopulateGamesTable
{
	private Connection conn;
	private ArrayList<Statement> statements;
	private PreparedStatement psInsert;
	private ResultSet rs;

	public static void main(String[] args)
	{
		ArrayList<ArrayList<String>> qs = DBUtils.readFilesIntoListOfLists(DBUtils.USER_LOG_NAMES);
//		DBUtils.printArrayList(teams);
				
		PopulateGamesTable db = new PopulateGamesTable();
		db.uploadData(qs);
//      System.out.println("PopulateCardsTable finished");
	}

	public PopulateGamesTable() {
		DBUtils.loadDriver(DBUtils.DB_DRIVER);
		statements = new ArrayList<Statement>(); // list of Statements, PreparedStatements
	}

	public void uploadData(ArrayList<ArrayList<String>> sessions) {
		try {
			conn = DBUtils.getDBConnection("TUG", "tugofwar", DBUtils.DB_PROTOCOL);
			conn.setAutoCommit(false);
			statements.add(conn.createStatement());

			// parameter 1 is num (int), parameter 2 is addr (varchar)
//			psInsert = conn.prepareStatement(DBUtils.SQL_INSERT_GAMES);
//			statements.add(psInsert);
			
			PreparedStatement psSelect = conn.prepareStatement(DBUtils.SQL_PID_FROM_UIDS);
			statements.add(psSelect);
			
			PreparedStatement psSelectNull = conn.prepareStatement(DBUtils.SQL_PID_FROM_UID);
			statements.add(psSelectNull);
			
			PreparedStatement psGetUid = conn.prepareStatement(DBUtils.SQL_UID_FROM_NAME);
			statements.add(psGetUid);

			ArrayList<String> teams = DBUtils.readFileIntoList(Constants.FNAME_TEAM_DECK);
			cleanUpTeams(teams);
			teams.addAll(DBUtils.readFileIntoList(Constants.FNAME_TRICK_DECK));
			HashMap<String, LogCard> cardMap = cleanUpAndMapCards(111, teams);

			ArrayList<LogGame> allGames = new ArrayList<LogGame>();
			for(int i = 0; i < sessions.size(); i++) {
				ArrayList<String> session = sessions.get(i);
				insertGamesIntoTable(session, psInsert, psSelect, psSelectNull, psGetUid, conn, allGames, cardMap);            
			}
			
			//insertStatementsSQL(DBUtils.SQL_INSERT_GAMES, allGames, DBUtils.SQL_TYPES_GAMES);
			for(LogGame lg: allGames) {
//				insertStatementsSQL(DBUtils.SQL_INSERT_PLOGS, lg.getPairLogs(), DBUtils.SQL_TYPES_PLOGS);
//				insertStatementsSQL(DBUtils.SQL_INSERT_ULOGS, lg.getUserLogs(), DBUtils.SQL_TYPES_ULOGS);
//				insertStatementsSQL(DBUtils.SQL_INSERT_SLOGS, lg.getStateLogs(), DBUtils.SQL_TYPES_SLOGS);
			}
		}
		catch (SQLException sqle) {
			DBUtils.printSQLException(sqle);
		} finally {
			// release all open resources to avoid unnecessary memory usage
			DBUtils.cleanUp(rs, statements, conn);
		}
	}
	
	private void insertStatementsSQL(String sqlString, ArrayList games, int[] types) throws SQLException{
		PreparedStatement ps = conn.prepareStatement(sqlString);
		statements.add(ps);
		for(int i = 0; i < games.size(); i++) {
			SQLType lg = (SQLType) games.get(i);
			DBUtils.prepareSingleInsertIntoTable(lg.toSQLString(), ps, types, false);
//			System.out.println(Arrays.asList(lg.toSQLString()));
		}
		conn.commit();
	}
	
	private void cleanUpTeams(ArrayList<String> teams) {
		for(int i = 0; i < teams.size(); i++) {
			String t = teams.get(i);
			t = t.replace(",", ", ");
			teams.set(i, t);
		}
	}
	
	private HashMap<String, LogCard> cleanUpAndMapCards(int startPos, ArrayList<String> teams) {
		HashMap<String, LogCard> cardMap = new HashMap<String, LogCard>();
		for(int i = 0; i < teams.size(); i++) {
			String line = teams.get(i);
			int lastComma = line.lastIndexOf(",");
			String newStr = line.substring(0, lastComma);
			String[] vals = DBUtils.cleanUpCardData(line);
			LogCard lc = new LogCard(startPos, vals);
			cardMap.put(newStr, lc);
			startPos++;
		}
		return cardMap;
	}

	/* This now assumes that we are only dealing with one file */
	private void insertGamesIntoTable(ArrayList<String> lines, PreparedStatement psInsert, PreparedStatement ps, PreparedStatement psNull, PreparedStatement psGetUId, Connection conn, ArrayList<LogGame> allGames, HashMap<String, LogCard> cardMap) throws SQLException {
		HashMap<String, LogGame> gamesMap;
		ArrayList<String> pairList = generatePairList(lines);
		System.out.println("--------------");
		
		ArrayList<String[]> gamePairs = generateGamePairs(lines, pairList);
//		DBUtils.printArrayOfArrayList(gamePairs);
//		System.out.println("***************");
		HashMap<String, String> pairMapping = new HashMap<String, String>();
		gamePairs = cleanUpGamePairs(gamePairs, pairMapping);
//		DBUtils.printArrayOfArrayList(gamePairs);
		
		ArrayList<String[]> pairIDs = convertPairsToIDs(gamePairs, ps, psNull);
		ArrayList<LogGame> gamesList = generateLogGamesList(gamePairs, pairIDs, allGames, psGetUId, pairList, cardMap);
		gamesMap = generateLogGamesMap(pairList, gamesList, gamePairs);
		mapLogFileToGames(lines, gamesMap);
		for(int i = 0; i < gamesList.size(); i++) {
			LogGame lg = gamesList.get(i);
			lg.populateFieldsFromFullLog();
//			System.out.println(Arrays.asList(lg.getSQLString()));
		}
		//DBUtils.printArrayOfArrayList(gamePairs);
		//Next thing is to convert the pair name to the appropriate game table. Could do this with a hashmap, with names to the specific game
		/*for(String l: lines) {
			ArrayList<String> elems = DBUtils.getParsedRegex(l);
			if(elems != null) {
				String[] realVals = DBUtils.getParsedNames(elems.get(1));
				String pairKey = realVals[0]+realVals[1];
				if(!games.containsKey(pairKey)) {
					games.put(pairKey, realVals);
					cleanUpPairData(realVals, ps);
//					System.out.println(pairKey + ", " + Arrays.asList(realVals));
//					String[] realVals = cleanUpPairData(userPair, types);
					DBUtils.prepareSingleInsertIntoTable(realVals, psInsert, types, false);
				}
			}
		}
		conn.commit();*/
	}
	
	private void mapLogFileToGames(ArrayList<String> lines, HashMap<String, LogGame> gamesMap) {
		for(String l: lines) {
			String name = DBUtils.getNameLineFromRegex(l);
			LogGame game = gamesMap.get(name);
			if(game != null) {
				game.addToFullLog(l);
			}
		}
	}
	
	private HashMap<String, LogGame> generateLogGamesMap(ArrayList<String> pairList, ArrayList<LogGame> gamesList, ArrayList<String[]> gamePairs) {
		HashMap<String, LogGame> gamesMap = new HashMap<String, LogGame>();
		
		for(String pair: pairList) {
			String[] names = DBUtils.getParsedNames(pair);
			int i = getGamePairIndex(gamePairs, names);
			if(i != -1) {
				gamesMap.put(pair, gamesList.get(i));
				//System.out.println("linking " + pair + " to " + Arrays.asList(gamePairs.get(i)));
			}
		}
		return gamesMap;
	}
	
	private ArrayList<LogGame> generateLogGamesList(ArrayList<String[]> gamePairs, ArrayList<String[]> pairIDs, ArrayList<LogGame> allGames, PreparedStatement ps, ArrayList<String> pairList, HashMap<String, LogCard> cardMap) throws SQLException {
		if(gamePairs.size() != pairIDs.size()) {
			return null;
		}
		ArrayList<LogGame> gamesList = new ArrayList<LogGame>();
		for(int i = 0; i < gamePairs.size(); i++) {
			HashMap<String, String> uidMap = DBUtils.linkUIDsToNames(gamePairs.get(i), ps);
			LogGame newGame = new LogGame(gamePairs.get(i), pairIDs.get(i), allGames.size()+1, pairList, uidMap, cardMap);
			gamesList.add(newGame);
			allGames.add(newGame);
		}
		return gamesList;
	}

	private ArrayList<String[]> cleanUpGamePairs(ArrayList<String[]> gamePairs, HashMap<String, String> pMapping) {
		ArrayList<String[]> newGamePairs = new ArrayList<String[]>();
		for(int i = 0; i < gamePairs.size(); i++) {
			String[] realVals1 = DBUtils.getParsedNames(gamePairs.get(i)[0]);
			String[] realVals2 = DBUtils.getParsedNames(gamePairs.get(i)[1]);
			addIntoGamePairs(newGamePairs, realVals1, realVals2, pMapping);
		}
		return newGamePairs;
	}
	
	private ArrayList<String[]> convertPairsToIDs(ArrayList<String[]> gamePairs, PreparedStatement ps, PreparedStatement psNull) throws SQLException {
		ArrayList<String[]> idPairs = new ArrayList<String[]>();
		for(String[] gamePair: gamePairs) {
			String[] idPair = new String[2];
			idPair[0] = convertPairToID(gamePair[0], gamePair[1], ps, psNull);
			idPair[1] = convertPairToID(gamePair[2], gamePair[3], ps, psNull);
			idPairs.add(idPair);
		}
		return idPairs;
	}
	
	private String convertPairToID(String name1, String name2, PreparedStatement ps, PreparedStatement psNull) throws SQLException {
		if(name2 != null) {
			return DBUtils.getPIDFromNames(name1, name2, ps);
		}
		return DBUtils.getPIDFromName(name1, psNull);
	}
	
	private void addIntoGamePairs(ArrayList<String[]> newGamePairs, String[] pair1, String[] pair2, HashMap<String, String> pMapping) {
		if(!inGamePairs(newGamePairs, pair1, pair2)) {
			String[] pairArr = new String[4];
			pairArr[0] = pair1[0];
			pairArr[1] = pair1[1];
			pairArr[2] = pair2[0];
			pairArr[3] = pair2[1];
			newGamePairs.add(pairArr);
		}
	}
	
	private boolean inGamePairs(ArrayList<String[]> newGamePairs, String[] pair1, String[] pair2) {
		for(String[] alreadyAdded:newGamePairs) {
			String addedKey1 = alreadyAdded[0] + alreadyAdded[1];
			String addedKey2 = alreadyAdded[2] + alreadyAdded[3];
			if(DBUtils.pairsAreTheSame(addedKey1, addedKey2, pair1, pair2)) {
				return true;
			}
		}
		return false;
	}
	
	private int getGamePairIndex(ArrayList<String[]> gamePairs, String[] pair) {
		for(int i = 0; i < gamePairs.size(); i++) {
			String[] gamePair = gamePairs.get(i);
			String addedKey1 = gamePair[0] + gamePair[1];
			String addedKey2 = gamePair[2] + gamePair[3];
			if(DBUtils.pairInTheGame(addedKey1, addedKey2, pair)) {
				return i;
			}
		}
		return -1;
	}
	
	private ArrayList<String[]> generateGamePairs(ArrayList<String> lines, ArrayList<String> pairList) {
		ArrayList<String> pairsNotMatched = (ArrayList<String>) pairList.clone();
		ArrayList<String[]> gamePairs = new ArrayList<String[]>();
		while(pairsNotMatched.size() > 0) {
			String pair = pairsNotMatched.remove(0);
			gamePairs.add(generateGamePair(lines, pair, pairsNotMatched));
		}
		return gamePairs;
	}
	
	private String[] generateGamePair(ArrayList<String> lines, String pairName, ArrayList<String> pairsNotMatched) {
		String[] gamePair = new String[2];
		for(int i = 0; i < lines.size(); i++) {
			String l = lines.get(i);
			ArrayList<String> parsed = DBUtils.getParsedRegex(l);
			if(parsed != null) {
				String pairName2 = parsed.get(1);
				if(pairName != null && pairName.equals(pairName2)) {
					if(actionHasDeck(parsed)) {
						String otherPair = findOppositePair(lines, i, pairName, parsed.get(2), pairsNotMatched);
						if(otherPair != null) {
							gamePair[0] = pairName;
							gamePair[1] = otherPair;
							pairsNotMatched.remove(otherPair);
							return gamePair;
						}
					}
				}
			}
		}
		return null;
	}
	
	private boolean actionHasDeck(ArrayList<String> parsed) {
		return actionHasSpecificDeck(parsed, "myTeam");
	}
	
	private boolean actionHasODeck(ArrayList<String> parsed) {
		return actionHasSpecificDeck(parsed, "oTeam");
	}
	
	private boolean actionHasSpecificDeck(ArrayList<String> parsed, String text) {
		return parsed.get(2).startsWith(text);
	}
	
	private String findOppositePair(ArrayList<String> lines, int start, String pairName, String cardType, ArrayList<String> pairsNotMatched) {
		cardType = cardType.substring(cardType.indexOf("{"));
		for(String l: lines) {
			ArrayList<String> parsed = DBUtils.getParsedRegex(l);
			if(parsed != null && !parsed.get(1).equals(pairName) && actionHasODeck(parsed)) {
				String oCardType = parsed.get(2).substring(parsed.get(2).indexOf("{"));
				if(oCardType.equals(cardType)) {
					return parsed.get(1);
				}
			}
		}
		return null;
	}

	private ArrayList<String> generatePairList(ArrayList<String> lines) {
		ArrayList<String> pairNames = new ArrayList<String>();
		for(String l:lines) {
			String pName = DBUtils.getNameLineFromRegex(l);
			if(pName != null && !pName.equals("null") && !pairNames.contains(pName)) {
				pairNames.add(pName);
			}
		}
		return pairNames;
	}
	
	private void cleanUpPairData(String[] pairNames, PreparedStatement ps) throws SQLException {
		for(int i = 0; i < pairNames.length; i++) {
			pairNames[i] = DBUtils.getUIDFromName(pairNames[i], ps);
		}
	}
}
