import java.io.*;
import java.security.*;


/* Protection.java */
public class Protection {
  public static byte[] makeDigest(String user, String password,
      long t1, double q1) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("SHA");
    md.update(user.getBytes());
    md.update(password.getBytes());
    md.update(makeBytes(t1, q1));
    return md.digest();
  }

  public static byte[] makeBytes(long t, double q) {
    try {
      ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
      DataOutputStream dataOut = new DataOutputStream(byteOut);
      dataOut.writeLong(t);
      dataOut.writeDouble(q);
      return byteOut.toByteArray();
    }
    catch (IOException e) {
      return new byte[0];
    }
  }
}
