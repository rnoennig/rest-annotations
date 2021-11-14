package de.rnoennig.openapi.generator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import de.rnoennig.openapi.TransferObject;
import de.rnoennig.openapi.example.CustomerEndpoint;
import de.rnoennig.openapi.example.OpenApiDef;

/**
 * Scans endpoint resources and creates rest operation paths and schemna definitions
 */
public class Dispatcher {
	List<PathItem> paths;

	/**
	 * key is same as schema.$ref, e.g.
	 * "#/definitions/"+requestBodyClass.getSimpleName()
	 */
	Map<String, Schema> definitions;

	public Dispatcher() {
		paths = new ArrayList<>();
		definitions = new HashMap<>();
	}

	public static void main(String[] args) {
		RestApiParser apiParser = new RestApiParser();
		apiParser.addDefinition(OpenApiDef.class);
		apiParser.addResource(CustomerEndpoint.class);
		Dispatcher generator = new Dispatcher();
		generator.setPaths(apiParser.getPaths());
		generator.setDefinitions(apiParser.getDefinitions());
		testCreateNewCustomer(generator);
	}

	private void setDefinitions(Map<String, Schema> definitions) {
		this.definitions = definitions;
	}

	private void setPaths(List<PathItem> paths) {
		this.paths = paths;
	}

	@SuppressWarnings("unused")
	private static void testGetAllCustomers(Dispatcher generator) {
		String method = "GET";
		String path = "/customer?limit=250";
		String requestBody = null;
		generator.dispatch(method, path, requestBody);
	}

	@SuppressWarnings("unused")
	private static void testGetCustomer(Dispatcher generator) {
		String method = "GET";
		String path = "/customer/100";
		String requestBody = null;
		generator.dispatch(method, path, requestBody);
	}

	@SuppressWarnings("unused")
	private static void testCreateNewCustomer(Dispatcher generator) {
		String method = "POST";
		String path = "/customer";
		String requestBody = "{\n" + "    \"name\": \"Clint Nuvo\",\n" + "    \"address\":{\n"
				+ "      \"zipcode\": \"10117\",\n" + "      \"streetNumber\": 10,\n" + "      \"city\": \"Berlin\",\n"
				+ "      \"street\": \"Maikäferpfad\"\n" + "    }\n" + "}";
		generator.dispatch(method, path, requestBody);
	}

	private String dispatch(String method, String path, String requestBody) {
		// find method
		for (PathItem pathItem : paths) {
			System.out.println("Pathitem is " + pathItem);
			if (pathItem.get != null && !method.equals("GET")) {
				// wrong method
				continue;
			}
			if (pathItem.post != null && !method.equals("POST")) {
				// wrong method
				continue;
			}
			// TODO add other http methods checks, e.g. put and patch

			Map<String, Object> pathParams = extractPathParameters(pathItem, path);
			Map<String, Object> queryParams = extractQueryParameters(pathItem, path);
			// TODO differentiate between required and optional parameters
			if (pathParams.size() + queryParams.size() == pathItem.parameters.size()) {
				System.out.println("Would dispatch to " + path);
				try {
					System.out.println("newIsntance is " + pathItem.method.getDeclaringClass());
					Object newInstance = pathItem.method.getDeclaringClass().getDeclaredConstructor().newInstance();

					List<Object> args = new ArrayList<>();

					if (pathItem.parameters != null) {
						for (de.rnoennig.openapi.generator.Parameter param : pathItem.parameters) {
							if (param.in.equals("path")) {
								args.add(pathParams.get(param.name));
							} else {
								args.add(queryParams.get(param.name));
							}
						}
					}

					if (requestBody != null) {
						Parameter requestBodyParameter = pathItem.method
								.getParameters()[pathItem.method.getParameterCount() - 1];
						@SuppressWarnings("unchecked")
						TransferObject tranferObject = parseJSON(requestBody,
								(Class<? extends TransferObject>) requestBodyParameter.getType());
						args.add(tranferObject);
					}
					pathItem.method.invoke(newInstance, args.toArray());
					break;
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| InstantiationException | SecurityException | NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		// TODO validate params
		// validate body
		// call method and get result
		// TODO convert result into json
		String json = "{}";
		return json;
	}

	private Map<String, Object> extractQueryParameters(PathItem pathItem, String path) {
		Map<String, Object> params = new HashMap<>();
		String query = URI.create(path).getRawQuery();
		if (query != null) {
			String[] keyValuePairs = query.split("&");
			for (String keyValuePair : keyValuePairs) {
				String[] keyValuePairSplitted = keyValuePair.split("=");
				params.put(keyValuePairSplitted[0], keyValuePairSplitted[1]);
			}
		}
		return params;
	}

	private TransferObject parseJSON(String requestBody, Class<? extends TransferObject> type)
			throws IllegalAccessException, InstantiationException, NoSuchMethodException, SecurityException,
			IllegalArgumentException, InvocationTargetException {
		JSONObject requestBodyJSON = new JSONObject(requestBody);
		TransferObject newInstance = (TransferObject) assignValue(requestBodyJSON, type);
		return newInstance;
	}

	private Object assignValue(Object value, Class<?> targetType) throws IllegalAccessException, InstantiationException,
			NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		if (targetType.isAssignableFrom(String.class) || targetType.isAssignableFrom(Integer.class)) {
			return value;
		} else if (value instanceof JSONObject) {
			Object targetInstance = targetType.getDeclaredConstructor().newInstance();
			for (String key : ((JSONObject) value).keySet()) {
				System.out.println("key is " + key);
				String setterMethodName = "set" + String.valueOf(key.charAt(0)).toUpperCase() + key.substring(1);
				for (Method method : targetInstance.getClass().getMethods()) {
					if (method.getName().equals(setterMethodName)) {
						Object property = assignValue(((JSONObject) value).get(key),
								method.getParameters()[0].getType());
						method.invoke(targetInstance, property);
						break;
					}
				}
			}
			return targetInstance;
		} else if (value instanceof JSONArray) {
			// TODO support lists
			throw new RuntimeException("not implemented yet");
		} else {
			throw new RuntimeException("not implemented yet, type was " + targetType);
		}
	}

	private Map<String, Object> extractPathParameters(PathItem pathItem, String requestPath) {
		String[] pathItemPathSegments = pathItem.path.split("/");
		String[] requestPathSegments = requestPath.split("/");

		Map<String, Object> params = new HashMap<>();
		if (pathItemPathSegments.length != requestPathSegments.length) {
			return params;
		}
		Pattern paramRegex = Pattern.compile("\\{(\\w+)\\}");

		for (int i = 0; i < pathItemPathSegments.length; i++) {
			String pathItemPathSegment = pathItemPathSegments[i];
			String requestPathSegment = requestPathSegments[i];

			if (pathItemPathSegment.contains("{")) {
				Matcher matcher = paramRegex.matcher(pathItemPathSegment);
				if (matcher.find()) {
					String paramName = matcher.group(1);
					params.put(paramName, requestPathSegment);
				}
			}
		}
		return params;
	}

}
