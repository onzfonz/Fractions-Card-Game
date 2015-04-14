package logparser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

import basic.Constants;

public class PopulateCardsTable
{
    private Connection conn;
    private ArrayList<Statement> statements;
    private PreparedStatement psInsert;
//    private ResultSet rs;
    
	public static void main(String[] args)
    {
        ArrayList<String> teams = DBUtils.readFileIntoList(Constants.FNAME_TEAM_DECK);
//      DBUtils.printArrayList(teams);
        ArrayList<String> tricks = DBUtils.readFileIntoList(Constants.FNAME_TRICK_DECK);
        
        PopulateCardsTable db = new PopulateCardsTable();
        db.uploadData(teams, tricks);
        System.out.println("PopulateCardsTable finished");
    }
    
    public PopulateCardsTable() {
    	DBUtils.loadDriver(DBUtils.DB_DRIVER);
    	statements = new ArrayList<Statement>(); // list of Statements, PreparedStatements
    }
    
    public void uploadData(ArrayList<String> teams, ArrayList<String> tricks) {
    	try {
            conn = DBUtils.getDBConnection(DBUtils.DB_SCHEMA, DBUtils.DB_NAME, DBUtils.DB_PROTOCOL);
            conn.setAutoCommit(false);
            statements.add(conn.createStatement());
            
           // parameter 1 is num (int), parameter 2 is addr (varchar)
            psInsert = conn.prepareStatement(DBUtils.SQL_INSERT_CARDS);
            statements.add(psInsert);
            
            insertCardsIntoTable(teams, tricks, psInsert);
            conn.commit();            
        }
        catch (SQLException sqle) {
            DBUtils.printSQLException(sqle);
        } finally {
            // release all open resources to avoid unnecessary memory usage
        	DBUtils.cleanUp(null, statements, conn);
        }
    }
    
    private void insertCardsIntoTable(ArrayList<String> teams, ArrayList<String> tricks, PreparedStatement ps) throws SQLException{
        insertTeamsIntoTable(teams, ps);
        insertTricksIntoTable(tricks, ps);
    }
    
    private void insertTeamsIntoTable(ArrayList<String> lines, PreparedStatement ps) throws SQLException {
    	for(String l: lines) {
    		String[] vals = l.split(",");
    		String[] realVals = DBUtils.cleanUpTeamsData(vals);
    		System.out.println(Arrays.asList(realVals));
//    		DBUtils.prepareSingleInsertIntoTable(realVals, ps, DBUtils.SQL_TYPES_CARDS, false);
    	}
    }
    
    private void insertTricksIntoTable(ArrayList<String> lines, PreparedStatement ps) throws SQLException {
    	for(String l: lines) {
    		String[] vals = l.split(",");
    		String[] realVals = DBUtils.cleanUpTricksData(vals);
    		System.out.println(Arrays.asList(realVals));
//    		DBUtils.prepareSingleInsertIntoTable(realVals, ps, DBUtils.SQL_TYPES_CARDS, false);
    	}
    }
}
