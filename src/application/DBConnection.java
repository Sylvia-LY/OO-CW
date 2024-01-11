package application;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    /**
     * Builds a database connection.
     *
     * @return Connection object representing the database connection.
     */
	public static Connection getDBConnection() {
		Connection con = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/shop", "root", "000000");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return con;

	}

}
