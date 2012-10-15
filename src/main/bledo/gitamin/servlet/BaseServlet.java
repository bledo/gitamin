package bledo.gitamin.servlet;

import java.util.HashMap;
import java.util.Map;
import bledo.gitamin.Gitamin;
import bledo.mvc.BledoServlet;
import bledo.mvc.Request;
import bledo.mvc.response.Response;

public class BaseServlet extends BledoServlet
{
	private static final long serialVersionUID = 1L;
	

	@Override
	protected Response processRequest(Request request) throws Exception
	{
		Response resp = super.processRequest(request);
		Gitamin.session.end(request, resp);
		return resp;
	}
	
	
	private void _add_notification(Request req, String type, String msg)
	{
		Map<String, String> msgs = (Map<String, String>) req.getAttribute("alert-messages");
		if (msgs == null)
		{
			msgs = new HashMap<String, String>();
		}
		msgs.put(msg, type);
	}
	
	protected void _error(Request req, String msg)
	{
		_add_notification(req, "alert-error", msg);
	}
	
	protected void _warning(Request req, String msg)
	{
		_add_notification(req, "alert-warning", msg);
	}
	
	protected void _info(Request req, String msg)
	{
		_add_notification(req, "alert-info", msg);
	}
	
	protected void _success(Request req, String msg)
	{
		_add_notification(req, "alert-success", msg);
	}
	
}
