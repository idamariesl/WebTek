package dwebtek;

import org.jdom2.Document;

/**
 * This class is a service-class for the owners of the webshop with ID=32.
 * The class provides static methods for making specific POST and GET requests to the dWebTek cloud at services.brics.dk
 */
public class CloudTransaction {
	private static final String CLOUD_URL = "http://services.brics.dk/java4/cloud/";
	private static final String SHOP_ID = "32";

	private int responseCode;
	private Document responseDoc;

	public CloudTransaction() {
		responseCode = 0;
		responseDoc = null;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public Document getResponseDoc() {
		return responseDoc;
	}

	/**
	 * Method for listing the deleted items in an ArrayList
	 */
	public int  deletedItems(){
		String URLString = CLOUD_URL + "listDeletedItemIDs?shopID=" + SHOP_ID;
		HttpCommunication httpComm = new HttpCommunication(URLString);
		httpComm.httpGet();

		responseCode = httpComm.getResponseCode();
		responseDoc = httpComm.getResponseDoc();

		return responseCode;
	}

	/**
	 * Method for listing ALL items in an ArrayList
	 */
	public int allItems(){
		String URLString = CLOUD_URL + "listItems?shopID=" + SHOP_ID;
		HttpCommunication httpComm = new HttpCommunication(URLString);
		httpComm.httpGet();

		responseCode = httpComm.getResponseCode();
		responseDoc = httpComm.getResponseDoc();

		return responseCode;
	}

	/**
	 * This method performs the creatItem POST-request for the given parameter.
	 * @return responseCode from the cloud
	 */
	public int createItem(Document requestDoc){
		String URLString = CLOUD_URL + "createItem";
		HttpCommunication httpComm = new HttpCommunication(URLString);
		httpComm.httpPost(requestDoc);
		return httpComm.getResponseCode();
	}

	/**
	 * This method performs the deleteItem POST-request for the given parameter.
	 * @return responseCode from cloud
	 */
	public  int deleteItem(Document requestDoc){
		String URLString = CLOUD_URL + "deleteItem";
		HttpCommunication httpComm = new HttpCommunication(URLString);
		httpComm.httpPost(requestDoc);
		return httpComm.getResponseCode();
	}

	/**
	 * This method performs the adjustItemStock POST-request for the given parameters
	 * @return responseCode from cloud
	 */
	public int adjustItemStock(Document requestDoc){
		String URLString = CLOUD_URL + "adjustItemStock";
		HttpCommunication httpComm = new HttpCommunication(URLString);
		httpComm.httpPost(requestDoc);
		return httpComm.getResponseCode();
	}

	/**
	 * This method performs the modifyItem POST-request for the given parameters
	 * @return responseCode from cloud
	 */
	public int modifyItem(Document requestDoc) 	{
		String URLString = CLOUD_URL + "modifyItem";
		HttpCommunication httpComm = new HttpCommunication(URLString);
		httpComm.httpPost(requestDoc);
		return httpComm.getResponseCode();
	}

	/**
	 * Method for creating a customer in the clou
	 * @param requestDoc a document that must contain the expected
	 * elements for the createCustomer post. 
	 * @return a document that contains the username and the customerID
	 */
	public Document createCustomer(Document requestDoc){
		String URLString = CLOUD_URL + "createCustomer";
		HttpCommunication httpComn = new HttpCommunication(URLString);
		httpComn.httpPost(requestDoc);
		responseDoc = httpComn.getResponseDoc();
		return httpComn.getResponseDoc();
	}
	
	/**
	 * Method for logging in. 
	 * @param requestDoc a document that must contain the expected
	 * elements for the login post.
	 * @return  the responsecode from the doPost-method in the HttpCommmunication-class
	 */
	public int login(Document requestDoc) {
		String URLString = CLOUD_URL + "login";
		HttpCommunication httpComn = new HttpCommunication(URLString);
		httpComn.httpPost(requestDoc);
		responseCode = httpComn.getResponseCode();
		return httpComn.getResponseCode();
	}
}


