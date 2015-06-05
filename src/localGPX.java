import java.io.BufferedWriter;
import java.io.File;
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
import java.util.List;


import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

/*
 * Программа собирает траки с регистратора neoline V50 и формирует 
 * траки поездок.
 * Данные сохраняются на диск в виде gpx файлов.
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
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	localGPX() throws ClassNotFoundException, SQLException{
		
		setTitle("Выгрузка в GPX");
		
		setBounds(100, 100, 590, 237); 	
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
		btnExit.setBounds(389, 154, 142, 25);
		getContentPane().add(btnExit);
		
		tablo = new JLabel("Жду...");
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
		
		JButton btnNewButton = new JButton("анализ");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				try {
					
					analizGPX.main(null);
					
				} catch (Exception e) {
					
					e.printStackTrace();
					
				}
				
			}
			
		});
		
		btnNewButton.setBounds(260, 154, 97, 25);
		getContentPane().add(btnNewButton);
		
		addWindowListener(new java.awt.event.WindowAdapter() {
			
			public void windowClosing(java.awt.event.WindowEvent evt) {
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
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		
		new localGPX().setVisible(true);

	}//public static void main(String[] args) throws ClassNotFoundException, SQLException
}//public class localGPX


class newDb {
	/*
	 * проверим на существование базы данных
	 * и создадим при необходимости
	 */
	static String DB_NAME 		= "tParam";
	static String TBL_LOGGER 	= "CREATE TABLE IF NOT EXISTS logger(ID INTEGER PRIMARY KEY AUTOINCREMENT,filename TEXT);";
	static String TBL_POINTS 	= "CREATE TABLE IF NOT EXISTS points(ID INTEGER PRIMARY KEY AUTOINCREMENT,unixTime NUMERIC,tmStamp NUMERIC,lat REAL,lon REAL,speed REAL,date TEXT,time TEXT,alldate TEXT,course REAL,file TEXT,descr TEXT,deltaTime INTEGER);";
	static String TBL_PROCESS 	= "CREATE TABLE IF NOT EXISTS process(ID INTEGER PRIMARY KEY AUTOINCREMENT,time long,descr text,start boolean,end boolean)";
	static long stOld 	= 0; //прошлый штамп времени
	
	newDb() throws ClassNotFoundException, SQLException{
		
		createTables();
		clearTables();
		
	}//newDb()
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	описание:
	//		выполним sql инструкцию (без извлечения результата)
	static public void executeQw(String sql) throws SQLException, ClassNotFoundException {
		 
		Class.forName("org.sqlite.JDBC");
		Connection bd = DriverManager.getConnection("jdbc:sqlite:"+DB_NAME+".db");
		Statement st  = bd.createStatement();
		st.setQueryTimeout(60);
		st.execute(sql);
		st.close();
		
	}//static public void executeQw(String sql) throws SQLException, ClassNotFoundException
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	описание:
	//		создадим таблицы в базе , если их нет.
	static public void createTables() throws SQLException, ClassNotFoundException {
		
		Class.forName("org.sqlite.JDBC");
		Connection bd = DriverManager.getConnection("jdbc:sqlite:"+DB_NAME+".db");
		Statement st  = bd.createStatement();
		st.setQueryTimeout(60);
		
		st.execute(TBL_LOGGER);
		st.execute(TBL_POINTS);
		st.execute(TBL_PROCESS);
		
		st.close();
		
	}//static public void createTables() throws SQLException, ClassNotFoundException

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	описание:
	//		создадим таблицы в базе , если их нет.
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
	//	описание:
	//		обработка списка файлов
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
	//	описание:
	//		разбор содержимого файла и запись данных в БД
	//		для дальнейшей обработки.
	public static void parseNMEA(String adr,String fileName) throws Exception {
		
		String[] s 	= function.loadData(adr);
		long 	stT	= 0; //***текущий штамп времени
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
		 * универсальная функция , возвращает результат запроса @sql
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
	//	обработка трака в базе данных.
	public static void solveGpxTrack(String dr) throws ClassNotFoundException, SQLException {
		
		int i=0;
		long firstPoint 	= 0;
		long secondPoint 	= 0;
		String dateMark 	= "";
		List<listID> v = new ArrayList<listID>();
				
		
		ResultSet x = getResult("SELECT * FROM points WHERE lat<9999 ORDER BY tmStamp;");
		
		while (x.next()) {
			
			if (i==0) {
				
				//старт выборки....
				firstPoint  = Long.valueOf(x.getString("tmStamp"));
				secondPoint = firstPoint; //**первая точка двоилась. устраним
				
			}
			
			i++;
			
			if (Long.valueOf(x.getString("tmStamp")) - secondPoint >10000000){
				
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
		
		for (int i1=0;i1<v.size();i1++){
			
			ResultSet y = getResult("SELECT * FROM points WHERE (tmStamp>="
											+String.valueOf(v.get(i1).start)
											+") and (tmStamp<="
											+String.valueOf(v.get(i1).end)
											+") ORDER BY tmStamp;");
			
			saveGPX(y , v.get(i1).id,dr);
			
			Class.forName("org.sqlite.JDBC");
			Connection bd = DriverManager.getConnection("jdbc:sqlite:"+DB_NAME+".db");
			Statement st  = bd.createStatement();
			st.setQueryTimeout(60);
			
			st.execute("UPDATE points SET descr='" + v.get(i1).id + "' WHERE (tmStamp>="
											+String.valueOf(v.get(i1).start)
											+") and (tmStamp<="
											+String.valueOf(v.get(i1).end)
											+");");
			st.close();
			
		}//for
		
		
	}//public static void solveGpxTrack() throws ClassNotFoundException, SQLException
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void saveGPX(ResultSet rx , String id,String dr) throws ClassNotFoundException, SQLException {
		//**пишем GPX файл из базы данных
		//**@id номер трака в базе
		//**@dr адрес куда пишем
		String adrFile = dr+"\\"+id+".gpx";
		/*
		File theDir = new File(adrFile);
		
		if (theDir.exists()) {
			//**файл есть , не пишем.
			return;
		}
		*/
		Writer writer = null;
		
		try {
			writer =new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(adrFile), "utf-8"));
			String s = 
					"<?xml version='1.0' encoding='UTF-8'?>"		
					+"<gpx xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' version='1.0'>";
	    
			writer.write(s, 0 , s.length());
			s ="<trk>";
			writer.write(s, 0 , s.length());
			s = "   <name>"+id+"</name>";
			writer.write(s, 0 , s.length());
			s ="       <trkseg>";
			writer.write(s, 0 , s.length());
			int i = 0;
			while (rx.next()) {
				if (rx.getString("lat").equals("99999.0") != true & i>0) {
					s ="         <trkpt lat='"+String.valueOf(cc(rx.getString("lat")))
								+"' lon='"+String.valueOf(cc(rx.getString("lon")))+"'>";
					writer.write(s, 0 , s.length());
					s ="         <time>"+rx.getString("alldate")+"</time>";
					writer.write(s, 0 , s.length());
					s ="         <speed>"+rx.getString("speed")+"</speed>";
					writer.write(s, 0 , s.length());
					s ="         <ele>0.0</ele>";
					writer.write(s, 0 , s.length());
					s ="        </trkpt>";
					writer.write(s, 0 , s.length());
					
				}
				i++;
			}
			s ="       </trkseg>";
			writer.write(s, 0 , s.length());
			s ="</trk>";
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
	
	static Double cc(String s) {
		/*
		 * если координата умножена на 10 то приведем ее в
		 * нужный вид, потом преобразуем в десятичный формат
		 * И дальше все вычисляется достаточно просто:
		 *	46°58,1305' = 46 + (58,1305 / 60) = 46.968841
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
		
		String in = function.getDir("Укажите имя папки в которой лежат файлы NMEA:");
		String out = function.getDir("Укажите имя папки для выгрузки:"); 
		
		localGPX.tablo.setText("читаю файлы");
		newDb.beginData(in);
		localGPX.tablo.setText("выгружаю");
		newDb.solveGpxTrack(out);
		localGPX.progressBar.setValue(0);
		localGPX.tablo.setText("ГОТОВО");
		localGPX.opProgressBar.setValue(0);

	} //public static void beginProcessing() throws IOException

}//class goNmea extends Thread

class listID {
	
	long start;
	long end;
	String id;
	
}//class recGeo

