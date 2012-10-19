package bledo.gitamin;

public class Keys
{
	/**
	 * Resource bundle name.  The name of the resource bundle properties file
	 */
	public static String default_welcome_url = "/Index";
	
	/**
	 * Resource bundle name.  The name of the resource bundle properties file
	 */
	public static String resource_bundle = "Gitamin_messages";
	
	/**
	 * key to store resource bundle in request attributes
	 */
	public static String req_attr_resource_bundle = "resource_bundle";
	
	/**
	 * Session key to store welcome URL when user tries to 
	 * access URL without a valid session
	 */
	public static String session_welcomepage = "gitamin.welcomepage";
	
	/**
	 * Session key for user object
	 */
	public static String session_user = "user";
	
	/**
	 * Session key for logged flag
	 */
	public static String session_is_logged = "is_logged";
	
	
	/**
	 * Session key to store alert messages
	 */
	public static String session_alert = "alert-messages";
	
	public static String alert_error = "alert-error";
	public static String alert_warning = "alert-warning";
	public static String alert_info = "alert-info";
	public static String alert_success = "alert-success";
}
