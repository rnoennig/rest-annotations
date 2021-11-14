package de.rnoennig.openapi.generator;

import java.util.Map;

import de.rnoennig.openapi.annotation.resource.MediaType;

public class RequestBody {
	String description;
	/**
	 * 
	 */
	Map<String, MediaType> content;
	boolean required;
}
