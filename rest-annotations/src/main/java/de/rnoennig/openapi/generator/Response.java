package de.rnoennig.openapi.generator;

import de.rnoennig.openapi.annotation.resource.MediaType;

public class Response {
	/**
	 * 200 or default
	 */
	String statusCode;
	String description;
	MediaType content;
}
