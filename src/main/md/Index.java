/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package md;

import bledo.Json;
import bledo.Util;
import bledo.mvc.Request;
import bledo.mvc.response.Redirect;
import bledo.mvc.response.Response;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import md.testdirector.Entity;
import md.testdirector.EntityResult;
import md.testdirector.TestDirector;
import md.view.VelocityResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author rxr
 */
@WebServlet(name = "Index", urlPatterns = {"/Index/*"})
public class Index extends BaseServlet
{
	private final static Logger log = LoggerFactory.getLogger(Index.class);

	public static String def_limit = "20";

	private TestDirector _getTd(Request req) throws Exception
	{
		md.testdirector.Auth auth = (md.testdirector.Auth) req.getSession().getAttribute("testDirectorAuth");
		/*
		if (auth == null)
		{
			auth = new md.testdirector.Auth();
			auth.USERNAME = (String) req.getSession().getAttribute("username");
			auth.PASSWORD = (String) req.getSession().getAttribute("password");
			req.getSession().setAttribute("testDirectorAuth", auth);
		}
		//log.debug("{}", auth);
		*/
		return new TestDirector(auth);
	}

	public Response index(Request req) throws Exception
	{
		return mytickets(req);
	}
	public Response mytickets(Request req) throws Exception
	{
		String user = (String) req.getSession().getAttribute("username");
		String base = req.getContextPath() + "/Index/mytickets";
		String query = req.getParam("q", "{owner["+ user +"];status[NOT Closed]}");
		String filterParam = req.getParam("filter", "{}");
		String limitParam = req.getParam("l", def_limit);
		String pageParam = req.getParam("p", "1");
		String orderParam = req.getParam("o", "id");
		String direction = req.getParam("d", "1");
		return _getListing(req, base, query, filterParam, limitParam, pageParam, orderParam, direction);
	}

	public Response alltickets(Request req) throws Exception
	{
		String base = req.getContextPath() + "/Index/alltickets";
		String query = req.getParam("q", "{}");
		String filterParam = req.getParam("filter", "{}");
		String limitParam = req.getParam("l", def_limit);
		String pageParam = req.getParam("p", "1");
		String orderParam = req.getParam("o", "id");
		String direction = req.getParam("d", "1");
		return _getListing(req, base, query, filterParam, limitParam, pageParam, orderParam, direction);
	}

	/**
	 * 
	 * @param req
	 * @param query
	 * @param filterParam JSON
	 * @param limitParam int default def_limit
	 * @return
	 * @throws Exception 
	 */
	public Response _getListing(
			Request req,
			String base,
			String query,
			String filterParam,
			String limitParam,
			String pageParam,
			String orderParam,
			String direction
	) throws Exception
	{
		log.info("{}");

		//log.debug("{}", auth);
		TestDirector td = _getTd(req);


		//
		if (query.isEmpty()) {
			query = "{}";
		}


		// if Filter/// then replace query
		if (filterParam.isEmpty()) { filterParam = "{}"; }
		Json filter = bledo.Util.jsonDecode(filterParam);
		if (!filter.isEmpty())
		{
			StringBuilder sb = new StringBuilder("{");
			int i = 0;
			for (String key : filter.keySet())
			{
				String val = filter.getString(key);
				if (val == null || val.isEmpty())
				{
					continue;
				}

				if (i > 0) { sb.append(";"); }

				if ("id".equals(key))
				{
					int x = 0;
					try { x = Integer.parseInt(val); } catch(NumberFormatException e) {}
					sb.append(key) .append("[") .append(x) .append("]");
				}
				else
				{
					sb.append(key) .append("['") .append(val) .append("']");
				}

				i++;
			}
			sb.append("}");
			query = sb.toString();
		}


		// Calculate Limit
		int limit = Integer.parseInt(limitParam);
		if (limit > 1000) { limit = 1000; }

		// Calculate laste page
		EntityResult entityCount = td.list(query, null, 1, 1);
		log.debug("get count td.list(query:{}, null, 1, 1)", query);
		double a1 = entityCount.count;
		double a2 = limit;
		int lastPage =  (int) Math.ceil( a1 / a2);
		if (lastPage < 1) { lastPage = 1; }
		//log.debug("found {} entities in entityCount", entityCount.count);



		// Calculate current page
		int page = (int) Integer.parseInt(pageParam);
		log.debug("page : parse({}) : {} ", new Object[]{pageParam, page});
		if (page < 1) { page = 1; }
		if (page > lastPage) { page = lastPage; }

		// calculate offset
		int offset = ( limit * (page - 1 ) ) + 1;
		log.debug("offset = ({} * ({} - 1 ) ) + 1", new Object[]{limit, page});


		// Order
		String order,directionOposite;
		if ("1".equals( direction) )
		{
			order = "{"+orderParam+"[DESC]}";
			directionOposite = "0";
		}
		else
		{
			order = "{"+orderParam+"[ASC]}";
			directionOposite = "1";
		}




		log.debug("td.list(query:{}, order:{} limit:{}, offset:{});", new Object[]{query, order, limit, offset});
		EntityResult entityResult = td.list(query, order, limit, offset);
		//log.debug("found {} entities in entityResult", entityResult.result.size());


		// next page
		int next = page + 1;

		// prev page
		int prev = page - 1;


		VelocityResponse resp = VelocityResponse.newInstance(req, "Index/_ticket_listing.vm"); // (req, getClass());
		resp.assign("result", entityResult.result);
		resp.assign("result_count", entityResult.result.size());
		resp.assign("page", page);
		resp.assign("query", query);
		resp.assign("limit", limit);
		resp.assign("lastPage", lastPage);
		resp.assign("next", next);
		resp.assign("prev", prev);
		resp.assign("direction", direction);
		resp.assign("order", orderParam);
		resp.assign("filter", filter);
		resp.assign("lbase", base);

		resp.assign("myTicketsUrl", req.getContextPath() + "/Index/mytickets");



		resp.assign("olink",
			base +"?p="+page
			+"&l="+limit
			+"&q="+ Util.urlEncode(query)
			+ "&d=" + directionOposite
		);

		resp.assign("flink",
			base +"?p=1"
			+"&l="+limit
		);


		return resp;
	}


