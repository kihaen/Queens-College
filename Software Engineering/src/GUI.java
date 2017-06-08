import java.awt.*;
import java.awt.Container;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;

public class GUI extends JFrame implements ActionListener {
	JFileChooser newfile; //  instantiates a new file chooser, for the purpose of changing database.
	JMenuBar menuBar; // only one is necessary
	JMenu file, subfile; // tabs in the menu bar
	JMenuItem Browse,Backup,Clear,Toggle;
	JList list;
	JLabel tid;
	private JTextArea textArea;
	private DefaultListModel listModel;
	JButton send;
	private KeyStroke keystroke;
	private static final String key = "ENTER"; 
	private static HashData DATABASE;
	private static String togglestring = "false";
	private boolean Searchmode = false; // boolean identifier for which mode the gui is in. internet search mode or offline database mode.
	
	public GUI(HashData database){
		Container CP = this.getContentPane();
		CP.setLayout(new BorderLayout()); // Supers a frame BorderLayout from AWT, form left to right
		DATABASE = database; //  i need this database to work with
		//Creating the menu bar itself
		menuBar = new JMenuBar();
		
		// the first menu items
		file = new JMenu("File");
		menuBar.add(file);
		
		//Jmenu-items within the Jmenu
		Browse = new JMenuItem("Find");
		file.add(Browse);
		file.addSeparator(); // finished with file show visible seperator
		Backup = new JMenuItem("Restore");
		file.add(Backup);
		file.addSeparator();
		Clear = new JMenuItem("Clear DB");
		file.add(Clear);
		file.addSeparator();
		Toggle = new JMenuItem("SearchMode");
		file.add(Toggle);
		file.addSeparator();
		
		//JLabel
		tid = new JLabel("Enter Your Query");
		
		//Buttons
		send = new JButton("Send");
		
		//Button Handling;
		Browse.addActionListener(this);
		Browse.setActionCommand("browse");
		Backup.addActionListener(this);
		Backup.setActionCommand("backup");
		send.addActionListener(this);
		send.setActionCommand("Send");
		Clear.addActionListener(this);
		Clear.setActionCommand("clear");
		Toggle.addActionListener(this);
		Toggle.setActionCommand("toggle");
		
		
		//initializing a list
		list = new JList();
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(250, 80));
		
		listModel = new DefaultListModel();
		ListHandler(listModel,"Enter in this format: Command|ISBN|Author|Title|YearPublished|DatePurchased"); // default handler for everything added to list
		
		list = new JList(listModel);
		
		// text area
		textArea = new JTextArea(
			    "Enter commands here: but clear this first"
			);
			textArea.setFont(new Font("Serif", Font.ITALIC, 16));
			textArea.setLineWrap(false);
			
		// keystroke
			keystroke = KeyStroke.getKeyStroke(key);
	        Object actionKey = textArea.getInputMap(
	                JComponent.WHEN_FOCUSED).get(keystroke);
	        textArea.getActionMap().put(actionKey, wrapper);
	        
			
		//Container Content
	    CP.add(tid, BorderLayout.NORTH);
		CP.add(list,BorderLayout.CENTER);
		CP.add(textArea, BorderLayout.PAGE_END);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//jframe size defaults
				setJMenuBar(menuBar);
				setTitle("Database by Kihaen Baik");
				setSize(700, 300); 
				setVisible(true);

	}
	public void ListHandler(DefaultListModel list,String typed){ // stuff that should happen on the list per request
		//list.clear();
		if(list.size()>10){
			list.clear();
		}
		list.addElement(typed);
	}
	public void actionPerformed(ActionEvent e) {
		if("browse".equals(e.getActionCommand())){
			//jchooser goes here
			newfile = new JFileChooser();
			int returnValue = newfile.showOpenDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
		          File selectedFile = newfile.getSelectedFile();
		          //do something here with the selected file
		          String sendthis = selectedFile.getAbsolutePath();
		          try {
					DATABASE = new HashData(sendthis);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		    }
		}
		if("backup".equals(e.getActionCommand())){
			try {
				DATABASE = new HashData("TraceLog.txt");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if("Send".equals(e.getActionCommand())){
			if(Searchmode == false){
				String temp = textArea.getText();
				int Key = DATABASE.Hashline(temp);
				ListHandler(listModel,"Operation was Successful");
				ListHandler(listModel,"ISBN | Author | Title | Year Published | Publisher | Paperback Price |"
						+ " HardCover Price | Quantity");
				ListHandler(listModel,DATABASE.Query(Key).toString());
				textArea.setText(null);
				//toggleid = new JLabel("Searchmode = " + togglestring);
			}
			else{
				String text = textArea.getText();
				try {
					GetURLInfo searcher = new GetURLInfo(text);
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		if("toggle".equals(e.getActionCommand())){
			Searchmode = !Searchmode;
			if(Searchmode == true){
				togglestring = "True";
				textArea.setText("Enter your ISBN here: (but clear this first)");
			}
			else{
				togglestring = "False";
				textArea.setText("Enter your Query here: (but clear this first)");
			}
			ListHandler(listModel,"Searchmode = " + togglestring);
		}
		if("clear".equals(e.getActionCommand())){ // reconstruct the database based on the input file name.
			DATABASE = new HashData();
		}
	}
	private Action wrapper = new AbstractAction() { // button emulator!
        @Override
        public void actionPerformed(ActionEvent ae) {
            send.doClick();
        }
    };
	public void valueChanged(ListSelectionEvent e) { // button enabling a disabling based on if there is a selection

	    if (e.getValueIsAdjusting() == false) {

	        if (list.getSelectedIndex() == -1) {
	        //No selection, disable fire button.
	           // fireButton.setEnabled(false); 

	        } else {
	        //Selection, enable the fire button.
	           // fireButton.setEnabled(true);
	        }
	    }
	}
	

}
