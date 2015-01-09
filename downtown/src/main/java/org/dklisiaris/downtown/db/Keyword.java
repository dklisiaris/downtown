package org.dklisiaris.downtown.db;

public class Keyword {
	int id;
	String name,type;
	
	public Keyword(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Keyword(int id, String name, String type) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
		
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Keyword [id=" + id + ", name=" + name + "]";
	}
	
	
}
