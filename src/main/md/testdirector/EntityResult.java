/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package md.testdirector;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ricardo
 */
@XmlRootElement(name="Entities")
public class EntityResult {
	//@XmlElementWrapper(name = "Entities")
	@XmlElement(name = "Entity",required=false)
	public List<Entity> result = new ArrayList<Entity>();

	@XmlAttribute(name="TotalResults",required=false)
	public int count = 0;

	public List<Entity> getRresult() { return result; }
	public int getCcount() { return count; }
}
