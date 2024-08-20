import java.io.*;
import java.net.*;
import java.util.*;

/* MulticastServerThread.java */

public class MulticastServerThread implements Serializable{
    protected MulticastSocket socket=null;
    protected BufferedReader in = null;
    protected boolean moreQuotes = true;


    private long FIVE_SECONDS = 5000;
    String groupId;
    int groupPort;
    
    public MulticastServerThread(String groupId,int groupPort) throws IOException {
	this.groupId = groupId;
        this.groupPort = groupPort;
        socket = new MulticastSocket(groupPort);
    }

    public void multicast(byte[] buf) {
            try {
                //byte[] buf = new byte[256];

                //buf = mesg.getBytes();

		    // send it
                //InetAddress group = InetAddress.getByName("230.0.0.1");
                InetAddress group = InetAddress.getByName(groupId);
                DatagramPacket packet = new DatagramPacket(buf, buf.length, group, groupPort);
                socket.send(packet);

            } catch (IOException e) {
                e.printStackTrace();
		moreQuotes = false;
            }
//	socket.close();
    }
}
