/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bledo.mvc;

import bledo.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

/**
 *
 * @author ricardo
 */
public class FakeRequest implements Request
{
	protected String strUrl;
	protected URL url;
	protected String contextPath;
	protected String servletPath;
	protected String action;
	protected Map<String, Object> _params = new HashMap<String, Object>();
	protected Map<String, String> _cookies = new HashMap<String, String>();

	public FakeRequest(String url, String contextPath, String servletPath, String defAction) throws MalformedURLException
	{
		this.strUrl = url;
		this.url = new URL(url);
		this.action = defAction;
		this.contextPath = contextPath;
		this.servletPath = contextPath;
		_init();
	}
	private void _init()
	{
		String uri = url.getPath();
		String path = Util.trim( uri.replaceFirst( contextPath + servletPath, ""), "/");
		String[] dirs = path.split("/");
		final int len = dirs.length;
		
		// action
		if (len > 0)
		{
			if (!dirs[0].isEmpty())
			{
				action = dirs[0];
			}
		}

		
		/*
		 * parameters
		 */
		int keyKey;
		int valKey;
		String val;
		for (int i = 1; i < len; i += 2)
		{
			keyKey = i;
			valKey = i + 1;
			
			if (valKey >= len)
			{
				val = "";
			}
			else
			{
				val = dirs[valKey];
			}
			_params.put(Util.urlDecode(dirs[keyKey]), Util.urlDecode(val) );
		}
	}

	@Override
	public String getUri() {
		return strUrl;
	}

	@Override
	public String getAction() {
		return action;
	}

	@Override
	public Map<String, Object> getParamMap() {
		return _params;
	}

	@Override
	public String getParam(String k) {
		return getParam(k, null);
	}

	@Override
	public String getParam(String k, String def_val) {
		String val =  (String) _params.get(k);
		if (val == null) {
			val = def_val;
		}
		return val;
	}

	public void setParam(String k, String val) {
		_params.put(k, val);
	}

	@Override
	public String getCookie(String k) {
		return getCookie(k, null);
	}

	@Override
	public String getCookie(String k, String def_val) {
		String val =  (String) _cookies.get(k);
		if (val == null) {
			val = def_val;
		}
		return val;
	}

	@Override
	public String getScheme() {
		return url.getProtocol();
	}

	@Override
	public String getHost() {
		return url.getHost();
	}

	@Override
	public int getPort() {
		return url.getPort();
	}

	@Override
	public String getAuthType() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Cookie[] getCookies() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public long getDateHeader(String string) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getHeader(String string) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Enumeration<String> getHeaders(String string) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getIntHeader(String string) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getMethod() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getPathInfo() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getPathTranslated() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getContextPath() {
		return contextPath;
	}

	@Override
	public String getQueryString() {
		return url.getQuery();
	}

	@Override
	public String getRemoteUser() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isUserInRole(String string) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Principal getUserPrincipal() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getRequestedSessionId() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getRequestURI() {
		return url.getPath();
	}

	@Override
	public StringBuffer getRequestURL() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getServletPath() {
		return servletPath;
	}

	@Override
	public HttpSession getSession(boolean bln) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public HttpSession getSession() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean authenticate(HttpServletResponse hsr) throws IOException, ServletException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void login(String string, String string1) throws ServletException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void logout() throws ServletException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Part getPart(String string) throws IOException, ServletException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Object getAttribute(String string) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getCharacterEncoding() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setCharacterEncoding(String string) throws UnsupportedEncodingException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getContentLength() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getContentType() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getParameter(String string) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Enumeration<String> getParameterNames() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String[] getParameterValues(String string) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getProtocol() {
		return url.getProtocol();
	}

	@Override
	public String getServerName() {
		return url.getHost();
	}

	@Override
	public int getServerPort() {
		return url.getPort();
	}

	@Override
	public BufferedReader getReader() throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getRemoteAddr() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getRemoteHost() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setAttribute(String string, Object o) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void removeAttribute(String string) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Locale getLocale() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Enumeration<Locale> getLocales() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isSecure() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String string) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getRealPath(String string) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getRemotePort() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getLocalName() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getLocalAddr() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getLocalPort() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ServletContext getServletContext() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public AsyncContext startAsync(ServletRequest sr, ServletResponse sr1) throws IllegalStateException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isAsyncStarted() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isAsyncSupported() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public AsyncContext getAsyncContext() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public DispatcherType getDispatcherType() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
