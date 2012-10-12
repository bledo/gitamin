package bledo;

import java.io.Writer;

public interface TplParser
{
    public void evaluate(String tplName, String strTpl, Writer writer);
	public void assign(String key, Object val);
	public String fetch(String tpl);
}
