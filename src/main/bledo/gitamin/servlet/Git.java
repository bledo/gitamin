package bledo.gitamin.servlet;

import bledo.gitamin.Gitamin;
import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

@WebServlet(name = "Auth", urlPatterns = {"/git/*"})
public class Git extends org.eclipse.jgit.http.server.GitServlet
{
	private static final long serialVersionUID = 1L;
	
	//base-path
	@Override
	public void init(final ServletConfig config) throws ServletException
	{
		super.init(
			new ServletConfig() {
				@Override
				public String getServletName() {
					return config.getServletName();
				}
				
				@Override
				public ServletContext getServletContext() {
					return config.getServletContext();
				}
				
				@Override
				public Enumeration<String> getInitParameterNames() {
					return config.getInitParameterNames();
				}
				
				@Override
				public String getInitParameter(String arg0) {
					if ("base-path".equals(arg0))
					{
						return Gitamin.config.getGitRepositoriesPath();
					}
					else if ("eport-all".equals(arg0))
					{
						return Gitamin.config.getGitExportAll();
					}
					
					return config.getInitParameter(arg0);
				}
			}
		);
	}
}
