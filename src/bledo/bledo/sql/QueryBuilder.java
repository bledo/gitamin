package bledo.sql;

import java.util.ArrayList;
import java.util.List;

public class QueryBuilder
{
	public static void main(String[] args) throws Exception
	{
		QueryBuilder qb = QueryBuilder.start();
		qb.from("account")
		.leftJoin("user", "user.account_id = account.account_id")
		.where("user.name = ?", 3)
		.where("user.email IS NOT NULL");
		
		System.out.println(qb.getSql());
		for (Object val : qb.getArgs())
		{
			System.out.println(val);
		}
	}
	
	public static QueryBuilder start()
	{
		return new QueryBuilder();
	}
	
	protected String[] _from_fields;
	protected String _from;
	public QueryBuilder from(String tbl) {
		String[] parts = tbl.split("\\.");
		String table; if ( parts.length > 1) { table = parts[1]; } else { table = parts[0]; }
		from(tbl, new String[]{ table + ".*" });
		return this;
	}
	public QueryBuilder from(String tbl, String[] flds) {
		_from = tbl;
		_from_fields = flds;
		return this;
	}
	
	protected List<Stmt> _join = new ArrayList<Stmt>();
	protected QueryBuilder _join(String joinType, String tbl, String on, String[] keys, Object[] vals)
	{
		_join.add(new Stmt(joinType + " " + tbl + " ON " + on, vals, keys));
		return this;
	}

	public QueryBuilder leftJoin(String tbl, String on, String[] keys, List<Object> vals)
	{
		return _join("LEFT JOIN", tbl, on, keys, vals.toArray());
	}
	public QueryBuilder leftJoin(String tbl, String on, String[] keys)
	{
		return _join("LEFT JOIN", tbl, on, keys, new Object[]{});
	}
	public QueryBuilder leftJoin(String tbl, String on)
	{
		return _join("LEFT JOIN", tbl, on, new String[]{}, new Object[]{});
	}

	public QueryBuilder rightJoin(String tbl, String on, String[] keys, List<Object> vals)
	{
		return _join("RIGHT JOIN", tbl, on, keys, vals.toArray() );
	}

	public QueryBuilder rightJoin(String tbl, String on, String[] keys)
	{
		return _join("RIGHT JOIN", tbl, on, keys, new Object[]{} );
	}

	public QueryBuilder rightJoin(String tbl, String on)
	{
		return _join("RIGHT JOIN", tbl, on, new String[]{}, new Object[]{} );
	}
	
	private class Stmt {
		public String stmt;
		public Object[] args;
		public String[] keys;

		public Stmt(String stmt, Object[] args) {
			this(stmt, args, new String[]{});
		}

		public Stmt(String stmt, Object[] args, String[] keys) {
			this.stmt = stmt;
			this.args = args;
			this.keys = keys;
		}
	}
	
	protected List<Stmt> _where = new ArrayList<Stmt>();

	public QueryBuilder where(String cond, Object[] args)
	{
		_where.add(new Stmt(cond, args));
		return this;
	}
	public QueryBuilder where(String cond, Object arg)
	{
		_where.add(new Stmt(cond, new Object[]{ arg }));
		return this;
	}
	public QueryBuilder where(String cond)
	{
		return where(cond, new Object[]{});
	}
	
	private List<String> _groupBy = new ArrayList<String>();
	public QueryBuilder groupBy(String...fields)
	{
		for (String f : fields)
		{
			_groupBy.add(f);
		}
		return this;
	}
	
	private List<String> _orderBy = new ArrayList<String>();
	public QueryBuilder orderBy(String...fields)
	{
		for (String f : fields)
		{
			_orderBy.add(f);
		}
		return this;
	}

	public String getSql()
	{
		StringBuilder sb = new StringBuilder();

		// SELECT
		sb.append("SELECT ");
		sb.append("\n");

		int fcount = 0;
		// from fields
		for (String f : _from_fields) {
			if (fcount > 0) { sb.append(", "); }
			sb.append(f);
			fcount++;
		}
		// fields from join
		for (Stmt join : _join) {
			for (String f : join.keys)
			{
				if (fcount > 0) { sb.append(", "); }
				sb.append(f);
				fcount++;
			}
		}
		sb.append("\n");

		// FROM
		sb.append("FROM ").append(_from);
		
		// JOIN
		for (Stmt join : _join)
		{
			sb.append(" ")
			  .append(join.stmt)
			  .append("\n");
		}
		
		// WHERE
		if (!_where.isEmpty())
		{
			sb.append(" WHERE ");
			int i = 0;
			for (Stmt where : _where)
			{
				if (i > 0) { sb.append(" AND "); }
				sb.append("(").append( where.stmt ).append(")");
				sb.append("\n");
				i++;
			}
		}
		
		// GROUP
		if (!_groupBy.isEmpty())
		{
			sb.append(" GROUP BY ");
			int i = 0;
			for (String group : _groupBy)
			{
				if (i > 0) { sb.append(", "); }
				sb.append(group);
				i++;
			}
			
		}
		
		// ORDER
		if (!_orderBy.isEmpty())
		{
			sb.append(" ORDER BY ");
			int i = 0;
			for (String order : _orderBy)
			{
				if (i > 0) { sb.append(", "); }
				sb.append(order);
				i++;
			}
		}
		
		return sb.toString();
	}

	public List<Object> getArgs()
	{
		List<Object> vals = new ArrayList<Object>();

		// JOIN
		for (Stmt stmt : _join)
		{
			for (Object arg : stmt.args)
			{
				vals.add(arg);
			}
		}
		
		// WHERE
		for (Stmt stmt : _where)
		{
			for (Object arg : stmt.args)
			{
				vals.add(arg);
			}
		}

		return vals;
	}
	
}



