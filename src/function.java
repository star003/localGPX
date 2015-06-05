import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFileChooser;

public class function {
	
	///////////////////////////////////////////////////////////////////////////////////
	
	public static String deleteNouse(String s) {
		/*
		 * ������ ���������������� ������� �� ������ S
		 */
		return s.replaceAll("\\.", "-").replaceAll("\\:", "_");
	} //public static String deleteNouse(String s)
	
	///////////////////////////////////////////////////////////////////////////////////
	
	public static String getTimeCreateFile( String adr) throws IOException {
		/*
		 * ������ ����� �������� ����� 
		 * ������� ����� �� ������ @adr
		 */
		Path path = Paths.get(adr);
		BasicFileAttributes attributes = 
		Files.readAttributes(path, BasicFileAttributes.class);
		FileTime creationTime = attributes.creationTime();
		return creationTime.toString();
		
	} //public static String getTimeCreateFile( ) throws IOException
	
	///////////////////////////////////////////////////////////////////////////////////
	
	public static String getDir(String ops){
		/*
		 * ������ ������ ���������� 
		 * @ops - ��������� ���� ������
		 * ������ ���� � ��������� ����������
		 */
		JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle(ops);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        	
        	String r = chooser.getSelectedFile().getAbsolutePath();
			return r;
			
         } 
        else {
        	
        	return null;
        	
        }
	}//public String getDir()

	///////////////////////////////////////////////////////////////////////////////////
	
	static public ResultSet getResult(String sql) throws ClassNotFoundException{
		/*
		 * ������������� ������� , ���������� ��������� ������� @sql
		 */
		try {
			
			Class.forName("org.sqlite.JDBC");
			Connection bd 			= DriverManager.getConnection("jdbc:sqlite:param.db");
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
	
	static String delSymbol(String s) {
		/*
		 * �������� ���� ������ @s �� ������� ���������������� ��������
		 * ���� ������ - �� ������� ��...
		 */
		return s.replace(",",".").replace("-",".");
	} //static String delSymbol(String s)
	
	///////////////////////////////////////////////////////////////////////////////////
	
	static public boolean findData(String id) throws SQLException, ClassNotFoundException  {
		/*
		 * ������ � ���� ������� @id ���� ������ �� ������ ������
		 */
		ResultSet r =  function.getResult("SELECT * FROM logger WHERE filename = '"+id+"';");
		int 	i		= 0;
		
		while (r.next()) {
			
			i++;
			
		}
		
		return  (i>0) ? true: false;
		
	}//public static boolean findData(String id) throws ClassNotFoundException, SQLException
	
	///////////////////////////////////////////////////////////////////////////////////
	
	public static String dirName(String f) {
		/*
		 * ������ ���� �� �������� ����� @f, 
		 * ��� ����� ������ ����������
		 */
		if(f==null) return "resizeDir";
		
		return f.split(" ")[0];
	}//public static String dirName(String f)
	
	///////////////////////////////////////////////////////////////////////////////////
	
	public static String addLeadingZeroes(int size, int value) {
		
	    return String.format("%0"+size+"d", value);
	    
	} //public static String addLeadingZeroes(int size, int value)
	
	///////////////////////////////////////////////////////////////////////////////////
	
	static public String[] loadData(String lnk) throws IOException {
		/*
		***********************************************************
		** ������������� ����������, ������ String[] ����� �� lnk 
		** ������ ��� ����											
		***********************************************************
		*/
		System.out.println(lnk);
		String[] everything=null;
		BufferedReader br = new BufferedReader(new FileReader(lnk));
		
		try {
			
			StringBuilder sb = new StringBuilder();
		    String line = br.readLine();
		    
		    while (line != null) {
		    	
		    	sb.append(line);
		        sb.append("\n");
		        line = br.readLine();
		        
		    }
		    
		    everything = sb.toString().split("\\s+");
		    
		    } finally {
		    	
		        br.close();
		        
		    }
		
		    return everything;
		    
	} //static String[] loadData(String lnk) throws Exception
	
	///////////////////////////////////////////////////////////////////////////////////
	
	public static String alldata(String dat,String tim) {
		/*
		 * ����������� � ������ ���� alldata = '2013-11-25T08:40:00.598Z'
		 * dat =220114 , tim = 140250.00
		 */
		if (dat.length()<6 | tim.length()<9) {
			return "";
		}
		String[] dt = dat.split("");
		String[] tm = tim.split("");
		return "20"+dt[5]+dt[6]+"-"+dt[3]+dt[4]+"-"+dt[1]+dt[2]+"T"+tm[1]+tm[2]+":"+tm[3]+tm[4]+":"+tm[5]+tm[6]+".001Z";
	} // public static String alldata(String dat,String tim)
	
	///////////////////////////////////////////////////////////////////////////////////
	
	public static String dataR(String dat) {
		/*
		 * ������ ���� � ���� 31-12-2013 �� 311213
		 */
		if (dat.length()<6 ) {
			
			return "";
			
		}
		
		String[] dt = dat.split("");
		return dt[1]+dt[2]+"-"+dt[3]+dt[4]+"-"+"20"+dt[5]+dt[6];
		
	} // public static String dataR(String dat)
	
	///////////////////////////////////////////////////////////////////////////////////
	
	public static String timeR(String tim) {
		/*
		 * ������ ����� � ���� 14:02:50 ��  140250.00
		 */
		if (tim.length()<9) {
			
			return "";
			
		}
		
		String[] tm = tim.split("");
		return tm[1]+tm[2]+":"+tm[3]+tm[4]+":"+tm[5]+"0";
		
	} //s tatic String timeR(String tim)
	
	///////////////////////////////////////////////////////////////////////////////////
	
	static public boolean existRecordForFile(String fileName) throws ClassNotFoundException, SQLException {
		/*
		 * �������� ������� ������ � �� ��� ������������ ����� @fileName
		 */
		Class.forName("org.sqlite.JDBC");
		Connection bd 			= DriverManager.getConnection("jdbc:sqlite:param.db");
		ResultSet resultSet 	= null; 
		Statement st 			= bd.createStatement();
		st.setQueryTimeout(60);
		resultSet  				= st.executeQuery( "SELECT MAX(id),file FROM points WHERE (file ='"+fileName+"') and (id>714000) GROUP BY file  ;" );
		
		int 	i		= 0;
		while (resultSet.next()) {
			i++;
		}
		
		bd.close();
		
		return  (i>0) ? true: false;
		
	}// public static boolean existRecordForFile(String fileName) throws ClassNotFoundException, SQLException
	
	///////////////////////////////////////////////////////////////////////////////////
	 
	static public long retTimeStamp(String s)  {
		
		 DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
		 Date d;
		 
		try {
			
			d = formatter.parse(s);
			//System.out.println(d.getTime());
			return d.getTime();
			
		} catch (ParseException e) {
			
			System.out.println("eroor "+s);
			
			return 0;
			
		}
		 
	 } //static public long retTimeStamp(String s)
	
	///////////////////////////////////////////////////////////////////////////////////
	
	public static void createDir(String adr){
		/*
		 * ��������� ������� ���������� @adr
		 * ���� ��� , ������� 
		 */
			File theDir = new File(adr);
			  if (!theDir.exists())
			  {
			    System.out.println("creating directory: " + adr);
			    boolean result = theDir.mkdir();  
			    if(result){    
			       System.out.println("DIR created");  
			     }//if
			    else {
			    	System.out.println("DIR NOT created"); 
			    }
			  }
	}//static void createDir(String adr)
	
	///////////////////////////////////////////////////////////////////////////////////
	
	static double distance(double lat1,double lon1,double lat2,double lon2) {
			/* ������� ����������(������1,�������1,������2,�������2)   
			 * ���� ���������.
			 * ������  = latitude  (lat)
			 * ������� = longitude (lon)
			 * ��� ������� �� ������ �� ������� ��������� ��� ������!
			 * ����� ����� ����� ������� ������ ����� ����� �������.
			 * ������������ �������� ������ � ���� �� ��������� ������ ������ �:
			 * - ���� ������ ������ 50 ��������: � = 111,33 - 0,0156�2 - 0,023� ;
			 * - ���� ������ ������ 50 ��������: � = 135,35 - 0,00586�2 - 0,978� .
			 *
			 * � � ��� �������: b = 110,44 + 0,014�.
			 *
			 * ����� ��� 
			 * ���������� ������� ����� �������� ���� ����� (� ��������! � ����������� ������)  - s 
			 * �
			 * ����� ��������� ���� ����� (���� � �������� � ����������� ������) - d.
			 *
			 * ���������� �����: ((bs)2 + (ad)2)1/2.
			 * (�.�. ������ ���������� �� ����� ��������� ������������ bs � ad)
			  
			D =0;
		    A =0;
			B =0;
			A1=0;
			B1=0;
			S =0;  
			�������� = (������1+������2)/2;
			���� (��������<50) �����  
				A = 111.33-(0.0156*�������(��������))-(0.023*��������);
			�����               
				A = 135.35-(0.00586*�������(��������))-(0.978*��������);
			���������;	
			B =110.44+(0.014*��������);
			//B1=110.44+(0.014*������2);
			S = ������1-������2;
			D = �������1-�������2; 
			��� =(������(�������(B*S)+�������(A*D)))*1000; 
			//��������(������("A=[A] B=[B] S=[S] D=[D] ���=[���]"));
			������� ���;
		 
		������������   
		*/
			double mLat = (lat1+lat2)/2;
			double A =0;
			if (mLat<50) {
				A = 111.33-(0.0156*Math.pow(mLat,2))-(0.023*mLat);
			}
			else {
				A = 135.35-(0.00586*Math.pow(mLat,2))-(0.978*mLat);	
			}
			double B =110.44+(0.014*mLat);
			double S = lat1-lat2;
			double D = lon1-lon2; 
			return Math.sqrt((Math.pow(B*S,2)+Math.pow(A*D,2)))*1000;
	} //static double distance(double lat1,double lon1,double lat2,double lon2)
	
	///////////////////////////////////////////////////////////////////////////////////
	
	public static void main(String args[]) throws IOException, NoSuchAlgorithmException {
		
	}//public static void main(String args[]) throws IOException
	
	 ///////////////////////////////////////////////////////////////////////////////////
	 
	 static public void executeQ(String sql) throws SQLException, ClassNotFoundException {
		 
			/*
			 * �������� sql ���������� (��� ���������� ����������)
			 */
			Class.forName("org.sqlite.JDBC");
			Connection bd = DriverManager.getConnection("jdbc:sqlite:param.db");
			Statement st  = bd.createStatement();
			st.setQueryTimeout(60);
			st.execute(sql);
			st.close();
			
	}//static public void executeQ(String sql) throws SQLException, ClassNotFoundException
		
	////////////////////////////////////////////////////////////////////////////////////////
		
	static public void addCommentInBD(String comment,long SessionID) throws ClassNotFoundException, SQLException {
		
		String sql = "INSERT INTO process (time,descr,session) VALUES("+String.valueOf(new Date().getTime())+",'"+comment+"',"+String.valueOf(SessionID)+");";
		executeQ(sql);
		
	} //static public void addCommentInBD(String comment,boolean start, boolean end) {
	
	////////////////////////////////////////////////////////////////////////////////////////
	//	��������:
	//		������ ����������� ����� ����� f
	static public String crc(String f) throws IOException, NoSuchAlgorithmException {
		
		InputStream fi = new BufferedInputStream(new FileInputStream(f));
				
		MessageDigest md = MessageDigest.getInstance("SHA1");
	    byte[] dataBytes = new byte[1024];
	    int nread = 0; 
			 
	    while ((nread = fi.read(dataBytes)) != -1) {
			    	
	    	md.update(dataBytes, 0, nread);
			      
	    };
			 
		byte[] mdbytes = md.digest();
		StringBuffer sb = new StringBuffer("");
			    
		for (int i = 0; i < mdbytes.length; i++) {
			    	
			sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
			    	
		}
			 
		return sb.toString();
		
	}	//static public String crc(String f) throws IOException, NoSuchAlgorithmException
	
}//public class function

 

