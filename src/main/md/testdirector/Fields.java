/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package md.testdirector;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author ricardo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "field" })
public class Fields {
	@XmlElement(name = "Field", required = true)
	protected List<Field> field;
	/**
	 * @param fields
	 */
	public Fields(Fields fields) {
		field = new ArrayList<Field>(fields.getField());
	}
	/**
	 *
	 */
	public Fields() {}

	/**
	 * Gets the value of the field property.
	 *
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any
	 * modification you make to the returned list will be present inside the JAXB object. This
	 * is why there is not a set method for the field property.
	 *
	 *
	 * For example, to add a new item, do as follows:
	 *
	 *
	 *
	 * getField().add(newItem);
	 *
	 *
	 *
	 *
	 *
	 * Objects of the following type(s) are allowed in the list {@link Entity.Fields.Field }
	 *
	 *
	 */
	public List<Field> getField() {
		if (field == null) {
			field = new ArrayList<Field>();
		}
		return this.field;
	}
}
