package org.dklisiaris.downtown.maps;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class CompanyMarker implements ClusterItem {	
	private int id;
	private String title, snippet;
	private final LatLng mPosition;
	
	public CompanyMarker(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }
	
	public CompanyMarker(int id, String title, String snippet, LatLng mPosition) {
		super();
		this.id = id;
		this.title = title;
		this.snippet = snippet;
		this.mPosition = mPosition;
	}
	
	public CompanyMarker(int id, String title, String snippet, double lat, double lng) {
		super();
		this.id = id;
		this.title = title;
		this.snippet = snippet;
		this.mPosition = new LatLng(lat, lng);
	}
	
	@Override
	public LatLng getPosition() {		
		return mPosition;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	

}
