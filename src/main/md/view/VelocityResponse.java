/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package md.view;

import bledo.Util;
import bledo.mvc.Request;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author rxr
 */
public class VelocityResponse extends bledo.mvc.response.VelocityResponse
{
	public static VelocityResponse newInstance(Request req, String view)
	{
		// Get View
		String layout = "md/view/template.vm";
		VelocityResponse resp = new VelocityResponse(layout, "md/view/" + view);
		return resp;
	}

	public static VelocityResponse newInstance(Request req, Class cls)
	{
		// Get View
		String view = null;
		String clsName = cls.getName();
		if (cls != null) {
			String[] clsNameParts = clsName.split("\\.");
			if ( clsNameParts.length > 0 )
			{
				view = "md/view/" + clsNameParts[ clsNameParts.length - 1 ] + "/" + req.getAction() + ".vm";
			}
		}

		String layout = "md/view/template.vm";
		VelocityResponse resp = new VelocityResponse(layout, view);
		return resp;
	}

	@Override
	public void printBody(Request req, HttpServletResponse resp) throws Exception
	{
		_CONTENTTOKEN = "CONTENT";
		assign("auth", req.getSession().getAttribute("is_logged"));
		assign("base", req.getContextPath());
		assign("util", Util.class);

		String user = (String) req.getSession().getAttribute("username");
		assign("username", user);
		//assign("mytickets", req.getContextPath() + "/Index/index?p=1&amp;l=50&amp;q=" + Util.urlEncode( "{owner[" + user + "]}") );
		assign("mytickets", req.getContextPath() + "/Index/mytickets" );
		super.printBody(req, resp);
	}


	public VelocityResponse(String layout, String view) { super(layout, view); }
}
