import java.awt.Dimension;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JProgressBar;

public class analizGPX  extends javax.swing.JFrame {
	
	static long stop_Time_Sec = 60; //**время в секундах , превышая которое будем считать что это остановка
	
	private static final long serialVersionUID = 20140304;
	
	static boolean goReading = false;
	
	static String txt = "";
	
	static public JTextArea txTablo = new JTextArea();
	
	static String pathDB = "tParam.db";
	
	static public JProgressBar progressBar = new JProgressBar();
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void main(String[] args) throws Exception  {
		
		try {
			
			analizGPX dialog = new analizGPX();
			dialog.setPreferredSize(new Dimension(400, 80));;
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			
		} catch (Exception e) {
			
		}
		
	}	//public static void main(String[] args) throws Exception
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	public analizGPX() {
		setBounds(100, 100, 720, 572);
		getContentPane().setLayout(null);
		
		txTablo.setRows(17);
		txTablo.setColumns(80);
		
		txTablo.setBounds(39, 30, 680, 174);
		JScrollPane outScroll = new JScrollPane(txTablo);
		outScroll.setBounds(10, 10, 680, 422);
		getContentPane().add(outScroll);
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel.setBounds(405, 475, 285, 39);
		getContentPane().add(panel);
		
		JButton btnFileList = new JButton("инф. о траке");
		btnFileList.addActionListener(new ActionListener() {
			
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				
				solveData rs = new solveData();
				
				try {
					if (solveData.started) {
						
						rs.stop();
						rs.start();
						
					}	
					else {
						
						rs.start();
						
					}
					
				} catch (Exception e1) {
					
					txTablo.append("\n ошибка запуска потока анализа трака :");
					e1.printStackTrace();
					
				}
				
			}
			
		});
		
