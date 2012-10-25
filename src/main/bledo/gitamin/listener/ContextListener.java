package bledo.gitamin.listener;

import bledo.gitamin.Gitamin;
import java.io.IOException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.apache.sshd.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class ContextListener implements ServletContextListener
{
	private static final Logger log = LoggerFactory.getLogger(ContextListener.class);
	private SshServer sshd = null;


	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try {
			//
			sshd = SshServer.setUpDefaultServer();
			
			//
			int sshPort = Gitamin.config.getGitSshPort();
			log.debug("ssh server port : {}", sshPort);
			sshd.setPort(sshPort);
			
			sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));
			
			//
			String sshShell = "/usr/bin/git-shell";
			log.debug("ssh server shell : {}", sshShell);
			sshd.setShellFactory(new ProcessShellFactory(new String[] { sshShell }));
			
			//
			sshd.setPasswordAuthenticator(new GitaminSshPassAuth());
			
			//
			log.info("starting ssh server...");
			sshd.start();
			log.info("...SSH Started!");
		} catch (IOException e) {
			log.error("error starting ssh shell : {}", e);
		}
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0)
	{
		try {
			if (sshd != null) {
				log.info("stopping ssh server...");
				sshd.stop();
				log.info("...ssh server stopped!");
			}
		} catch (InterruptedException e) {
			log.error("{}", e);
		}
	}
}
