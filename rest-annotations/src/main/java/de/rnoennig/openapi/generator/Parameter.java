package de.rnoennig.openapi.generator;

/**
 * https://swagger.io/specification/#parameter-object
 */
public class Parameter {
	String name;
	/**
	 * Possible values are "query", "header", "path" or "cookie".
	 */
	String in;
	String description;
	Boolean required;
	Schema schema;
}
