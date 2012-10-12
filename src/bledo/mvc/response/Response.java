/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bledo.mvc.response;

import bledo.mvc.Cookie;
import bledo.mvc.Request;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author ricardo
 */
public interface Response {
	public Map<String, String> getHeaders(Request req);
	public void printBody(Request req, HttpServletResponse resp) throws Exception;
	public List<Cookie> getCookies(Request req);
	public int getStatus();
}
