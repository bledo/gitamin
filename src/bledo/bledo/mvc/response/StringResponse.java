/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bledo.mvc.response;

import java.io.OutputStream;
import java.io.PrintWriter;

import bledo.mvc.Request;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;



/**
 *
 * @author ricardo
 */
public class StringResponse extends AbstractResponse implements Response {

	protected String out;

	public StringResponse(String out) { this.out = out; }
	@Override public void printBody(Request req, HttpServletResponse resp) throws IOException
	{
		PrintWriter pw = resp.getWriter();
		pw.print(out);
		pw.close();
	}
}
