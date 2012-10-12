package qc.rest.examples;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import md.testdirector.Auth;
import qc.rest.examples.infrastructure.Response;
import qc.rest.examples.infrastructure.RestConnector;
/**
 *
 * This example shows how to work with attachments.
 * It demonstrates read, write, update and delete operations
 * for files.
 */
public class AttachmentsExample {
	public static void main(String[] args) throws Exception
	{
		Auth auth = new Auth();
		RestConnector con = new RestConnector(auth);
		
		AuthenticateLoginLogoutExample login = new AuthenticateLoginLogoutExample(con);
		CreateDeleteExample writeExample = new CreateDeleteExample(con);
		UpdateExample updater = new UpdateExample(con);
		AttachmentsExample example = new AttachmentsExample(con);
		login.login(auth);
		final String requirementsUrl = con.buildEntityCollectionUrl("requirement");
		final String createdEntityUrl =
			writeExample.createEntity(requirementsUrl, writeExample.getEntityToPostXml());
		//Before editing an entity, check it out if versioning is enabled. Otherwise lock it.
		if (auth.VERSIONED) {
			String checkout = updater.checkout(createdEntityUrl, "check out comment1", -1);
			System.out.println("entity checked out: " + checkout.trim());
		}
		else {
			String lock = updater.lock(createdEntityUrl);
			System.out.println("entity locked: " + lock.trim());
		}
		//The file names to use on the server side
		String multipartFileName = "multiPartFileName.txt";
		String octetStreamFileName = "octetStreamFileName.txt";
		//Attach the file data to the entity
		String newMultiPartAttachmentUrl =
			example.attachWithMultipart(
			createdEntityUrl,
			"content of file".getBytes(),
			"text/plain",
			multipartFileName,
			"some random description");
		String newOctetStreamAttachmentUrl =
			example.attachWithOctetStream(
			createdEntityUrl,
			"a completely differnt file".getBytes(),
			octetStreamFileName);
		//If versioning is enabled, changes are not visible to other users until we check them in.
		if (auth.VERSIONED) {
			boolean checkin = updater.checkin(createdEntityUrl);
			System.out.println("entity checkin: " + checkin);
		}
		else {
			boolean unlock = updater.unlock(createdEntityUrl);
			System.out.println("entity unlocked: " + unlock);
		}
		System.out.println("multipart attachment url: " + newMultiPartAttachmentUrl);
		System.out.println("octet stream attachment url: " + newOctetStreamAttachmentUrl);
		//Read the data and its metadata back from the server
		String readAttachments = example.readAttachments(createdEntityUrl);
		System.out.println("reading file attachments: " + readAttachments);
		byte[] readAttachmentData = example.readAttachmentData(newOctetStreamAttachmentUrl);
		System.out.println("reading octet-stream file attachment: "
			+ new String(readAttachmentData));
		String readAttachmentDetails = example.readAttachmentDetails(newOctetStreamAttachmentUrl);
		System.out.println("reading octet-stream file attachment details: " + readAttachmentDetails);
		readAttachmentData = example.readAttachmentData(newMultiPartAttachmentUrl);
		System.out.println("reading multipart file attachment: " + new String(readAttachmentData));
		readAttachmentDetails = example.readAttachmentDetails(newMultiPartAttachmentUrl);
		System.out.println("reading multipart file attachment details: " + readAttachmentDetails);
		System.out.println("now updating the octet-stream attachment file description and data...");
		
		if (auth.VERSIONED) {
			String checkout = updater.checkout(createdEntityUrl, "check out comment1", -1);
			System.out.println("entity checked out: " + checkout.trim());
		}
		else {
			String lock = updater.lock(createdEntityUrl);
			System.out.println("entity locked: " + lock.trim());
		}
		//Update data of file
		String attachmentDataUpdateResponseXml =
			example.updateAttachmentData(
			createdEntityUrl,
			"updated file contents".getBytes(),
			octetStreamFileName);
		System.out.println("attachment data update response xml:"
			+ attachmentDataUpdateResponseXml.trim());
		//Update description of file
		String attachmentMetadataUpdateResponseXml =
			example.updateAttachmentDescription(
			createdEntityUrl,
			"completely new description",
			octetStreamFileName);
		System.out.println("attachment metadata update response xml:"
			+ attachmentMetadataUpdateResponseXml.trim());
		//Check in
		if (auth.VERSIONED) {
			boolean checkin = updater.checkin(createdEntityUrl);
			System.out.println("entity checkin: " + checkin);
		}
		else {
			boolean unlock = updater.unlock(createdEntityUrl);
			System.out.println("entity unlocked: " + unlock);
		}
		//Show changes
		readAttachmentData = example.readAttachmentData(newOctetStreamAttachmentUrl);
		System.out.println("reading octet-stream file attachment: "
			+ new String(readAttachmentData));
		//Cleanup
		writeExample.deleteEntity(createdEntityUrl);
		login.logout();
	}
	/**
	 * @param entityUrl
	 *            the entity whose attachment to update
	 * @param bytes
	 *            the data to write instead of previously stored data
	 * @param attachmentFileName
	 *            the attachment for which to update the file name on the server side.
	 * @return
	 */
	private String updateAttachmentData(String entityUrl, byte[] bytes, String attachmentFileName)
		throws Exception {
		Map<String, String> requestHeaders = new HashMap<String, String>();
		//This line makes the update be on data and not properties such as description.
		requestHeaders.put("Content-Type", "application/octet-stream");
		requestHeaders.put("Accept", "application/xml");
		byte[] ret =
			con.httpPut(entityUrl + "/attachments/" + attachmentFileName, bytes, requestHeaders).getResponseData();
		return new String(ret);
	}
	/**
	 * @param entityUrl
	 *            url of entity whose attachment's description we want to update
	 * @param description
	 *            string to store as description
	 * @param attachmentFileName
	 *            the attachment file name on the server-side whose description we'd like to update
	 * @return
	 */
	private String updateAttachmentDescription(
		String entityUrl,
		String description,
		String attachmentFileName) throws Exception {
		Map<String, String> requestHeaders = new HashMap<String, String>();
		//This line makes the update be on properties such as description and not on the files binary data
		requestHeaders.put("Content-Type", "application/xml");
		requestHeaders.put("Accept", "application/xml");
		byte[] ret =
			con.httpPut(
			entityUrl + "/attachments/" + attachmentFileName,
			("<Entity Type=\"attachment\"><Fields><Field Name=\"description\"><Value>"
			+ description + "</Value></Field></Fields></Entity>").getBytes(),
			requestHeaders).getResponseData();
		return new String(ret);
	}
	
