package org.dklisiaris.downtown.db;

public class Banner {
	int cat_id,weight;
	String name;
	boolean isIcon;
	public Banner(int cat_id, int weight, String name, boolean isIcon) {
		super();
		this.cat_id = cat_id;
		this.weight = weight;
		this.name = name;
		this.isIcon = isIcon;
	}
	public int getCat_id() {
		return cat_id;
	}
	public void setCat_id(int cat_id) {
		this.cat_id = cat_id;
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
	public boolean isIcon() {
		return isIcon;
	}
	public void setIcon(boolean isIcon) {
		this.isIcon = isIcon;
	}
	@Override
	public String toString() {
		return "Banner [cat_id=" + cat_id + ", weight=" + weight + ", name="
				+ name + ", isIcon=" + isIcon + "]";
	}
	
}
