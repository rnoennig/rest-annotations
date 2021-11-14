package de.rnoennig.openapi.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.rnoennig.openapi.example.CustomerEndpoint;
import de.rnoennig.openapi.example.OpenApiDef;

public class OpenApiGenerator {
	List<PathItem> paths;

	/**
	 * key is same as schema.$ref, e.g.
	 * "#/definitions/"+requestBodyClass.getSimpleName()
	 */
	Map<String, Schema> definitions;

	public OpenApiGenerator() {
		paths = new ArrayList<>();
		definitions = new HashMap<>();
	}

	public static void main(String[] args) {
		RestApiParser apiParser = new RestApiParser();
		apiParser.addDefinition(OpenApiDef.class);
		apiParser.addResource(CustomerEndpoint.class);
		OpenApiGenerator generator = new OpenApiGenerator();
		generator.setPaths(apiParser.getPaths());
		generator.setDefinitions(apiParser.getDefinitions());
		String yaml = generator.toYaml();
		System.out.println("yaml result:");
		System.out.println(yaml);
	}

	private void setDefinitions(Map<String, Schema> definitions) {
		this.definitions = definitions;
	}

	private void setPaths(List<PathItem> paths) {
		this.paths = paths;
	}

	public String toYaml() {
		Yaml yaml = new Yaml();

		// TODO add openapi, info, servers

		yaml.appendKey("definitions");
		for (Map.Entry<String, Schema> definition : definitions.entrySet()) {
			Schema schema = definition.getValue();
			if (schema == null) {
				throw new IllegalStateException("Schema was null for defintion " + definition.getKey());
			}
			yaml.appendKey(definition.getKey().substring(definition.getKey().lastIndexOf('/') + 1));
			appendSchema(yaml, schema);
			yaml.decreaseIndentation();
		}
		yaml.decreaseIndentation();

		yaml.appendKey("paths");

		for (PathItem pathItem : paths) {
			yaml.append(pathItem.path);
			yaml.appendWithoutIndentation(":");
			yaml.addNewLine();
			yaml.increaseIndentation();

			Operation op = null;
			if (pathItem.post != null) {
				op = pathItem.post;
				yaml.append("post:");
			}
			if (pathItem.get != null) {
				op = pathItem.get;
				yaml.append("get:");
			}
			yaml.addNewLine();
			yaml.increaseIndentation();

			yaml.appendKeyValue("summary", op.summary);

			// each param
			if (pathItem.parameters != null && !pathItem.parameters.isEmpty()) {
				yaml.appendKey("parameters");

				for (de.rnoennig.openapi.generator.Parameter param : pathItem.parameters) {
					yaml.appendKeyValue("- in", param.in);
					yaml.increaseIndentation();
					yaml.appendKeyValue("name", param.name);

					yaml.appendKey("schema");

					yaml.appendKeyValue("type", param.schema.type);
					yaml.appendKeyValue("format", param.schema.format);

					yaml.decreaseIndentation();
					yaml.decreaseIndentation();
				}
			}
			yaml.decreaseIndentation();

			yaml.appendKey("requestBody");
			// TODO hier weiter
			yaml.appendKeyValue("description", "bla");

			yaml.appendKey("content");
			yaml.appendKey("application/json");
			yaml.appendKey("schema");

			yaml.appendKeyValue("type", "typi");
			yaml.appendKeyValue("format", "forma");

			yaml.decreaseIndentation();
			yaml.decreaseIndentation();
			yaml.decreaseIndentation();

			yaml.decreaseIndentation();

			yaml.decreaseIndentation();
			yaml.decreaseIndentation();
		} // for path in paths

		return yaml.toString();
	}

	/**
	 * Recursive rendering of nested schemas
	 * 
	 * @param yaml
	 * @param schema
	 */
	private void appendSchema(Yaml yaml, Schema schema) {
		if (schema.$ref != null) {
			yaml.appendKeyValue("$ref", schema.$ref);
		} else {
			if (schema.properties != null) {
				yaml.appendKeyValue("type", "\"object\"");
				yaml.appendKey("properties");
				for (Entry<String, Schema> prop : schema.properties.entrySet()) {
					yaml.appendKey(prop.getKey());
					appendSchema(yaml, prop.getValue());
					yaml.decreaseIndentation();
				}
				yaml.decreaseIndentation();
			} else {
				// "boolean", "object", "array", "number", or "string"
				yaml.appendKey("type");
				yaml.decreaseIndentation();
				yaml.appendKey("format");
				yaml.decreaseIndentation();
			}
		}
	}

}
