import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

public class gui extends JFrame
{
	private Logger log = null;
	
	private JPanel pnlMain = null;
	private JPanel northPanel = null;
	private JButton btnStart = null;
	private JFrame frame = null;
	private JTextArea txtArea = null;
	private JScrollPane scroller= null;
	
	private boolean isStarted = true;   // if set to false, program should stop. checked by control object
	private boolean bSmartLoot = true;
	
	private JCheckBox chxSmartLoot = null;
	
	public gui(Logger l)
	{
		log = l;
		
		pnlMain = new JPanel();
		northPanel = new JPanel();   // save room for other things later on, functionalities.
		btnStart = new JButton("Stop");		
		frame = new JFrame("");
		txtArea = new JTextArea();
		chxSmartLoot =  new JCheckBox("Smart Loot");
		chxSmartLoot.setSelected(true);
		
		//txtArea.setPreferredSize( new Dimension( 200, 300) );
		scroller = new JScrollPane(txtArea);	
		scroller.setPreferredSize(new Dimension(200,150));

		
		pnlMain.setLayout(new BorderLayout());
		pnlMain.add(BorderLayout.NORTH, northPanel);
		pnlMain.add(BorderLayout.SOUTH, btnStart);
		pnlMain.add(BorderLayout.CENTER, scroller);
		
		
		northPanel.setLayout(new FlowLayout());
		northPanel.add(chxSmartLoot);
		
		
		
				
		ActionListener();
		
		
		frame.getContentPane().add(pnlMain);		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
		frame.setSize(200, 300);
		frame.pack();
		frame.setVisible(true);
		
	}
	
	
	public void ActionListener()
	{
		/*
		 * Button ACtion listener
		 */
		btnStart.addActionListener(new ActionListener() {           
            public void actionPerformed(ActionEvent e)
            {
            	if(isStarted)
            	{
            		btnStart.setText("Start");    
            	}
            	else
            	{
            		btnStart.setText("Stop");    
            	}
        		isStarted = !isStarted;              
                
            }
         });  
		
		
		chxSmartLoot.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent actionEvent)
			{
				AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
		        boolean selected = abstractButton.getModel().isSelected();
		        bSmartLoot = selected;
		        
		        info("Smart Loot set to " + selected);
		       
			}
			
		});
	}
	/*
	 * log the string, also display in text field
	 */
	public void info(String s)
	{
		log.info(s);
		txtArea.append(s+"\n");		
		
		JScrollBar vertical = scroller.getVerticalScrollBar();
		vertical.setValue( vertical.getMaximum() );
	}
	
	public boolean getStarted()
	{
		return isStarted;
	}
	
	public boolean getSmartLoot()
	{
		return bSmartLoot;
	}
	
}
