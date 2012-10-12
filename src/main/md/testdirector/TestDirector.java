/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package md.testdirector;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qc.rest.examples.AuthenticateLoginLogoutExample;
import qc.rest.examples.UpdateExample;
import qc.rest.examples.infrastructure.Response;
import qc.rest.examples.infrastructure.RestConnector;

/**
 *
 * @author ricardo
 */
public class TestDirector
{
	private RestConnector con;
	private AuthenticateLoginLogoutExample login;
	//private String newCreatedResourceUrl;
	private String requirementsUrl;

	private Map<String, String> requestHeaders = new HashMap<String, String>();

	final static Logger log = LoggerFactory.getLogger(TestDirector.class);
	
	/*
	public static void main(String[] args) throws Exception
	{
		Auth auth = new Auth();
		TestDirector td = new TestDirector(auth);
		// public List<Entity> list(String fields, String order, int limit, int offset) throws Exception
		EntityResult list = td.list(null, null, 10, 1);
		for (Entity entity : list.result)
		{
			System.out.println( entity.getType() );
			System.out.println( "\t" + entity.get("id") );
		}
		//System.out.println(list);
		td.logout();

	}
	*/

	public static Auth authenticate(String username, String password) throws Exception
	{
		Auth auth = new Auth();
		auth.USERNAME = username;
		auth.PASSWORD = password;

		RestConnector con = new RestConnector(auth);
		AuthenticateLoginLogoutExample login = new AuthenticateLoginLogoutExample(con);
		if ( !login.login(auth) )
		{
			throw new Exception("Wront Username or Password");
		}

		return auth;
	}

	public TestDirector(Auth auth) throws Exception
	{
		this.con = new RestConnector(auth);
		this.login = new AuthenticateLoginLogoutExample(con);
		if ( !this.login.login(auth) )
		{
			throw new Exception("Wront Username or Password");
		}

		this.requirementsUrl = con.buildEntityCollectionUrl("defect");
		System.out.println(requirementsUrl);

		//CreateDeleteExample writeExample = new CreateDeleteExample();
		//newCreatedResourceUrl = writeExample.createEntity(requirementsUrl, writeExample.getEntityToPostXml());

		// request headers
		requestHeaders.put("Accept", "application/xml");
	}

	/**
	 * 
	 * @param fields id,name...
	 * @param order {id[DESC]}
	 * @param limit 10
	 * @param offset 1
	 * @return
	 * @throws Exception 
	 */
	public EntityResult list(String filter, String order, int limit, int offset) throws Exception
	{
		//Read a simple resource. This example is not an entity.
		//String resourceWeWantToRead = con.buildUrl("qcbin/rest/server");
		//String responseStr = con.httpGet(resourceWeWantToRead, null, requestHeaders).toString();
		
		//Query a collection of entities:
		StringBuilder b = new StringBuilder();

		//The query: where field name starts with 
		//b.append("?");
		if (filter == null)
		{
			filter = "{}";
		}
		//b.append("query={name[r*]}");
		b.append("query=").append(bledo.Util.urlEncode(filter));
		//b.append("query=").append(filter);

		//The fields to display: id, name
		//if (fields != null) { b.append("&fields=").append(fields); }

		//The sort order: descending by ID (highest ID first)
		if (order == null) {
			order = "{id[DESC]}";
		}
		b.append("&order-by=").append( bledo.Util.urlEncode(order) ); //{id[DESC]}");

		//Display 10 results
		b.append("&page-size=").append(limit);

		//Counting from the 1st result, inclusive
		b.append("&start-index=").append(offset);
		//log.debug("Test director request {}?{}", new Object[]{requirementsUrl, b.toString()});
		Response resp  = con.httpGet(requirementsUrl, b.toString(), requestHeaders);
		Exception ex = resp.getFailure();
		if (ex != null) {
			throw ex;
		}

		String listFromCollectionAsXml = resp.toString();

		//System.out.println("response for list requirements: " + listFromCollectionAsXml);

		//return listFromCollectionAsXml;

		JAXBContext ctx = JAXBContext.newInstance(EntityResult.class);
		Unmarshaller marshaller = ctx.createUnmarshaller();
		EntityResult res = (EntityResult) marshaller.unmarshal(new StringReader( listFromCollectionAsXml ));
		return res;

		/*
		//Now show that you can do something with that object
		List<Field> fields = entity.getFields().getField();
		System.out.print("listing fields from marshalled object: ");
		for (Field field : fields) {
			System.out.print(field.getName() + "=" + field.getValue() + ", ");
		}
		System.out.println("");
		//cleanup
		//writeExample.deleteEntity(newCreatedResourceUrl).toString().trim();
		* */
	}

	public Entity getEntity(int id) throws Exception
	{
		/*
		//Read the entity we generated in the above step. Perform a get operation on its URL.
		String postedEntityReturnedXml = con.httpGet(newCreatedResourceUrl, null, requestHeaders).toString();
		System.out.println("response for retrieving entity: " + postedEntityReturnedXml.trim());
		//xml -> class instance
		Entity entity = EntityMarshallingUtils.marshal(Entity.class, postedEntityReturnedXml);
		return entity;
		*/

		String listFromCollectionAsXml = con.httpGet(requirementsUrl+"/"+id, null, requestHeaders).toString();
		//System.out.println("response for list requirements: " + listFromCollectionAsXml);

		//return listFromCollectionAsXml;

		JAXBContext ctx = JAXBContext.newInstance(Entity.class);
		Unmarshaller marshaller = ctx.createUnmarshaller();
		Entity res = (Entity) marshaller.unmarshal(new StringReader( listFromCollectionAsXml ));
		return res;
	}

	public void update(Entity entity) throws JAXBException, Exception
	{
		UpdateExample example = new UpdateExample(con);

		JAXBContext ctx = JAXBContext.newInstance(Entity.class);
		Marshaller marshaller = ctx.createMarshaller();
		//Unmarshaller marshaller = ctx.createUnmarshaller();
		StringWriter writer =  new StringWriter();
		marshaller.marshal(entity, writer);
		String updateXml = writer.toString();
		log.debug("Update Xml: {}", updateXml);

		 requestHeaders.put("Content-Type", "application/xml");
		 Response put = con.httpPut(requirementsUrl+"/"+entity.get("id"), updateXml.getBytes(), requestHeaders);
		 requestHeaders.remove("Content-Type");
		 //return put;
		//String put = example.update(requirementsUrl+"/"+entity.get("id"), updateXml).toString();
	}

	public void logout() throws Exception
	{
		this.login.logout();
	}
}
