package bledo.gitamin.servlet;

import bledo.gitamin.VelocityResponse;
import bledo.mvc.Request;
import bledo.mvc.response.Response;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;


@WebServlet(name = "Index", urlPatterns = {"/Index/*"})
public class Index extends PrivateServlet
{
	private static final long serialVersionUID = 1L;

	public Response index(Request request) throws IOException
	{
		Process git = Runtime.getRuntime().exec("/usr/lib/git-core/git-http-backend");

		VelocityResponse resp = VelocityResponse.newInstance(request, getClass());
		return resp;
	}
}
