package dwebtek;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jdom2.Document;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

@Path("shop")
public class ShopService {
	/**
	 * Our Servlet session. We will need this for the shopping basket
	 */
	HttpSession session;

	public ShopService(@Context HttpServletRequest servletRequest) {
		session = servletRequest.getSession();
	}

	/**
	 * This method takes an arrayList of products 
	 * and put them into a JSONArray as JSONObjects. 
	 * @return a String that contains the values of all the objects.
	 */
	@GET
	@Path("items")
	public String getItems() {
		//You should get the items from the cloud server.upgra
		//In the template we just construct some simple data as an array of objects
		JSONArray array = new JSONArray();
		XMLPackUnpacker pack = new XMLPackUnpacker();
		List<Product> products = pack.unpackItemList();        

		for(Product product : products) {
			JSONObject object = new JSONObject();
			object.put("name", product.getName());
			object.put("price",product.getPrice());
			object.put("description", product.getDescription());
			object.put("id", product.getId());
			object.put("stock", product.getStock());
			array.put(object);	
		}

		//You can create a MessageBodyWriter so you don't have to call toString() every time
		return array.toString();
	}
	
	/**
	 * This method takes FormParameters (it has to be forms when we do a POST),
	 * as Strings, and use them to call the login-method in the XMLPackUnPacker-class. 
	 * The method logs the user in, if the username and password is correct.
	 * @param un
	 * @param pw
	 * @return a string to tell whether the user is logged in or not
	 */
	@POST
	@Path("login")
	public String login(@FormParam("customerName") String un, @FormParam("customerPass") String pw)  {
		//if(session.getAttribute("loginSession")!=null)
		XMLPackUnpacker pack = new XMLPackUnpacker();
		int responseCode = pack.login(un, pw);
		System.out.println(responseCode);
		if(responseCode ==  200){
			return "You are now logged in as: " + un;
		}
		else {
			return "WROONG!";
		}
	}

	/**
	 * If the username isn't taken then the method 
	 * creates a user in the cloud.  
	 * @param un
	 * @param pw
	 * @return a String there tell if the creation of the account succeeded or not.
	 */
	@POST
	@Path("create")
	public String create(@FormParam("customerName") String un, @FormParam("customerPass") String pw) {

		System.out.println(un);
		XMLPackUnpacker pack = new XMLPackUnpacker();
		Document customerDoc = pack.createCustomer(un,pw);
		//if responsecode contains usernameTaken..
		//do some error stuf
		System.out.println(customerDoc==null);
		if(pack.existElement(customerDoc, "usernameTaken")) {
			System.out.println("Find et andet brugernavn");
			return "The username must be taken";
		}
		else {
			System.out.println("Du er nu oprettet");
			session.setAttribute("loginSession", un);
			return "You have now created an account! You username is: " + un;
		}
	}


	@GET
	@Path("hello")	
	public String printHello(){
		return "Hello  World";
	}
}