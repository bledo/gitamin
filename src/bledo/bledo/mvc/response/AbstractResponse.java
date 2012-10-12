package bledo.mvc.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import bledo.mvc.Cookie;
import bledo.mvc.Request;



public abstract class AbstractResponse implements Response {
	
	protected Map<String, String> headers = new HashMap<String, String>();
	protected List<Cookie> _cookies = new ArrayList<Cookie>();
	public void putHeader(String name, String val) { headers.put(name, val); }
	@Override public Map<String, String> getHeaders(Request req) { return headers; }
	@Override public List<Cookie> getCookies(Request req) { return _cookies; }
	public void addCookie(Cookie c) { _cookies.add(c); }
	
	private int _status = 200;
	public void setStatus(int status) {
		_status = status;
	}
	
	@Override
	public int getStatus()
	{
		return _status;
	}
}
