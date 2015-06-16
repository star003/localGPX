import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/*
 * класс хранит / создает / записывает 
 * настройки программы
 */

public class setting {

	public static String load_Path 			= "G://DCIM//100MEDIA//"; //**путь от куда читаем файлы (флеш регистратора)
	public static String save_Path			= "E://_reg//GPX//"	;//**путь куда сохраняем траки (папка на диске)
	public static String last_Opened_File 	= ""	; //**последний анализируемый трак (имя файла)
	public static long parking_slot			= 10000000; //**интервал между данными (в МС!!! или СЕК*1000) ,который мы считаем парковкой и делим трак на отдельные файлы
	
	public static Double x_Lat = 0.0001; //**отклонение координаты широты для попадание в poi (0.0001 ->11 метров)
	public static Double x_Lon = 0.0002; //**отклонение координаты долготы для попадание в poi (0.0002 ->12 метров)
	
	public static Double wait_Lat = 0.001; //**отклонение координаты широты для попадание в poi ДЛЯ определения остановки (0.001 ->110 метров)
	public static Double wait_Lon = 0.002; //**отклонение координаты долготы для попадание в poi определения остановки (0.002 ->120 метров)
	
	public static int control_Distance 		= 50; //**дистанция в метрах
	public static int control_Time 			= 20; //**за время в сек
	public static int limit_To_Time_Wait 	= 200; //**до этой цифры в секундах считаем остановкой, больше стоянка
	
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
			
			System.out.println("не удалось загрузить настройки");
			
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
		    
			saveProps.storeToXML(new FileOutputStream("settings.xml"), "настройки программы");
			
		} catch (IOException e) {
			
			System.out.println("не удалось записать настройки");
			
		}
	    
	}//static void saveSetting()
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
}//public class setting
