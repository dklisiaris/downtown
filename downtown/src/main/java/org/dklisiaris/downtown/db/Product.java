package org.dklisiaris.downtown.db;

import java.util.ArrayList;


/**
 * Deprecated
 * It was used for products in early versions
 * Replaced with the Company Class
 * @author MeeC
 *
 */
public class Product {

	static int total_id=0;
	int id;
	String name, address,tel,description;
	ArrayList<String> image_url;
	String category,subcategory,area;
	boolean avail;
	String website;
	double latitude,longitude;
	
	public Product(){
		this.id=total_id;
		Product.total_id++;
	}
	
	public Product( String name, String address, String tel,
			String description, ArrayList<String> image_url, String category,
			String subcategory, String area, boolean avail, String website,
			double latitude, double longitude) {
		//this.id = id;
		this.name = name;
		this.address = address;
		this.tel = tel;
		this.description = description;
		this.image_url = image_url;
		this.category = category;
		this.subcategory = subcategory;
		this.area = area;
		this.avail = avail;
		this.website = website;
		this.latitude = latitude;
		this.longitude = longitude;
		this.id=total_id;
		Product.total_id++;
		
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
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
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
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSubcategory() {
		return subcategory;
	}
	public void setSubcategory(String subcategory) {
		this.subcategory = subcategory;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public boolean isAvail() {
		return avail;
	}
	public void setAvail(boolean avail) {
		this.avail = avail;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
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

}
