import java.io.*;
import java.net.*;
import java.security.*;
import javax.crypto.*;
import java.sql.*; 
import javax.crypto.spec.*;
import java.security.spec.*;
import java.util.Hashtable; 
import java.util.Enumeration; 
import javax.swing.*;

public class ProtectedServer extends Thread {
   Hashtable users;
   JTextArea ta = null;
   JTextArea users_list = null;
   ServerSocket s;
   MulticastServerThread ob;
   String groupId;
   int groupPort;
   int serverPort;
   

  
  public ProtectedServer(JTextArea ta,JTextArea users_list, String groupId,int groupPort, int serverPort) {
   users = new Hashtable();
   this.ta = ta;
   this.users_list = users_list;
   this.groupId = groupId;
   this.groupPort = groupPort;
   this.serverPort = serverPort;
  }
  public boolean authenticate(String username,String password,DataInputStream in)
      throws IOException, NoSuchAlgorithmException {
    long t1 = in.readLong();
    double q1 = in.readDouble();
    int length = in.readInt();
    byte[] protected1 = new byte[length];
    in.readFully(protected1,0,length);
    byte[] local = Protection.makeDigest(username, password, t1, q1);
    return (MessageDigest.isEqual(protected1, local));
  }
 
  protected String lookupPassword(String user) {
      String url = "jdbc:mysql://localhost:3306/";
      String dbName = "test";
      String driver = "com.mysql.jdbc.Driver";
      String userName = "root"; 
      String password = "root";

	Connection con=null;
        Statement stmt;
        //String url;
        String sql;
	String passwd=null;
            try 
                 {
                   Class.forName(driver).newInstance();
                   con = DriverManager.getConnection(url+dbName,userName,password);

                 }
                catch(Exception ce)
                  {
                    ta.append(ce+"\n");
                  }
                try
                 {
                   //url="jdbc:mysql://localhost:3306/test";
                   //con=DriverManager.getConnection(url,"root","root");
                   stmt=con.createStatement();
                   ResultSet rs=stmt.executeQuery("select password from users where username='"+user+"'");
		   if(rs.next())
		   	passwd=rs.getString(1);
		   rs.close();
		   stmt.close();
		   con.close();
		 }catch(SQLException ce){
			ta.append("Error:"+ce);
		 }
		   if(passwd!=null)
		     return passwd;		
		   return(""+"null");
  }
 
