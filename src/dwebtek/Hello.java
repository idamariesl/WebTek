package dwebtek;

import javax.ws.rs.GET;
import javax.ws.rs.Path;



@Path("service")
public class Hello{
	@GET
	@Path("hello")
	
	public String printHello(){
		return "Hello  World";
	}

}
