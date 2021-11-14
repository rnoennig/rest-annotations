package de.rnoennig.openapi.generator;

public class Yaml {
	int indentLevel;
	StringBuilder sb;
	
	public Yaml() {
		indentLevel = 0;
		sb = new StringBuilder();
	}

	public void increaseIndentation() {
		indentLevel++;
	}

	public void decreaseIndentation() {
		indentLevel--;
	}

	public void addNewLine() {
		sb.append(System.lineSeparator());
	}

	public void append(String value) {
		addIndentation();
		sb.append(value);
	}

	public void appendWithoutIndentation(String value) {
		sb.append(value); 
	}
	
	@Override
	public String toString() {
		return sb.toString();
	}
	
	private void addIndentation() {
		for (int i=0; i < indentLevel; i++) {
			for (int ii=0; ii < 2; ii++) {
				sb.append(" "); 
			}
		}
	}

	public void appendKeyValue(String key, String value) {
		append(key);
		appendWithoutIndentation(": ");
		appendWithoutIndentation(value);
		addNewLine();
	}

	public void appendKey(String key) {
		append(key);
		appendWithoutIndentation(":");
		increaseIndentation();
		addNewLine();
	}
}
