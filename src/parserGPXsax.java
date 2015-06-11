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
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
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
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	static long grtMS(String s) throws Exception{
		//********************************************
		//**вернет количество милесекунд от 1970 года
		//**на вход дата вида 2013-05-14 08:01:02
		//********************************************
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-MM hh:mm:ss");
		Date date = sdf.parse(s);
		return date.getTime(); 
		
	}//static long grtMS(String s) throws Exception{
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	static String unixToDate(long unixSeconds){
		
		Date date = new Date(unixSeconds); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+6"));  
		String formattedDate = sdf.format(date);
		return formattedDate;
		
	}//static String unixToDate(long unixSeconds)

	/////////////////////////////////////////////////////////////////////////////////////////////////
	
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
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	static Double midleSpeed(List<recGeo1> x) {
	//*************************************
	//*вычисляет среднюю скорость
	//*************************************	
		//
		return (x.get(x.size()-1).total/((x.get(x.size()-1).absTime-x.get(1).absTime)*0.001))*3.6;
		
	}//static Double midleSpeed(List<recGeo1> x)
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
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
        						
        				lat = Double.valueOf(x.item(i).getNodeValue());
        				
        			}
        					
        			if(x.item(i).getNodeName().equals("lon")) {
        						
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
        	
        }//try
		
        catch (Exception e) {
        	
        }//catch
		
		//*****может это тупо но расчитаем растояние потом и преобразуем время с датой****
		
		Double totalDistance= 0.0;
		
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
				v1.speed 	= 0.0;//spd;
				v1.absTime  = absTm;
				//****направление
				v1.Direction = i > 1  ? calcDirection(v.get(i-1).lat,v.get(i-1).lon,v.get(i).lat,v.get(i).lon) : 0.0;
				v.set(i,v1);
				
			}//try
			
			catch (Exception e) {
				
			}//catch
			
		}
		
		return v;
		
	} //static List<recGeo1> getData(String fileName) throws Exception
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	/*
	 * вычисление направления...
	 * http://brullworfel.ru/blog/calcdirection-delphi/
	 * 
	function CalcDirection(Lat1,Long1,Lat2,Long2: Single) : Single;
	var
	x1,y1,y2 : Single;
	begin
	x1 := sin((-Long2+Long1)*Pi/180);
	y1 := cos(Lat2*Pi/180) * tan(Lat1*Pi/180);
	y2 := sin(Lat2*pi/180) * cos((-Long2+Long1)*pi/180);
	Result := ArcTan(x1/(y1-y2))*180/pi;
	
	if Result < 0 then
		Result := 360 + Result;
	
	if (Long2 < Long1) and (Long2 > (Long1-180)) then
		if Result > 180 then
			Result := Result - 180;
	if Result > 360 then 
		Result :=Result - 360;
	end;
	*/
	
	static Double calcDirection(Double Lat1,Double Long1,Double Lat2, Double Long2) {
		
		Double x1 = Math.sin((-Long2+Long1)*Math.PI/180);
		Double y1 = Math.cos(Lat2*Math.PI/180) * Math.tan(Lat1*Math.PI/180);
		Double y2 = Math.sin(Lat2*Math.PI/180) * Math.cos((-Long2+Long1)*Math.PI/180);
		Double Result = Math.atan(x1/(y1-y2))*180/Math.PI;
		
		if (Result < 0) {
			
			Result = 360 + Result;
			
		}
		
		if (Long2 < Long1 & Long2 > (Long1-180))  {
		
			if (Result > 180) {
				
				Result = Result - 180;
				
			}
			
		}
		
		return Double.isNaN(Result)  ? 0.0 : Result;
		
	}//static Double CalcDirection(Double Lat1,Double Long1,Double Lat2, Double Long2) 
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void main(String[] args) throws Exception{
		
		v = getData("E://_reg//GPX//2015-06//2015-06-08T05_59_27-001Z.gpx");
		System.out.println("total1 "+v.get(v.size()-1).total+" метров");
		System.out.println("middle speed "+midleSpeed(v));
		
		int i=0;
		
		for (recGeo1 g : v) {
			
			if (i==0) {
				
				i++;
				continue;
				
			}
			
			System.out.print(g.Direction);
			System.out.print("	");
			System.out.print(g.time);
			System.out.println();
			i++;
			
		}
		
	}//public static void main(String[] args) throws Exception
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
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
	Double	Direction;	//*** направление относительно прошлой точки
	
	String  wpName;		//*** имя путевой точки если она распознана
	
	String  ds1;		//*** любая информация 1
	String  ds2;		//*** любая информация 2
	String  ds3;		//*** любая информация 3
	
}//class recGeo


