package bledo;

import bledo.mvc.Request;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//@Log
public class Util {

	/**
	 * PHP version of trim
	 * 
	 * @param str String to trim
	 * @param charlist List of characters to trim
	 * @return
	 */
	public static String trim(String str, String charlist)
	{
		// NULL
		if (str == null) {
			return "";
		}
		// empty
		if (str.length() < 1) {
			return str;
		}

		StringBuilder sb = new StringBuilder(str);

		// remove characters from the begining of string
		while (charlist.indexOf( sb.charAt(0)) != -1)
		{
			sb.delete(0, 1);
			if (sb.length() < 1) { return ""; }
		}

		// remove characters from the end of the string
		if (sb.length() > 0)
		{
			int pos = sb.length() - 1;
			char c = sb.charAt( pos );
			while (charlist.indexOf(c) != -1)
			{
				sb.deleteCharAt(pos);
				pos = sb.length() - 1;
				if (pos < -1) { return ""; }
				c = sb.charAt( pos );
			}
		}

		return sb.toString();
	}
	public static String trim(String str)
	{
		return trim(str, " \n\t\r\0");
	}
	
	
	/**
	 * Loads a class and returns an object of the class type
	 * 
	 * @param className full name of the class to load
	 * @param params Constructor Parameters
	 * @return Object
	 * @throws Exception
	 */
	public static Object loadClass(String className, Object[] params)
	throws IllegalAccessException, InstantiationException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException
	{
		Class<?> c = Class.forName(className);
		return loadClass(c, params);
	}
	
	/**
	 * Loads a class and returns an object of the class type
	 * 
	 * @param className full name of the class to load
	 * @param params Constructor Parameters
	 * @return Object
	 * @throws Exception
	 */
	public static Object loadClass(Class<?> cls, Object[] params)
	throws IllegalAccessException, InstantiationException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException
	{
			@SuppressWarnings("rawtypes")
			Class[] clsParams = new Class[params.length];
			
			for (int i = 0; i < params.length; i++)
			{
				clsParams[i] = params[i].getClass();
			}
		
			@SuppressWarnings("rawtypes")
			java.lang.reflect.Constructor constructor = cls.getConstructor(clsParams);
			Object o = constructor.newInstance(new Object[0]);
			return o;
	}
	
