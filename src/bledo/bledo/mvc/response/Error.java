/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bledo.mvc.response;

import bledo.mvc.HttpError404;
import bledo.mvc.Request;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author rxr
 */
public class Error extends AbstractResponse implements Response
{
	public static final Logger log = LoggerFactory.getLogger(Error.class);

	protected Exception e;
	public Error(Request req , Exception ex)
	{
		e = ex;
		if (ex instanceof HttpError404)
		{
			setStatus(404);
		}
		else
		{
			setStatus(500);
		}
	}

	@Override
	public void printBody(Request req, HttpServletResponse resp) throws Exception
	{
		PrintWriter pw = resp.getWriter();
		log.debug("printingBody ... Errr");
		pw.print("Error : " + e.getMessage() + "<br/>");
		pw.print("<br>");
		e.printStackTrace( pw );
	}
}