  public void run() {
   
   try{
    ob = new MulticastServerThread(groupId,groupPort);
    //int port = 7999;
    SecretKey longtimeKey = null;
    SecretKey key = null;
    SecretKey currentKey = null;
    int noUsers=0;
    s = new ServerSocket(serverPort);
    //ProtectedServer server = new ProtectedServer();
    for(;;){
	 ta.append("Waiting for Members:\n");
   	 Socket client = s.accept();
	 ta.append("Spawning............\n");
	 DataInputStream in = new DataInputStream(client.getInputStream());
    	 int opt=in.readInt();
    	 String username = in.readUTF();
    	 String password = lookupPassword(username);


    if(opt == 1) {
	ta.append("New User tried to contact:\n");
	if(authenticate(username,password,in)){
	   if(!(addUser(username,password))){
		ta.append("Client failed to log in.\n");
		try{
         	DataOutputStream out = new DataOutputStream(client.getOutputStream());
         	out.writeInt(0);
		}catch(Exception e) {}

             }
	  else{   	
	     noUsers++;
     	     ta.append("Client logged in.\n");
             DataOutputStream out=new DataOutputStream(client.getOutputStream());
               try{
			currentKey = makeKey();
			if(noUsers>1) {
			   //Wrap the current using the key
			   byte[] buff = makeWrap(currentKey,key);				
			   //Create a ByteArrayOutputStream to send all the data into it 
			   ByteArrayOutputStream bout = new ByteArrayOutputStream();
			   DataOutputStream dout = new DataOutputStream(bout);
			   //Write the flag value 1 to differntiate between the mesg and key packets
			   dout.writeInt(1);
			   //Write the length of the key buffer
		           dout.writeInt(buff.length);	
			   //Write the wrapped key 
			   dout.write(buff,0,buff.length);
			   dout.flush();
                           ob.multicast(bout.toByteArray());
			   dout.close();
			}
		        key = currentKey;
			SecretKey longtermKey = makeDESKey(password.getBytes(),0);
			byte[] buff = makeWrap(currentKey,longtermKey);
                        out.writeInt(1);
                        out.write(buff, 0,buff.length);

			//Multicasting the Current Users List
			StringBuffer userslist = getUsers();
			String list = userslist.toString();	
		        users_list.setText(list);
			byte[] buffer = list.getBytes();	
       			Cipher cipher =Cipher.getInstance("DES");
        		cipher.init(Cipher.ENCRYPT_MODE, key);
        		ByteArrayOutputStream cbout = new ByteArrayOutputStream();
        		CipherOutputStream cout = new CipherOutputStream(cbout,cipher);
        		cout.write(buffer,0,buffer.length);
        		cout.flush();
        		cout.close();
        		byte[] encryptMesg = cbout.toByteArray();
			//Send the users list to the new Member 
			out.writeInt(encryptMesg.length);
			out.write(encryptMesg,0,encryptMesg.length);

			//Write the message
            		//Create a ByteArrayOutputStream to send all the data into it
            		ByteArrayOutputStream bout = new ByteArrayOutputStream();
            		DataOutputStream dout = new DataOutputStream(bout);
            		dout.writeInt(3);
            		dout.writeInt(encryptMesg.length);
            		dout.write(encryptMesg,0,encryptMesg.length);
            		dout.flush();
            		dout.close();
            		ob.multicast(bout.toByteArray());

		 }catch(Exception e) {
			ta.append(e+"\n");
		}
	     }
	}
      else{
     	 ta.append("Client failed to log in.\n");
        try{
      	 DataOutputStream out = new DataOutputStream(client.getOutputStream());
     	 out.writeInt(0);
	}catch(Exception e) {}
    	}
     }
   if(opt == 0) {
	try {
	if(authenticate(username,password,in)){
	    ta.append("Multicasting the Message\n");
            int encryptmsgLen = in.readInt();
            byte[] mesg = new byte[encryptmsgLen];
            in.readFully(mesg,0,encryptmsgLen);
		
	   
            //Write the message
            //Create a ByteArrayOutputStream to send all the data into it
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(bout);
            dout.writeInt(2);
	    dout.writeInt(encryptmsgLen);
	    dout.write(mesg,0,encryptmsgLen);
            dout.flush();		
	    dout.close();
            ob.multicast(bout.toByteArray()); 
	}
      }catch(Exception e) {}	
	
   }
   if(opt == 2) {
     try { 
	if(authenticate(username,password,in)){
	  ta.append("ClientLoggedout\n");
           noUsers--;
	  // if(noUsers == 0)
	//	key = null;
	   removeUser(username);

	  //Multicasting the Current Users List
          StringBuffer userslist = getUsers();
          String list = userslist.toString();
	  users_list.setText(list);
          byte[] buffer = list.getBytes();
          Cipher cipher =Cipher.getInstance("DES");
          cipher.init(Cipher.ENCRYPT_MODE, key);
          ByteArrayOutputStream cbout = new ByteArrayOutputStream();
          CipherOutputStream cout = new CipherOutputStream(cbout,cipher);
          cout.write(buffer,0,buffer.length);
          cout.flush();
          cout.close();
          byte[] encryptMesg = cbout.toByteArray();
                                                                                                                                             
          //Write the message
          //Create a ByteArrayOutputStream to send all the data into it
          ByteArrayOutputStream bout = new ByteArrayOutputStream();
          DataOutputStream dout = new DataOutputStream(bout);
          dout.writeInt(3);
          dout.writeInt(encryptMesg.length);
          dout.write(encryptMesg,0,encryptMesg.length);
          dout.flush();
          dout.close();
          ob.multicast(bout.toByteArray());
          currentKey = makeKey();
          if(noUsers>1) {
               //Wrap the current using the key
               byte[] buff = makeWrap(currentKey,key);
               //Create a ByteArrayOutputStream to send all the data into it
               bout = new ByteArrayOutputStream();
               dout = new DataOutputStream(bout);
               //Write the flag value 1 to differntiate between the mesg and key packets
               dout.writeInt(1);
               //Write the length of the key buffer
               dout.writeInt(buff.length);
               //Write the wrapped key
               dout.write(buff,0,buff.length);
               dout.flush();
               ob.multicast(bout.toByteArray());
               dout.close();
               key = currentKey;
             }

      } 
    }catch(Exception e){} 
  }
  } 
  }catch(Exception e){ta.append(e+"\n");}
 
  
}
public void stopServer() {
    try{
        //Write the message
        //Create a ByteArrayOutputStream to send all the data into it
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);
        dout.writeInt(4);
        dout.flush();
        dout.close();
        ob.multicast(bout.toByteArray());
	s.close();
       }catch(Exception e){ta.append(e+"\n");
	}
}
	
