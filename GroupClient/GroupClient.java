import java.io.*;  
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.security.*;
import java.util.Date;
import javax.crypto.*;
import java.security.spec.*;
import javax.crypto.spec.*;
import java.sql.*;

public class GroupClient extends JFrame implements ActionListener
 {
 JButton ok,cancel;
 JTextField username;
 JPasswordField password; 
 JTextField groupId;
 JTextField groupPort;
 JTextField serverId;
 JTextField serverPort;
 
 Key key;
 SecretKey longtermKey;
        public GroupClient()
	{
			
                 super("Client Authentication");
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
                   }


                        JPanel p1=new JPanel();
                        ok=new JButton("Ok");
                        cancel=new JButton("Cancel");

                        username=new JTextField("your name",20);
                        password=new JPasswordField("",20);
			groupId = new JTextField("192.168.1.8",20);
			groupPort = new JTextField("4446",20);
			serverId = new JTextField("localhost",20);
			serverPort = new JTextField("7999",20);

                        char star='*';
                        password.setEchoChar(star);
                        GridBagLayout grid=new GridBagLayout();
                        GridBagConstraints c = new GridBagConstraints();


                        //grid.setHgap(5);
                        //grid.setVgap(20);
                        p1.setLayout(grid);

                        c.gridx=1;
                        c.gridy=1;
                        p1.add(new JLabel("UserName"),c);
                        c.gridx=2;
                        c.gridy=1;
                        p1.add(username,c);

                        c.gridx=1;
                        c.gridy=2;
                        p1.add(new JLabel("Password"),c);

                        c.gridx=2;
                        c.gridy=2;
                        p1.add(password,c);
			
			c.gridx=1;
                        c.gridy=3;
                        p1.add(new JLabel("GroupId"),c);
                                                                                                                             
                        c.gridx=2;
                        c.gridy=3;
                        p1.add(groupId,c);
				
			c.gridx=1;
                        c.gridy=4;
                        p1.add(new JLabel("GroupPort"),c);
                                                                                                                             
                        c.gridx=2;
                        c.gridy=4;
                        p1.add(groupPort,c);
	
			c.gridx=1;
                        c.gridy=5;
                        p1.add(new JLabel("ServerId"),c);
                                                                                                                             
                        c.gridx=2;
                        c.gridy=5;
                        p1.add(serverId,c);

			c.gridx=1;
                        c.gridy=6;
                        p1.add(new JLabel("ServerPort"),c);
                                                                                                                             
                        c.gridx=2;
                        c.gridy=6;
                        p1.add(serverPort,c);	
                        JPanel p2=new JPanel();
                        p2.setLayout(new FlowLayout());
                        p2.add(ok);
                        p2.add(cancel);
                        
                        ok.addActionListener(this);
                        cancel.addActionListener(this);
                        
                        contentPane.add(p1,BorderLayout.CENTER);
                        contentPane.add(p2,BorderLayout.SOUTH);
                        setSize(600,350 );
                        setLocation(300,150);
                        show();
          }
 public void sendAuthentication(String user, String password,
     OutputStream outStream) throws IOException, NoSuchAlgorithmException {
    DataOutputStream out = new DataOutputStream(outStream);
    long t1 = (new Date()).getTime();
    double q1 = Math.random();
    byte[] protected1 = Protection.makeDigest(user, password, t1, q1);

    out.writeInt(1);
    out.writeUTF(user);
    out.writeLong(t1);
    out.writeDouble(q1);
    out.writeInt(protected1.length);
    out.write(protected1);
    out.flush();
  }
  

public void actionPerformed(ActionEvent ae){
        if(ae.getSource()==ok){
        try{
	            Socket s = new Socket("localhost",7999);
	            char[] pwd=password.getPassword();
     		    String psw = new String(pwd);
	            System.out.println("in action performed USERNAME "+username.getText());
	            System.out.println("in action performed PASSWORD "+psw);

                    sendAuthentication(username.getText(),psw,s.getOutputStream());
    	            System.out.println("outputstream "+s.getOutputStream().toString());
System.out.println("output"+s.getOutputStream().toString());

                    DataInputStream in = new DataInputStream(s.getInputStream());
                    int val = in.readInt();
					
					if(in!=null){
					System.out.println("in is not null "+in);
					System.out.println("in is not null, val is "+val);
					}
                    
                    if(val==1){
                          setLocation(250,150);
                        byte[] b  = new byte[16];
                        in.readFully(b,0,16);
                        SecretKeyFactory desFactory = SecretKeyFactory.getInstance("DES");
                        KeySpec spec = new DESKeySpec(psw.getBytes(),0 );
                        longtermKey = desFactory.generateSecret(spec);
                        Cipher cipher =Cipher.getInstance("DES");
                        cipher.init(Cipher.UNWRAP_MODE,longtermKey);
                        key  = cipher.unwrap(b,"DES",Cipher.SECRET_KEY);
                    System.out.println("after cipher ");

                        StringBuffer users = new StringBuffer();
                        int encrylength = in.readInt();
                        byte[] buff = new byte[encrylength];
                        in.read(buff,0,encrylength);
                        Cipher decipher = Cipher.getInstance("DES");
                        decipher.init(Cipher.DECRYPT_MODE,key);
                        ByteArrayInputStream dbin = new ByteArrayInputStream(buff);
                        CipherInputStream cin = new CipherInputStream(dbin, decipher);
                        byte[] decrymesg = new byte[encrylength];
                        int no = cin.read(decrymesg);
            
              			System.out.println("after cipher-dicipher"+no);

                         while(no!=-1) {
                                                 users.append(new String(decrymesg,0,no));
                                                 no = cin.read(decrymesg);
			}

//			GroupMember ob = new GroupMember(username.getText(),psw,key,users.toString());
System.out.println("before groupmember call "+key.toString()+": group id : "+groupId.getText()+": server id : "+serverId.getText()+" : server port : "+serverPort.getText());

GroupMember ob = new GroupMember(username.getText(),psw,key,users.toString(),groupId.getText(),Integer.parseInt(groupPort.getText()),serverId.getText(),Integer.parseInt(serverPort.getText()));
			ob.setVisible(true);
                        ob.setLocation(300,150);
                        setVisible(false);
                    }
                    else
					{
                         setLocation(250,150);
                         JOptionPane.showMessageDialog(this,"Invalid UserName or Password or Member has already logged","ErrorMessage",JOptionPane.ERROR_MESSAGE);
                    }

                    in.close();
                    s.close();
                    
           } catch(Exception ce){
                 setLocation(250,150);
                 JOptionPane.showMessageDialog(this,""+ce,"ErrorMessage",JOptionPane.ERROR_MESSAGE);

           }
           username.setText("");
           password.setText("");

	 }
         if(ae.getSource()==cancel){
	 	System.exit(0);
	  }
            }
               public static void main(String arg[]){
                 new GroupClient();                 
               }
}


