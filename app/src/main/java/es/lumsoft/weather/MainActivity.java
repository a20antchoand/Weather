package es.lumsoft.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private static final String BOOK_BASE_URL =  "https://api.openweathermap.org/data/2.5/weather";
    private static final String QUERY_PARAM = "q";
    private static final String MAX_RESULTS = "maxResults";
    private static final String PRINT_TYPE = "printType";
    private static final String LOG_TAG = "WEATHER_TONI:";
    private static final String APPID = "adda022dba703f71804f68a9f9084a31";

    DecimalFormat df = new DecimalFormat("#.##");
    TextView tvInfoCiutat;
    EditText ciutat;
    Button buscar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setup();

    }

    public void setup() {
        tvInfoCiutat = findViewById(R.id.tvInfoCiutat);
        buscar = findViewById(R.id.buscar);
        ciutat = findViewById(R.id.ciutat);

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarCiutat();
            }
        });
    }
    
    public void buscarCiutat() {

        new buscarCiutat(tvInfoCiutat, this).execute(ciutat.getText().toString());

    }

    static String getInfoCiutat(String ciutat, MainActivity mainActivity){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONString = null;

        try {

            String url = BOOK_BASE_URL + "?q=" + ciutat + "&appid=" + APPID;

            System.out.println("URL" + url);

            Uri builtURI = Uri.parse(url).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, ciutat)
                    .appendQueryParameter(MAX_RESULTS, "10")
                    .appendQueryParameter(PRINT_TYPE, "books")
                    .build();

            URL requestURL = new URL(builtURI.toString());

            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            try {
                InputStream inputStream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder builder = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    builder.append("\n");
                }
                if (builder.length() == 0) {
                    return null;
                }

                bookJSONString = builder.toString();
            } catch (Exception e) {
                Log.d(LOG_TAG, "CIUTAT NO TROBADA");
                return null;
            }





        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Log.d(LOG_TAG, bookJSONString);
        return bookJSONString;
    }

    private void mostrarToast(String s) {

        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();

    }

    private class buscarCiutat extends AsyncTask<String, Void, String> {

        private TextView tvInfoCiutat;
        private MainActivity mainActivity;

        public buscarCiutat(TextView mTitleText, MainActivity mainActivity) {
            this.tvInfoCiutat = mTitleText;
            this.mainActivity = mainActivity;
        }



        @Override
        protected String doInBackground(String... strings) {

            return getInfoCiutat(strings[0], mainActivity);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(s);
                    JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                    JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                    String description = jsonObjectWeather.getString("description");
                    JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                    double temp = jsonObjectMain.getDouble("temp") - 273.15;
                    double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                    float pressure = jsonObjectMain.getInt("pressure");
                    int humidity = jsonObjectMain.getInt("humidity");
                    JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                    String wind = jsonObjectWind.getString("speed");
                    JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                    String clouds = jsonObjectClouds.getString("all");
                    JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                    String countryName = jsonObjectSys.getString("country");
                    String cityName = jsonResponse.getString("name");
                    String output = "Current weather of " + cityName + " (" + countryName + ")"
                            + "\n Temp: " + df.format(temp) + " °C"
                            + "\n Feels Like: " + df.format(feelsLike) + " °C"
                            + "\n Humidity: " + humidity + "%"
                            + "\n Description: " + description
                            + "\n Wind Speed: " + wind + "m/s (meters per second)"
                            + "\n Cloudiness: " + clouds + "%"
                            + "\n Pressure: " + pressure + " hPa";

                    try {
                        tvInfoCiutat.setText(output);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }



}








/*
*
*  private String mostra() throws JSONException {
            try {

                URL url = new URL();
                con = (HttpURLConnection) url.openConnection();
                con.connect();


                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String linea;

                while ((linea = reader.readLine()) != null) {
                    Log.d("MOSTRA", linea);

                    JSONObject jsonResponse = new JSONObject(linea);
                    JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                    JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                    String description = jsonObjectWeather.getString("description");
                    JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                    double temp = jsonObjectMain.getDouble("temp") - 273.15;
                    double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                    float pressure = jsonObjectMain.getInt("pressure");
                    int humidity = jsonObjectMain.getInt("humidity");
                    JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                    String wind = jsonObjectWind.getString("speed");
                    JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                    String clouds = jsonObjectClouds.getString("all");
                    JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                    String countryName = jsonObjectSys.getString("country");
                    String cityName = jsonResponse.getString("name");
                    String output = "Current weather of " + cityName + " (" + countryName + ")"
                            + "\n Temp: " + df.format(temp) + " °C"
                            + "\n Feels Like: " + df.format(feelsLike) + " °C"
                            + "\n Humidity: " + humidity + "%"
                            + "\n Description: " + description
                            + "\n Wind Speed: " + wind + "m/s (meters per second)"
                            + "\n Cloudiness: " + clouds + "%"
                            + "\n Pressure: " + pressure + " hPa";
                    Log.d("MOSTRA", output);

                    return output;
                }

                reader.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException er) {
                er.printStackTrace();

            } finally {
                con.disconnect();

            }

            return null;

        }
*
*
* */