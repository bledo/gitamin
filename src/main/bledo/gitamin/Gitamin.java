package bledo.gitamin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import bledo.Util;
import bledo.gitamin.db.DbException;
import bledo.gitamin.db.NotFoundException;
import bledo.gitamin.db.User;
import bledo.mvc.Request;

public class Gitamin
{
	private static final Logger log = LoggerFactory.getLogger(Gitamin.class);
	
	public static String _(Request req, String key, Object...args)
	{
		ResourceBundle rb = (ResourceBundle) req.getAttribute( Keys.req_attr_resource_bundle );
		if (rb == null) {
			rb = ResourceBundle.getBundle(Keys.resource_bundle, req.getLocale());
			req.setAttribute(Keys.req_attr_resource_bundle, rb);
		}
		
		String pattern = rb.getString(key);
		return MessageFormat.format(pattern, args);
	}
	
	private static void _setAlertMsg(Request req, String type, String msg)
	{
		HttpSession sess = req.getSession(true);
		@SuppressWarnings("unchecked")
		Map<String, String> msgs = (Map<String, String>) sess.getAttribute( Keys.session_alert );
		if (msgs == null)
		{
			msgs = new HashMap<String, String>();
			sess.setAttribute( Keys.session_alert, msgs);
		}
		msgs.put(msg, type);
	}
	
	public static void alertError(Request req, String msg) {
		_setAlertMsg(req, Keys.alert_error, msg);
	}
	
	public static void alertWarning(Request req, String msg) {
		_setAlertMsg(req, Keys.alert_warning, msg);
	}
	
	public static void alertInfo(Request req, String msg) {
		_setAlertMsg(req, Keys.alert_info, msg);
	}
	
	public static void alertSuccess(Request req, String msg) {
		_setAlertMsg(req, Keys.alert_success, msg);
	}
	
	
	
	public static class config {
		
		private static Properties _props = null;
		private static String getProp(String key)
		{
			if (_props == null)
			{
				try {
					_props = Util.loadProperties("gitamin");
				} catch (IOException e) {
					_props = new Properties();
					e.printStackTrace();
				}
			}
			return _props.getProperty(key);
		}
		
		public static String getGitRepositoriesPath()
		{
			return getProp(Keys.git_repositories_paths);
		}

		public static int getGitSshPort()
		{
			return 2222;
		}
		
		public static String getGitExportAll()
		{
			return getProp(Keys.git_export_all);
		}
	};
	
	
	public static class session {
		
		public static void login(Request req, User user)
		{
			HttpSession sess = req.getSession(true);
			sess.setAttribute(Keys.session_user, user);
			sess.setAttribute(Keys.session_is_logged, new Boolean(true));
		}
		
		public static void logout(Request req)
		{
			if (!isLogged(req)) { return; } // not logged, return
			req.getSession(true).setAttribute("is_logged", false);
		}
		
		public static Boolean isLogged(Request req)
		{
			HttpSession sess =  req.getSession(true);
			Boolean logged = (Boolean) sess.getAttribute("is_logged");
			if (logged == null) {
				return false;
			}
			return logged;
		}
		
		public static void setWelcomeUrl(Request req, String uri)
		{
			HttpSession sess = req.getSession(true);
			sess.setAttribute(Keys.session_welcomepage, uri);
		}
		
		public static String getWelcomeUrl(Request req)
		{
			HttpSession sess = req.getSession(true);
			String uri = (String) sess.getAttribute(Keys.session_welcomepage);
			if (uri == null) {
				uri = req.getContextPath() + Keys.default_welcome_url;
			} else {
				sess.setAttribute(Keys.session_welcomepage, null);
			}
			return uri;
		}
		
		public static Map<String, String> getAlertMessages(Request req)
		{
			HttpSession sess = req.getSession(true);
			
			@SuppressWarnings("unchecked")
			Map<String, String> msgs = (Map<String, String>) sess.getAttribute( Keys.session_alert );
			if (msgs == null)
			{
				msgs = new HashMap<String, String>();
			}
			sess.setAttribute(Keys.session_alert, new HashMap<String, String>());
			
			return msgs;
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
					String sql = "CREATE TABLE IF NOT EXISTS user (username string, email string, `password` string, name string, active number, primary key(username))";
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
					stmt = conn.prepareStatement("SELECT * FROM user WHERE (username = ? or email = ?) AND `password` = ?");
					stmt.setString(1, user);
					stmt.setString(2, user);
					stmt.setString(3, Util.md5( pass ) );
					
					if ( !stmt.execute() ) {
						throw new NotFoundException( "Record not found" );
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





