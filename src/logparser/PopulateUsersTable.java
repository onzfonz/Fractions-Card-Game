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
//    private static final int[] SQL_TYPES = {Types.INTEGER, Types.VARCHAR, Types.SMALLINT, Types.CHAR, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT};
//    private static String SQL_USERS_INSERT = "insert into users (uid, uname, gender, treatment, u3cset, u3frac, u4cset, u4frac, u4ela, aq1, aq2, aq3, aq4, aq5, aq6, aq7, aq8, aq9, aq10, aq11, aq12, aq13, aq14, aq15, aq16, aq17, aq18, aq19, aq20, aq21, aq22, aqtot, mq1, mq2, mq3, mq4, mq5, mq6, mq7, mq8, mq9, mq10, mq11, mq12, mq13, mq14, mq15, mq16, mq17, mq18, mq19, mq20, mq21, mq22, mqtot, zq1, zq2, zq3, zq4, zq5, zq6, zq7, zq8, zq9, zq10, zq11, zq12, zq13, zq14, zq15, zq16, zq17, zq18, zq19, zq20, zq21, zq22, zqtot) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final int[] SQL_TYPES = {Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.CHAR, Types.SMALLINT, Types.SMALLINT, Types.VARCHAR, Types.SMALLINT, Types.SMALLINT, Types.CHAR, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.CHAR, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.SMALLINT, Types.VARCHAR, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.SMALLINT, Types.VARCHAR, Types.VARCHAR};
    private static String SQL_USERS_INSERT = "insert into users (uid, uname, lastname, teacher, instudy, hasprepost, notes, gender, camath, mathlev, speced, englishprof, islatino, treatment, group1, side1, group2, side2, group3, side3, pre1, pre2, pre3, pre4, pre5, pre6, pre7, pre8, pre9, pre10, pre11, pre12, pre13, pre14, pre15, pre16, pre17, pre18, pre19, pre20, pre21, pre22a, pre22b, pre22c, pre22d, pre22e, pre22f, pre23a, pre23b, pre23c, pre23d, pre23e, pre24, pre25, pre26, pre27a, pre27b, pre27c, pre28a, pre28b, pre28c, pre28d, pretot, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22a, p22b, p22c, p22d, p22e, p22f, p23a, p23b, p23c, p23d, p23e, p24, p25, p26, p27a, p27b, p27c, p28a, p28b, p28c, p28d, pa1, pa2, pa3, pa4, pa5, pa6, pa7, pa8, pa9, pa10, pa11, pa12, pa13, ptot, delta, prep, pnotes) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
    	BufferedReader bf = openDBFile("sqldatausersgarfield2.csv");
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
            conn = DBUtils.getDBConnection(DBUtils.DB_SCHEMA, DBUtils.DB_NAME, protocol);
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
    		System.out.println("inserting - " + l);
    		String[] vals = l.split(",");
    		transformGenderToBinary(vals, 8);  //gender column is 8th one, in Hawes it was the 3rd col.
    		DBUtils.prepareSingleInsertIntoTable(vals, ps, SQL_TYPES, false);
//    		ps.executeUpdate();
    		System.out.println("executed it again?");
    	}
    }
    
    private void transformGenderToBinary(String[] vals, int genderCol) {
    	genderCol--;
    	if(vals[genderCol].equals("M")) {
    		vals[genderCol] = "1";
    	}else if(vals[genderCol].equals("F")) {
    		vals[genderCol] = "0";
    	}
    }
}
