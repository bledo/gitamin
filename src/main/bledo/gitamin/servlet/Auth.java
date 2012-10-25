package bledo.gitamin.servlet;

import bledo.gitamin.Gitamin;
import static bledo.gitamin.Gitamin._;
import bledo.gitamin.VelocityResponse;
import bledo.gitamin.db.DbException;
import bledo.gitamin.db.NotFoundException;
import bledo.gitamin.db.User;
import bledo.mvc.Cookie;
import bledo.mvc.Request;
import bledo.mvc.response.Redirect;
import bledo.mvc.response.Response;
import javax.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "Auth", urlPatterns = {"/Auth/*"})
public class Auth extends BaseServlet
{
	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(Auth.class);
	
	public Response index(Request req)
	{
		return login(req);
	}
	
	public final Response login(Request req)
	{
		VelocityResponse resp = VelocityResponse.newInstance(req, "Auth/login.vm");
		
		resp.assign("username", req.getParam("username",""));
		
		String remember = req.getCookie("login_remember","0");
		if ("1".equals(remember)) {
			resp.assign("checked", "checked");
		} else {
			resp.assign("checked", "unchecked");
		}
		
		return resp;
	}
	
	public final Response dologin(Request req)
	{
		Boolean isLogged = Gitamin.session.isLogged(req);
		if (isLogged)
		{
			return new Redirect(req.getContextPath() + "/Index");
		}
		
		User user;
		try {
			String username = req.getParam("username");
			String password = req.getParam("password");
			String remember = req.getParam("login_remember", "0");
			user = Gitamin.storage.userAuth(username, password);
			if (!user.active)
			{
				Gitamin.alertError(req, _(req, "login.inactive.account"));
				return login(req);
			}
			else
			{
				Redirect resp = new Redirect( Gitamin.session.getWelcomeUrl(req) ); 
				Gitamin.session.login(req, user);  // login
				
				// remember button is clicked
				if ("1".equals(remember))
				{
					Cookie cookie = new Cookie("login_remember", "1");
					cookie.setPath(req.getContextPath() + "/Login/");
					resp.addCookie(cookie);
				}
				return resp;
			}
		} catch (DbException e) {
			log.error("{}", e);
			Gitamin.alertError(req, _(req, "login.auth.internal.error"));
			return login(req);
		} catch (NotFoundException e) {
			log.error("{}", e);
			Gitamin.alertError(req, _(req, "login.auth.error"));
			return login(req);
		}
	}
}
