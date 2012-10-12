/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package md.testdirector;

import bledo.Util;
import bledo.VelocityTplParser;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "fields" })
@XmlRootElement(name = "Entity")
public class Entity {
	@XmlElement(name = "Fields", required = true)
	protected Fields fields;
	@XmlAttribute(name = "Type", required = true)
	protected String type;
	/**
	 * @param entity
	 */
	public Entity(Entity entity) {
		type = new String(entity.getType());
		fields = new Fields(entity.getFields());
	}

	public void setOwner(String o)
	{
		set("owner", o);
	}
	public String getOwner()
	{
		return get("owner");
	}


	/**
	 *
	 */
	public Entity() {}
	/**
	 * Gets the value of the fields property.
	 *
	 * @return possible object is {@link Fields }
	 *
	 */
	public Fields getFields() {
		return fields;
	}
	/**
	 * Sets the value of the fields property.
	 *
	 * @param value
	 * allowed object is {@link Fields }
	 *
	 */
	public void setFields(Fields value) {
		this.fields = value;
	}
	/**
	 * Gets the value of the type property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getType() {
		return type;
	}
	/**
	 * Sets the value of the type property.
	 *
	 * @param value
	 * allowed object is {@link String }
	 *
	 */
	public void setType(String value) {
		this.type = value;
	}

	public String get(String key)
	{
		Fields fields = this.getFields();
		if (fields == null) {
			return "";
		}

		List<Field> fldList = fields.getField();
		if (fldList == null)
		{
			return "";
		}

		for (Field field : fldList)
		{
			if ( key.equals(field.getName()) )
			{
				if (field.getValue().isEmpty())
				{
					return "";
				}
				else
				{
					return field.getValue().get(0);
				}
			}

		}

		return null;
	}

	public String set(String key, String val)
	{
		for (Field field : this.getFields().getField())
		{
			if ( key.equals(field.getName()) )
			{
				if (field.getValue().isEmpty())
				{
					field.getValue().add(val);
				}
				else
				{
					field.getValue().set(0, val);
				}
			}

		}

		return null;
	}

	public String getDevComments()
	{
		return getDevComments(false);
	}

	public String getDevComments(boolean removeHtmlTag)
	{
		String comment = get("dev-comments");
		if (comment == null) { comment = ""; }
		if (removeHtmlTag)
		{
			comment = comment.replace("<html>", "");
			comment = comment.replace("<body>", "");
			comment = comment.replace("</body>", "");
			comment = comment.replace("</html>", "");
		}
		return comment;
	}

	public void addComment(String user, Date date, String comment)
	{
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		addComment(user, df.format(new Date()), comment);
	}

	public void addComment(String user, String date, String comment)
	{
		StringBuilder sb = new StringBuilder(get("dev-comments"));
		sb.length();

		if (sb.length() < 1 || sb.indexOf("<html>") == -1 ) {
			sb.setLength(0);
			sb.append("<html>\n<body>\n</body>\n</html>\n");
		}

		int pos = sb.lastIndexOf("</body>");
		String htmlComment = _new_comment(user, date, comment);
		sb.replace(pos, pos, htmlComment);
		set("dev-comments", sb.toString());
	}

	private String _new_comment(String user, String date, String comment)
	{
		VelocityTplParser parser = new VelocityTplParser();
		parser.assign("user", user);
		parser.assign("date", date);
		parser.assign("comment", comment);
		parser.assign("util", Util.class);
		return parser.fetch("md/testdirector/Entity_new_comment.vm");
	}

	public List<String> getAvailStatuses()
	{
		List<String> list = new ArrayList<String>();
		list.add("New");
		list.add("Assigned");
		list.add("In DEV");
		list.add("Fixed");
		list.add("Closed");
		return list;
	}
}