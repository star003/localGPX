import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;

/*
 * управление точками интереса
 * добавление, редактирование , удаление
 */

public class editPoints extends JFrame {
	
	private static final long serialVersionUID = 1L;
	String globalID = "";
	private JFrame editPoints;
	private JTable tblEdit;
	private JTextField textLat;
	private JTextField textLon;
	private JTextField textSpeed;
	private JTextField textDescr;
	static String pathDB = "tParam.db";
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void main(String[] args) {
		
		startDialog();
		
	} //public static void main(String[] args)

	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void startDialog() {
		
		EventQueue.invokeLater(new Runnable() {
			
			public void run() {
				
				try {
					
					editPoints frame = new editPoints();
					frame.setVisible(true);
					
				} catch (Exception e) {
					
					e.printStackTrace();
					
				}
				
			}
			
		});
		
	}//public static void startDialog()
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	public editPoints() throws ClassNotFoundException, SQLException {
		
		initialize();
		
	}//public editPoints() throws ClassNotFoundException, SQLException
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * очистит поля формы после записи нового/редактирования трака
	 */
	private void clearFields() {
		
		textLat.setText("");
		textLon.setText("");
		textSpeed.setText("");
		textDescr.setText("");
		
	}//private void clearFields()
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void initialize() throws ClassNotFoundException, SQLException  {
		
		editPoints = new JFrame();
		setBounds(0, 0, 800, 588);
		editPoints.getContentPane().setLayout(null);
		
		getContentPane().setLayout(null);
		/*
		tblEdit = new JTable();
		tblEdit.setBounds(20, 41, 700, 355);
		tblEdit.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		*/
		textLat = new JTextField();
		textLat.setBounds(166, 495, 123, 20);
		getContentPane().add(textLat);
		textLat.setColumns(10);
		
		textLon = new JTextField();
		textLon.setBounds(315, 495, 133, 20);
		getContentPane().add(textLon);
		textLon.setColumns(10);
		
		textSpeed = new JTextField();
		textSpeed.setBounds(461, 495, 133, 20);
		getContentPane().add(textSpeed);
		textSpeed.setColumns(10);
		
		textDescr = new JTextField();
		textDescr.setBounds(609, 495, 147, 20);
		
		final ResultSet x1 = function.getResult("SELECT * FROM poi;", pathDB);
		
		tblEdit = new JTable(buildTableModel(x1,false));
		
		tblEdit.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent arg0) {
				
	            int[] selectedRows = tblEdit.getSelectedRows();
	            int i 			= 0;
                int selIndex 	= selectedRows[i];
                TableModel model= tblEdit.getModel();
                globalID 		= model.getValueAt(selIndex	, 0).toString();
               	textLat.setText(model.getValueAt(selIndex	, 1).toString());
               	textLon.setText(model.getValueAt(selIndex	, 2).toString());
               	textSpeed.setText(model.getValueAt(selIndex	, 3).toString());
               	textDescr.setText(model.getValueAt(selIndex	, 4).toString());
               	
			} //public void mouseClicked(MouseEvent arg0) 
			
		});
		
		tblEdit.setAlignmentX(Component.LEFT_ALIGNMENT);
		tblEdit.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblEdit.setBounds(10, 10, 337, 208);
		
		TableColumnModel columnModel = tblEdit.getColumnModel();
		
		columnModel.getColumn(0).setPreferredWidth(5);
		columnModel.getColumn(1).setPreferredWidth(10);
		columnModel.getColumn(2).setPreferredWidth(10);
		columnModel.getColumn(3).setPreferredWidth(10);
		columnModel.getColumn(4).setPreferredWidth(285);

		JScrollPane scrollPane = new JScrollPane(tblEdit);
		scrollPane.setBounds(20, 41, 736, 382);
		getContentPane().add(scrollPane);
		JButton btnReadData = new JButton("Получить данные");
		btnReadData.setEnabled(false);
		btnReadData.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				
				function.tableOperation(tblEdit, "",pathDB);
				
			}//public void actionPerformed(ActionEvent arg0)
			
		});
		
		btnReadData.setBounds(20, 7, 150, 23);
		getContentPane().add(btnReadData);
		
		JButton btnSaveData = new JButton("Изменить данные");
		btnSaveData.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				//***редактируем данные строки таблицы
				
				String sql = 
				"UPDATE poi SET  speed= "	 +textSpeed.getText() +
				" ,lat= "	 + function.delSymbol(textLat.getText()) + 
				" ,lon= " 	 + function.delSymbol(textLon.getText()) +
				" ,descr= '" + textDescr.getText() + 
				"' WHERE id=" + globalID + ";" ;
				
				function.tableOperation(tblEdit, sql,pathDB);
				
				clearFields();
				
			}//public void actionPerformed(ActionEvent e)
			
		});
		
		btnSaveData.setBounds(180, 7, 150, 23);
		getContentPane().add(btnSaveData);
		
		JButton btnDeleteData = new JButton("Удалить данные");
		btnDeleteData.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				String sql ="DELETE FROM poi WHERE id = "+globalID+";";
				function.tableOperation(tblEdit, sql,pathDB);
				
			}//public void actionPerformed(ActionEvent e)
			
		});
		
		btnDeleteData.setBounds(340, 7, 150, 23);
		getContentPane().add(btnDeleteData);

		JButton btnNewData = new JButton("Ввести  новые");
		btnNewData.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
					String sql = 
					"INSERT INTO poi (speed,lat,lon,descr) VALUES " +
					" ("	+ (textSpeed.getText() 	== null ? 0: textSpeed.getText()) +
					" ,"	+(textLat.getText()		== null ? 0 : function.delSymbol(textLat.getText()))+ 
					" ," 	+(textLon.getText()		==null ? 0 : function.delSymbol(textLon.getText()))+
					" ,'" 	+(textDescr.getText()	==null ? "ERROR!!!":textDescr.getText())+"');";
					function.tableOperation(tblEdit, sql,pathDB);
					clearFields();
					
			}//public void actionPerformed(ActionEvent e)
			
		});
		
		btnNewData.setBounds(501, 7, 150, 23);
		getContentPane().add(btnNewData);
		getContentPane().add(textDescr);
		textDescr.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("LAT");
		lblNewLabel.setBounds(166, 466, 56, 16);
		getContentPane().add(lblNewLabel);
		
		JLabel lblLon = new JLabel("LON");
		lblLon.setBounds(315, 466, 56, 16);
		getContentPane().add(lblLon);
		
		JLabel lblNewLabel_1 = new JLabel("SPEED");
		lblNewLabel_1.setBounds(461, 466, 56, 16);
		getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("DESCR");
		lblNewLabel_2.setBounds(609, 466, 56, 16);
		getContentPane().add(lblNewLabel_2);
		setTitle("Редактор точек");
		
	}// private void initialize() throws ClassNotFoundException, SQLException
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static DefaultTableModel buildTableModel(ResultSet rs,boolean emtyModel) throws SQLException {
		
		if (emtyModel == true) {
			
			//**случай , когда нужно получить просто пустую таблицу
			Vector<String> columnNames = new Vector<String>();
			
			columnNames.add("ID");
			columnNames.add("lat");
			columnNames.add("lon");
			columnNames.add("speed");
			columnNames.add("descr");
			
			Vector<Vector<Object>> data = new Vector<Vector<Object>>();
			
			return new DefaultTableModel(data, columnNames);
			
		}
		
	    ResultSetMetaData metaData = rs.getMetaData();
	    // names of columns
	    Vector<String> columnNames = new Vector<String>();
	    int columnCount = metaData.getColumnCount();
	    
	    for (int column = 1; column <= columnCount; column++) {
	    	
	        columnNames.add(metaData.getColumnName(column));
	        
	    }
	    
	    // data of the table
	    Vector<Vector<Object>> data = new Vector<Vector<Object>>();
	    
	    while (rs.next()) {
	    	
	        Vector<Object> vector = new Vector<Object>();
	        
	        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
	        	
	            vector.add(rs.getObject(columnIndex));
	        }
	        
	        data.add(vector);
	        
	    }
	    
	    return new DefaultTableModel(data, columnNames);
	    
	} //public static DefaultTableModel buildTableModel(ResultSet rs,boolean emtyModel) throws SQLException
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
} //public class editPoints extends JFrame
