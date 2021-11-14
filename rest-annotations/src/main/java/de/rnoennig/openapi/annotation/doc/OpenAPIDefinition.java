package de.rnoennig.openapi.annotation.doc;

public @interface OpenAPIDefinition {

	Info info();

	Server[] servers();

}
