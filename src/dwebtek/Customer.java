package dwebtek;

public class Customer {
	private int customerID;
	private String customerName; 
	
	public Customer(int customerID, String customerName) {
		this.customerID = customerID;
		this.customerName = customerName;
	}

	public int getID() {
		return customerID;
	}
	
	public String getName() {
		return customerName;
	}
}
