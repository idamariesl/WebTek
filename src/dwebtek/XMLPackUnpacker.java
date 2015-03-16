package dwebtek;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.Text;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderXSDFactory;
import org.jdom2.output.XMLOutputter;

/**
 * This class provides static methods for handling the XML from the cloud and making it more JAVA-friendly.
 * it also provides a validation-method for validating Document-objects before they are sent with requests.
 *
 */
public class XMLPackUnpacker {
	private static final Namespace NAMESPACE = Namespace.getNamespace("http://www.cs.au.dk/dWebTek/2014");
	private static final String SCHEMA_URL = "https://www.dropbox.com/s/4twf4nifg36w4bh/cloudMVO.xsd?dl=1";
	private static final String KEY = "EFE80AE51F99C3753FCAA9D6";

	private int responseCode;

	public XMLPackUnpacker() {
		responseCode = 0;
	}

	public int getResponseCode() {
		return responseCode;
	}

	/**
	 * This method fetches the XML-document containing all the items in the store, and saves this information in Product-objects
	 * and add them to an arraylist that is returned.
	 * @return
	 */
	public ArrayList<Product> unpackItemList() {	
		// Deletet items
		CloudTransaction cloudTransAllItems = new CloudTransaction();
		ArrayList<Element> all = new ArrayList<Element>();

		if (cloudTransAllItems.allItems() == HttpCommunication.TRANS_OK) {
			Document allDoc = cloudTransAllItems.getResponseDoc();
			List<Element> list = allDoc.getRootElement().getChildren();
			for(Element n : list){
				all.add(n);
			}
		}

		// All items
		CloudTransaction cloudTransDeletedItems = new CloudTransaction();
		ArrayList<String> del = new ArrayList<String>();

		if (cloudTransDeletedItems.deletedItems() == HttpCommunication.TRANS_OK) {
			Document delDoc = cloudTransDeletedItems.getResponseDoc();
			List<Element> list = delDoc.getRootElement().getChildren();
			for(Element n : list) {
				del.add(n.getValue());
			}
		}

		// Filter out the deleted items.
		ArrayList<Product> products = new ArrayList<Product>();

		if (cloudTransAllItems.getResponseCode() == HttpCommunication.TRANS_OK && 
				cloudTransDeletedItems.getResponseCode() == HttpCommunication.TRANS_OK ) {
			for(Element n : all)
			{
				if(!del.contains(n.getChild("itemID",NAMESPACE).getValue()))
				{

					Element descriptionElement = n.getChild("itemDescription",NAMESPACE);
					String itemID = n.getChildText("itemID", NAMESPACE);
					String itemName = n.getChildText("itemName", NAMESPACE);
					String itemURL = n.getChildText("itemURL", NAMESPACE);
					String itemPrice = n.getChildText("itemPrice",NAMESPACE);
					String itemStock = n.getChildText("itemStock",NAMESPACE);
					//Store the descriptionElement as a string:
					String descriptionXML = makeXmlString(descriptionElement);

					//Convert DescriptionElement to a String with HTHML-tags:
					Element documentInDescription = descriptionElement.getChild("document", NAMESPACE);
					String description = "";
					for(Content c: documentInDescription.getContent()) {
						description+=XMLtoHTML(c);
					}
					//Store the information in a Product object:
					Product p = new Product(itemID, itemName, itemURL, itemPrice,itemStock, description, descriptionXML);
					products.add(p);
				}
			}
		}
		return products;
	}

	/**
	 * This method takes an XML-element in the format of an Element object and converts it to a string of XML
	 * @param element the XML Element-object we wish to convert to string.
	 * @return a string consisting of XML
	 */
	public static String makeXmlString(Element element) {
		//Defining an XMLOutputter for outputting our XML to a string later:
		XMLOutputter outputter = new XMLOutputter();
		//outputting the content to a string:
		String xmlString = outputter.outputElementContentString(element);
		return xmlString;
	}

