/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bledo;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ricardo
 */
public class Json extends HashMap<String, Object>
{

	public Json() {
	}

	public Json(Map<? extends String, ? extends Object> m) {
		super(m);
	}

	public Json(int initialCapacity) {
		super(initialCapacity);
	}

	public Json(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public String getString(String key)
	{
		return (String) get(key);
	}

}
