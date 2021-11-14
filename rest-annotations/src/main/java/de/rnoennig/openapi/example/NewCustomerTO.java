package de.rnoennig.openapi.example;

import de.rnoennig.openapi.TransferObject;
import de.rnoennig.openapi.annotation.doc.Description;

@Description(value = "New customer object")
public class NewCustomerTO extends TransferObject {
	
	private String name;
	private Address address;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

}
