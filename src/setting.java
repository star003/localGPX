import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/*
 * ����� ������ / ������� / ���������� 
 * ��������� ���������
 */

public class setting {

	public static String load_Path 			= "G://DCIM//100MEDIA//"; //**���� �� ���� ������ ����� (���� ������������)
	public static String save_Path			= "E://_reg//GPX//"	;//**���� ���� ��������� ����� (����� �� �����)
	public static String last_Opened_File 	= ""	; //**��������� ������������� ���� (��� �����)
	public static long parking_slot			= 10000000; //**�������� ����� ������� (� ��!!! ��� ���*1000) ,������� �� ������� ��������� � ����� ���� �� ��������� �����
	
	public static Double x_Lat = 0.0001; //**���������� ���������� ������ ��� ��������� � poi (0.0001 ->11 ������)
	public static Double x_Lon = 0.0002; //**���������� ���������� ������� ��� ��������� � poi (0.0002 ->12 ������)
	
	public static Double wait_Lat = 0.001; //**���������� ���������� ������ ��� ��������� � poi ��� ����������� ��������� (0.001 ->110 ������)
	public static Double wait_Lon = 0.002; //**���������� ���������� ������� ��� ��������� � poi ����������� ��������� (0.002 ->120 ������)
	
	public static int control_Distance 		= 50; //**��������� � ������
	public static int control_Time 			= 20; //**�� ����� � ���
	public static int limit_To_Time_Wait 	= 200; //**�� ���� ����� � �������� ������� ����������, ������ �������
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	setting()  {
		Properties loadProps = new Properties();
		try {
			
			loadProps.loadFromXML(new FileInputStream("settings.xml"));
			
			load_Path 		= loadProps.getProperty("load_Path") == null ? load_Path : loadProps.getProperty("load_Path");
			save_Path		= loadProps.getProperty("save_Path") == null ? save_Path : loadProps.getProperty("save_Path");
			last_Opened_File= loadProps.getProperty("last_Opened_File") == null ? last_Opened_File : loadProps.getProperty("last_Opened_File");
			
			parking_slot= (Long) (loadProps.getProperty("parking_slot") == null ? parking_slot : loadProps.getProperty("parking_slot"));
			
			x_Lat= (Double) (loadProps.getProperty("x_Lat") == null ? x_Lat : loadProps.getProperty("x_Lat"));
			x_Lon= (Double) (loadProps.getProperty("x_Lon") == null ? x_Lon : loadProps.getProperty("x_Lon"));
			
			wait_Lat= (Double) (loadProps.getProperty("wait_Lat") == null ? wait_Lat : loadProps.getProperty("wait_Lat"));
			wait_Lon= (Double) (loadProps.getProperty("wait_Lon") == null ? wait_Lon : loadProps.getProperty("wait_Lon"));

			control_Distance= (Integer) (loadProps.getProperty("control_Distance") == null ? control_Distance : loadProps.getProperty("control_Distance"));
			control_Time = (Integer) (loadProps.getProperty("control_Time ") == null ? control_Time  : loadProps.getProperty("control_Time "));
			
			limit_To_Time_Wait= (Integer) (loadProps.getProperty("limit_To_Time_Wait") == null ? limit_To_Time_Wait : loadProps.getProperty("limit_To_Time_Wait"));
			
		} catch (IOException e) {
			
			System.out.println("�� ������� ��������� ���������");
			
		}
		
	}//setting()
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	static void saveSetting(){
		
	    try {
	    	
	    	Properties saveProps = new Properties();
	    	
		    saveProps.setProperty("load_Path", load_Path);
		    saveProps.setProperty("save_Path", save_Path);
		    saveProps.setProperty("last_Opened_File", last_Opened_File);
		    
		    saveProps.setProperty("parking_slot", String.valueOf(parking_slot));
		    saveProps.setProperty("x_Lat", String.valueOf(x_Lat));
		    saveProps.setProperty("x_Lon", String.valueOf(x_Lon));
		    
		    saveProps.setProperty("wait_Lat", String.valueOf(wait_Lat));
		    saveProps.setProperty("wait_Lon", String.valueOf(wait_Lon));
		    saveProps.setProperty("control_Distance", String.valueOf(control_Distance));
		    
		    saveProps.setProperty("control_Time", String.valueOf(control_Time));
		    saveProps.setProperty("limit_To_Time_Wait", String.valueOf(limit_To_Time_Wait));
		    
			saveProps.storeToXML(new FileOutputStream("settings.xml"), "��������� ���������");
			
		} catch (IOException e) {
			
			System.out.println("�� ������� �������� ���������");
			
		}
	    
	}//static void saveSetting()
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
}//public class setting