public boolean addUser(String username,String password){
	if(users.containsKey(username))
		return false;
	users.put(username,password);
	return true;
}
public void displayUsers() {
	for(Enumeration e = users.keys();e.hasMoreElements();)
                users_list.append("users:"+e.nextElement()+"\n");

}
public StringBuffer getUsers() {
	StringBuffer temp = new StringBuffer();
        for(Enumeration e = users.keys();e.hasMoreElements();)
               temp.append(e.nextElement()+"\n");
                                                                                                                                             
	return temp;
}

public void removeUser(String key) {
	users.remove(key);	
}
public byte[] makeByteFromDESKey(SecretKey ki) 
	throws NoSuchAlgorithmException,InvalidKeySpecException {
		SecretKeyFactory desFactory = SecretKeyFactory.getInstance("DES");
		DESKeySpec spec = (DESKeySpec) desFactory.getKeySpec(ki, DESKeySpec.class);
		return spec.getKey();
	} 
public SecretKey makeDESKey(byte[] input, int offset)
	 throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
		SecretKeyFactory desFactory = SecretKeyFactory.getInstance("DES");
		KeySpec spec = new DESKeySpec(input, offset);
		return desFactory.generateSecret(spec);
	}
public SecretKey makeKey()
       throws NoSuchAlgorithmException {
		KeyGenerator keyGen = KeyGenerator.getInstance("DES");
		keyGen.init(new SecureRandom());
		return keyGen.generateKey();
	}	
public byte[] makeEncryption(byte[] buffer,javax.crypto.SecretKey key)
	throws NoSuchAlgorithmException, NoSuchPaddingException , InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
	Cipher cipher =Cipher.getInstance("DES");
	cipher.init(Cipher.ENCRYPT_MODE, key);
	cipher.update(buffer);
	return cipher.doFinal();
    }	
public byte[] makeWrap(SecretKey cur_key,SecretKey key)
        throws NoSuchAlgorithmException, NoSuchPaddingException , InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        Cipher cipher =Cipher.getInstance("DES");
        cipher.init(Cipher.WRAP_MODE, key);
        return cipher.wrap(cur_key);
    }
/*public SecretKey getLongtimeKey()
 	throws NoSuchAlgorithmException,InvalidKeyException, InvalidKeySpecException {
		SecretKeyFactory desFactory = SecretKeyFactory.getInstance("DES");
                KeySpec spec = new DESKeySpec(password.getBytes(),0 );
                return desFactory.generateSecret(spec);
      }*/ 
}

