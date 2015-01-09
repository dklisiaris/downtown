package org.dklisiaris.downtown;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import org.dklisiaris.downtown.R;

public class TestActivity extends ActionBarActivity  {
	
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     
        setContentView(R.layout.test);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Test");
                

	}
	

}
