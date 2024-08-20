import java.util.*;
import java.sql.*;
import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
public  class CreateUser extends JFrame implements ActionListener
          {
                JPanel north,south;
                
                JLabel jl0;
                JLabel jl1;
                JLabel jl2;
		JLabel jl3;
                
                JTextField tf0;
		JPasswordField password1;
		JPasswordField password2;
		JComboBox resp;


                JButton  ok;
                JButton cancel;
                
               
                public CreateUser()
                {
                	super("CREATION OF USERS");
                	setSize(300,150);
			setLocation(550,450);
                	Container contentPane=getContentPane();
                	contentPane.setLayout(new BorderLayout());
                
                	north=new JPanel();
                	south=new JPanel();


                	north.setLayout(new GridLayout(4,2));
                	south.setLayout(new GridLayout(1,2));



                	String lab[] = {"USER NAME","PASSWORD","RE-ENTER PASSWORD","PREVILEGES"	};


                	ok = new JButton("Ok");
                	cancel = new JButton("CANCEL");


                	jl0 = new JLabel(lab[0]);
                	jl1 = new JLabel(lab[1]);
                	jl2 = new JLabel(lab[2]);
			jl3 = new JLabel(lab[3]);
			resp = new JComboBox();
			resp.addItem(new String("Administrator"));
			resp.addItem(new String("GroupMember"));	
                
                
                	tf0= new JTextField(4);
			password1 = new JPasswordField("",8);
			password2 = new JPasswordField("",8);
                
               		north.add(jl0);
                	north.add(tf0);
                	north.add(jl1);
                	north.add(password1);
                	north.add(jl2);
               		north.add(password2);
			north.add(jl3);		
			north.add(resp);


               		south.add(ok);
                	south.add(cancel);

                	contentPane.add(north, BorderLayout.NORTH);
                	contentPane.add(south, BorderLayout.SOUTH);
                
                ok.addActionListener(this);
                cancel.addActionListener(this);
                
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
       String url = "jdbc:mysql://localhost:3306/";
       String dbName = "test";
       String driver = "com.mysql.jdbc.Driver";
       String userName = "root"; 
       String password = "root";

       if(ae.getSource()==ok)
         {
           String psw1 = new String(password1.getPassword());
	   String psw2 = new String(password2.getPassword());
          if(psw1.equals(psw2))
          {
             setVisible(true);
             Connection con=null;
             Statement stmt;
             //String url;
             try
              {
		Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url+dbName,userName,password);

              }
             catch(Exception ce)
              {
                System.out.println(ce);
              }
             try
              {  
		 //url="jdbc:mysql://localhost:3306/test";
		 //con=DriverManager.getConnection(url,"root","root");
                 stmt=con.createStatement();
                                    
                 int r= stmt.executeUpdate("insert into users values ('"
                 +tf0.getText()
                 +"','"+psw1
                 +"','"+resp.getSelectedItem()
                 +"')");
                 r=stmt.executeUpdate("commit");
                                          
                 tf0.setText("");
                 password1.setText("");
                 password2.setText("");
               }
            catch(SQLException ce)
               {
                  System.out.println(ce);
		  setLocation(250,150);
                  JOptionPane.showMessageDialog(this,""+ce,"ErrorMessage",JOptionPane.ERROR_MESSAGE);
               }
                                                
             }
           else
               {
                 setLocation(250,150);
                 JOptionPane.showMessageDialog(this,"Passwords don't Match","ErrorMessage",JOptionPane.ERROR_MESSAGE);
                }
        }
       if(ae.getSource()==cancel)
        {
        setVisible(false);
        }
      }


 /************************************************
          public static void main(String ar[])
         {
                CreateUser ob1=new CreateUser();
                ob1.setVisible(true);
         }
*************************************************/
        }





