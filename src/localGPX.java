import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.InvalidPropertiesFormatException;
import java.util.List;


import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

/*
 * ��������� �������� ����� � ������������ neoline V50 � ��������� 
 * ����� �������.
 * ������ ����������� �� ���� � ���� gpx ������.
 * 2015-05-28
 */

public class localGPX extends javax.swing.JFrame {
	 
	private static final long serialVersionUID = 20150527;
	public static JLabel tablo;
	public static JProgressBar progressBar;
	public static JProgressBar opProgressBar;
	
	static goTNmea x2;
	private JLabel lblCurF;
	private JLabel lblCurPr;
	
	public static setting se = new setting(); //**�������� ���������....
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	localGPX() throws ClassNotFoundException, SQLException {
		
		setTitle("�������� � GPX");
		
		setBounds(100, 100, 590, 275); 	
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setForeground(new Color(0, 206, 209));
		progressBar.setBounds(129, 51, 402, 27);
		
		getContentPane().add(progressBar);
		
		JButton btnStart = new JButton("start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				x2 = new goTNmea();
				goTNmea.started = true;
				x2.start();
				
			}
		});
		btnStart.setBounds(42, 154, 97, 25);
		getContentPane().add(btnStart);
		
		JButton btnStop = new JButton("stop");
		btnStop.addActionListener(new ActionListener() {
			
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				
				if (goTNmea.started == true){
					
					x2.stop();
					
				}	
				
			}
			
		});
		
		btnStop.setBounds(151, 154, 97, 25);
		getContentPane().add(btnStop);
		
		JButton btnExit = new JButton("exit");
		btnExit.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			
			public void actionPerformed(ActionEvent e) {
				
				if (goTNmea.started == true){
					
					x2.stop();
					
				}
				
				System.exit(0);
				
			}
			
		});
		
		btnExit.setBounds(434, 154, 97, 25);
		getContentPane().add(btnExit);
		
		tablo = new JLabel("���...");
		tablo.setBounds(63, 128, 468, 16);
		getContentPane().add(tablo);
		
		opProgressBar = new JProgressBar();
		opProgressBar.setStringPainted(true);
		opProgressBar.setForeground(new Color(0, 206, 209));
		opProgressBar.setBounds(129, 11, 402, 27);
		getContentPane().add(opProgressBar);
		
		lblCurF = new JLabel("CUR F:");
		lblCurF.setBounds(42, 11, 56, 16);
		getContentPane().add(lblCurF);
		
		lblCurPr = new JLabel("CUR PR");
		lblCurPr.setBounds(42, 51, 56, 16);
		getContentPane().add(lblCurPr);
		
		JButton btnNewButton = new JButton("������");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				try {
					
					analizGPX.main(null);
					
				} catch (Exception e) {
					
					e.printStackTrace();
					
				}
				
			}
			
		});
		
		btnNewButton.setBounds(325, 154, 97, 25);
		getContentPane().add(btnNewButton);
		
		JButton btnPoi = new JButton("POI");
		btnPoi.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				editPoints.main(null);
				
			}
			
		});
		
		btnPoi.setBounds(325, 192, 97, 25);
		getContentPane().add(btnPoi);
		
		JButton btnSet = new JButton("setting");
		btnSet.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				
				formSetting.main(null);
				
			}
			
		});
		
		btnSet.setBounds(434, 192, 97, 25);
		getContentPane().add(btnSet);
		
		addWindowListener(new java.awt.event.WindowAdapter() {
			
			public void windowClosing(java.awt.event.WindowEvent evt) {
				setting.saveSetting();
				//toClose(evt);
			}
		
			public void windowOpened(java.awt.event.WindowEvent evt) {
        	
				try {
         		
					new newDb();
				
				} catch (ClassNotFoundException | SQLException e) {
				
				}
         	
			}
        
		});
		
		
		
	}//localGPX() throws ClassNotFoundException, SQLException
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, InvalidPropertiesFormatException, FileNotFoundException, IOException{
		
		new localGPX().setVisible(true);

	}//public static void main(String[] args) throws ClassNotFoundException, SQLException
}//public class localGPX