	/**
	 * This method takes a JDOM object of type Content and turns it into a String containing valid HTML.
	 * It is a precondition for this method that the argument passed to it, is either of type Text or type Element.
	 * The method uses recursion to solve the task.
	 * @param con a Content object that is a child of a w:document-element.
	 * @return a String containing valid HTML.
	 */
	public static String XMLtoHTML(Content con) {
		String result = "";
		if (con.getClass()==Text.class) {
			return ((Text) con).getText();
		}
		//If the content is not of type Text, we know from our schema-file that the content will be an Element-object.
		Element el = (Element) con;

		if(el.getName().equals("bold")) {
			result += "<b> ";
			for (Content c: el.getContent()) {
				result +=XMLtoHTML(c);
			}
			result += "</b> ";

		}
		if(el.getName().equals("italics")) {
			result += "<i> ";
			for (Content c: el.getContent()) {
				result +=XMLtoHTML(c);
			}
			result += "</i> ";

		}
		if(el.getName().equals("list")) {
			result += "<ul> ";
			for (Content c: el.getContent()) {
				result +=XMLtoHTML(c);
			}
			result += "</ul> ";	
		}
		if(el.getName().equals("item")) {
			result += "<li> ";
			for (Content c: el.getContent()) {
				result +=XMLtoHTML(c);
			}
			result += "</li> ";

		}
		return result;
	}

	/**
	 * This method validates a given XML JDOM Document object and validates it against the schema-file produces in Hand-In 2
	 * (It is also located in the project folder, called cloudMVO.xsd). returns the validated document, or null if validation was unsuccesful
	 * @param doc
	 * @return the validated Document or null if unsuccessful
	 */
	public static Document validateXMLDoc(Document doc) {
		Document validatedDocument = null;
		try {
			XMLOutputter out = new XMLOutputter();
			String docAsString = out.outputString(doc);
			URL xsdURL = new URL(SCHEMA_URL);
			XMLReaderJDOMFactory factory = new XMLReaderXSDFactory(xsdURL);
			SAXBuilder validBuilder = new SAXBuilder(factory);

			InputStream is = new ByteArrayInputStream(docAsString.getBytes("UTF-8"));
			// Here we build a document to see if it is valid:
			validatedDocument = validBuilder.build(is);
		} catch (MalformedURLException e) {
			System.out.println("Something went wrong creating the path url for the schema-file");
		} catch (JDOMException e) {
			System.out.println("Something went wrong using jdom in the validateXMLDoc method");
		} catch (UnsupportedEncodingException e) {
			System.out.println("Something went wrong using ByteArrayInputStream in the validateXMLDoc method");		
		} catch (IOException e) {
			System.out.println("Something went wrong using I/O in validateXMLDoc-method");
		} 
		return validatedDocument;
	}

	public int createItem(String name) {
		CloudTransaction cloudTrans = new CloudTransaction();

		Element element = new Element("createItem"); 
		element.setNamespace(NAMESPACE); 
		element.addContent(new Element ("shopKey", NAMESPACE).setText(KEY));
		element.addContent(new Element ("itemName", NAMESPACE).setText(name));

		cloudTrans.createItem(new Document(element));
		return cloudTrans.getResponseCode(); 
	}
	/**
	 * Method for creating customers in the shop
	 * @param customerName, the username
	 * @param customerPass, the password
	 * @return response document that contains the username and the customerID
	 */
	public Document createCustomer(String customerName, String customerPass) {
		CloudTransaction cloudTrans = new CloudTransaction();		
		Element element = new Element("createCustomer"); 
		element.setNamespace(NAMESPACE); 
		element.addContent(new Element ("shopKey", NAMESPACE).setText(KEY));
		element.addContent(new Element ("customerName", NAMESPACE).setText(customerName));
		element.addContent(new Element ("customerPass", NAMESPACE).setText(customerPass));
		Document createDoc = cloudTrans.createCustomer(new Document(element));
		responseCode = cloudTrans.getResponseCode();
		return createDoc;
	}
	
	/**
	 * Method for logging in. 
	 * @param customerName
	 * @param customerPass
	 * @return the response code
	 */
	public Document login(String customerName, String customerPass) {
		CloudTransaction cloudTrans = new CloudTransaction();
		Element element = new Element("login");
		element.setNamespace(NAMESPACE);
		element.addContent(new Element ("customerName", NAMESPACE).setText(customerName));
		element.addContent(new Element ("customerPass", NAMESPACE).setText(customerPass));
		Document loginDoc = cloudTrans.login(new Document(element));
		responseCode = cloudTrans.getResponseCode();
		return loginDoc;
	}
	
