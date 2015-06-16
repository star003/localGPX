import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

	/*
	 * форма установок программы
	 */

public class formSetting  extends javax.swing.JFrame {

	
	private static final long serialVersionUID = 20150611;

	formSetting(){
		
		setTitle("настройки программы");
		
		setBounds(100, 100, 590, 275); 	
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		JLabel lblload_Path = new JLabel("load_Path");
		lblload_Path.setBounds(44, 27, 200, 21);
		getContentPane().add(lblload_Path);
		
		addWindowListener(new java.awt.event.WindowAdapter() {
			
			public void windowClosing(java.awt.event.WindowEvent evt) {

			}
		
			public void windowOpened(java.awt.event.WindowEvent evt) {
        	
			}
        
		});

	}//formSetting()
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void main(String[] args) {
		
		new formSetting().setVisible(true);
		
	}//public static void main(String[] args)
}//public class formSetting  extends javax.swing.JFrame
