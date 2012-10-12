package bledo.mvc;

import java.util.Map;

public interface SessionStore {
	public Map<String, Object> read(String sessid);
	public void destroy(String sessid);
	public void write(String sessid, Map<String, Object> data);
}
