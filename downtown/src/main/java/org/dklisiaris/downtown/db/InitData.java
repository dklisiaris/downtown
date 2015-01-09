package org.dklisiaris.downtown.db;

import java.util.ArrayList;

public class InitData {
	String lastUpdate;
	ArrayList<String> init_images;
	ArrayList<String> favs;
	
	public InitData() {
		super();
	}
	public InitData(String lastUpdate, ArrayList<String> init_images) {
		super();
		this.lastUpdate = lastUpdate;
		this.init_images = init_images;
	}
	
	public InitData(String lastUpdate, ArrayList<String> init_images,
			ArrayList<String> favs) {
		super();
		this.lastUpdate = lastUpdate;
		this.init_images = init_images;
		this.favs = favs;
	}
	public String getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public ArrayList<String> getInit_images() {
		return init_images;
	}
	public void setInit_images(ArrayList<String> init_images) {
		this.init_images = init_images;
	}
	public ArrayList<String> getFavs() {
		return favs;
	}
	public void setFavs(ArrayList<String> favs) {
		this.favs = favs;
	}
	@Override
	public String toString() {
		return "InitData [lastUpdate=" + lastUpdate + ", init_images="
				+ init_images + ", favs=" + favs + "]";
	}
	
	
}
