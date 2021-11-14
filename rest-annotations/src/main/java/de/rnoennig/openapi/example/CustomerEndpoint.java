package de.rnoennig.openapi.example;

import java.util.Collections;
import java.util.List;

import de.rnoennig.openapi.annotation.doc.Description;
import de.rnoennig.openapi.annotation.doc.Summary;
import de.rnoennig.openapi.annotation.resource.Consumes;
import de.rnoennig.openapi.annotation.resource.GET;
import de.rnoennig.openapi.annotation.resource.MediaType;
import de.rnoennig.openapi.annotation.resource.POST;
import de.rnoennig.openapi.annotation.resource.Path;
import de.rnoennig.openapi.annotation.resource.PathParam;
import de.rnoennig.openapi.annotation.resource.QueryParam;

@Path("customer")
public class CustomerEndpoint {
	
	public CustomerEndpoint() {
		
	}
	
	@GET
	@Summary("Returns all customers")
	public List<CustomerTO> getAllCustomers(@QueryParam("limit") String limit) {
		System.out.println("CustomerEndpoint::getAllCustomers");
		System.out.println("Query parameter 'limit' was: " + limit);
		return Collections.emptyList();
	}
	
	@Path("{id}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	//@Produces(MediaType.APPLICATION_JSON)
	@Summary("Returns the customer with the given id")
	public CustomerTO getCustomer(@PathParam("id") String id) {
		System.out.println("CustomerEndpoint::getCustomer");
		System.out.println("Path parameter 'id' was: " + id);
		return new CustomerTO();
	}
	
	@POST
	@Summary("Creates a new customer")
	@Description("Creates a new customer if the given data can be validated and no such customer exists yet")
	public CustomerTO createCustomer(NewCustomerTO customer) {
		System.out.println("CustomerEndpoint::createCustomer");
		System.out.println("Address of new customer: " + customer.getAddress());
		privateMethodsWillBeIgnored();
		return new CustomerTO();
	}
	
	private void privateMethodsWillBeIgnored() {
		
	}
}
