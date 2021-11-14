package de.rnoennig.openapi.example;

import de.rnoennig.openapi.annotation.doc.Contact;
import de.rnoennig.openapi.annotation.doc.Info;
import de.rnoennig.openapi.annotation.doc.License;
import de.rnoennig.openapi.annotation.doc.OpenAPIDefinition;
import de.rnoennig.openapi.annotation.doc.Server;

@OpenAPIDefinition(
		  info = @Info(
		  title = "Code-First Approach (reflectoring.io)",
		  description = "" +
		    "Lorem ipsum dolor ...",
		  contact = @Contact(
		    name = "Reflectoring", 
		    url = "https://reflectoring.io", 
		    email = "petros.stergioulas94@gmail.com"
		  ),
		  license = @License(
		    name = "MIT Licence", 
		    url = "https://github.com/thombergs/code-examples/blob/master/LICENSE")),
		  servers = @Server(url = "http://localhost:8080")
		)
public class OpenApiDef {

}
