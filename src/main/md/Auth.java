/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package md;

import bledo.mvc.BledoServlet;
import bledo.mvc.Cookie;
import bledo.mvc.Request;
import bledo.mvc.response.Redirect;
import bledo.mvc.response.Response;
import javax.servlet.annotation.WebServlet;
import md.view.VelocityResponse;

/**
 *
 * @author rxr
 */
@WebServlet(name = "Auth", urlPatterns = {"/Auth/*"})
public class Auth extends BledoServlet
{
	public Response index(Request req)
	{
		VelocityResponse resp = VelocityResponse.newInstance(req, "Login/_login.vm");
		resp.assign("username", req.getCookie("username"));
		return resp;
	}

	public Response doLogin(Request req)
	{
		String user = req.getParam("username");
		String pass = req.getParam("password");

		Response resp;

		try
		{
			md.testdirector.Auth auth = md.testdirector.TestDirector.authenticate(user, pass);

			req.getSession().setAttribute("is_logged", true);
			req.getSession().setAttribute("username", user);
			//req.getSession().setAttribute("password", pass);
			String myTicketsUrl = req.getContextPath() + "/Index/mytickets";
			resp = new Redirect(myTicketsUrl);
			((Redirect)resp).addCookie(new Cookie("username", user)); // putHeader("username", user);

			req.getSession().setAttribute("testDirectorAuth", auth);
		}
		catch (Exception e)
		{
			resp = index(req);
			((VelocityResponse)resp).addCookie(new Cookie("username", user)); //putHeader("username", user);
			((VelocityResponse)resp).assign("error", e.getMessage());
		}
		return resp;
	}

	public Response logout(Request req)
	{
		req.getSession().setAttribute("is_logged", false);
		return new Redirect(req.getContextPath()+"/Auth/index");
	}
}
