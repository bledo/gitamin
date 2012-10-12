package bledo.mvc.response;

import bledo.mvc.Request;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VelocityResponse extends AbstractResponse
{
	public static final Logger log = LoggerFactory.getLogger(VelocityResponse.class);
	
	private String layout = null;
	private String view = null;
	protected String _CONTENTTOKEN = "content";

	/**
	 * Constructor takes a layout file and view file
	 * 
	 * @param layout Main layout file used to render. E.g. view/template.vm
	 * @param view View to be embedded within layout. E.g. view/User/index.vm 
	 */
	public VelocityResponse(String layout, String view)
	{
		log.info("");
		setLayout(layout);
		setView(view);
	}
	
	public String getLayout() { return layout; }
	public void setLayout(String layout) { this.layout = layout; }
	public String getView() { return view; }
	public void setView(String view) { this.view = view; }
	
	private VelocityContext _context = new VelocityContext();
	public void assign(String key, Object obj)
	{
		_context.put(key, obj);
	}
	
	
	@Override
	public void printBody(Request req, HttpServletResponse resp) throws Exception
	{
		if (_context.get(_CONTENTTOKEN) == null && view != null && !view.isEmpty())
		{
			Template viewTpl = getTemplateInstance( view );
			StringWriter sw = new StringWriter();
			viewTpl.merge(_context, sw);
			_context.put(_CONTENTTOKEN, sw.toString());
		}
		
		log.info("layout file : {}", layout);
		Template layoutTpl = getTemplateInstance(layout);
		PrintWriter pw = resp.getWriter();
		layoutTpl.merge(_context, pw);
		pw.close();
	}
	
	public String fetch(String tpl)
	{
		StringWriter sw = new StringWriter();
		Template template = getTemplateInstance(tpl);
		template.merge(_context, sw);
		return sw.toString();
	}
	
	
	private static VelocityEngine ve = new VelocityEngine();
	private static boolean _init = false;
	public static Template getTemplateInstance(String tpl)
	{
		if (_init == false) {
			/*
			 * ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
			 * ve.setProperty("file.resource.loader.path", "/home/ricardo/dev/newpp/src/main/webapp/WEB-INF/view");
			 */
			
			ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			
			ve.setProperty( RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute" );
			ve.setProperty("runtime.log.logsystem.log4j.logger", "VelocitytplParser");
			
			ve.init();
			_init = true;
		}
		
		log.info("getting template {}", tpl);
		Template template = ve.getTemplate(tpl);
		return template;
	}
}
