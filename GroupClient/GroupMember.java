/* GroupMember.java */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.util.*;
import java.io.*;
import java.net.*;
import javax.crypto.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.spec.*;
class GroupMember extends JFrame implements ActionListener,WindowListener,Runnable {
     JPanel center,center_e,center_we,south;
     JScrollPane scPane;
     JTextArea ta0,ta1,users_list;
     JButton send,clear,logout;
     //ClientSer ser;
     String username; 
     String password;
     Key key;
     Thread t; 
     MulticastSocket ms;
     Key currentKey;
     String groupId;
     int groupPort;
     String serverId;
     int serverPort;
     

  public  GroupMember(String username, String password,Key key,String users,String groupId,int groupPort,String serverId,int serverPort)
   {
     super("Group Member");
     this.username = username;
     this.password = password;
     this.key = key;
     this.currentKey = key;
     this.groupId = groupId;
     this.groupPort = groupPort;
     this.serverId = serverId;
     this.serverPort = serverPort; 
     setSize(700,300);
     String plaf="com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
     try
        {
         UIManager.setLookAndFeel(plaf);
         SwingUtilities.updateComponentTreeUI(this);
        }
       catch(Exception e){}
     
     Container contentPane=getContentPane();
     GridLayout grid=new GridLayout(3,2);
     contentPane.setLayout(new BorderLayout());

     //contentPane.setBackground(new Color(70,0,35));
     center_we = new JPanel(grid);
     //center_we.setBackground(new Color(70,0,35));
     grid.setHgap(2);
     grid.setVgap(20);
     JLabel group=new JLabel(username);
     group.setFont(new Font("Monotype Corsiva",Font.BOLD+Font.ITALIC,30));
     center_we.add(group);
     center_we.add(new JLabel());
     center_we.add(new JLabel("Messages From Group:"));
     ta0=new JTextArea(3,20);
     ta0.setEditable(false);
     scPane=new JScrollPane(ta0);
     center_we.add(scPane);
     center_we.add(new JLabel("Type Message to send to Group:"));
     ta1=new JTextArea(3,20);
     scPane=new JScrollPane(ta1);
     center_we.add(scPane);

     center_e=new JPanel();
     users_list=new JTextArea(20,15);
     users_list.setText(users);
     users_list.setEditable(false);
     scPane=new JScrollPane(users_list);
     center_e.add(scPane);
     center=new JPanel();
     center.setLayout(new BorderLayout());
     center.add(center_e,BorderLayout.EAST);
     center.add(center_we,BorderLayout.WEST);

     south=new JPanel(new GridLayout(1,4));
     south.add(send=new JButton("Send"));
     south.add(clear=new JButton("Clear"));
     south.add(logout=new JButton("Logout"));
     contentPane.add(center,BorderLayout.CENTER);
     contentPane.add(south,BorderLayout.SOUTH);
     send.addActionListener(this);
     clear.addActionListener(this);
     logout.addActionListener(this);
     addWindowListener(this);
     t = new Thread(this);
     t.start(); 
   }
public void run(){
                int i=1;
                try{
                        ms = new MulticastSocket(groupPort);
                        InetAddress ia= InetAddress.getByName(groupId);
                        ms.joinGroup(ia);
                        byte[] buffer = new byte[512];
                        for(;;){
                                DatagramPacket dp= new DatagramPacket(buffer, buffer.length);
                                ms.receive(dp);
                                ByteArrayInputStream bin = new ByteArrayInputStream(dp.getData());
                                DataInputStream din = new DataInputStream(bin);
                                int flag = din.readInt();
                                if(flag == 1) {
                                        int length = din.readInt();
                                        byte[] buff = new byte[length];
                                        din.readFully(buff,0,length);
                                        Cipher cipher =Cipher.getInstance("DES");
                                        cipher.init(Cipher.UNWRAP_MODE,key);
                                        currentKey = cipher.unwrap(buff,"DES",Cipher.SECRET_KEY);
                                        key = currentKey;
				
                                }
                                if(flag == 2) {
                                        int encrylength = din.readInt();
                                        byte[] buff = new byte[encrylength];
                                        din.read(buff);
                                                                                                                                             
                                        Cipher decipher = Cipher.getInstance("DES");
                                        decipher.init(Cipher.DECRYPT_MODE,key);
                                        ByteArrayInputStream dbin = new ByteArrayInputStream(buff);
                                        CipherInputStream cin = new CipherInputStream(dbin, decipher);
                                        byte[] decrymesg = new byte[encrylength];
                                        int no = cin.read(decrymesg);
					while(no!=-1) {
						 ta0.append(new String(decrymesg,0,no));
						 no = cin.read(decrymesg);
					}
					ta0.append("\n");
                                }
				if(flag == 3){
					users_list.setText("");
					int encrylength = din.readInt();
                                        byte[] buff = new byte[encrylength];
                                        din.read(buff);
                                        Cipher decipher = Cipher.getInstance("DES");
                                        decipher.init(Cipher.DECRYPT_MODE,key);
                                        ByteArrayInputStream dbin = new ByteArrayInputStream(buff);
                                        CipherInputStream cin = new CipherInputStream(dbin, decipher);
                                        byte[] decrymesg = new byte[encrylength];
                                        int no = cin.read(decrymesg);
                                        while(no!=-1) {
                                                 users_list.append(new String(decrymesg,0,no));
                                                 no = cin.read(decrymesg);
                                        }
                                }
				if(flag == 4) {
					setLocation(250,150);
                			JOptionPane.showMessageDialog(this,"ServerStopped-Press Ok to exit","ErrorMessage",JOptionPane.ERROR_MESSAGE);
					System.exit(0);
			        }	
					

					
                        }
                }catch(Exception e) {
                        ta0.append("\nError:"+e);
                }
        }

public void windowOpened(java.awt.event.WindowEvent we){}
public void windowClosed(java.awt.event.WindowEvent we){}
public void windowIconified(WindowEvent we){}
public void windowDeiconified(WindowEvent we){}
public void windowActivated(WindowEvent we){}
public void windowDeactivated(WindowEvent we){}

public void windowClosing(WindowEvent we) {
              try{
                 Socket s = new Socket(serverId,serverPort);
                 DataOutputStream out = new DataOutputStream( s.getOutputStream());
                 long t1 = (new java.util.Date()).getTime();
                 double q1 = Math.random();
                 byte[] protected1 = Protection.makeDigest(username, password, t1, q1);
                 String msg = username+" :"+ta1.getText();
                 byte[] buffer = msg.getBytes();
                                                                                                                                             
                 Cipher cipher =Cipher.getInstance("DES");
                 cipher.init(Cipher.ENCRYPT_MODE, key);
		 cipher.update(buffer);
                 byte[] encryptMesg =  cipher.doFinal();
                                                                                                                                             
                 out.writeInt(2);
                 out.writeUTF(username);
                 out.writeLong(t1);
                 out.writeDouble(q1);
                 out.writeInt(protected1.length);
                 out.write(protected1);
                 out.flush();
                 out.close();
                 s.close();
                 System.exit(0);
            }catch(Exception ce){
                 setLocation(250,150);
                JOptionPane.showMessageDialog(this,""+ce,"ErrorMessage",JOptionPane.ERROR_MESSAGE);
                }
      }

