/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package md;

import bledo.mvc.BledoServlet;
import bledo.mvc.Request;
import bledo.mvc.response.Redirect;
import bledo.mvc.response.Response;

/**
 *
 * @author ricardo
 */
public class BaseServlet extends BledoServlet
{
	@Override
	public Response processRequest(Request request) throws Exception
	{
		md.testdirector.Auth auth = (md.testdirector.Auth) request.getSession().getAttribute("testDirectorAuth");
		Boolean is_logged = (Boolean) request.getSession().getAttribute("is_logged");
		if (auth == null || is_logged == null || is_logged != true)
		{
			return new Redirect(request.getContextPath() + "/Auth");
		}
		return super.processRequest(request);
	}
}