class newDb {
	/*
	 * �������� �� ������������� ���� ������
	 * � �������� ��� �������������
	 */
	static String DB_NAME 		= "tParam";
	static String TBL_LOGGER 	= "CREATE TABLE IF NOT EXISTS logger(ID INTEGER PRIMARY KEY AUTOINCREMENT,filename TEXT);";
	static String TBL_POINTS 	= "CREATE TABLE IF NOT EXISTS points(ID INTEGER PRIMARY KEY AUTOINCREMENT,unixTime NUMERIC,tmStamp NUMERIC,lat REAL,lon REAL,speed REAL,date TEXT,time TEXT,alldate TEXT,course REAL,file TEXT,descr TEXT,deltaTime INTEGER);";
	static String TBL_PROCESS 	= "CREATE TABLE IF NOT EXISTS process(ID INTEGER PRIMARY KEY AUTOINCREMENT,time long,descr text,start boolean,end boolean)";
	static String TBL_POI 		= "CREATE TABLE IF NOT EXISTS poi(ID INTEGER PRIMARY KEY AUTOINCREMENT,lat REAL,lon REAL,speed NUMERIC,descr TEXT);";
	static String TBL_LOG 		= "CREATE TABLE IF NOT EXISTS logdrive(ID INTEGER PRIMARY KEY AUTOINCREMENT,unixTime NUMERIC,time TEXT,descr TEXT);";
	static long stOld 	= 0; //������� ����� �������
	
	newDb() throws ClassNotFoundException, SQLException{
		
		createTables();
		clearTables();
		
	}//newDb()
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	��������:
	//		�������� sql ���������� (��� ���������� ����������)
	static public void executeQw(String sql) throws SQLException, ClassNotFoundException {
		 
		Class.forName("org.sqlite.JDBC");
		Connection bd = DriverManager.getConnection("jdbc:sqlite:"+DB_NAME+".db");
		Statement st  = bd.createStatement();
		st.setQueryTimeout(60);
		st.execute(sql);
		st.close();
		
	}//static public void executeQw(String sql) throws SQLException, ClassNotFoundException
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	��������:
	//		�������� ������� � ���� , ���� �� ���.
	static public void createTables() throws SQLException, ClassNotFoundException {
		
		Class.forName("org.sqlite.JDBC");
		Connection bd = DriverManager.getConnection("jdbc:sqlite:"+DB_NAME+".db");
		Statement st  = bd.createStatement();
		st.setQueryTimeout(60);
		
		st.execute(TBL_LOGGER);
		st.execute(TBL_POINTS);
		st.execute(TBL_PROCESS);
		st.execute(TBL_POI);
		st.execute(TBL_LOG);
		
		st.close();
		
	}//static public void createTables() throws SQLException, ClassNotFoundException

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	��������:
	//		�������� ������� � ���� , ���� �� ���.
	static public void clearTables() throws SQLException, ClassNotFoundException {
		
		Class.forName("org.sqlite.JDBC");
		Connection bd = DriverManager.getConnection("jdbc:sqlite:"+DB_NAME+".db");
		Statement st  = bd.createStatement();
		st.setQueryTimeout(60);
		
		st.execute("delete from logger;");
		st.execute("delete from points;");
		st.execute("delete from process;");
		st.close();
		
	}//static public void clearTables() throws SQLException, ClassNotFoundException 
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	��������:
	//		��������� ������ ������
	static void beginData(String dr) throws ClassNotFoundException, SQLException, Exception {
		
		File directory 	= new File(dr);
		File[] fList 	= directory.listFiles();
		int stPos = fList.length;
		int i = 0;
		
		for (File file : fList){
			
			if (file.getName().indexOf(".nmea") >0 ) {
				
				parseNMEA(dr+"//"+file.getName(),file.getName());
				int tp = Math.round(i*100/stPos);
				
				try {
					
					localGPX.progressBar.setValue(tp);
					
				}
				
				catch (NullPointerException e){
					
				}
					
			}//if
			
			i++;
			
		}//for
		
		//!lbl.setText("end...");
		
	}//static void beginData(String dr) throws ClassNotFoundException, SQLException, Exception
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//	��������:
	//		������ ����������� ����� � ������ ������ � ��
	//		��� ���������� ���������.
	public static void parseNMEA(String adr,String fileName) throws Exception {
		
		String[] s 	= function.loadData(adr);
		
		long 	stT	= 0; //***������� ����� �������
		
		Class.forName("org.sqlite.JDBC");
		Connection bd = DriverManager.getConnection("jdbc:sqlite:"+DB_NAME+".db");
		Statement st  = bd.createStatement();
		st.setQueryTimeout(60);
		int stPos = s.length;
		
		for (int i = 0 ;i<s.length;i++) {
			
			if (s[i].indexOf("GPRMC")>0) {
				
				String tm = s[i];
				stT = function.retTimeStamp(function.alldata(tm.split(",")[9],tm.split(",")[1]));
				
				String zp = " INSERT INTO points (lat,lon,speed,date,time,alldate,course,file,tmStamp,deltaTime) "+
						"VALUES("	+(tm.split(",")[3].equals("")==true ? 99999.0 : tm.split(",")[3])+","+
						(tm.split(",")[5].equals("")==true ? 99999.0:tm.split(",")[5])+","+
						(tm.split(",")[7].equals("")==true ? 0:tm.split(",")[7])+",'"+
						function.dataR(tm.split(",")[9])+"','"+
						function.timeR(tm.split(",")[1])+"','"+
						function.alldata(tm.split(",")[9],tm.split(",")[1])+"',"+
						((tm.split(",")[10].equals("")==true) ? 999:tm.split(",")[10])+",'"+
						fileName+"',"
						+String.valueOf(stT)+",0);";
				
				st.execute(zp);
				stOld = stT;
				
				try {
					
					int tp = Math.round(i*100/stPos==0?1:i);
					localGPX.opProgressBar.setValue(tp);
					
				}
				
				catch (NullPointerException e){
					
				}
				
			} //if
			
		}//for
		
		st.close();
		
		localGPX.opProgressBar.setValue(0);
		
	}//public static void parseNMEA(String adr) throws Exception
	
