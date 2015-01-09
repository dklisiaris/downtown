package org.dklisiaris.downtown.db;

public class Image {
	int co_id,weight;
	String name;
	
	public Image(int co_id, int weight, String name) {
		super();
		this.co_id = co_id;
		this.weight = weight;
		this.name = name;
	}

	public int getCo_id() {
		return co_id;
	}

	public void setCo_id(int co_id) {
		this.co_id = co_id;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Image [co_id=" + co_id + ", weight=" + weight + ", name="
				+ name + "]";
	}
	
}
