import java.awt.Dimension;
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

public class analizGPX  extends javax.swing.JFrame {
	
	static long stop_Time_Sec = 60; //**время в секундах , превышая которое будем считать что это остановка
	
	private static final long serialVersionUID = 20140304;
	
	static boolean goReading = false;
	
	static String txt = "";
	
	static public JTextArea txTablo = new JTextArea();
	
	public static void main(String[] args) throws Exception  {
		
		try {
			
			analizGPX dialog = new analizGPX();
			dialog.setPreferredSize(new Dimension(400, 80));;
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			
		} catch (Exception e) {
		}
		
	}	//public static void main(String[] args) throws Exception
	
	public analizGPX() {
		setBounds(100, 100, 442, 350);
		getContentPane().setLayout(null);
		
		txTablo.setRows(17);
		txTablo.setColumns(80);
		
		txTablo.setBounds(39, 30, 385, 174);
		JScrollPane outScroll = new JScrollPane(txTablo);
		outScroll.setBounds(10, 10, 400, 200);
		getContentPane().add(outScroll);
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel.setBounds(10, 265, 400, 39);
		getContentPane().add(panel);
		
		JButton btnFileList = new JButton("инф. о траке");
		btnFileList.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				try {
					
					minInfo();
					
				} catch (Exception e1) {
					
					e1.printStackTrace();
					
				}
				
			}
			
		});
		
		panel.add(btnFileList);
		
		JButton btnNewButton_3 = new JButton("exit");
		panel.add(btnNewButton_3);
		btnNewButton_3.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				System.exit(0);
			}
		});
		//getContentPane().add(txTablo);
	}	
	
	static void minInfo() throws Exception{
		
		String r = "";
		
		JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("E://_reg//GPX//"));
        chooser.setDialogTitle("");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        	
        	r = chooser.getSelectedFile().getAbsolutePath();

        } 
        
        txTablo.setText(null);
        
		List<recGeo1> x =  parserGPXsax.getData(r);
		
		txTablo.append("\n Начало :");
		txTablo.append(parserGPXsax.unixToDate(x.get(1).absTime));
		
		txTablo.append("\n Окончание :");
		
		System.out.println(parserGPXsax.unixToDate(x.get(x.size()-2).absTime));
		txTablo.append(parserGPXsax.unixToDate(x.get(x.size()-2).absTime));
		
		txTablo.append("\n Всего пройдено : "+x.get(x.size()-1).total+" метров");
		
		txTablo.append("\n Время по траку (минут): ");
		
		System.out.println((x.get(x.size()-1).absTime - x.get(2).absTime)/60000);
		txTablo.append( 
				String.valueOf((x.get(x.size()-1).absTime - x.get(2).absTime)/60000));
		
		long oldTm = 0;
		long st = x.get(2).absTime;
		long totalTimeStop = 0;
		
		for (recGeo1 y : x) {
			
			long pr = (y.absTime - st)/1000 >0 ?(y.absTime - st)/1000:0;
			
			if(pr - oldTm > stop_Time_Sec){
				
				txTablo.append("\n    Остановка ");
				
				txTablo.append(String.valueOf((pr - oldTm)/60));
				
				txTablo.append(" минут   ");
				
				txTablo.append("\n");
				
				totalTimeStop+= (pr - oldTm)/60;
				
			}
			
			oldTm = pr;
			
		}
		
		txTablo.append("\n    Общее время остановки (минут): ");
		
		txTablo.append(String.valueOf(totalTimeStop));
		
		txTablo.append("\n Общее время движения (минут): ");
		
		long allDriveMinute =((x.get(x.size()-1).absTime - x.get(2).absTime)/60000)-totalTimeStop; 
		txTablo.append(String.valueOf(allDriveMinute));
		x.clear();
		
	}//public analizGPX()
		
}//public class analizGPX
