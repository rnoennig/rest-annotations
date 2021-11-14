package de.rnoennig.openapi.generator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectUtil {

	public static List<Getter> getGetters(Class<?> clazz) {
		List<Getter> result = new ArrayList<>();
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().startsWith("get") && method.getParameterCount() == 0) {
				String name = method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4);
				Class<?> type = method.getReturnType();
				result.add(new Getter(type, name));
			}
		}
		return result;
	}

	public static String getDefinition(Class<?> clazz) {
		return "#/definitions/"+clazz.getSimpleName();
	}

}
