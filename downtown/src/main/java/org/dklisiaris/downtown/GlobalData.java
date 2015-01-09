package org.dklisiaris.downtown;


import java.util.ArrayList;

import org.dklisiaris.downtown.db.Company;
import org.dklisiaris.downtown.db.Product;

import android.app.Application;
import android.util.SparseArray;

public class GlobalData extends Application {

	private String category=null,subCategory=null,address=null,product=null;
	private ArrayList<Product> selected_products=null;
	private ArrayList<Product> all_products=null;
	private SparseArray<String> catsMap=null;
	
	private ArrayList<Company> selected_companies=null;
	private String catID=null;

	public GlobalData(){
		super();		
	}
		
	public SparseArray<String> getCatsMap() {
		return catsMap;
	}

	public void setCatsMap(SparseArray<String> catsMap) {
		this.catsMap = catsMap;
	}

	public ArrayList<Product> getAll_products() {
		return all_products;
	}

	public void setAll_products(ArrayList<Product> all_products) {
		this.all_products = all_products;
	}
	
	public ArrayList<Company> getSelected_companies() {
		return selected_companies;
	}
	public void setSelected_companies(ArrayList<Company> selected_companies) {
		this.selected_companies = selected_companies;
	}
	
	public String getCatID() {
		return catID;
	}
	public void setCatID(String catID) {
		this.catID = catID;
	}
	public String getSubCategory() {
		return subCategory;
	}
	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}
	public ArrayList<Product> getSelected_products() {
		return selected_products;
	}
	public void setSelected_products(ArrayList<Product> selected_products) {
		this.selected_products = selected_products;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}

}
