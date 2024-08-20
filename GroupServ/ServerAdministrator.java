import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;

/* ServerAdministrator.java */

public class ServerAdministrator extends JFrame implements ActionListener
  {
      JButton openGroup,close;
      JLabel jl0,jl1,jl2,jl3,jl4;
      JTextField groupName,groupId,groupPort,portNum;
      JPanel north,center,south;

     public ServerAdministrator()
        {
         setTitle("Open Group");
         setLocation(50,50);
         setSize(890,600);
         Container contentPane=getContentPane();
         contentPane.setLayout(new BorderLayout());
         String plaf="com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

         try
          {
            UIManager.setLookAndFeel(plaf);
            SwingUtilities.updateComponentTreeUI(contentPane);
          }
          catch(Exception e)
           {
		System.out.println(""+e);	
           }


         String label[]={"Open Group",
                        "Group Name",
			"Group Id",
			"Group Port",
                        "Server Port"};

        jl0 = new JLabel(label[0]);
        jl1 = new JLabel(label[1]);
        jl2 = new JLabel(label[2]);
	jl3 = new JLabel(label[3]);
	jl4 = new JLabel(label[4]);

	groupName = new JTextField("Om",20);
	groupId = new JTextField("192.168.1.8",20);
	groupPort = new JTextField("4446",20);
	portNum = new JTextField("7999",20);

	GridBagLayout grid=new GridBagLayout();
	
	north = new JPanel();
        center=new JPanel();
        south=new JPanel();

        Box b=Box.createVerticalBox();
        b.add(Box.createGlue());
	
	north.setLayout(new FlowLayout());
        center.setLayout(grid);
        south.setLayout(new GridLayout(1,2)); 

        jl0.setFont(new Font("Monotype Corsiva",Font.BOLD+Font.ITALIC,30));
	north.add(jl0);

	GridBagConstraints c = new GridBagConstraints();
	c.gridx = 1;
        c.gridy = 1;
        center.add(jl1,c);
        c.gridx = 2;
        c.gridy = 1;
        center.add(groupName,c);
                                                                                
        c.gridx = 1;
        c.gridy = 2;
        center.add(jl2,c);
	
	c.gridx = 2;
	c.gridy = 2;
	center.add(groupId,c);

	c.gridx = 1;
        c.gridy = 3;
        center.add(jl3,c);
                                                                                
        c.gridx = 2;
        c.gridy = 3;
        center.add(groupPort,c);

	c.gridx = 1;
        c.gridy = 4;
        center.add(jl4,c);
                                                                                                                             
        c.gridx = 2;
        c.gridy = 4;
        center.add(portNum,c);
        openGroup=new JButton("OpenGroup",new ImageIcon("NEW.GIF"));
        close=new JButton("Exit",new ImageIcon("EXIT.GIF"));
	
	south.add(openGroup);
	south.add(close);

        contentPane.add(north,BorderLayout.NORTH);
        contentPane.add(center,BorderLayout.CENTER);
        contentPane.add(south,BorderLayout.SOUTH);




         openGroup.addActionListener(this);
         close.addActionListener(this);

         addWindowListener(new WindowAdapter()
                {
                public void WindowClosing(WindowEvent we)
                  {
                	System.exit(0);
                  }
                });


	}
      public void actionPerformed(ActionEvent ae)
        {
          if(ae.getSource()==openGroup)
                {
		  String groupNa = groupName.getText();
		  String groupIdd = groupId.getText();
		  int groupPor = Integer.parseInt(groupPort.getText()); 
		  int serverPor = Integer.parseInt(portNum.getText());
		  GroupController ob = new GroupController(groupNa, groupIdd, groupPor,serverPor);
		   ob.setVisible(true);
		}
         if(ae.getSource()==close)
			System.exit(0);
	}
        public static void main(String args[])
 	 {
                JFrame f=new ServerAdministrator();
		f.show();
	}
}