 public void actionPerformed(ActionEvent ae){
 if( ae.getSource()==send)
   {

    if(ta1!=null){
    try{
	Socket s = new Socket(serverId,serverPort);
	//Socket s = new Socket("144.16.67.66",7999);
        DataOutputStream out = new DataOutputStream( s.getOutputStream());
    	long t1 = (new java.util.Date()).getTime();
   	double q1 = Math.random();
    	byte[] protected1 = Protection.makeDigest(username, password, t1, q1);
        String msg = username+" :"+ta1.getText();
	byte[] buffer = msg.getBytes();	

	Cipher cipher =Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
	ByteArrayOutputStream cbout = new ByteArrayOutputStream();
	CipherOutputStream cout = new CipherOutputStream(cbout,cipher);
	cout.write(buffer,0,buffer.length);
        cout.flush();
	cout.close();	
        byte[] encryptMesg = cbout.toByteArray();
		
        out.writeInt(0);
        out.writeUTF(username);
        out.writeLong(t1);
        out.writeDouble(q1);
        out.writeInt(protected1.length);
        out.write(protected1);
	out.writeInt(encryptMesg.length);	
        out.write(encryptMesg);
        out.flush();
	out.close();
	s.close();
        ta1.setText("");
    }catch(Exception ce){
	setLocation(250,150);
        JOptionPane.showMessageDialog(this,""+ce,"ErrorMessage",JOptionPane.ERROR_MESSAGE);
     }
    }
  }
 if( ae.getSource()==clear){
    ta0.setText("");
   }
 if( ae.getSource()==logout)
   {
    try{
        Socket s = new Socket(serverId,serverPort);
        DataOutputStream out = new DataOutputStream( s.getOutputStream());
        long t1 = (new java.util.Date()).getTime();
        double q1 = Math.random();
        byte[] protected1 = Protection.makeDigest(username, password, t1, q1);
        out.writeInt(2);
        out.writeUTF(username);
        out.writeLong(t1);
        out.writeDouble(q1);
        out.writeInt(protected1.length);
        out.write(protected1);
        out.flush();
        out.close();
        s.close();
	System.exit(0);
    }catch(Exception ce){
        setLocation(250,150);
        JOptionPane.showMessageDialog(this,""+ce,"ErrorMessage",JOptionPane.ERROR_MESSAGE);
     }
    }

 }
/* public static void main(String arg[]){
   new GroupMember("Sangameswar").setVisible(true);
  }*/}
