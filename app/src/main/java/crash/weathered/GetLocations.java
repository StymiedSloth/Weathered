package crash.weathered;

/**
 * Created by harshadeep on 1/30/15.
 */
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GetLocations {
    private final String MY_PREFS_NAME = "settings";
    SharedPreferences prefs;

    public GetLocations(Activity activity){
        prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    Set<String> getCity(){

        Set<String> myStrings = prefs.getStringSet("locations", new HashSet<String>());
        return myStrings;
    }

    void setCity(String city){

        SharedPreferences.Editor editor = prefs.edit();
        Set<String> myStrings = prefs.getStringSet("locations", new HashSet<String>());

        myStrings.add(city);

        editor.putStringSet("locations", myStrings);
        editor.commit();
    }

}