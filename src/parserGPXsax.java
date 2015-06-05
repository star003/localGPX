import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class parserGPXsax {
	//***************************
	//**вариант через парсер
	//***************************
	static List<recGeo1> v = new ArrayList<recGeo1>();
	
	static Double calculateDistance(Double latA, Double longA, Double latB, Double longB) {
		 //******************************************************
		 //** http://www.kobzarev.com/programming/calculation-of-distances-between-
		 //** cities-on-their-coordinates.html#comment-20522
		 //**вычисляет расстояние между 2мя координатами
		 //******************************************************
		    int EARTH_RADIUS = 6372795;
		 
		    Double lat1 = latA * Math.PI / 180;
		    Double lat2 = latB * Math.PI / 180;
		    Double long1 = longA * Math.PI / 180;
		    Double long2 = longB * Math.PI / 180;
		  
		    Double cl1 = Math.cos(lat1);
		    Double cl2 = Math.cos(lat2);
		    Double sl1 = Math.sin(lat1);
		    Double sl2 = Math.sin(lat2);
		    Double delta = long2 - long1;
		    Double cdelta = Math.cos(delta);
		    Double sdelta = Math.sin(delta);
		  
		    Double y = Math.sqrt(Math.pow(cl2 * sdelta, 2) + Math.pow(cl1 * sl2 - sl1 * cl2 * cdelta, 2));
		    Double x = sl1 * sl2 + cl1 * cl2 * cdelta;
		  
		    Double ad = Math.atan2(y, x);
		    Double dist = Math.ceil(ad * EARTH_RADIUS);
		  
		    return dist;
		} //static Double calculateDistance(Double latA, Double longA, Double latB, Double longB)
	
	static long grtMS(String s) throws Exception{
		//********************************************
		//**вернет количество милесекунд от 1970 года
		//**на вход дата вида 2013-05-14 08:01:02
		//********************************************
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-MM hh:mm:ss");
		Date date = sdf.parse(s);
		return date.getTime(); 
		
	}//static long grtMS(String s) throws Exception{
	
	static String unixToDate(long unixSeconds){
		
		Date date = new Date(unixSeconds); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+6"));  
		String formattedDate = sdf.format(date);
		return formattedDate;
		
	}//static String unixToDate(long unixSeconds)

	static String[] getDt(String s) throws Exception{
		//**************************************************
		//**вернем мультистроку с датой и временем из строки
		//**вида 2012-08-13T02:54:56Z
		//**0-дата ,1 - время
		
		try {
			
			String[] x 		= s.split("T");
			String[] x001 	= x[1].split("Z");
			return (x[0]+";"+x001[0]).split(";");
			
		}
		
		catch(Exception e) {
			return (";;").split(";");
			
		}
		
	}//static String[] getDt(String s)
	
	static Double midleSpeed(List<recGeo1> x) {
	//*************************************
	//*вычисляет среднюю скорость
	//*************************************	
		//
		return (x.get(x.size()-1).total/((x.get(x.size()-1).absTime-x.get(1).absTime)*0.001))*3.6;
		
	}//static Double midleSpeed(List<recGeo1> x)
	
	static List<recGeo1> getData(String fileName) throws Exception{
		//************************************************
		//**разберем файл и вычислим параметры
		//**объекта recGeo1
		//************************************************
		//System.out.println("начало чтения ");
		
		try {
			
        	DocumentBuilderFactory dbf 	= DocumentBuilderFactory.newInstance();
        	DocumentBuilder 		db	= dbf.newDocumentBuilder();
        	Document 				doc	= db.parse(new InputSource(new FileReader(fileName)));
        	doc.getDocumentElement().normalize();
        	//****переберем тег*****
        	NodeList nodeList = doc.getElementsByTagName("trkpt");
        	//System.out.println("позиций "+nodeList.getLength());
        	
        	for (int z=0;z<nodeList.getLength();z++) {
        		/*
        		if (z % 10000 ==0 ) {
        			//***каждые 10 000 точек сообщим что мы еще живы
					System.out.println("позиция "+z);
				}
				*/
        		
        		Double lat  = 0.0;
        		Double lon  = 0.0;
				String date = null;
        		Node node = nodeList.item(z);
        		NodeList x1 = node.getChildNodes();
        		
        			for(int i1=0;i1<x1.getLength();i1++) {
        				
        				if (x1.item(i1).getNodeName().equals("time")) {
        					
        					//System.out.println(x1.item(i1).getNodeName()+" = "+x1.item(i1).getTextContent());
        					date = x1.item(i1).getTextContent();
        					
        				}
        				
        			}//for i1
        			
        				//*****переберем атрибуты тэга*****
        				NamedNodeMap x = node.getAttributes();
        				
        				for(int i=0;i<x.getLength();i++) {
        					
        					if(x.item(i).getNodeName().equals("lat")) {
        						
        						//System.out.println(x.item(i).getNodeName()+" = "+Double.valueOf(x.item(i).getNodeValue()));
        						
        						lat = Double.valueOf(x.item(i).getNodeValue());
        					}
        					
        					if(x.item(i).getNodeName().equals("lon")) {
        						
            					//System.out.println(x.item(i).getNodeName()+" = "+Double.valueOf(x.item(i).getNodeValue()));
            					
        						lon = Double.valueOf(x.item(i).getNodeValue());
            				}
        					
        				}//for i
        				
        				recGeo1 v1 = new recGeo1();
        				v1.lat = lat;
        				v1.lon = lon;
        				v1.date= date;
        				v1.time= date;
        				v1.distance =0.0;
    					v1.total    =0.0;
    					v1.absTime  =0;
    					v.add(v1);
    					
        	}//for z
        	//System.out.println("конец формирования данных ");
        }//try
        catch (Exception e) {
        }//catch
		//System.out.println(v.size());
		//*****может это тупо но расчитаем растояние потом и преобразуем время с датой****
		Double totalDistance= 0.0;
		//Double spd 			= 0.0;
		
		for (int i=1;i<v.size();i++){
			
			try {
				
				Double ds = calculateDistance(v.get(i).lat,v.get(i).lon,  v.get(i-1).lat, v.get(i-1).lon);
				totalDistance += ds;
				recGeo1 v1 	= new recGeo1();
				v1.lat 		= v.get(i).lat;
				v1.lon 		= v.get(i).lon;
				v1.date		= getDt(v.get(i).date)[0];
				v1.time		= getDt(v.get(i).date)[1];
				v1.distance = ds;
				v1.total    = totalDistance;
				long absTm 	= grtMS(v1.date+" "+v1.time);
				//Double spd 	= (ds/((absTm-v.get(i-2).absTime)*0.001))*3.6;
				v1.speed 	= 0.0;//spd;
				v1.absTime  = absTm;
				v.set(i,v1);
				
			}//try
			
			catch (Exception e) {
				//System.out.println("eeee");
				
				//Double spd 	= 0.0;	
				
	        }//catch
			
		}
		
		return v;
		
	} //static List<recGeo1> getData(String fileName) throws Exception
	
	
	public static void main(String[] args) throws Exception{
		
		v = getData("2012-08-13_05-54-56.gpx");
		System.out.println("total1 "+v.get(v.size()-1).total+" метров");
		System.out.println("middle speed "+midleSpeed(v));
		
	}//public static void main(String[] args) throws Exception
	
}//public class parserGPXsax

class recGeo1 {
	
	Double  lat;		//*** широта
	Double  lon;		//*** долгота
	Double  distance; 	//*** расстояние между пред. точкой и текущей
	Double  total; 		//*** расстояние с начала и до текущей точки
	String  time;		//*** время на точке
	long	absTime;	//*** время в непрерывном исчислении
	Double	speed;		//*** скорость на данном участке
	String  date;		//*** дата на точке
	
}//class recGeo


