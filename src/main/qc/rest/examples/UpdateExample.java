/**
 *
 */
package qc.rest.examples;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import md.testdirector.Auth;
import qc.rest.examples.infrastructure.Entity;
import qc.rest.examples.infrastructure.EntityMarshallingUtils;
import qc.rest.examples.infrastructure.Response;
import qc.rest.examples.infrastructure.RestConnector;
/**
 * This example shows how to change data on existing entities.
 *
 */
public class UpdateExample {
 public static void main(String[] args) throws Exception
 {
	 Auth auth = new Auth();
         RestConnector con = new RestConnector(auth);

        AuthenticateLoginLogoutExample login = new AuthenticateLoginLogoutExample(con);
        CreateDeleteExample writeExample = new CreateDeleteExample(con);
        UpdateExample example = new UpdateExample(con);
         login.login(auth);
         String requirementsUrl = con.buildEntityCollectionUrl("requirement");
         String newEntityToUpdateUrl =
         writeExample.createEntity(requirementsUrl, writeExample.getEntityToPostXml());

         //Create an XML that, when posted, modifies the entity.
         String updatedEntityXml = generateUpdateXml("request-note", "im an updated value");
                        
         //If versioning is enabled, check out the entity. Otherwise, lock it.
         if (auth.VERSIONED) {
                 //Note that we selected an entity that supports versioning on a project that supports versioning. Otherwise, this call would fail.
                 String checkout = example.checkout(newEntityToUpdateUrl, "check out comment1", -1);
                 System.out.println("entity checked out: " + checkout.trim());
         }
         else {
                 String lock = example.lock(newEntityToUpdateUrl);
                 System.out.println("entity locked: " + lock.trim());
         }
                        
         //Update the entity
         String put = example.update(newEntityToUpdateUrl, updatedEntityXml).toString();
         System.out.println("entity updated: " + put.trim());
                        
         // If versioning is enabled, check out the entity. Otherwise, lock it.
         if (auth.VERSIONED) {
                 boolean checkin = example.checkin(newEntityToUpdateUrl);
                 System.out.println("entity checkin: " + checkin);
         }
         else {
                 boolean unlock = example.unlock(newEntityToUpdateUrl);
                 System.out.println("entity unlocked: " + unlock);
         }
                        
         /*
         Now we do the same thing again, only this time with marshalling.
         */
         //checkout
         if (auth.VERSIONED) {
                 String checkout = example.checkout(newEntityToUpdateUrl, "check out comment2", -1);
                 System.out.println("entity checked out: " + checkout.trim());
         }
         else {
                 String lock = example.lock(newEntityToUpdateUrl);
                 System.out.println("entity locked: " + lock.trim());
         }
         
         //Create update string
         String entityUpdateXml =
         generateUpdateXml("request-note", "updating via marshal / unmarhsalling");
         
         //Create entity. (We could have instantiated the entity and used methods to set the new values.)
         Entity e = EntityMarshallingUtils.marshal(Entity.class, entityUpdateXml);

         //Do update operation
         String updateResponseEntityXml =
         example.update(
                 newEntityToUpdateUrl,
                 EntityMarshallingUtils.unmarshal(Entity.class, e)).toString();
                 
         //entity XML from server -gt; entity class instance
         Entity updateResponseEntity =
                        EntityMarshallingUtils.marshal(Entity.class, updateResponseEntityXml);
         System.out.println("entity updated: "
         + EntityMarshallingUtils.unmarshal(Entity.class, updateResponseEntity).trim());
         
         //checkin
         if (auth.VERSIONED) {
                 boolean checkin = example.checkin(newEntityToUpdateUrl);
                 System.out.println("entity checkin: " + checkin);
         }
         else {
                 boolean unlock = example.unlock(newEntityToUpdateUrl);
                 System.out.println("entity unlocked: " + unlock);
         }
         
         //cleanup
         writeExample.deleteEntity(newEntityToUpdateUrl);
         login.logout();
         }
         
         private RestConnector con;
         /**
         * @param r
         */
         public UpdateExample( RestConnector con) {
		 this.con = con;
         }
         /**
         * @param entityUrl
         * of the entity to checkout
         * @param comment
         * to keep on the server side of why you checked this entity out
         * @param version
         * to checkout or -1 if you want the latest
         * @return a string description of the checked out entity
         * @throws Exception
         */
         public String checkout(String entityUrl, String comment, int version) throws Exception {
         String commentXmlBit =
         ((comment != null) && !comment.isEmpty()
         ? "<Comment>" + comment + "</Comment>"
         : "");
         String versionXmlBit = (version >= 0 ? "<Version>" + version + "</Version>" : "");
         String xmlData = commentXmlBit + versionXmlBit;
         String xml =
         xmlData.isEmpty() ? "" : "<CheckOutParameters>" + xmlData + "</CheckOutParameters>";
         Map<String, String> requestHeaders = new HashMap<String, String>();
         requestHeaders.put("Content-Type", "application/xml");
         requestHeaders.put("Accept", "application/xml");
         Response response =
         con.httpPost(entityUrl + "/versions/check-out", xml.getBytes(), requestHeaders);
         return response.toString();
 }
 
 /**
 * @param entityUrl
 * to checkin
 * @return true if operation is successful
 * @throws Exception
 */
 public boolean checkin(String entityUrl) throws Exception {
         //Execute a post operation on the checkin resource of your entity.
         Response response = con.httpPost(entityUrl + "/versions/check-in", null, null);
         boolean ret = response.getStatusCode() == HttpURLConnection.HTTP_OK;
         return ret;
 }
 
 /**
 * @param entityUrl
 * to lock
 * @return the locked entity xml
 * @throws Exception
 */
 public String lock(String entityUrl) throws Exception {
         Map<String, String> requestHeaders = new HashMap<String, String>();
         requestHeaders.put("Accept", "application/xml");
         return con.httpPost(entityUrl + "/lock", null, requestHeaders).toString();
 }
 
         /**
 * @param entityUrl
 * to unlock
 * @return
 * @throws Exception
 */
 public boolean unlock(String entityUrl) throws Exception {
         return con.httpDelete(entityUrl + "/lock", null).getStatusCode() == HttpURLConnection.HTTP_OK;
 }
 
 /**
 * @param field
 * the field name to update
 * @param value
 * the new value to use
 * @return an XML that can be used to update an entity's single given field to given value
 */
 private static String generateUpdateXml(String field, String value) {
         return "<Entity Type=\"requirement\"><Fields>"
                 + "<Field Name=\""
                 + field
                 + "\"><Value>"
                 + value
                 + "</Value></Field>"
                 + "</Fields></Entity>";
 }
 
 /**
 * @param entityUrl
 * to update
 * @param updatedEntityXml
 * new entity descripion. only lists updated fields. unmentioned fields will not
 * change.
 * @return xml description of the entity on the serverside, after update.
 * @throws Exception
 */
 private Response update(String entityUrl, String updatedEntityXml) throws Exception {
         Map<String, String> requestHeaders = new HashMap<String, String>();
         requestHeaders.put("Content-Type", "application/xml");
         requestHeaders.put("Accept", "application/xml");
         Response put = con.httpPut(entityUrl, updatedEntityXml.getBytes(), requestHeaders);
         return put;
 }
}