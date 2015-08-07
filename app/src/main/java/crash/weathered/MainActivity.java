package crash.weathered;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class MainActivity extends ActionBarActivity implements NavigationDrawerCallbacks {
    private final String MY_PREFS_NAME = "settings";
    private List<DailyWeather> result;
    RecyclerView recList;
    private Toolbar mToolbar;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    Handler handler;
    private TextView weather_city;
    private TextView location;
    private TextView weather_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();
        weather_city = (TextView) findViewById(R.id.weather_city);
        location = (TextView) findViewById(R.id.location);
        weather_type = (TextView) findViewById(R.id.weather_type);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);


        recList = (RecyclerView) findViewById(R.id.dailyList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recList.setLayoutManager(llm);
//        createList(7);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this , AddLocation.class);
                startActivity(i);
            }
        });


        findViewById(R.id.loading).setVisibility(View.VISIBLE);
        if(retrieveLocationsList().isEmpty())
        {
            commitLocationsList("Dallas, US");
            updateWeatherData("Dallas, US");
        }
        else
            onNavigationDrawerItemSelected(0, "Dallas, US");
    }

    private void updateWeatherData(final String city){
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetch.getJSON(MainActivity.this, city,"current_weather");
                final JSONObject json_f = RemoteFetch.getJSON(MainActivity.this, city,"forecast");
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(MainActivity.this,
                                    "Place not found",
                                    Toast.LENGTH_LONG).show();
                            findViewById(R.id.loading).setVisibility(View.INVISIBLE);
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            renderCurrentWeather(json);
                            renderForecast(json_f);
                            findViewById(R.id.loading).setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderCurrentWeather(JSONObject json){
        try {
            location.setText(json.getString("name"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");

            weather_city.setText(
                    String.format("%.0f°", main.getDouble("temp")));

            weather_type.setText(details.getString("main"));

            ((ImageView) findViewById(R.id.background_image)).setImageDrawable(
                    getResources().getDrawable(chooseWeatherBackground(details.getInt("id"))));

        }catch(Exception e){
            Log.e("Weathered", "One or more fields not found in the JSON data");
        }
    }

    private void renderForecast(JSONObject json){
        try {
            if(findViewById(R.id.dailyList) !=null) {
                result = new ArrayList<DailyWeather>();
                Date d;
                Calendar cal = Calendar.getInstance();
                for (int i = 0; i < 7; i++) {
                    JSONObject details = json.getJSONArray("list").getJSONObject(i);
                    JSONObject temp = details.getJSONObject("temp");
                    JSONObject weather = details.getJSONArray("weather").getJSONObject(0);
                    DailyWeather dw;
                    dw = new DailyWeather(String.format("%.0f°", temp.getDouble("day")),
                            getResources().getDrawable(chooseWeatherIcon(weather.getInt("id"))),
                            (cal.get(Calendar.MONTH) +1)  + "/" +cal.get(Calendar.DAY_OF_MONTH));

                    result.add(dw);
                    cal.add(Calendar.DATE, 1);
                }

                WeatherAdapter wa = new WeatherAdapter(result);

                ((RecyclerView) findViewById(R.id.dailyList)).setAdapter(wa);
                playTransition();
            }

        }catch(Exception e){
            Log.e("Weathered", "One or more fields not found in the JSON data");
        }
    }

    private int chooseWeatherIcon(int wid)
    {
            if(wid>=200 && wid<=232)
                return R.drawable.ic_thunderstorms;
            else if(wid>=300 && wid<=321)
                return R.drawable.ic_rain;
            else if(wid>=500 && wid<=531)
                return R.drawable.ic_rain;
            else if(wid>=600 && wid<=622)
                return R.drawable.ic_snow;
            else if(wid>=701 && wid<=781)
                return R.drawable.ic_fog;
            else if(wid>=800 && wid<=803)
                return R.drawable.ic_partially_cloudy;
            else if(wid==804)
                return R.drawable.ic_cloudy;
            else if(wid>=900 && wid<=906)
                return R.drawable.ic_thunderstorms;
            else
                return R.drawable.ic_close;
    }

    private int chooseWeatherBackground(int wid)
    {
        recList = (RecyclerView) findViewById(R.id.dailyList);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(wid>=200 && wid<=232) {
            fab.setColorNormal(getResources().getColor(R.color.cloudy_weather));
            recList.setBackgroundColor( getResources().getColor(R.color.cloudy_weather));
            return R.drawable.thunderstorms;
        }
        else if(wid>=300 && wid<=321) {
            fab.setColorNormal( getResources().getColor(R.color.rainy_weather));
            recList.setBackgroundColor( getResources().getColor(R.color.rainy_weather));
            return R.drawable.rain;
        }
        else if(wid>=500 && wid<=531) {
            fab.setColorNormal( getResources().getColor(R.color.heavy_rain_weather));
            recList.setBackgroundColor( getResources().getColor(R.color.heavy_rain_weather));
            return R.drawable.rain_heavy;
        }
        else if(wid>=600 && wid<=622) {
            fab.setColorNormal(getResources().getColor(R.color.snow_weather));
            recList.setBackgroundColor(getResources().getColor(R.color.snow_weather));
            return R.drawable.snow;
        }
        else if(wid>=701 && wid<=781) {
            fab.setColorNormal( getResources().getColor(R.color.fog_weather));
            recList.setBackgroundColor( getResources().getColor(R.color.fog_weather));
            return R.drawable.fog;
        }
        else if(wid>=800 && wid<=803) {
            fab.setColorNormal( getResources().getColor(R.color.clear_weather));
            recList.setBackgroundColor( getResources().getColor(R.color.clear_weather));
            return R.drawable.partially_cloudy;
        }
        else if(wid==804) {
            fab.setColorNormal( getResources().getColor(R.color.cloudy_weather));
            recList.setBackgroundColor( getResources().getColor(R.color.cloudy_weather));
            return R.drawable.cloudy;
        }
        else if(wid>=900 && wid<=906) {
            fab.setColorNormal( getResources().getColor(R.color.cloudy_weather));
            recList.setBackgroundColor( getResources().getColor(R.color.cloudy_weather));
            return R.drawable.thunderstorms;
        }
        else {
            fab.setColorNormal( getResources().getColor(R.color.clear_weather));
            recList.setBackgroundColor( getResources().getColor(R.color.clear_weather));
            return R.drawable.clear;
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
        catch (IOException fnf)
        {
            commitLocationsList("Dallas, US");
        } catch (ClassNotFoundException e) {
            commitLocationsList("Dallas, US");
        }
    return new HashMap<String,String>();
    }

    private void commitLocationsList(String city)
    {
        HashMap<String, String> map = new HashMap<>();

        map.put(city.toLowerCase().trim().replace(' ','_'), city);

        try {
            File file = new File(getDir("data", MODE_PRIVATE), "map");
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(map);
            outputStream.flush();
            outputStream.close();
        }
        catch (IOException fnf)
        {
            Toast.makeText(MainActivity.this,
                    "Something went wrong,the app couldn't find the location. Try again later.",
                    Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void createList(int size) {
        if(findViewById(R.id.dailyList) !=null) {
            result = new ArrayList<DailyWeather>();

            for (int i = 0; i < size; i++) {
                DailyWeather dw = new DailyWeather("20°",getResources().getDrawable(R.drawable.ic_sunny), "10");
                result.add(dw);
            }

            WeatherAdapter wa = new WeatherAdapter(result);

            ((RecyclerView) findViewById(R.id.dailyList)).setAdapter(wa);
            playTransition();
        }
    }
    
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void playTransition() {
        if(findViewById(R.id.dailyList) !=null) {
            TransitionManager.beginDelayedTransition((RecyclerView) findViewById(R.id.dailyList), new Slide());
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position, String text) {
        try
        {
            HashMap<String,String> temp = retrieveLocationsList();
            if(text != null && text.length() > 0) {
                String key = text.toLowerCase().trim().replace(' ','_');
                if(temp.containsKey(key))
                updateWeatherData(temp.get(key));
            }
        }
        catch (NullPointerException npe)
        {

        }
    }
}
