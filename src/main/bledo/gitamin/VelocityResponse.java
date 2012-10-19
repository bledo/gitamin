/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bledo.gitamin;

import java.util.Map;
import bledo.VelocityTplParser;
import bledo.mvc.Request;
import javax.servlet.http.HttpServletResponse;
import bledo.Util;


/**
 *
 * @author rxr
 */
public class VelocityResponse extends bledo.mvc.response.VelocityResponse
{
	public static VelocityResponse newInstance(Request req, String view)
	{
		// Get View
		String layout = "co/bledo/view/template.vm";
		VelocityResponse resp = new VelocityResponse(layout, "co/bledo/view/" + view);
		return resp;
	}

	public static VelocityResponse newInstance(Request req, Class<?> cls)
	{
		// Get View
		String view = null;
		String clsName = cls.getName();
		if (cls != null) {
			String[] clsNameParts = clsName.split("\\.");
			if ( clsNameParts.length > 0 )
			{
				view = "co/bledo/view/" + clsNameParts[ clsNameParts.length - 1 ] + "/" + req.getAction() + ".vm";
			}
		}

		String layout = "co/bledo/view/template.vm";
		VelocityResponse resp = new VelocityResponse(layout, view);
		return resp;
	}

	@Override
	public void printBody(Request req, HttpServletResponse resp) throws Exception
	{
		_CONTENTTOKEN = "CONTENT";
		
		// Menu bar
		Boolean logged_in = (Boolean) req.getSession().getAttribute("logged_in");
		if (logged_in == null) { logged_in = false; }
		VelocityTplParser vp = new VelocityTplParser();
		if (logged_in) {
			assign("NAVBAR", vp.fetch("co/bledo/view/_nav_bar.vm"));
		} else {
			assign("NAVBAR", vp.fetch("co/bledo/view/_nav_bar_login.vm"));
		}
		
		// Alert messages
		Map<String, String> alert_messages = Gitamin.session.getAlertMessages(req);
		vp.assign("_alert_messages", alert_messages);
		assign("ALERT_MESSAGES", vp.fetch("co/bledo/view/_alert_messages.vm"));
		
		
		assign("TITLE", "Gitamin");
		assign("request", req);
		
		//assign("auth", req.getSession().getAttribute("is_logged"));
		//assign("base", req.getContextPath());
		assign("util", Util.class);
		assign("gitamin", Gitamin.class);
		//String user = (String) req.getSession().getAttribute("username");
		//assign("username", user);
		//assign("mytickets", req.getContextPath() + "/Index/mytickets" );
		super.printBody(req, resp);
	}

	public VelocityResponse(String layout, String view) { super(layout, view); }
}
