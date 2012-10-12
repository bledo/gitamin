package bledo.mvc.response;


import bledo.mvc.Request;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

public class Redirect extends AbstractResponse implements Response
{
	protected String _url;
	public Redirect(String url)
	{
		setStatus(302);
		_url = url;
	}
	
	@Override public Map<String, String> getHeaders(Request req)
	{
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("Location", _url);
		return map;
	}
	
	@Override
	public void printBody(Request req, HttpServletResponse resp) {
		// nothing to do
	}
}
