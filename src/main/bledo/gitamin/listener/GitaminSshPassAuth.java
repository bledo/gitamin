package bledo.gitamin.listener;

import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bledo.gitamin.Gitamin;
import bledo.gitamin.db.DbException;
import bledo.gitamin.db.NotFoundException;

public class GitaminSshPassAuth implements PasswordAuthenticator
{
	private static final Logger log = LoggerFactory.getLogger(GitaminSshPassAuth.class);
	
	@Override
	public boolean authenticate(String username, String password, ServerSession sess) {
		try {
			Gitamin.storage.userAuth(username, password);
			return true;
		} catch (NotFoundException | DbException e) {
			log.error("Authentication Error : {}", e);
		}
		return false;
	}

}
