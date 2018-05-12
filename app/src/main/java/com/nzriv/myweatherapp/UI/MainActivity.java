package com.nzriv.myweatherapp.UI;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nzriv.myweatherapp.R;
import com.nzriv.myweatherapp.databinding.ActivityMainBinding;
import com.nzriv.myweatherapp.weather.Current;
import com.nzriv.myweatherapp.weather.Day;
import com.nzriv.myweatherapp.weather.Forecast;
import com.nzriv.myweatherapp.weather.Hour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String HOURLY_FORECAST = "HOURLY_FORECAST";
    private Forecast forecast;
    private ImageView imageView;
//    @Nullable
//    @BindView(R.id.testButton)
//    Button test;
    @Nullable
    @BindView(R.id.hourlyButton) Button test;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getForecast();


    }

    private void getForecast() {
        //        Data Binding
        final ActivityMainBinding binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);

//        clicking on the link "Powered by Dark Sky" will take you to the appropriate website declared in strings.xml
        TextView darkSky = findViewById(R.id.darkSkyTextView);
        darkSky.setMovementMethod(LinkMovementMethod.getInstance());
        ButterKnife.bind(this);


        imageView = findViewById(R.id.iconImageView);

        String apiKey = "56b7f2f83517f94a8609dc016bb7cda5";
        double latitude = 51.0486;
        double longitude = -114.0708;

        String forecastURL = "https://api.darksky.net/forecast/"
                + apiKey + "/" + latitude + "," + longitude;

        if (isNetworkAvailable()) {

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(forecastURL)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
//                            current = getCurrentDetails(jsonData);
                            forecast = parseForecastDetails(jsonData);

//                            binding our data displayWeather to a new binding variable
                            Current current = forecast.getCurrent();
                            final Current displayWeather = new Current(
                                    current.getLocation(),
                                    current.getIcon(),
                                    current.getSummary(),
                                    current.getTimeZone(),
                                    current.getTime(),
                                    current.getTemperature(),
                                    current.getHumidity(),
                                    current.getPrecipChance());

                            binding.setWeather(displayWeather);

//                            background cannot update Main UI thread (imageView). Have to run a runOnUiThread to send
//                            data back to main UI thread for UI update.
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Drawable drawable = getResources().getDrawable(displayWeather.getIconID());
                                    imageView.setImageDrawable(drawable);
                                }
                            });

                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "IO Exception caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Exception caught", e);
                    }
                }
            });
        }
    }

    //    throws the exception to the line where the method is being called above. - in order to catch
//    the exception at the same time as ioexception. cleaner code
    private Current getCurrentDetails(String jsonData) throws JSONException {

        JSONObject forecast = new JSONObject(jsonData);
        JSONObject currently = forecast.getJSONObject("currently");

//        .getString gets the value from JSON key-value data set
        String timezone = forecast.getString("timezone");
        Log.i(TAG, "From JSON: " + timezone);

        Current current = new Current();

        current.setHumidity(currently.getDouble("humidity"));
        current.setTime(currently.getLong("time"));
        current.setIcon(currently.getString("icon"));
//        Fix location
        current.setLocation("Alcatraz Island, CA");
        current.setPrecipChance(currently.getDouble("precipProbability"));
        current.setSummary(currently.getString("summary"));
        current.setTemperature(currently.getDouble("temperature"));
        current.setTimeZone(timezone);

        Log.d(TAG, current.getFormattedTime());

        return current;
    }

    private Forecast parseForecastDetails(String jsonData) throws JSONException {
        Forecast forecast = new Forecast();

        forecast.setCurrent(getCurrentDetails(jsonData));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));
        forecast.setDailyForecast(getDailyForecast(jsonData));

        return forecast;
    }

    private Day[] getDailyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");

        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        Day[] days = new Day[data.length()];

        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonDays = data.getJSONObject(i);
            Day day = new Day();

            day.setSummary(jsonDays.getString("summary"));
            day.setTemperatureMax(jsonDays.getDouble("temperatureMax"));
            day.setIcon(jsonDays.getString("icon"));
            day.setTime(jsonDays.getLong("time"));
            day.setTimezone(timezone);

            days[i] = day;

        }

        return days;
    }

    private Hour[] getHourlyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");

        Hour[] hours = new Hour[data.length()];

//        Converting JSON array object to java array object.
        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonHour = data.getJSONObject(i);
//            Setting new variable hour within the for loop - prevents overwriting of same object.
            Hour hour = new Hour();

            hour.setSummary(jsonHour.getString("summary"));
            hour.setTemperature(jsonHour.getDouble("temperature"));
            hour.setIcon(jsonHour.getString("icon"));
            hour.setTime(jsonHour.getLong("time"));
            hour.setTimezone(timezone);

            hours[i] = hour;
        }

        return hours;
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;

        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        } else {
            Toast.makeText(this, R.string.network_unavailable_message, Toast.LENGTH_LONG).show();
        }
        return isAvailable;
    }

    private void alertUserAboutError() {

        AlertDialogFragment dialogFragment = new AlertDialogFragment();
        dialogFragment.show(getFragmentManager(), "error_dialog");
    }

    //    create android:onClick="refreshOnClick" for ImageView to implement method that is called from the click event.
//    makes ImageView act as a button
    public void refreshOnClick(View view) {
        Toast.makeText(this, "Data Refreshed", Toast.LENGTH_LONG).show();
        getForecast();
    }

    @Optional
    @OnClick(R.id.hourlyButton)
    public void startHourlyActivity(View view) {
        Intent intent = new Intent(this, HourlyForecastActivity.class);
//        puts array from this.forecast into intent
        intent.putExtra(HOURLY_FORECAST, this.forecast.getHourlyForecast());
        startActivity(intent);
    }
}
