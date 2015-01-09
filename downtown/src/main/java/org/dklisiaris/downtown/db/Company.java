package org.dklisiaris.downtown.db;

import java.util.ArrayList;

public class Company {

	int id;
	String name, address,description;
	ArrayList<String> image_url;
	ArrayList<String> tels;
	ArrayList<String> subcategories;
	String area,county,tk;
	int category,level,avail;
	String website,fax;
	double latitude,longitude;
	String first_img;

	
	public Company() {
		super();
	}

	public Company(int id, String name, String address,
			String description, ArrayList<String> image_url,
			ArrayList<String> tels, ArrayList<String> subcategories,
			int category, String area, String county,
			String tk, int level, int avail, String website, String fax,
			double latitude, double longitude) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.description = description;
		this.image_url = image_url;
		this.tels = tels;
		this.subcategories = subcategories;
		this.category = category;
		this.area = area;
		this.county = county;
		this.tk = tk;
		this.level = level;
		this.avail = avail;
		this.website = website;
		this.fax = fax;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	
		
	public Company(int id, String name, String address, String description,
			ArrayList<String> image_url, ArrayList<String> tels,
			ArrayList<String> subcategories, String area, String county,
			String tk, int category, int level, int avail, String website,
			String fax, double latitude, double longitude, String first_img) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.description = description;
		this.image_url = image_url;
		this.tels = tels;
		this.subcategories = subcategories;
		this.area = area;
		this.county = county;
		this.tk = tk;
		this.category = category;
		this.level = level;
		this.avail = avail;
		this.website = website;
		this.fax = fax;
		this.latitude = latitude;
		this.longitude = longitude;
		this.first_img = first_img;
	}

	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public ArrayList<String> getImage_url() {
		return image_url;
	}
	public void setImage_url(ArrayList<String> image_url) {
		this.image_url = image_url;
	}
	public ArrayList<String> getTels() {
		return tels;
	}
	public void setTels(ArrayList<String> tels) {
		this.tels = tels;
	}
	public ArrayList<String> getSubcategories() {
		return subcategories;
	}
	public void setSubcategories(ArrayList<String> subcategories) {
		this.subcategories = subcategories;
	}
	public int getCategory() {
		return category;
	}
	public void setCategory(int category) {
		this.category = category;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public String getTk() {
		return tk;
	}
	public void setTk(String tk) {
		this.tk = tk;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}	
	public int getAvail() {
		return avail;
	}
	public void setAvail(int avail) {
		this.avail = avail;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getFirst_img() {
		return first_img;
	}

	public void setFirst_img(String first_img) {
		this.first_img = first_img;
	}
	
	
}
