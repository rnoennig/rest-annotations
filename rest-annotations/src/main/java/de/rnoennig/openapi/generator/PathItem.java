package de.rnoennig.openapi.generator;

import java.lang.reflect.Method;
import java.util.List;

/**
 * https://swagger.io/specification/#path-item-object
 */
public class PathItem {
	String path;
	List<Parameter> parameters;
	Operation get;
	Operation put;
	Operation post;
	Operation delete;
	Operation patch;
	RequestBody requestBody;
	List<Response> responses;
	
	/**
	 * for dispatching
	 */
	Method method;
	@Override
	public String toString() {
		return "PathItem [path=" + path + "]";
	}

}
