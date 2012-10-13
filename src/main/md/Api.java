/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package md;

import bledo.rpc.json.JsonRpc;
import bledo.rpc.json.RpcServlet;
import java.util.Date;
import javax.servlet.annotation.WebServlet;
import md.testdirector.Entity;
import md.testdirector.TestDirector;

/**
 *
 * @author rxr
 */
@WebServlet("/Api")
public class Api extends RpcServlet
{
	private static final long serialVersionUID = 1L;
	
	@JsonRpc
	public boolean svnUpdateTd(
		String tdUser,
		String tdPass,
		String tdId,
		String svnUser,
		String comment,
		String status
	)
	{
		try
		{
			int id =  Integer.parseInt( tdId );

			md.testdirector.Auth auth = new md.testdirector.Auth();
			auth.USERNAME = tdUser;
			auth.PASSWORD = tdPass;
			TestDirector td = new TestDirector(auth);
			Entity entity = td.getEntity(id);
			if (entity == null) { return false; }
			entity.addComment(svnUser, new Date(), comment);
			if (status != null && !status.isEmpty()) {
				entity.set("status", status);
			}
			td.update(entity);

			Entity entity2 = td.getEntity(id);
			if ( ! entity2.get("status").equals(entity.get("status")) )
			{
				return false;
			}

			return true;
		}
		catch (Exception e)
		{
		}
		return false;
	}
}

