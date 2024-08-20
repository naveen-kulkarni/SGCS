import java.sql.*;

public class MysqlConnect{
  public static void main(String[] args)
    {
  System.out.println("MySQL Connect Example.");
  Connection con = null;
  String url = "jdbc:mysql://localhost:3306/";
  String dbName = "test";
  String driver = "com.mysql.jdbc.Driver";
  String userName = "root"; 
  String password = "root";
  try {
  Class.forName(driver).newInstance();
  con = DriverManager.getConnection(url+dbName,userName,password);
  String ss="select * from users";
  Statement stmt;
  stmt=con.createStatement();                              
  System.out.println("Connected to the database");
  stmt=con.createStatement();
  ResultSet rs= stmt.executeQuery(ss);
  while(rs.next())
  {
  String s1=rs.getString("username");
  String s2=rs.getString("password");
  System.out.println("Username " + s1);
  System.out.println("Password" + s2);
  }
  //String s1=rs.getString(0);
  //System.out.println(s1);
  con.close();
  System.out.println("Disconnected from database");
  } catch (Exception e) {
  e.printStackTrace();
  }
  }
}