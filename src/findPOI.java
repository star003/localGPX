import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class findPOI {

	static String DB_NAME 		= "tPOI";
	static String TBL_POI 		= "CREATE TABLE IF NOT EXISTS poi(ID INTEGER PRIMARY KEY AUTOINCREMENT,lat REAL,lon REAL,radius NUMERIC,descr TEXT);";
	static String TBL_POINTS 	= "CREATE TABLE IF NOT EXISTS points(ID INTEGER PRIMARY KEY AUTOINCREMENT,unixTime NUMERIC,tmStamp NUMERIC,lat REAL,lon REAL,speed REAL,date TEXT,time TEXT,alldate TEXT,course REAL,file TEXT,descr TEXT,deltaTime INTEGER);";

	static public void createTables() throws SQLException, ClassNotFoundException {
		
		Class.forName("org.sqlite.JDBC");
		Connection bd = DriverManager.getConnection("jdbc:sqlite:"+DB_NAME+".db");
		Statement st  = bd.createStatement();
		st.setQueryTimeout(60);
		
		st.execute(TBL_POINTS);
		st.execute(TBL_POI);
		st.close();
		
	}//static public void createTables() throws SQLException, ClassNotFoundException

	public static void main(String[] args) {
	

	}

}
