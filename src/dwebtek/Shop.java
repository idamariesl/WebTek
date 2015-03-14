package dwebtek;

public class Shop {

	private int shopID;
	private String shopName;
	private String shopURL;
	
	public Shop(int shopID, String shopName, String shopURL) {
		this.shopID = shopID;
		this.shopName = shopName;
		this.shopURL = shopURL;
	}
	
	public String getName() {
		return shopName;
	}
	
	public int getID() {
		return shopID;
	}
	
	public String getURL() {
		return shopURL;
	}
}
