package bledo.gitamin.servlet;

import bledo.gitamin.VelocityResponse;
import bledo.mvc.BledoServlet;
import bledo.mvc.HttpError404;
import bledo.mvc.Request;
import bledo.mvc.response.Response;

public class BaseServlet extends BledoServlet
{
	private static final long serialVersionUID = 1L;
	
	/*
	@Override
	protected Response processRequest(Request request) throws Exception
	{
		Response resp = super.processRequest(request);
		return resp;
	}
	*/
	
	@Override
	protected Response processRequestError(Request request, Exception e) throws Exception
	{
		VelocityResponse vr = null;
		try
		{
			throw e;
		}
		catch(HttpError404 ex)
		{
			vr = VelocityResponse.newInstance(request, "_404.vm");
			vr.assign("ERROR", ex);
		}
		catch (Exception ex)
		{
			vr = VelocityResponse.newInstance(request, "_error.vm");
			vr.assign("ERROR", ex);
		}
		return vr;
	}
	
}
