package de.rnoennig.openapi.generator;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.rnoennig.openapi.TransferObject;
import de.rnoennig.openapi.annotation.doc.Description;
import de.rnoennig.openapi.annotation.doc.Summary;
import de.rnoennig.openapi.annotation.resource.GET;
import de.rnoennig.openapi.annotation.resource.MediaType;
import de.rnoennig.openapi.annotation.resource.POST;
import de.rnoennig.openapi.annotation.resource.Path;
import de.rnoennig.openapi.annotation.resource.PathParam;
import de.rnoennig.openapi.annotation.resource.QueryParam;
import de.rnoennig.openapi.example.CustomerEndpoint;
import de.rnoennig.openapi.example.OpenApiDef;

/**
 * Scans endpoint resources and creates rest operation paths and schemna definitions
 */
public class RestApiParser {
	List<PathItem> paths;

	/**
	 * key is same as schema.$ref, e.g.
	 * "#/definitions/"+requestBodyClass.getSimpleName()
	 */
	Map<String, Schema> definitions;

	public RestApiParser() {
		paths = new ArrayList<>();
		definitions = new HashMap<>();
	}

	public static void main(String[] args) {
		RestApiParser generator = new RestApiParser();
		generator.addDefinition(OpenApiDef.class);
		generator.addResource(CustomerEndpoint.class);
	}

	public void addDefinition(Class<?> definition) {
		// TODO Auto-generated method stub
	}

	/**
	 * Scan all methods of the resource
	 * 
	 * @param resource
	 */
	public void addResource(Class<?> resource) {
		Method[] declaredMethods = resource.getDeclaredMethods();
		for (Method method : declaredMethods) {
			addResourceMethod(resource, method);
		}
	}

	private void addResourceMethod(Class<?> resource, Method method) {
		// TODO path should be definition + resource + operation level
		// doc infos on the resource level
		String resourcePath = null;
		Path resourcePathAnnotation = resource.getAnnotation(Path.class);
		if (resourcePathAnnotation != null) {
			resourcePath = "/" + resourcePathAnnotation.value();
		}

		if (Modifier.isPrivate(method.getModifiers())) {
			return;
		}

		PathItem pathItem = new PathItem();
		pathItem.method = method;

		Operation op = new Operation();
		if (method.getAnnotation(GET.class) != null) {
			pathItem.get = op;
		}
		if (method.getAnnotation(POST.class) != null) {
			pathItem.post = op;
		}

		Path operationPathAnnotation = method.getAnnotation(Path.class);
		String operationPath = null;
		if (resourcePath != null) {
			operationPath = resourcePath;
		}
		if (operationPathAnnotation != null) {
			operationPath += "/" + operationPathAnnotation.value();
		} else {
			System.out.println("keine Path annotation an method " + method);
		}

		pathItem.path = operationPath;

		Summary summary = method.getAnnotation(Summary.class);
		if (summary != null) {
			op.summary = summary.value();
		}

		Description description = method.getAnnotation(Description.class);
		if (description != null) {
			op.description = description.value();
		}

		pathItem.parameters = new ArrayList<>();
		Parameter[] parameters = method.getParameters();
		for (Parameter parameter : parameters) {
			addResourceMethodParameter(resource, method, pathItem, parameter);
		}

		paths.add(pathItem);
	}

	private void addResourceMethodParameter(Class<?> resource, Method method, PathItem pathItem, Parameter parameter) {
		de.rnoennig.openapi.generator.Parameter apiParameter = new de.rnoennig.openapi.generator.Parameter();
		PathParam pathParam = parameter.getAnnotation(PathParam.class);
		QueryParam queryParam = parameter.getAnnotation(QueryParam.class);
		if (pathParam != null) {
			apiParameter.in = "path";
		} else if (queryParam != null) {
			apiParameter.in = "query";
		}

		if (pathParam != null || queryParam != null) {
			apiParameter.name = "id";
			apiParameter.schema = new Schema();
			apiParameter.schema.type = "integer";
			apiParameter.schema.format = "int32";
			pathItem.parameters.add(apiParameter);
		} else {
			System.out.println("Trying to resolve " + parameter);
			// expect transfer object as request body
			pathItem.requestBody = new RequestBody();
			@SuppressWarnings("unchecked")
			Class<? extends TransferObject> requestBodyClass = (Class<? extends TransferObject>) parameter.getType();
			Description description = requestBodyClass.getAnnotation(Description.class);
			if (description != null) {
				pathItem.requestBody.description = description.value();
			}
			pathItem.requestBody.content = new HashMap<>();
			MediaType mediaType = new MediaType();

			Schema requestBodySchema = createSchema(requestBodyClass);
			mediaType.setSchema(requestBodySchema);
			pathItem.requestBody.content.put(mediaType.getName(), mediaType);
		}
	}

	private Schema createSchema(Class<?> clazz) {
		Schema schema = new Schema();

		// FIXME possible package conflicts
		String ref = ReflectUtil.getDefinition(clazz);
		if (definitions.get(ref) != null) {
			schema.$ref = ref;
			return schema;
		}

		schema.type = convertJavaTypeToJSONSchemaType(clazz);
		if (schema.type.equals("object")) {
			schema.properties = new HashMap<>();

			List<Getter> getters = ReflectUtil.getGetters(clazz);
			System.out.println("Iterating over getters for " + clazz);
			for (Getter getter : getters) {
				System.out.println("Iterating over getter " + getter);
				Schema memberSchema = createSchema(getter.getType());
				schema.properties.put(getter.getName(), memberSchema);
			}

			if (definitions.get(ref) == null) {
				definitions.put(ref, schema);
				schema = new Schema();
				schema.$ref = ref;
			}
		} else {
			System.out.println("Creating primitive schema for class " + clazz);
		}

		return schema;
	}

	// TODO "array", "number"
	// TODO primitive types
	private String convertJavaTypeToJSONSchemaType(Class<?> javaType) {
		String javaTypeName = javaType.getSimpleName();
		if (javaTypeName.equals("String")) {
			return "string";
		}
		if (javaTypeName.equals("Integer")) {
			return "number";
		}
		System.out.println("Type " + javaType + " resolved to 'object'");
		return "object";
	}

	public List<PathItem> getPaths() {
		return paths;
	}

	public Map<String, Schema> getDefinitions() {
		return definitions;
	}

}
