package de.rnoennig.openapi.annotation.doc;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ TYPE, METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {

	String value();

}
