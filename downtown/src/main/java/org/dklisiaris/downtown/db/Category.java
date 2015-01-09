package org.dklisiaris.downtown.db;

public class Category {
	int cat_id,cat_parent;
	String cat_name,cat_icon;
	
	
	
	public Category() {
		super();
	}

	public Category(int cat_id, String cat_name, int cat_parent) {
		super();
		this.cat_id = cat_id;
		this.cat_parent = cat_parent;
		this.cat_name = cat_name;
	}
		
	public Category(int cat_id, int cat_parent, String cat_name, String cat_icon) {
		super();
		this.cat_id = cat_id;
		this.cat_parent = cat_parent;
		this.cat_name = cat_name;
		this.cat_icon = cat_icon;
	}

	public int getCat_id() {
		return cat_id;
	}

	public void setCat_id(int cat_id) {
		this.cat_id = cat_id;
	}

	public int getCat_parent() {
		return cat_parent;
	}

	public void setCat_parent(int cat_parent) {
		this.cat_parent = cat_parent;
	}

	public String getCat_name() {
		return cat_name;
	}

	public void setCat_name(String cat_name) {
		this.cat_name = cat_name;
	}

	public String getCat_icon() {
		return cat_icon;
	}

	public void setCat_icon(String cat_icon) {
		this.cat_icon = cat_icon;
	}
	
	
}
