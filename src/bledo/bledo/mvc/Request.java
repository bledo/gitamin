/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bledo.mvc;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author ricardo
 */
public interface Request extends HttpServletRequest
{
	
	/**
	 * Returns full request URI
	 * E.g.
	 * 	http://example.com/path/base/controller/action
	 * 
	 */
	public String getUri();
	public String getAction();
	public Map<String, Object> getParamMap();
	public String getParam(String k);
	public String getParam(String k, String def_val);
	public String getCookie(String k);
	public String getCookie(String k, String def_val);

	@Override
	public String getScheme();
	public String getHost();
	public int getPort();
}