	public Response ticket(Request req) throws Exception
	{
		int id;
		try { id = Integer.parseInt( req.getParam("id") ); }
		catch(NumberFormatException e) {
			return new Redirect(req.getContextPath()+"/Login/index");
		}

		TestDirector td = _getTd(req);

		Entity entity = td.getEntity(id);

		//entity.addComment("rramirez", "m/d/Y", "hello");


		VelocityResponse resp = VelocityResponse.newInstance(req, getClass());
		resp.assign("entity", entity);
		resp.assign("action", req.getContextPath() + "/Index/updateTicket/id/" + id );
		return resp;
	}

	public Response updateTicket(Request req) throws Exception
	{
		int id;
		try { id = Integer.parseInt( req.getParam("id") ); }
		catch(NumberFormatException e) {
			return new Redirect(req.getContextPath()+"/Login/index");
		}

		String status = req.getParam("status", "");
		String owner = req.getParam("owner", "");
		String description = req.getParam("description", "");
		String newComment = req.getParam("newComment", "");

		TestDirector td = _getTd(req);

		Entity entity = td.getEntity(id);
		String errors = null;
		List<String> availStatuses =  entity.getAvailStatuses();
		if ( !availStatuses.contains(status) )
		{
			errors = "Invalid status, please try again";
		}
		else if (owner.isEmpty())
		{
			errors = "Owner is required";
		}

		Response resp;
		if (errors != null )
		{
			VelocityResponse vresp = VelocityResponse.newInstance(req, "Index/ticket.vm");
			vresp.assign("action", req.getContextPath() + "/Index/updateTicket/id/" + id );
			vresp.assign("error", errors);
			vresp.assign("entity", entity);
			resp = vresp;
		}
		else
		{
			entity.set("description", description);
			entity.set("status", status);
			entity.set("owner", owner);
			if ( ! newComment.trim().isEmpty())
			{
				entity.addComment((String)req.getSession().getAttribute("username"), new Date(), newComment); }
			td.update(entity);
			resp = new Redirect(req.getContextPath() + "/Index/mytickets");
		}

		return resp;
	}

	public Response newticket(Request req)
	{
		VelocityResponse resp = VelocityResponse.newInstance(req, "Index/ticket.vm");
		resp.assign("entity", new Entity() );
		resp.assign("action", req.getContextPath() + "/Index/createticket" );
		return resp;
	}

	public Response createticket(Request req)
	{
		VelocityResponse resp = VelocityResponse.newInstance(req, "Index/ticket.vm");
		Entity entity = new Entity();
		entity.setOwner( (String) req.getSession().getAttribute("username") );
		resp.assign("entity", entity );
		resp.assign("action", req.getContextPath() + "/Index/createticket" );
		return resp;
	}
}
