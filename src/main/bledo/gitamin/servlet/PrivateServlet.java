package bledo.gitamin.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bledo.gitamin.Gitamin;
import bledo.mvc.Request;
import bledo.mvc.response.Redirect;
import bledo.mvc.response.Response;

public class PrivateServlet extends BaseServlet
{
	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(Auth.class);

	@Override
	protected Response processRequest(Request req) throws Exception
	{
		Response resp = null;
		Boolean isLogged = Gitamin.session.isLogged(req);
		if (!isLogged)
		{
			log.error("{} requires authentication...redirected to Auth/login", req.getUri());
			Gitamin.session.setWelcomeUrl(req, req.getUri());
			Gitamin.alertError(req, Gitamin._(req, "auth.required"));
			resp = new Redirect(req.getContextPath() + "/Auth");
		}
		else
		{
			resp = super.processRequest(req);
		}
		
		return resp;
	}
	
}
