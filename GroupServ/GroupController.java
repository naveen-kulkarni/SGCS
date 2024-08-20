// Description:
//Contains  GroupController which is used to start and stop the Group


import java.util.*;
import java.sql.*;
import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

public  class GroupController extends JFrame implements ActionListener
                     
          {
                JPanel north,center,east,south;
                JLabel jl0;
                JTextArea ta;
		JTextArea users_list;
                JButton  startServer;
                JButton stopServer;
		JButton addUser;
                JButton exit;
                ProtectedServer mainServ;
		String groupName;
 		String groupId;
		int groupPort;
		int serverPort;
                
                public GroupController(String groupName,String groupId,int groupPort,int serverPort)
                {
                	super("GroupController");
			setLocation(50,50);
         		setSize(890,600);
			this.groupName = groupName;
			this.groupId = groupId;
			this.groupPort = groupPort;
			this.serverPort = serverPort;
	                Container contentPane = getContentPane();
       		        contentPane.setLayout(new BorderLayout());
                
	                north = new JPanel();
	         	south = new JPanel();
			center = new JPanel();
			east = new JPanel();
		
			//GridLayout grid = new GridLayout(2,2);
			//grid.setHgap(1);
	                north.setLayout(new GridLayout(2,2));
       		        center.setLayout(new FlowLayout());
			east.setLayout(new FlowLayout());
                	south.setLayout(new GridLayout(1,4));



                	startServer=new JButton("StartServer",new ImageIcon("NEW.GIF"));
                	stopServer=new JButton("StopServer",new ImageIcon("STOP.GIF"));
			addUser=new JButton("AddMember",new ImageIcon("AddNew.GIF"));
                	exit=new JButton("Exit",new ImageIcon("CROSS.GIF"));         
                /*startServer=new JButton("StartServer");
                stopServer=new JButton("StopServer");
                exit=new JButton("Exit");            */ 



	                jl0= new JLabel("Group Controller"+groupName);
			jl0.setFont(new Font("Monotype Corsiva",Font.BOLD+Font.ITALIC,30));
                
                	ta= new JTextArea(50,40);
			ta.setEditable(false);
			users_list = new JTextArea(50,20);
			users_list.setEditable(false);

	                north.add(jl0);
			north.add(new JLabel(""));
			north.add(new JLabel("Group Server:"));
			north.add(new JLabel("                             Users_List:"));
       		        center.add(new JScrollPane(ta));
			east.add(new JScrollPane(users_list)); 
                	south.add(startServer);
                	south.add(stopServer);
			south.add(addUser);
                	south.add(exit);

                	contentPane.add(north, BorderLayout.NORTH);
                	contentPane.add(center, BorderLayout.CENTER);
			contentPane.add(east, BorderLayout.EAST);
                	contentPane.add(south, BorderLayout.SOUTH);
                
                	startServer.addActionListener(this);
                	stopServer.addActionListener(this);
			addUser.addActionListener(this);
                	exit.addActionListener(this);
                             
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
       if(ae.getSource()==startServer)
        {
		try{
                        if(mainServ==null){
				ta.setText("");
                                mainServ=new  ProtectedServer(ta,users_list,groupId,groupPort,serverPort);
                                mainServ.start();
                        }
                        else {
                          setLocation(250,150);
                          JOptionPane.showMessageDialog(this,"Server is Already running on Port:","ErrorMessage",JOptionPane.ERROR_MESSAGE);
                        }

                            
        	 }
                catch(Exception ce)
                {
                 setLocation(250,150);
                 JOptionPane.showMessageDialog(this,""+ce,"ErrorMessage",JOptionPane.ERROR_MESSAGE);
                }
             }
       if(ae.getSource()==stopServer)
        {
		try{
			mainServ.stopServer();
                        mainServ=null;
			ta.setText("");
			users_list.setText("");
                 }
                catch(Exception ce)
                {
                 setLocation(250,150);
                 JOptionPane.showMessageDialog(this,""+ce,"ErrorMessage",JOptionPane.ERROR_MESSAGE);

                }
          }
	 if (ae.getSource()==addUser)
           {
		CreateUser obc = new CreateUser();
                obc.setVisible(true);
           }

         if(ae.getSource()==exit)
            {
              if(mainServ!=null){
                  mainServ.stopServer();
                  mainServ=null;
                  System.exit(0);
               }
                 System.exit(0);
         }
      }


 /*      public static void main(String ar[])
         {
                GroupController  ob1=new GroupController();
                ob1.setVisible(true);
         }*/
  }

  