	///////////////////////////////////////////////////////////////////////////////////
	
	static public ResultSet getResult(String sql) throws ClassNotFoundException{
		/*
		 * ������������� ������� , ���������� ��������� ������� @sql
		 */
		try {

			Class.forName("org.sqlite.JDBC");
			Connection bd 			= DriverManager.getConnection("jdbc:sqlite:"+DB_NAME+".db");
			ResultSet resultSet 	= null; 
			Statement st 			= bd.createStatement();
			st.setQueryTimeout(60);
			resultSet  				= st.executeQuery(sql);
			return resultSet;

		} catch (SQLException e) {

			e.printStackTrace();
			return null;

		}

	} //ResultSet getResult(String sql) throws ClassNotFoundException, SQLException

	///////////////////////////////////////////////////////////////////////////////////
	//	��������� ����� � ���� ������.
	public static void solveGpxTrack(String dr) throws ClassNotFoundException, SQLException {
		
		int i				=0;
		long firstPoint 	= 0;
		long secondPoint 	= 0;
		String dateMark 	= "";
		List<listID> v 		= new ArrayList<listID>();
				
		Class.forName("org.sqlite.JDBC");
		Connection bd 			= DriverManager.getConnection("jdbc:sqlite:"+DB_NAME+".db");
		Statement st 			= bd.createStatement();
		st.setQueryTimeout(60);
		ResultSet x = st.executeQuery("SELECT * FROM points WHERE lat<9999 ORDER BY tmStamp;");
		
		while (x.next()) {
			
			if (i==0) {
				
				//����� �������....
				firstPoint  = Long.valueOf(x.getString("tmStamp"));
				secondPoint = firstPoint; //**������ ����� ��������. ��������
				
			}
			
			i++;
			
			if (Long.valueOf(x.getString("tmStamp")) - secondPoint > setting.parking_slot){
				
				listID v1 	= new listID();
				v1.id 		= function.deleteNouse(dateMark); 
				v1.start 	= firstPoint;
				firstPoint  = secondPoint;
				v1.end 		= firstPoint-1;
				v.add(v1);
				
			}
			
			secondPoint =Long.valueOf(x.getString("tmStamp"));
			dateMark =x.getString("allDate");
			
		}
		
		listID v1 	= new listID();
		v1.id 		= function.deleteNouse(dateMark); 
		v1.start 	= firstPoint;
		firstPoint  = secondPoint;
		v1.end 		= firstPoint-1;
		v.add(v1);
		
		//**����� ������� ����� �� ���� ������
		//**��� ����������� ����������
		
		ResultSet  rWp = st.executeQuery("SELECT * FROM poi;");
		List<poiID> v8 		= new ArrayList<poiID>();
		
		while (rWp.next()) {
			
			poiID v7 = new poiID();
			
			v7.lat 		=rWp.getString("lat");
			v7.lon 		=rWp.getString("lon");
			v7.descr    =rWp.getString("descr");
			v8.add(v7);
		}	
		
		
		for (int i1 = 0 ; i1 < v.size() ; i1++ ){
			
			//**����� ����� ��� ���������� �����
			ResultSet rTr = st.executeQuery("SELECT * FROM points WHERE (tmStamp>="
											+String.valueOf(v.get(i1).start) + ") and (tmStamp<="
											+String.valueOf(v.get(i1).end) + ") ORDER BY tmStamp;");
			
			saveGPX(rTr , v8 , v.get(i1).id , dr);
			
			st.execute("UPDATE points SET descr='" + v.get(i1).id + "' WHERE (tmStamp>="
											+String.valueOf(v.get(i1).start)
											+") and (tmStamp<="
											+String.valueOf(v.get(i1).end)
											+");");
			st.close();
			
		}//for
		
		saveWp(v8 , dr); //**�������� ������� �����
		
	}//public static void solveGpxTrack() throws ClassNotFoundException, SQLException
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void saveGPX(ResultSet rx ,List<poiID> ry , String id , String dr) throws ClassNotFoundException, SQLException {
		
		//**����� GPX ���� �� ���� ������
		//**@id ����� ����� � ����
		//**@dr ����� ���� �����
		//**@ry ����� ������� ������� ����� (poi)
		
		String adrFile = dr+"\\"+id+".gpx";
		
		Writer writer = null;
		
		try {
			
			writer =new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(adrFile), "utf-8"));
			String s = 
					"<?xml version='1.0' encoding='UTF-8'?> \n"		
					+"<gpx xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' version='1.0'> \n";
			writer.write(s, 0 , s.length());
			/*
			for (poiID y : ry) {
				
				s ="<wpt lat='"+String.valueOf((y.lat))
						+"' lon='"+String.valueOf((y.lon))+"'>";
				writer.write(s, 0 , s.length());
				
				s ="<name>"+y.descr+"</name>";
				writer.write(s, 0 , s.length());
			
				s ="</wpt> \n";
				writer.write(s, 0 , s.length());
				
			}
			
			*/
			
