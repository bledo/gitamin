package bledo.gitamin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bledo.Util;
import bledo.gitamin.db.DbException;
import bledo.gitamin.db.NotFoundException;
import bledo.gitamin.db.User;
import bledo.mvc.Request;
import bledo.mvc.Session;
import bledo.mvc.response.Response;

public class Gitamin
{
	private static final Logger log = LoggerFactory.getLogger(Gitamin.class);
	
	public static String _(Request req, String key, Object...args)
	{
		ResourceBundle rb = (ResourceBundle) req.getAttribute("resource_bundle");
		if (rb == null) {
			rb = ResourceBundle.getBundle("Gitamin_messages", req.getLocale());
			req.setAttribute("resource_bundle", rb);
		}
		
		String pattern = rb.getString(key);
		return MessageFormat.format(pattern, args);
	}
	
	public static class session {
		
		public static void login(Request req, User user)
		{
			// already started?
			Session sess = (Session) req.getAttribute("session");
			if (sess == null) {
				sess = Session.start(req);
				req.setAttribute("session", sess);
			}
			
			// destroy current session if already logged
			Boolean logged = (Boolean) sess.get("is_logged");
			if (logged != null) {
				sess.destroy();
				
				// new session
				sess = Session.start(req);
				req.setAttribute("session", sess);
			}
			
			sess.put("user", user);
		}
		
		public static void logout(Request req)
		{
			if (!isLogged(req)) { return; } // not logged, return
			Session sess = getSession(req);
			sess.put("is_logged", false);
		}
		
		public static Boolean isLogged(Request req)
		{
			Session sess = getSession(req);
			Boolean logged = (Boolean) sess.get("is_logged");
			if (logged == null) {
				return false;
			}
			return logged;
		}
		
		private static Session getSession(Request request)
		{
			// already started
			Session sess = (Session) request.getAttribute("session");
			if (sess != null) {
				return sess;
			}
			
			// not started...start!
			sess = Session.start(request);
			request.setAttribute("session", sess);
			return sess;
		}
	
		public static void end(Request request, Response response)
		{
			Session sess = (Session) request.getAttribute("session");
			
			// no session...do nothing
			if (sess == null) {
				return;
			}
			
			// call stop on started session
			sess.stop(request, response);
		}
	};
	
	
	public static class storage
	{
		private static boolean __init = false;
		private static void _init() throws ClassNotFoundException
		{
			if (!__init)
			{
				Class.forName("org.sqlite.JDBC");
				
				Connection conn = null;
				PreparedStatement stmt = null;
				try {
					conn = DriverManager.getConnection("jdbc:sqlite:/tmp/gitamin.sqlite3");
					String sql = "CREATE TABLE IF NOT EXISTS user (username string, email string, `password` string, name string, primary key(username)";
					stmt = conn.prepareStatement(sql);
					stmt.execute();
				} catch (SQLException e) {
					log.error("{}", e);
				}
				finally
				{
					Util.closeQuietly(stmt);
					Util.closeQuietly(conn);
				}
			}
		}
		
		private static Connection getConnection() throws ClassNotFoundException, SQLException
		{
			_init();
			Connection conn = DriverManager.getConnection("jdbc:sqlite:/tmp/gitamin.sqlite3");
			return conn;
		}
		
		public static User userAuth(String user, String pass) throws NotFoundException, DbException
		{
			Connection conn = null;
			PreparedStatement stmt = null;
			ResultSet res = null;
			try {
					conn = getConnection();
					stmt = conn.prepareStatement("SELECT * FROM user WHERE (username = ? or email = ?) AND `pasword` = ?");
					stmt.setString(1, user);
					stmt.setString(2, user);
					stmt.setString(3, Util.md5( pass ) );
					
					if ( !stmt.execute() ) {
						throw new NotFoundException("Record not found");
					}
					
					res = stmt.getResultSet();
					User usr = new User(res);
					return usr;
			}
			catch(Exception e)
			{
				throw new DbException(e);
			}
			finally
			{
				Util.closeQuietly(stmt);
				Util.closeQuietly(conn);
			}
		}
	};
}





