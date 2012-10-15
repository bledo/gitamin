package bledo.gitamin.servlet;

import javax.servlet.annotation.WebServlet; 
import bledo.gitamin.VelocityResponse;
import bledo.mvc.Request;
import bledo.mvc.response.Response;


@WebServlet(name = "Index", urlPatterns = {"/Index/*"})
public class Index extends Auth
{
	private static final long serialVersionUID = 1L;

	public Response index(Request request)
	{
		VelocityResponse resp = VelocityResponse.newInstance(request, getClass());
		return resp;
	}
}
