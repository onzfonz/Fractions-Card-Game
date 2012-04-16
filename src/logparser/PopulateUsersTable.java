package logparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;

import java.util.ArrayList;

import logparser.DBUtils;

public class PopulateUsersTable
{
    private String driver = "org.apache.derby.jdbc.ClientDriver";
    private String protocol = "jdbc:derby://localhost:1527/";
    private static final int[] SQL_TYPES = {Types.INTEGER, Types.VARCHAR, Types.SMALLINT, Types.CHAR, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT};
    private static String SQL_USERS_INSERT = "insert into users (uid, uname, gender, treatment, u3cset, u3frac, u4cset, u4frac, u4ela, aq1, aq2, aq3, aq4, aq5, aq6, aq7, aq8, aq9, aq10, aq11, aq12, aq13, aq14, aq15, aq16, aq17, aq18, aq19, aq20, aq21, aq22, aqtot, mq1, mq2, mq3, mq4, mq5, mq6, mq7, mq8, mq9, mq10, mq11, mq12, mq13, mq14, mq15, mq16, mq17, mq18, mq19, mq20, mq21, mq22, mqtot, zq1, zq2, zq3, zq4, zq5, zq6, zq7, zq8, zq9, zq10, zq11, zq12, zq13, zq14, zq15, zq16, zq17, zq18, zq19, zq20, zq21, zq22, zqtot) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static void main(String[] args)
    {
        PopulateUsersTable db = new PopulateUsersTable();
        ArrayList<String> lines = db.readUsersFile();
        for(int i = 0; i < lines.size(); i++) { 
        	System.out.println(lines.get(i));
        }
        db.uploadData(lines);
        System.out.println("PopulateUsersTable finished");
    }
    
    public PopulateUsersTable() {
    	DBUtils.loadDriver(driver);
    }
    
    public ArrayList<String> readUsersFile() {
    	BufferedReader bf = openDBFile("sqldatausers.csv");
    	return retrieveLines(bf);
    }
    
    public BufferedReader openDBFile(String aFileName) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		InputStream is = cl.getResourceAsStream(aFileName);
		return new BufferedReader(new InputStreamReader(is));
	}
	
	private ArrayList<String> retrieveLines(BufferedReader bf) {
		ArrayList<String> temp = new ArrayList<String>();
		try {
			String l = bf.readLine(); //throw away first line.
			l = bf.readLine();
			while (l != null && !l.equals("")){
				temp.add(l);
				l = bf.readLine();
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
		return temp;
	}
    
    public void uploadData(ArrayList<String> lines) {
        Connection conn = null;
        ArrayList<Statement> statements = new ArrayList<Statement>(); // list of Statements, PreparedStatements
        PreparedStatement psInsert = null;
        ResultSet rs = null;
        try
        {
            conn = DBUtils.getDBConnection("TUG", "tugofwar", protocol);
        	conn.setAutoCommit(false);
            statements.add(conn.createStatement());

           // parameter 1 is num (int), parameter 2 is addr (varchar)
            psInsert = conn.prepareStatement(SQL_USERS_INSERT);
            statements.add(psInsert);

            insertAllIntoTable(lines, psInsert);
            conn.commit();            
        }
        catch (SQLException sqle) {
            DBUtils.printSQLException(sqle);
        } finally {
            // release all open resources to avoid unnecessary memory usage
        	DBUtils.cleanUp(rs, statements, conn);
        }
    }
    
    private void insertAllIntoTable(ArrayList<String> lines, PreparedStatement ps) throws SQLException{
    	for(String l: lines) {
    		String[] vals = l.split(",");
    		transformGenderToBinary(vals);
    		DBUtils.prepareSingleInsertIntoTable(vals, ps, SQL_TYPES, false);
    		ps.executeUpdate();
    	}
    }
    
    private void transformGenderToBinary(String[] vals) {
    	if(vals[2].equals("M")) {
    		vals[2] = "1";
    	}else if(vals[2].equals("F")) {
    		vals[2] = "0";
    	}
    }
}