	public static void closeQuietly(Closeable obj)
	{
		if (obj == null) { return; }
		
		try {
			obj.close();
		} catch (IOException e) {
		}
	}
	public static void closeQuietly(Connection obj)
	{
		if (obj == null) { return; }
		try {
			obj.close();
		} catch (SQLException e) { }
	}
	public static void closeQuietly(Statement obj)
	{
		if (obj == null) { return; }
		try {
			obj.close();
		} catch (SQLException e) { }
	}
	
	
	public static String urlEncode(String str)
	{
		try {
			return java.net.URLEncoder.encode(str, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
	
	public static String urlDecode(String str)
	{
		try {
			return java.net.URLDecoder.decode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
	
	public static String strReplace(String find, String rpl, String subject)
	{
		return strReplace(new String[]{find}, rpl, subject);
	}
	
	public static String strReplace(String[] find, String rpl, String subject)
	{
		StringBuilder sb = new StringBuilder(subject);
		for (int i = 0; i < find.length; i++)
		{
			int start = sb.indexOf(find[i]);
			while(start > -1)
			{
				sb.replace(start, start + rpl.length(), subject);
				start = sb.indexOf(find[i]);
			}
		}
		
		return sb.toString();

	}


	public static String url(String contextPath, String controller, String action, Map<String, String> params)
	{
		StringBuilder sb = new StringBuilder( contextPath );
		sb.append("/" );
		sb.append( controller );
		sb.append( "/" );
		sb.append( action );

		for (String key : params.keySet())
		{
			sb.append( "/" );
			sb.append( Util.urlEncode(key) );
			sb.append( "/" );
			sb.append( Util.urlEncode( params.get(key) ) );
		}

		return sb.toString();
	}

	protected static MessageDigest md5 = null;
	public static String md5(String str)
	{
		if (md5 == null) {
			try {
				md5 = MessageDigest.getInstance("MD5");
			} catch(NoSuchAlgorithmException e) {
				return "";
			}
		}

		// if null
		if (str == null) {
			str = "";
		}

		md5.reset();
		md5.update(str.getBytes());
		byte messageDigest[] = md5.digest();
		BigInteger bi = new BigInteger(1, messageDigest);
		String md5Str = String.format("%0" + (messageDigest.length << 1) + "X", bi);
		return md5Str.toLowerCase();
	}

	protected static MessageDigest sha1 = null;
	public static String sha1(String str)
	{
		if (sha1 == null) {
			try {
				sha1 = MessageDigest.getInstance("SHA1");
			} catch(NoSuchAlgorithmException e) {
				return "";
			}
		}

		// if null
		if (str == null) {
			str = "";
		}

		sha1.reset();
		sha1.update(str.getBytes());
		byte messageDigest[] = sha1.digest();
		BigInteger bi = new BigInteger(1, messageDigest);
		String sha1Str = String.format("%0" + (messageDigest.length << 1) + "X", bi);
		return sha1Str.toLowerCase();
	}


	/*
	public static SimpleDateFormat dateFormatter(Request req)
	{
		String formatStr = (String) req.getProp("DATEFORMAT");
		if (formatStr == null) {
			formatStr = "MM/dd/yyyy";
		}
		return new SimpleDateFormat(formatStr);
	}

	public static String dateFormat(Request req, Date date)
	{
		return dateFormatter(req).format(date);
	}
	*/

	public static Date dateParse(Request req, String dateStr)
	{
		return dateParse(req, dateStr, "MM/dd/yyyy");
	}

	public static Date dateParse(Request req, String dateStr, String format)
	{
		Date date;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			date = sdf.parse(dateStr);
		} catch (ParseException ex) {
			date = new Date();
		}

		return date;
	}

	
	public static int rand(int min, int max)
	{
		return min + ((int) ((Math.random() * ((max - min) + 1))));
	}
	
	public static String escapeHtml(String str)
	{
		return org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(str);
	}
	public static String nl2br(String str)
	{
		if (str == null) { str = ""; }
		str = str.replace("\r\n", "\n");
		str = str.replace("\r", "\n");
		str = str.replace("\n", "\n<br/>");
		return str;
	}

	public static String substr(String str, int start, int max)
	{
		if (str.length() >= max)
		{
			return str.substring(start, max);
		}
		return str;
	}
	
	
	 /**
     * Looks up a resource named 'name' in the classpath. The resource must map
     * to a file with .properties extention. The name is assumed to be absolute
     * and can use either "/" or "." for package segment separation with an
     * optional leading "/" and optional ".properties" suffix. Thus, the
     * following names refer to the same resource:
     * <pre>
     * some.pkg.Resource
     * some.pkg.Resource.properties
     * some/pkg/Resource
     * some/pkg/Resource.properties
     * /some/pkg/Resource
     * /some/pkg/Resource.properties
     * </pre>
     * 
     * @param name classpath resource name [may not be null]
     * @param loader classloader through which to load the resource [null
     * is equivalent to the application loader]
     * 
     * @return resource converted to java.util.Properties [may be null if the resource was not found and THROW_ON_LOAD_FAILURE is false]
	 * @throws IOException 
     */
    public static Properties loadProperties (String name, ClassLoader loader) throws IOException
    {
        if (name.startsWith ("/")) {
		    name = name.substring (1);
	    }
            
        if (name.endsWith (SUFFIX)) {
		    name = name.substring (0, name.length () - SUFFIX.length ());
	    }
        
        Properties result = null;
        
        InputStream in = null;
        try
        {
            if (loader == null) {
			loader = ClassLoader.getSystemClassLoader ();
		}
            
            name = name.replace ('.', '/');
                
            if (! name.endsWith (SUFFIX)) {
			name = name.concat (SUFFIX);
		}
                                
            // Returns null on lookup failures:
            in = loader.getResourceAsStream (name);
            if (in != null)
            {
                result = new Properties ();
                result.load (in); // Can throw IOException
            }
        }
        finally
        {
            if (in != null) {
			try { in.close (); } catch (Throwable ignore) {}
		}
        }
        
        
        return result;
    }
    
    /**
     * A convenience overload of {@link #loadProperties(String, ClassLoader)}
     * that uses the current thread's context classloader.
     * @throws IOException 
     */
    public static Properties loadProperties (final String name) throws IOException
    {
        return loadProperties (name,
            Thread.currentThread ().getContextClassLoader ());
    }
    private static final String SUFFIX = ".properties";


    public static Json jsonDecode(String jsonStr)
    {
	    Json json = new Json();
	    try {
		    JSONObject parser = new JSONObject(jsonStr);
		    Iterator it = parser.keys();
		    while (it.hasNext())
		    {
			    String key = (String) it.next();
			    json.put(key, _toJsonValue(parser.get(key)));
		    }
	    } catch (JSONException ex) {
	    }
	    return json;
    }

    private static Object _toJsonValue(Object obj)
    {
	    if (obj instanceof JSONObject)
	    {
		    JSONObject parser = (JSONObject) obj;
		    Json json = new Json();
		    Iterator it = parser.keys();
		    while (it.hasNext())
		    {
			    String key = (String) it.next();
			    try {
				    Object val =  parser.get(key);
				    json.put(key, _toJsonValue(val));
			    } catch (JSONException ex) { }
		    }
		    return json;
	    }
	    else if  (obj instanceof JSONArray)
	    {
		    ArrayList<Object> list = new ArrayList<Object>();
		    JSONArray jlist = (JSONArray) obj;
		    for (int i = 0; i < jlist.length(); i++)
		    {
			    try {
				    Object val = jlist.get(i);
				    list.add(_toJsonValue(val));
			    } catch (JSONException ex) { }
		    }
		    return list;
	    }

	    return obj;
    }

	public static boolean isEmpty(String str) {
		if (str == null) {
			return true;
		}

		if ("".equals(str)) {
			return true;
		}

		return false;
	}
}