			s ="<trk> \n";
			writer.write(s, 0 , s.length());
			s = "   <name>"+id+"</name> \n";
			writer.write(s, 0 , s.length());
			s ="       <trkseg> \n";
			writer.write(s, 0 , s.length());
			
			int i = 0;
			
			while (rx.next()) {
				
				if (rx.getString("lat").equals("99999.0") != true & i > 0) {
					
					s ="         <trkpt lat='"+String.valueOf(cc(rx.getString("lat")))
								+"' lon='"+String.valueOf(cc(rx.getString("lon")))+"'>";
					writer.write(s, 0 , s.length());
					s ="         <time>"+rx.getString("alldate")+"</time> \n";
					writer.write(s, 0 , s.length());
					s ="         <speed>"+rx.getString("speed")+"</speed> \n";
					writer.write(s, 0 , s.length());
					s ="         <ele>0.0</ele> \n";
					writer.write(s, 0 , s.length());
					s ="        </trkpt> \n";
					writer.write(s, 0 , s.length());
					
				}
				
				i++;
				
			}
			
			s ="       </trkseg> \n";
			writer.write(s, 0 , s.length());
			s ="</trk> \n";
			writer.write(s, 0 , s.length());
			s ="</gpx>";
			writer.write(s, 0 , s.length());
			
		} 
		
		catch (IOException ex) {

		} 
		
		finally {
			
			try {
				
				writer.close();
				
			} 
			catch (Exception ex) {}
			
		}
		
    } //saveUrl(String filename, String urlString)
	
	///////////////////////////////////////////////////////////////////////////////////
	/*
	 * ������ �������� ������� ����� � ���� ������ �������� ����� ����������� ����
	 */
	static public void saveWp(List<poiID> ry , String dr){
		
		Calendar currentTime = Calendar.getInstance();
		String id = "wpt_"+	String.valueOf(currentTime.get(1))+"-"
								+ (currentTime.get(2) + 1 >= 10 ? String.valueOf(currentTime.get(2) + 1) : "0"+String.valueOf(currentTime.get(2) + 1)) +"-"
								+(currentTime.get(5) >=10 ? String.valueOf(currentTime.get(5))  : "0"+String.valueOf(currentTime.get(5)));
		
		String adrFile = dr+"\\"+id+".gpx";
		
		Writer writer = null;
		
		try {
			
			writer =new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(adrFile), "utf-8"));
			String s = 
					"<?xml version='1.0' encoding='UTF-8'?> \n"		
					+"<gpx xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' version='1.0'> \n";
			writer.write(s, 0 , s.length());
			
			for (poiID y : ry) {
				
				s ="<wpt lat='"+String.valueOf((y.lat))
						+"' lon='"+String.valueOf((y.lon))+"'>";
				writer.write(s, 0 , s.length());
				
				s ="<name>"+y.descr+"</name>";
				writer.write(s, 0 , s.length());
			
				s ="</wpt> \n";
				writer.write(s, 0 , s.length());
				
			}
			
			s ="</gpx>";
			writer.write(s, 0 , s.length());
			
		} 
		
		catch (IOException ex) {

		} 
		
		finally {
			
			try {
				
				writer.close();
				
			} 
			
			catch (Exception ex) {}
			
		}
		
	}//static public void saveWp(List<poiID> ry , String dr)
	
	///////////////////////////////////////////////////////////////////////////////////
	
	static Double cc(String s) {
		/*
		 * ���� ���������� �������� �� 10 �� �������� �� �
		 * ������ ���, ����� ����������� � ���������� ������
		 * � ������ ��� ����������� ���������� ������:
		 *	46�58,1305' = 46 + (58,1305 / 60) = 46.968841
		 */
		
		if (Double.valueOf(s) <9999 & Double.valueOf(s) >100)  {
			
			String p0 = String.valueOf(Double.valueOf(s)*0.01);
			String p1 = p0.split("\\.")[1];
			
			return Double.valueOf(p0.split("\\.")[0]) 
					+(Double.valueOf(p1.substring(0,2)
							+"."+p1.substring(2,p1.length()))/60);
			 
		}
		
		String p1 = s.split("\\.")[1];
		
		return Double.valueOf(
				s.split("\\.")[0]) 
					+(Double.valueOf( p1.substring(0,2)
							+"."+p1.substring(2,p1.length()))/60);
		
	} //Double static cc(Staring s)
	
}//class newDb

