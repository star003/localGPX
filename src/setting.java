import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/*
 * ����� ������ / ������� / ���������� 
 * ��������� ���������
 */

public class setting {

	public static String loadPath 		;
	public static String savePath		;
	public static String lastOpenedFile	;
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	setting()  {
		Properties loadProps = new Properties();
		try {
			
			loadProps.loadFromXML(new FileInputStream("settings.xml"));
			loadPath 		= loadProps.getProperty("loadPath") == null ? "G://DCIM//100MEDIA//" : loadProps.getProperty("loadPath");
			savePath		= loadProps.getProperty("savePath") == null ? "E://_reg//GPX//" : loadProps.getProperty("savePath");
			lastOpenedFile	= loadProps.getProperty("lastOpenedFile") == null ? "" : loadProps.getProperty("lastOpenedFile");
			System.out.println(loadPath);
			
		} catch (IOException e) {
			
			System.out.println("�� ������� ��������� ���������");
			
		}
		
	}//setting()
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	static void saveSetting(){
		
	    try {
	    	
	    	Properties saveProps = new Properties();
		    saveProps.setProperty("loadPath", loadPath);
		    saveProps.setProperty("savePath", savePath);
		    saveProps.setProperty("lastOpenedFile", lastOpenedFile);
			saveProps.storeToXML(new FileOutputStream("settings.xml"), "��������� ���������");
			
		} catch (IOException e) {
			
			System.out.println("�� ������� �������� ���������");
			
		}
	    
	}//static void saveSetting()
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void main(String[] args)  {
		
	}//public static void main(String[] args)

	/////////////////////////////////////////////////////////////////////////////////////////////////
	
}//public class setting