	/**
	 * Method for selling items
	 * @param itemID
	 * @param customerID
	 * @param saleAmount
	 * @return the responseCode from the cloudPost-method
	 */
	public Document sellItems(int itemID, int customerID, int saleAmount) {
		CloudTransaction cloudTrans = new CloudTransaction();
		String itemIDString = "" + itemID;
		String customerIDString = "" + customerID;
		String saleAmountString = "" + saleAmount;
		Element element = new Element("sellItems"); 
		element.setNamespace(NAMESPACE); 
		element.addContent(new Element ("shopKey", NAMESPACE).setText(KEY));
		element.addContent(new Element ("itemID", NAMESPACE).setText(itemIDString));
		element.addContent(new Element ("customerID", NAMESPACE).setText(customerIDString));
		element.addContent(new Element ("saleAmount", NAMESPACE).setText(saleAmountString));
		cloudTrans.sellItems(new Document(element));
		responseCode = cloudTrans.getResponseCode();
		return cloudTrans.getResponseDoc();
	}

	/**
	 * Method that check whether a document contains a specific string
	 * @param document you want to check.
	 * @param element; the string you search for
	 * @return true if the element exist, false if not.
	 */
	public boolean existElement(Document doc, String element) {
		Element root = doc.getRootElement();
		if(root.getChild(element, NAMESPACE)!=null) {
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * This method takes a document and finds the customerID-element
	 * @param document
	 * @return a String for the customerID
	 */
	public String getId(Document doc) {
		Element root = doc.getRootElement();
		String id = root.getChild("customerID", NAMESPACE).getText();
		return id;
	}
	
	public int deleteItem(int itemID){
		CloudTransaction cloudTrans = new CloudTransaction();

		Element element = new Element("deleteItem"); 
		element.setNamespace(NAMESPACE); 
		element.addContent(new Element ("shopKey", NAMESPACE).setText(KEY));
		element.addContent(new Element ("itemID", NAMESPACE).setText("" + itemID));

		cloudTrans.deleteItem(new Document(element));
		return cloudTrans.getResponseCode(); 

	}

	public int adjustItemStock(int itemID, int adjustment){
		CloudTransaction cloudTrans = new CloudTransaction();

		Element element = new Element("adjustItemStock"); 
		element.setNamespace(NAMESPACE); 
		element.addContent(new Element ("shopKey", NAMESPACE).setText(KEY));
		element.addContent(new Element ("itemID", NAMESPACE).setText("" + itemID));
		element.addContent(new Element ("adjustment", NAMESPACE).setText("" + adjustment));

		cloudTrans.adjustItemStock(new Document(element));
		return cloudTrans.getResponseCode(); 


	}

	public int modifyItem(int itemID, String itemName, int itemPrice, String itemURL, String itemDescriptionXML)
	{
		CloudTransaction cloudTrans = new CloudTransaction();
		int responseCode = 0;

		try {
			//Turning our description into a proper xml-element:
			SAXBuilder builder = new SAXBuilder();
			InputStream stream = new ByteArrayInputStream(itemDescriptionXML.getBytes("UTF-8"));
			Document itemDescriptionContentAsDocument = builder.build(stream);
			Element descriptionContentElement = itemDescriptionContentAsDocument.getRootElement().clone();
			//Creating all the necessary elements for our modifyItem-request body:
			Element element = new Element("modifyItem"); 
			element.setNamespace(NAMESPACE); 
			element.addContent(new Element("shopKey", NAMESPACE).setText(KEY));  
			element.addContent(new Element ("itemID",NAMESPACE).setText("" + itemID)); 
			element.addContent(new Element ("itemName",NAMESPACE).setText(itemName)); 
			element.addContent(new Element ("itemPrice",NAMESPACE).setText("" + itemPrice)); 
			element.addContent(new Element ("itemURL",NAMESPACE).setText(itemURL));

			Element descriptionElement = new Element ("itemDescription",NAMESPACE);
			descriptionElement.addContent(descriptionContentElement);

			element.addContent(descriptionElement);
			Document modifyItemDocument = new Document(element);

			Document validatedDocument = XMLPackUnpacker.validateXMLDoc(modifyItemDocument);
			if (validatedDocument != null) {
				cloudTrans.modifyItem(validatedDocument);
				responseCode =  cloudTrans.getResponseCode();
			}

		} catch (UnsupportedEncodingException e) {
			System.out.println("Something went wrong making the inputStream from descriptionString");
		} 
		catch (JDOMException e) {
			System.out.println("Something went wrong when we were doing stuff to the description in JDOM");
		} 
		catch (IOException e) {
			System.out.println("Something went wrong when we were handling the description using SAXBuilder");
		}
		return responseCode; 
	}



}
