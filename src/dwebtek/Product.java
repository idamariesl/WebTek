package dwebtek;

import java.io.Serializable;

/**
 * A Product object represents an item in the webshop. 
 * @author Andreas
 *
 */
public class Product implements Serializable {


	private static final long serialVersionUID = 1L;
	private String descriptionXML;
	private String name;
	private String description;
	private String url;
	private int id;
	private int price;
	private int stock;
	
	public Product() {
		super();
	}

	public Product(String id, String name, String url, String price, String stock, String description, String descriptionXML){
		this.id = Integer.parseInt(id);
		this.name = name;
		this.url = url;
		this.price = Integer.parseInt(price);
		this.stock = Integer.parseInt(stock);
		this.description = description;
		this.descriptionXML = descriptionXML;
	}

	
	//Below is the getters and setters for the field variables of Product.
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getStock() {
		return stock;
	}
	public void setStock(int stock) {
		this.stock = stock;
	}
	public String getDescriptionXML() {
		return descriptionXML;
	}
	public void setDescriptionXML(String descriptionXML) {
		this.descriptionXML = descriptionXML;
	}
}