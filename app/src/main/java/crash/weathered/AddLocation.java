package crash.weathered;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class AddLocation extends ActionBarActivity {
    private EditText editText;
    private ImageView closebtn, addbtn;
    private final String MY_PREFS_NAME = "settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        editText = (EditText) findViewById(R.id.addnewlocation);
        closebtn = (ImageView) findViewById(R.id.close_btn);
        addbtn = (ImageView) findViewById(R.id.add_btn);
        // Request focus and show soft keyboard automatically
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddLocation.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCityFinal(editText.getText().toString());
            }
        });

    }

    private void addCityFinal(String location)
    {
        if(location.isEmpty())
        {
            Toast.makeText(this,"Yo! The location can't be empty.",Toast.LENGTH_SHORT).show();
        }
        else {
            commitLocationsList(location);
            Intent i = new Intent(AddLocation.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

    private HashMap<String,String> retrieveLocationsList()
    {
        try {
            File file = new File(getDir("data", MODE_PRIVATE), "map");
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
            HashMap<String,String> temp = (HashMap<String,String>)inputStream.readObject();
            inputStream.close();
            return temp;
        }
        catch (IOException fnf) {
            Toast.makeText(AddLocation.this,
                    "Something went wrong,the app couldn't find the location. Try again later.",
                    Toast.LENGTH_LONG).show();
        } catch (ClassNotFoundException e) {
            commitLocationsList("Dallas, US");
        }
        return new HashMap<String,String>();
    }

    private void commitLocationsList(String city)
    {
        HashMap<String, String> map = retrieveLocationsList();

        if(!map.containsKey(city.toLowerCase().trim().replace(' ','_'))) {
            map.put(city.toLowerCase().trim().replace(' ', '_'), city);

            try {
                File file = new File(getDir("data", MODE_PRIVATE), "map");
                ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
                outputStream.writeObject(map);
                outputStream.flush();
                outputStream.close();
            } catch (IOException fnf) {
                Toast.makeText(AddLocation.this,
                        "Something went wrong,the app couldn't find the location. Try again later.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void location_click(View view) {
        switch(view.getId()){
            case R.id.london_btn:
                addCityFinal("London, UK");
                break;
            case R.id.san_fran_btn:
                addCityFinal("San Francisco, US");
                break;
            case R.id.tokyo_btn:
                addCityFinal("Tokyo, JP");
                break;
            case R.id.nyc_btn:
                addCityFinal("New York, US");
                break;
            case R.id.paris_btn:
                addCityFinal("Paris, FR");
                break;
            case R.id.sydney_btn:
                addCityFinal("Sydney, AU");
                break;

        }

    }
}
