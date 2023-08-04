import java.sql.*;
public class DBConnection{
	public static Connection getConnection(){
		Connection conn = null;
		
		try{
			Class.forName("com.mysql.cj.jdbc.Driver");
			//connection
			String url = "jdbc:mysql://localhost:3306/atm_machine";
			String username = "root";
			String password = "";
			conn = DriverManager.getConnection(url,username,password);
			/*System.out.println("Connected");*/
		}catch(Exception e){
			System.out.println("Connection failed.");
			e.printStackTrace();
		}
		return conn;
	}
}