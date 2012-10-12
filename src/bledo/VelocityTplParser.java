package bledo;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VelocityTplParser implements TplParser
{
	public static final Logger log = LoggerFactory.getLogger(VelocityTplParser.class);
	
	private VelocityContext _context = new VelocityContext();

	@Override
	public void assign(String key, Object obj)
	{
		_context.put(key, obj);
	}
	
	/*
	 * private static int c = 1;
	 * @Override
	 * public String fetch(Request req, OutputStream os)
	 * {
	 * log.info("content view file : {}, {}",c++, view);
	 * 
	 * //log.debug("_context.get({}) = {}",_CONTENTTOKEN, _context.get(_CONTENTTOKEN));
	 * if (_context.get(_CONTENTTOKEN) == null && view != null && !view.isEmpty())
	 * {
	 * Template viewTpl = getTemplateInstance( view );
	 * StringWriter sw = new StringWriter();
	 * viewTpl.merge(_context, sw);
	 * _context.put(_CONTENTTOKEN, sw.toString());
	 * }
	 * 
	 * log.info("layout file : {}", layout);
	 * Template layoutTpl = getTemplateInstance(layout);
	 * PrintWriter pw = new PrintWriter(os);
	 * layoutTpl.merge(_context, pw);
	 * pw.close();
	 * }
	 */
	
	@Override
	public String fetch(String tpl)
	{
		StringWriter sw = new StringWriter();
		Template template = getTemplateInstance(tpl);
		template.merge(_context, sw);
		return sw.toString();
	}
	
	public static Template getTemplateInstance(String tpl)
	{
		VelocityEngine engine = _getEngine();
		Template template = engine.getTemplate(tpl);
		return template;
	}
	
	@Override
	public void evaluate(String tplName, String strTpl, Writer writer)
	{
		VelocityEngine engine = _getEngine();
		engine.evaluate(_context, writer, tplName, strTpl);
	}
	
	private static VelocityEngine veCp = null; //new VelocityEngine();
	private static VelocityEngine _getEngine()
	{
		if ( veCp == null) {
			
			veCp = new VelocityEngine();
			veCp.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			veCp.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			
			veCp.setProperty( RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute" );
			veCp.setProperty("runtime.log.logsystem.log4j.logger", "VelocityTplParser");
			/*
			 * veCp.setProperty("runtime.log.error.stacktrace", true);
			 * veCp.setProperty("runtime.log.info.stacktrace", true);
			 * veCp.setProperty("runtime.log.warn.stacktrace", true);
			 * veCp.setProperty("runtime.log.invalid.references", true);
			 * veCp.setProperty("runtime.references.strict", true);
			 * veCp.setProperty("velocimacro.arguments.strict", true);
			 */
			veCp.setProperty("macro.provide.scope.control", true);
			
			veCp.init();
		}
		
		return veCp;
	}
	
	/*
	 * private static boolean _veFileInit = false;
	 * private static VelocityEngine veFile = new VelocityEngine();
	 * public static Template getFileTpl(String tpl)
	 * {
	 * if (_veFileInit == false) {
	 * 
	 * veFile.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
	 * veFile.setProperty("file.resource.loader.path", "/home/ricardo/dev/newpp/src/main/webapp/WEB-INF/view");
	 * 
	 * veFile.init();
	 * _veFileInit = true;
	 * }
	 * 
	 * Template template = veFile.getTemplate(tpl);
	 * return template;
	 * }
	 */
}
