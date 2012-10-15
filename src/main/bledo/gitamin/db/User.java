package bledo.gitamin.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
	public User(ResultSet res) throws SQLException {
		username	= res.getString("username");
		email		= res.getString("email");
		password	= res.getString("password");
		active		= res.getBoolean("active");
	}
	
	
	public String username = "";
	public String password = "";
	public String email = "";
	public boolean active = true;
}
