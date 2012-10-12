/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bledo.mvc;

/**
 *
 * @author ricardo
 */
public class Cookie {
	protected String name;
	protected String value;
	protected String domain = "";
	protected boolean httpOnly = false;
	protected int maxAge = -1;
	protected String path = "/";
	protected boolean secure = false;

	public Cookie(String name, String value)
	{
		setName(name);
		setValue(value);
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Boolean getHttpOnly() {
		return httpOnly;
	}

	public void setHttpOnly(Boolean httpOnly) {
		this.httpOnly = httpOnly;
	}

	public Integer getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(Integer maxAge) {
		this.maxAge = maxAge;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Boolean getSecure() {
		return secure;
	}

	public void setSecure(Boolean secure) {
		this.secure = secure;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