	RestConnector con;
	public AttachmentsExample( RestConnector con)
	{
		this.con = con;
	}
	
	/**
	 * @param attachmentUrl
	 *            of attachment
	 * @return the XML of the metadata on the requested attachment
	 */
	private String readAttachmentDetails(String attachmentUrl) throws Exception {
		Map<String, String> requestHeaders = new HashMap<String, String>();
		/* A get operation that specifies with an accept header that we must have an application/xml reply.
		 * An alt query parameter could also have been used. */
		requestHeaders.put("Accept", "application/xml");
		return con.httpGet(attachmentUrl, null, requestHeaders).toString();
	}
	/**
	 * @param attachmentUrl
	 *            of attachment
	 * @return the contents of the file
	 */
	private byte[] readAttachmentData(String attachmentUrl) throws Exception {
		Map<String, String> requestHeaders = new HashMap<String, String>();
		/* a get operation that specifies via accept header that we must have an application/octet-stream reply.
		 * an alt query parameter could also have been used. */
		requestHeaders.put("Accept", "application/octet-stream");
		return con.httpGet(attachmentUrl, null, requestHeaders).getResponseData();
	}
	/**
	 * @param entityUrl
	 *            of the entity whose attachments we want to get
	 * @return an xml with metadata on all attachmens of the entity
	 * @throws Exception
	 */
	private String readAttachments(String entityUrl) throws Exception {
		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Accept", "application/xml");
		return con.httpGet(entityUrl + "/attachments", null, requestHeaders).toString();
	}
	/**
	 * @param entityUrl
	 *            url of entity to attach the file to
	 * @param fileData
	 *            content of file
	 * @param filename
	 *            to use on serverside
	 * @return
	 */
	private String attachWithOctetStream(String entityUrl, byte[] fileData, String filename)
		throws Exception {
		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Slug", filename);
		requestHeaders.put("Content-Type", "application/octet-stream");
		Response response = con.httpPost(entityUrl + "/attachments", fileData, requestHeaders);
		return response.getResponseHeaders().get("Location").iterator().next();
	}
	/**
	 * @param entityUrl
	 *            url of entity to attach the file to
	 * @param bytes
	 *            content of file
	 * @param contentType
	 *            of the file - txt/html or xml, or octetstream etc..
	 * @param filename
	 *            to use on serverside
	 * @return
	 */
	private String attachWithMultipart(
		String entityUrl,
		byte[] fileData,
		String contentType,
		String filename,
		String description) throws Exception {
		/*
		 * headers:
		 * Content-Type: multipart/form-data; boundary=<boundary>
		 * //Template for file mime part:
		 * --<boundary>\r\n
		 * Content-Disposition: form-data; name="<fieldName>"; filename="<filename>"\r\n
		 * Content-Type: <mime-type>\r\n
		 * \r\n
		 * <file-data>\r\n
		 * <boundary>--
		 * //Template for post parameter mime part, such as description and/or filename:
		 * --<boundary>\r\n
		 * Content-Disposition: form-data; name="<fieldName>"\r\n
		 * \r\n
		 * <value>\r\n
		 * <boundary>--
		 * //End of parts:
		 * --<boundary>--
		 * We need three parts: filename(template for parameter), description(template for parameter) and file data(template for file).
		 */
		//This can be pretty much any string - it's used to signify the different mime parts
		String boundary = "exampleboundary";
		//Template to use when sending field data (assuming non-binary data)
		String fieldTemplate =
			"--%1$s\r\n"
			+ "Content-Disposition: form-data; name=\"%2$s\" \r\n\r\n"
			+ "%3$s"
			+ "\r\n";
		//Template to use when sending file data (binary data still needs to be suffixed)
		String fileDataPrefixTemplate =
			"--%1$s\r\n"
			+ "Content-Disposition: form-data; name=\"%2$s\"; filename=\"%3$s\"\r\n"
			+ "Content-Type: %4$s\r\n\r\n";
		String filenameData = String.format(fieldTemplate, boundary, "filename", filename);
		String descriptionData = String.format(fieldTemplate, boundary, "description", description);
		String fileDataSuffix = "\r\n--" + boundary + "--";
		String fileDataPrefix =
			String.format(fileDataPrefixTemplate, boundary, "file", filename, contentType);
		//The order is extremely important: The filename and description come before file data. The name of the file in the file part and in the filename part value MUST MATCH.
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bytes.write(filenameData.getBytes());
		bytes.write(descriptionData.getBytes());
		bytes.write(fileDataPrefix.getBytes());
		bytes.write(fileData);
		bytes.write(fileDataSuffix.getBytes());
		bytes.close();
		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Content-Type", "multipart/form-data; boundary=" + boundary);
		Response response =
			con.httpPost(entityUrl + "/attachments", bytes.toByteArray(), requestHeaders);
		return response.getResponseHeaders().get("Location").iterator().next();
	}
}