/////////////////////////////////////////////////////////////////////////////////////

class goTNmea extends Thread {

	static boolean started;

	/////////////////////////////////////////////////////////////////////////////////////

	public void run() {

		try {

			beginProcessing();

		} catch (Exception e) {
			
			e.printStackTrace();
		
		}

	} //public void run()

	/////////////////////////////////////////////////////////////////////////////////////

	public static void beginProcessing() throws Exception {
		
		String in 	= function.getDir("������� ��� ����� � ������� ����� ����� NMEA:",setting.load_Path);
		String out 	= function.getDir("������� ��� ����� ��� ��������:",setting.save_Path); 
		
		localGPX.tablo.setText("����� �����");
		newDb.beginData(in);
		localGPX.tablo.setText("��������");
		newDb.solveGpxTrack(out);
		localGPX.progressBar.setValue(0);
		localGPX.tablo.setText("������");
		localGPX.opProgressBar.setValue(0);

	} //public static void beginProcessing() throws IOException

}//class goNmea extends Thread

	/////////////////////////////////////////////////////////////////////////////////////

class listID {
	
	long start;
	long end;
	String id;
	
}//class recGeo

	/////////////////////////////////////////////////////////////////////////////////////

class poiID {
	
	String lat;
	String lon;
	String descr;
	
}//class poiID

