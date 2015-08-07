package crash.weathered;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by harshadeep on 1/28/15.
 */
public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>{

    private List<DailyWeather> dayWeatherList;

    public WeatherAdapter(List<DailyWeather> dayWeatherList) {
        this.dayWeatherList = dayWeatherList;
    }

    @Override
    public int getItemCount() {
        return dayWeatherList.size();
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder WeatherViewHolder, int i) {
        DailyWeather dw = dayWeatherList.get(i);
        WeatherViewHolder.vDayWeather.setText(dw.day_weather);
        WeatherViewHolder.vDayWeatherIcon.setImageDrawable(dw.day_weather_icon);
        WeatherViewHolder.vDay.setText(dw.day);
    }

    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.content_column, viewGroup, false);

        return new WeatherViewHolder(itemView);
    }

    public static class WeatherViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected TextView vDayWeather;
        protected ImageView vDayWeatherIcon;
        protected TextView vDay;
        public WeatherViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            vDayWeather = (TextView) v.findViewById(R.id.day_weather);
            vDayWeatherIcon = (ImageView) v.findViewById(R.id.day_weather_icon);
            vDay = (TextView) v.findViewById(R.id.day);
        }

        @Override
        public void onClick(View v) {
//            Log.d("log","onClick " + getPosition() + " " + vTitle);
//            Intent i = new Intent(v.getContext(), DetailActivity.class);
//            i.putExtra("item_text",vDayWeather.getText());
//            v.getContext().startActivity(i);
        }
    }
}
