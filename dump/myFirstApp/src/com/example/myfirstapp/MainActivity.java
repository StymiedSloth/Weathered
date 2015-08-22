package com.example.myfirstapp;

import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends ActionBarActivity {
    
    private static HashMap<String, Person> contacts = new HashMap<String, Person>();
    CSVAdapter mAdapter;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Lookup our ListView
      		ListView mList = (ListView)findViewById(R.id.listViewMain );
      		
      		//Create Adapter. The second parameter is required by ArrayAdapter
      		//which our Adapter extends. In this example though it is unused,
      		//so we'll pass it a "dummy" value of -1.
      		mAdapter = new CSVAdapter(this, -1);
      		
      		//attach our Adapter to the ListView. This will populate all of the rows.
      		mList.setAdapter(mAdapter);
		
    }



	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
}
