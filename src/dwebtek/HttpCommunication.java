package dwebtek;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;	
import org.jdom2.output.XMLOutputter;
/**
 *This class provides static methods for making POST and GET Requests to a given server, provided by the user
 */
public class HttpCommunication {
	public static int TRANS_OK = 200;
	private int responseCode;
	private Document responseDoc;
	private URL url;

	public HttpCommunication (String urlString) {
		responseCode = 0;
		responseDoc = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			System.out.println("Something went wrong instantiating the URL objects");
		}
	}

	/**
	 * Setting up the connection to make POST-requests,
	 * making a post-request using XMLOutputter. 
	 * printing the response code to the terminal. returns it as well.
	 * @param document
	 * @param cloud: a string with the URL for the cloud
	 * @return the response code received  
	 */

	public void httpPost(Document document) {
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.connect();
			new XMLOutputter().output(document, System.out);
			new XMLOutputter().output(document, con.getOutputStream());  // MVO Jeg vil gerne have fat i responsedok
			responseCode = con.getResponseCode();
System.out.print("httpPost 0 " + responseCode );
			InputStream responseStream = con.getInputStream();
			// If the inputStream is different from 0, there are no bits to build a document on 
			if(responseStream.available()!=0){
				System.out.print("httpPost 1 " + responseCode );
				SAXBuilder builder = new SAXBuilder();
				System.out.print("httpPost 2 " + responseCode );
				responseDoc = builder.build(responseStream);
				System.out.print("httpPost 3 " + responseDoc==null );
				
			}
		} catch (ProtocolException e) {
			System.out.println("Something went wrong setting the request method ");
		} catch (JDOMException e) {
			System.out.println("Something went wrong with JDOM ");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Something went wrong connecting to the cloud");
		}
		//Close the connection no matter what. 
		finally{con.disconnect();}
	}

	/**
	 * Setting up the connection to make GET-requests,
	 * making a GET-request
	 * returns the response as a Document-object
	 * @param cloud a String with the URL for the cloud
	 * @return Document . The response from the cloud as a Document
	 */
	public  void httpGet() {
		System.out.println("Jeg er her : httpGet 0");
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			System.out.println("Jeg er her : httpGet 1");

			responseCode = con.getResponseCode();
			if (responseCode == TRANS_OK) {
				System.out.println("Jeg er her : httpGet 2");
				InputStream responseStream = con.getInputStream();
				SAXBuilder builder = new SAXBuilder();
				responseDoc = builder.build(responseStream);  
			}
		} 
		catch(JDOMException e) {
			System.out.println("Something went wrong building the response XML-document");
		} 
		catch (IOException e) {
			System.out.println("Something went wrong connecting to the url");
			System.out.println("the message is: " + e.getMessage());
		}
		//Close the connection no matter what. 
		finally{con.disconnect();}
	}

	public int getResponseCode() {
		return responseCode;
	}

	public Document getResponseDoc() {
		return responseDoc;
	}

}