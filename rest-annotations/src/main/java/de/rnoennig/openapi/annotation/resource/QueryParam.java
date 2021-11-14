package de.rnoennig.openapi.annotation.resource;

import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryParam {

	/**
	 * 
	 * @return name of the query parameter
	 */
	String value();

}
