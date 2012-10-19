package bledo.mvc;

import bledo.mvc.response.Response;
import java.util.Map;

public class Session {
	private final static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Session.class);
	
	public static Session start(Request req)
	{
		return start(req, SessionStoreFile.getInst());
	}
	
	public static Session start(Request req, SessionStore store)
	{
		log.info("session.start");
		boolean isNew = false;
		String sessid = req.getCookie("BSID");
		
		if (sessid == null) {
			isNew = true;
			sessid = _gen_sess_id();
			log.debug("null sessid. generated : {}", sessid);
		} else if (sessid.isEmpty()) {
			isNew = true;
			sessid = _gen_sess_id();
			log.debug("empty sessid. generated : {}", sessid);
		}
		log.debug("Instantiating sessid : {}", sessid);
		return new Session(sessid , store, isNew);
	}
	
	private static String _gen_sess_id()
	{
		return java.util.UUID.randomUUID().toString();
	}
	
	
	private String sessid;
	private boolean isNew;
	private boolean hasChanged = false;
	private SessionStore store;
	private Map<String, Object> _data;
	
	private Session(String id, SessionStore store, boolean isNew)
	{
		this.sessid = id;
		this.isNew = isNew;
		this.store = store;
		_data = store.read(sessid);
	}
	
	public Object get(String key) {
		hasChanged = true;
		return _data.get(key);
	}
	public String getString(String key) {
		return (String) get(key);
	}
	public void put(String key, Object val) {
		hasChanged = true;
		_data.put(key, val);
	}
	
	
	public void stop(Request req, Response resp)
	{
		if (isNew) {
			Cookie c = new Cookie("BSID", sessid);
			resp.getCookies(req).add(c);
		}
		
		if (hasChanged)
		{
			store.write(sessid, _data);
		}
	}
	
	public boolean isNew()
	{
		return isNew;
	}
	
	public void destroy()
	{
		store.destroy(sessid);
	}
}








