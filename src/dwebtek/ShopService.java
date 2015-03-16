package dwebtek;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
		return array.toString();
	}

	/**
	 * This method takes FormParameters (it has to be forms when we do a POST),
	 * as Strings, and use them to call the login-method in the XMLPackUnPacker-class. 
	 * The method logs the user in, if the username and password is correct.
	 * After logging in we set the logginsession to hold the customerID, 
	 * and set the mapsession to null, so each customer gets it's own cart.
	 * @param un username entered at the webpage
	 * @param pw password entered at the webpage
	 * @return a string to tell whether the user is logged in or not
	 */
	@POST
	@Path("login")
	public String login(@FormParam("customerName") String un, @FormParam("customerPass") String pw)  {
		XMLPackUnpacker pack = new XMLPackUnpacker();
		Document loginDoc = pack.login(un, pw);
		if(pack.getResponseCode() ==  200){
			String id = pack.getId(loginDoc);
			session.setAttribute("userID", id);
			session.setAttribute("mapsession", null);
			return "You are now logged in as: " + un;
		}
		else {
			return "Something went wrong trying to log in. Maybe your password is incorrect";
		}
	}

	/**
	 * If the username isn't taken then the method 
	 * creates a user in the cloud.  
	 * After creating a account we set the logginsession to hold the customerID, 
	 * and set the mapsession to null, so each customer gets it's own cart.
	 * @param un
	 * @param pw
	 * @return a String there tell if the creation of the account succeeded or not.
	 */
	@POST
	@Path("create")
	public String create(@FormParam("customerName") String un, @FormParam("customerPass") String pw) {
		XMLPackUnpacker pack = new XMLPackUnpacker();
		Document customerDoc = pack.createCustomer(un,pw);
		if( pack.getResponseCode()== 200){
			String id = pack.getId(customerDoc);
			session.setAttribute("userID", id);
			session.setAttribute("mapsession", null);
			return "You have now created an account! You username is: " + un;
		}
		else {
			return "The username must be taken";			
		}
	}

	/**
	 * Method that returns a int that is the 
	 * customer ID
	 * @return
	 */
	@GET
	@Path("getID")
	public int getId() {
		return Integer.parseInt((String) session.getAttribute("userID"));
	}

	/**
	 * The method sells the items by calling the sellItems-method
	 * in the XMLPackUnPacker-class.
	 * To give the customerID, we call getAtribute on the logginsession.
	 * @return a string to tell the user what was bought. 
	 */
	@POST
	@Path("sellItems")
	public String sellItems() {
		XMLPackUnpacker pack = new XMLPackUnpacker();
		Map<String, Integer> cart = (Map<String, Integer>) session.getAttribute("mapsession");
		Iterator it = cart.entrySet().iterator();
		int customerID = getId();
		String temp = "";

		while (it.hasNext()) {
			Map.Entry mapEntry = (Map.Entry) it.next();
			int id = Integer.parseInt(mapEntry.getKey().toString());
			int amount = Integer.parseInt(mapEntry.getValue().toString());
			Document sellDoc = pack.sellItems(id, customerID, amount);
			if (pack.getResponseCode() == 200 ) {
				if (pack.existElement(sellDoc, "ok")) {
					temp += "You have now bought: " + id + "<br/>";
				}
				else if (pack.existElement(sellDoc, "itemSoldOut")) {
					temp = "Unfortunately, we are out stock of: " + id;
				}
				else {
					temp = "Something went buying your products";
				}
			}	
		}
		session.setAttribute("mapsession", null);
		temp += "<br/> Press the refresh-button to update the list of items"; 
		return temp;
	}


	/**
	 * Saving the items in a HasMap. 
	 * If the mapsession is different from null, we add one of the item to the map.
	 * Else we make a new map, and add one. 
	 * @param itemID the id of the item we want to add in the cart.
	 */
	@POST
	@Path("addToCart")
	public void addToCart(@FormParam("itemID") String itemID) {
		@SuppressWarnings("unchecked")
		Map<String, Integer> cart = (Map<String, Integer>) session.getAttribute("mapsession");
		if(cart != null) {
			if(cart.containsKey(itemID)) {
				int currentvalue = cart.get(itemID);
				cart.put(itemID, currentvalue +1);
			}
			else {
				cart.put(itemID, 1);
			}
		}
		else {
			cart = new HashMap<String, Integer>();
			session.setAttribute("mapsession", cart);
			cart.put(itemID, 1);
		}
		session.setAttribute("mapsession", cart);
	}

	/**
	 * Subtract one items from the cart(hashMap) if there more than 1.
	 * If there only is one, we delete the item from the map.
	 * @param itemID the id of id we want to take away from the cart. 
	 */
	@POST
	@Path("subFromCart")
	public void subFromCart(@FormParam("itemID") String itemID) {
		@SuppressWarnings("unchecked")
		Map<String, Integer> cart = (Map<String, Integer>) session.getAttribute("mapsession");
		if(cart != null) {
			if(cart.containsKey(itemID) &&  cart.get(itemID) == 1) {
				cart.remove(itemID);
			}
			else if (cart.containsKey(itemID)) {
				int currentvalue = cart.get(itemID);
				cart.put(itemID, currentvalue -1);
			}
		}
		else {
			cart = new HashMap<String, Integer>();
			session.setAttribute("mapsession", cart);
			cart.put(itemID, 1);
		}
	}
}