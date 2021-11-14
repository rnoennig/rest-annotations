package de.rnoennig.openapi.annotation.resource;

import de.rnoennig.openapi.generator.Schema;

public class MediaType {
	public final static String APPLICATION_JSON = "application/json";
	/**
	 * application/json
	 */
	String name;
	Schema schema;

	// example
	// examples
	// encoding
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Schema getSchema() {
		return schema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}
}