		JButton btnPoi = new JButton("POI");
		btnPoi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				editPoints.main(null);
			}
		});
		panel.add(btnPoi);
		
		panel.add(btnFileList);
		
		JButton btnNewButton_3 = new JButton("остановки");
		panel.add(btnNewButton_3);
		
		
		progressBar.setBounds(10, 446, 680, 14);
		getContentPane().add(progressBar);
		
		btnNewButton_3.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				try {
					
					//waitAnaliz();
					
				} catch (Exception e1) {
					
					txTablo.append("\n ошибка wait анализа :");
					
				}
				
			}
		});
		//getContentPane().add(txTablo);
	}	

	//////////////////////////////////////////////////////////////////////////////////////////////
	
	static void minInfo() throws Exception{
		
		String r = "";
		
		JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File(setting.save_Path == null ? ".":setting.save_Path));
        chooser.setDialogTitle("");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        	
        	r = chooser.getSelectedFile().getAbsolutePath();
        	//setting.savePath = r;
        } 
        
        txTablo.setText(null);
        
        progressBar.setValue(0);
        
		List<recGeo1> x =  parserGPXsax.getData(r);
		
		txTablo.append("\n ТРАК :");
		txTablo.append(r);
		txTablo.append("\n Начало :");
		txTablo.append(parserGPXsax.unixToDate(x.get(1).absTime));
		
		txTablo.append("\n Окончание :");
		
		txTablo.append(parserGPXsax.unixToDate(x.get(x.size()-2).absTime));
		
		txTablo.append("\n Всего пройдено : "+x.get(x.size()-1).total+" метров");
		
		txTablo.append("\n Время по траку (минут): ");
		
		txTablo.append( 
				String.valueOf((x.get(x.size()-1).absTime - x.get(2).absTime)/60000));
		
		long oldTm = 0;
		long st = x.get(2).absTime;
		long totalTimeStop = 0;
		int i = 0;
		int stPos = x.size();
		
		for (recGeo1 y : x) {
			
			long pr = (y.absTime - st)/1000 >0 ?(y.absTime - st)/1000:0;
			
			if(pr - oldTm > stop_Time_Sec){
				
				totalTimeStop+= (pr - oldTm)/60;
				
			}
			
			oldTm = pr;
			
			i++;
			
			int tp = Math.round(i*100/stPos);
			
			progressBar.setValue(tp);
			
		}
		
		
		txTablo.append("\n	-----    Общее время остановки (минут): ");
		
		txTablo.append(String.valueOf(totalTimeStop));
		
		
		txTablo.append("\n Общее время движения (минут): ");
		
		long allDriveMinute =((x.get(x.size()-1).absTime - x.get(2).absTime)/60000)-totalTimeStop; 
		txTablo.append(String.valueOf(allDriveMinute));
		
		progressBar.setValue(0);
		
		infoPOI(x);
		waitAnaliz(x);
		
		x.clear();
		
		solveData.started = false;
		
	}//public analizGPX()
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * подцепим точки интереса по пути прохождения 
	 */
	static void infoPOI(List<recGeo1> x) throws ClassNotFoundException, SQLException {
		
		int i = 0;
		int stPos = x.size();
		
		Class.forName("org.sqlite.JDBC");
		Connection bd = DriverManager.getConnection("jdbc:sqlite:"+pathDB);
		Statement st  = bd.createStatement();
		st.setQueryTimeout(60);
		
		st.execute("DELETE FROM logdrive;");
		
		String pr 	= "";
		String cur 	= "";
		
		/*!
		 * параметр x.get(0).absTime всегда равен 0
		 * сделаем его = x.get(2).absTime, что бы не искажать анализ времени начала трака
		 */
		
		if (x.get(0).absTime ==0) {
			x.get(0).absTime = x.get(2).absTime;
			x.get(1).absTime = x.get(2).absTime;
		}
		//*!
		
		long oldTm 	= 0;
		long st1 	= x.get(2).absTime;
		
		for (recGeo1 y : x) {
			
			String strQ = "SELECT * FROM poi WHERE (lat >="+String.valueOf(y.lat - setting.x_Lat)
					+" and lat <="+String.valueOf(y.lat + setting.x_Lat)+") and (lon >="
					+String.valueOf(y.lon - setting.x_Lon)+" and lon <="+String.valueOf(y.lon + setting.x_Lon)+") LIMIT 1;";
			
			ResultSet  r = st.executeQuery(strQ);
			
			long pr1 = (y.absTime - st1)/1000 >0 ?(y.absTime - st1)/1000:0;
			
			if(pr1 - oldTm > stop_Time_Sec){
				
				//** вычислим стоянки 
				String sq = " INSERT INTO logdrive (unixTime,time,descr) VALUES("+String.valueOf(y.absTime)+",'"+parserGPXsax.unixToDate(y.absTime)+"','---> стоянка: "+String.valueOf((pr1 - oldTm)/60)+" мин.');";
				st.execute(sq);
				
			}
			
			oldTm = pr1;
			
			while (r.next()) {
				 
				cur = r.getString("descr");
				
				if (cur.equals(pr)!=true) {
					
					String sq = " INSERT INTO logdrive (unixTime,time,descr) VALUES("+String.valueOf(y.absTime)+",'"+parserGPXsax.unixToDate(y.absTime)+"','"+cur+"');";
					st.execute(sq);
					pr = cur;
					y.wpName = cur;

				}	
			
			}
			
			i++;
			int tp = Math.round(i*100/stPos);
			progressBar.setValue(tp);
			
		}
		txTablo.append("\n    ---------------------	--------------------- ");
		txTablo.append("\n    путевые точки: ");
		
		//ResultSet  r1 = st.executeQuery("SELECT MIN(time) as minTime, MAX(time) as maxTime,descr FROM logdrive GROUP BY descr ORDER BY ID DESC;");
		ResultSet  r1 = st.executeQuery("SELECT time as minTime, time as maxTime , descr FROM logdrive ;");
		
		while (r1.next()) {
			
			txTablo.append("\n "+r1.getString("minTime"));
			txTablo.append("	");
			txTablo.append(r1.getString("descr"));
			
		}	
		
		st.close();
		txTablo.append("\n    ---------------------	--------------------- ");
		progressBar.setValue(0);
		
	}//static void infoPOI()

	/////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * анализируем кратковременные остановки 
	 */
	static void waitAnaliz(List<recGeo1> x) throws Exception{
		
		/*
		String r = "";
		
		JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File(setting.savePath == null ? "E://_reg//GPX//2015-06//":setting.savePath));
        chooser.setDialogTitle("");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        txTablo.setText(null);
        
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        	r = chooser.getSelectedFile().getAbsolutePath();
        } 
        txTablo.setText(null);
        List<recGeo1> x =  parserGPXsax.getData(r);
        */
		
        Class.forName("org.sqlite.JDBC");
		Connection bd = DriverManager.getConnection("jdbc:sqlite:"+pathDB);
		Statement st  = bd.createStatement();
		st.setQueryTimeout(60);
		
        progressBar.setValue(0);
        
		ArrayList<recGeo1> stac = new ArrayList<recGeo1>(); //**стек координат для анализа
		ArrayList<recGeo1> rez = new ArrayList<recGeo1>();  //**массив результата анализа 
		
		Double summDist = 0.0; //**счетчик дистанции в стеке 
		Double totalWait = 0.0;
		
		if (x.get(0).absTime ==0) {
			
			x.get(0).absTime = x.get(2).absTime;
			x.get(1).absTime = x.get(2).absTime;
			
		}
		
		for (recGeo1 y : x) {
			
			stac.add(y);
			summDist += y.distance;
			
			if (summDist > setting.control_Distance) {
				
				if(stac.size()>setting.control_Time) {
					/*
					 * запишем результат в массив. Это остановка
					 */
					double tim = (stac.get(stac.size()-1).absTime - stac.get(0).absTime)/1000;
					if (tim <= setting.limit_To_Time_Wait & tim >0) {
						stac.get(0).ds1 			= "ожидание "+String.valueOf( 
							(stac.get(stac.size()-1).absTime - stac.get(0).absTime)/1000)+" c.";
					
						totalWait +=  tim<= setting.limit_To_Time_Wait & tim >0 ? tim :0;
					
						boolean killFor = false;
					
						/*
						 * найдем что это за точка , если она есть в базе
						 */
					
						for (recGeo1 recGeo1 : stac) {
						
							String strQ = "SELECT * FROM poi WHERE (lat >=" + String.valueOf(recGeo1.lat - setting.wait_Lat)
								+" and lat <=" + String.valueOf(recGeo1.lat + setting.wait_Lat)+") and (lon >="
								+ String.valueOf(recGeo1.lon - setting.wait_Lon) + " and lon <=" + String.valueOf(recGeo1.lon + setting.wait_Lon) + ") LIMIT 1;";
							
							ResultSet  r1 = st.executeQuery(strQ);
						
							while (r1.next()) {
							
								stac.get(0).ds1 = stac.get(0).ds1.concat(r1.getString("descr"));
								killFor = true;
							
							}
						
							if (killFor == true){
							
								killFor = false;
								break;
							
							}
						
						}
					
						rez.add(stac.get(0));
					}
					
				}
				
				summDist = 0.0;
				stac.clear();
				
			}
			/*
			txTablo.append(String.valueOf(y.distance));
			txTablo.append("	");
			txTablo.append(String.valueOf(y.absTime));
			txTablo.append("	");
			txTablo.append(String.valueOf(summDist));
			txTablo.append("	");
			txTablo.append(String.valueOf(stac.size()>30 ? "wait" : "" ));
			txTablo.append(" \n");
			*/

		}
		
		txTablo.append(" \n");
		
		txTablo.append("----------------------- \n");
		for (recGeo1 rt : rez) {
			
			txTablo.append(parserGPXsax.unixToDate(rt.absTime));
			txTablo.append("	");
			txTablo.append(String.valueOf(rt.ds1));
			txTablo.append("\n");
			
		}
		
		txTablo.append("Ожидание всего:	");
		txTablo.append(String.valueOf(totalWait));
		txTablo.append("\n");
		
	}//static void waitAnaliz()
	
}//public class analizGPX

class solveData extends Thread {

	static boolean started;

	/////////////////////////////////////////////////////////////////////////////////////

	public void run() {

		try {

			beginProcessing();

		} catch (Exception e) {
			
			analizGPX.txTablo.append("\n ошибка старта потока расчета :");
			
		}

	} //public void run()

	/////////////////////////////////////////////////////////////////////////////////////

	public static void beginProcessing() throws Exception {
		
		analizGPX.minInfo();

	} //public static void beginProcessing() throws IOException

}//class goNmea extends Thread

