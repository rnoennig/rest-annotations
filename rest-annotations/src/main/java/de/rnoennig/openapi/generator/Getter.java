package de.rnoennig.openapi.generator;

public class Getter {

	private String name;
	private Class<?> type;

	public Getter(Class<?> type, String name) {
		this.type = type;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Class<?> getType() {
		return type;
	}

	@Override
	public String toString() {
		return "Getter [name=" + name + ", type=" + type + "]";
	}

}
