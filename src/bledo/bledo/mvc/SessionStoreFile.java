package bledo.mvc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.HashMap;

public class SessionStoreFile implements SessionStore
{
	private final static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SessionStoreFile.class);
	
	public static SessionStoreFile getInst()
	{
		String dir = System.getProperty("java.io.tmpdir") + File.separator + "bledo.session";
		log.info("");
		File file = new File(dir);
		boolean dirExists = true;
		
		if ( !file.exists() ) {
			log.warn("directory {} does not exists. will attempt to create", dir);
			if ( !file.mkdir() ) {
				log.error("could not create directory {}", dir);
				dirExists = false;
			}
			log.warn("create session directory {}", dir);
		} else if (!file.isDirectory()) {
			log.warn("file {} already exists and it is not a directory", dir);
			dirExists = false;
		} else if (!file.canRead()) {
			log.error("not enough permission to read session from directory {}", dir);
			dirExists = false;
		}
		
		// create store object
		SessionStoreFile store = null;
		if (dirExists) {
			store = new SessionStoreFile(dir);
		} else {
			store = new SessionStoreFile(null);
		}
		return store;
	}
	
	private String _tmp;
	private SessionStoreFile(String dir)
	{
		_tmp = dir;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> read(String sessid)
	{
		log.info("");
		if (_tmp == null) {
			return new HashMap<String, Object>();
		}
		
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			log.debug("reading session from {}{}{}", new Object[]{_tmp, File.separator, sessid});
			fis = new FileInputStream(_tmp + File.separator + sessid);
			ois = new ObjectInputStream(fis);
			return (HashMap<String, Object>) ois.readObject();
		} catch (FileNotFoundException e) {
			log.error("{}", e);
		} catch (IOException e) {
			log.error("{}", e);
		} catch (ClassNotFoundException e) {
			log.error("{}", e);
		} finally {
			if (fis != null) { try { fis.close(); } catch (IOException e) { } }
			if (ois != null) { try { ois.close(); } catch (IOException e) { } }
		}
		
		return new HashMap<String, Object>();
	}

	@Override
	public void write(String sessid, Map<String, Object> data) {
		log.info("");
		if (_tmp == null) {
			return;
		}
		
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		
		try {
			log.debug("writing session to {}{}{}", new Object[]{_tmp,File.separator,sessid});
			fos = new FileOutputStream(_tmp + File.separator + sessid);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(data);
		} catch (FileNotFoundException e) {
			log.error("{}", e);
		} catch (IOException e) {
			log.error("{}", e);
		} finally {
			if (fos != null) { try { fos.close(); } catch (IOException e) { } }
			if (oos != null) { try { oos.close(); } catch (IOException e) { } }
		}
	}

	@Override
	public void destroy(String sessid) {
		File file = new File(_tmp + File.separator + sessid);
		file.delete();
	}
	
}
