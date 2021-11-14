package de.rnoennig.openapi.generator;

import java.util.Map;

/**
 * https://swagger.io/specification/#schema-object
 */
public class Schema {
	// - - - nullable
	/**
	 * integer
	 */
	String type;
	/**
	 * int64
	 */
	String format;
	
	String $ref;
	// - - - minimum: 1
	Map<String, Schema> properties;
}
