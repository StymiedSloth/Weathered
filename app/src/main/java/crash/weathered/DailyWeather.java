package crash.weathered;

import android.graphics.drawable.Drawable;

/**
 * Created by harshadeep on 1/28/15.
 */
public class DailyWeather {
    protected String day_weather;
    protected Drawable day_weather_icon;
    protected String day;


    public DailyWeather()
    {

    }
    public DailyWeather(String day_weather, Drawable day_weather_icon, String day)
    {
        this.day_weather = day_weather;
        this.day_weather_icon = day_weather_icon;
        this.day = day;
    }

}
