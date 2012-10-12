/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bledo.mvc;

import bledo.Util;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 *
 * @author ricardo
 */
public class HttpRequest extends HttpServletRequestWrapper implements Request
{
	private final static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(HttpRequest.class);

	protected String action;
	protected Map<String, Object> _params = new HashMap<String, Object>();
	protected Map<String, String> _cookies = new HashMap<String, String>();
	
	protected URL url;
	protected HttpServletRequest req;

	public HttpRequest(HttpServletRequest req, String defAction) throws MalformedURLException
	{
		super(req);

		//
		this.action = defAction;

		//
		StringBuffer sb = req.getRequestURL();
		String qstring = req.getQueryString();
		if (qstring != null) {
			sb.append(qstring);
		}
		this.url = new URL(sb.toString());
		
		//
		this.req = req;
		
		//
		_params.putAll(req.getParameterMap());

		//
		javax.servlet.http.Cookie[] cookies = req.getCookies();
		if (cookies!=null)
		{
			for (javax.servlet.http.Cookie c: req.getCookies())
			{
				_cookies.put(c.getName(), c.getValue());
			}
		}

		//
		_init();
		
	}

	/**
	 * Returns a parameter from the POST or GET
	 * 
	 * @param key
	 * @return the value from GET or POST method or null
	 */
	@Override
	public String getParam(String key)
	{
		return getParam(key, null);
	}

	/**
	 * Returns a parameter from the POST or GET
	 * 
	 * @param key
	 * @return the value from GET or POST method or defVal
	 */
	@Override
	public String getParam(String key, String defVal)
	{
		String ret = defVal;
		Object val = _params.get(key);
		if (val != null) {
			if (val instanceof String[]) {
				ret = ((String[])val)[0];
			}
			else if (val instanceof String)
			{
				ret = (String) val;
			}
		} 
		return ret;
	}
	
	@Override
	public Map<String, Object> getParamMap()
	{
		return _params;
	}

	@Override
	public String getAction() {
		return action;
	}


	private void _init()
	{
		String uri = req.getRequestURI();
		String contextPath = req.getContextPath();
		String servletPath = req.getServletPath();
		String path = Util.trim( uri.replaceFirst( contextPath + servletPath, ""), "/");
		String[] dirs = path.split("/");
		final int len = dirs.length;
		
		// action
		if (len > 0)
		{
			if (!dirs[0].isEmpty())
			{
				action = dirs[0];
			}
		}

		log.debug("action: {}", action);

		
		/*
		 * parameters
		 */
		int keyKey;
		int valKey;
		String val;
		for (int i = 1; i < len; i += 2)
		{
			keyKey = i;
			valKey = i + 1;
			
			if (valKey >= len)
			{
				val = "";
			}
			else
			{
				val = dirs[valKey];
			}
			_params.put(Util.urlDecode(dirs[keyKey]), Util.urlDecode(val) );
		}
	}

	@Override
	public String getHost() {
		return url.getHost();
	}

	@Override
	public int getPort() {
		return url.getPort();
	}

	/**
	 * Returns full request URI
	 * E.g.
	 * 	http://example.com/path/base/controller/action
	 * 
	 */
	@Override
	public String getUri() {
		
		String prot = req.getProtocol();
		int port = url.getPort();
		if ( (prot.equals("https") && port == 443) || (prot.equals("http") && port == 80))
		{
			// no port
			return prot+"://"+req.getServerName() + req.getRequestURI();
		}
		else
		{
			// port
			return prot+"://"+req.getServerName() + ":" + port + req.getRequestURI();
		}
	}

	@Override
	public String getCookie(String k) {
		return this.getCookie(k, null);
	}

	@Override
	public String getCookie(String k, String def_val) {
		String ret =  _cookies.get(k);
		if (ret == null) {
			return def_val;
		}
		return ret;
	}
}





