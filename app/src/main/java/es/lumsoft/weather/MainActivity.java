package es.lumsoft.weather;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    TextView tvInfoCiutat;
    EditText ciutat;
    Button buscar;

    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String apiId = "adda022dba703f71804f68a9f9084a31";
    DecimalFormat df = new DecimalFormat("#.##");

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

        buscar.setOnClickListener(view -> {
            try {
                mostra();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }



    private void mostra() throws JSONException {
        String tempUrl = "";
        String city = ciutat.getText().toString().trim();
        if(city.equals("")){
            tvInfoCiutat.setText("No has introduit una ciutat valida");
        }else{

            tempUrl = url + "?q=" + city + "&appid=" + apiId;

            System.out.println(tempUrl);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, response -> {

                String output = "";

                try {

                    //Agafem l aresposta i la parsejem a JSONObject
                    JSONObject jsonResposta = new JSONObject(response);

                    //Agafem les dades del clima
                    JSONArray jsonArray = jsonResposta.getJSONArray("weather");
                    JSONObject jsonObjectClima = jsonArray.getJSONObject(0);
                    String descripcio = jsonObjectClima.getString("description");

                    //Agafem les dades principals
                    JSONObject jsonObjectMain = jsonResposta.getJSONObject("main");
                    double temperatura = jsonObjectMain.getDouble("temp") - 273.15;
                    double sensacio = jsonObjectMain.getDouble("feels_like") - 273.15;
                    float presio = jsonObjectMain.getInt("pressure");
                    int humitat = jsonObjectMain.getInt("humidity");

                    //Agafem les dades del vent
                    JSONObject jsonObjectVent = jsonResposta.getJSONObject("wind");
                    String velocitat = jsonObjectVent.getString("speed");

                    //Agafem les dades del nubols
                    JSONObject jsonObjectNubols = jsonResposta.getJSONObject("clouds");
                    String nubols = jsonObjectNubols.getString("all");

                    //Agafem les dades del sistema
                    JSONObject jsonObjectSys = jsonResposta.getJSONObject("sys");
                    String nomPais = jsonObjectSys.getString("country");
                    String nomCiutat = jsonResposta.getString("name");

                    //Posem el text de color negre
                    tvInfoCiutat.setTextColor(Color.BLACK);

                    //creem el string resultant
                    output += "Clima actual de " + nomCiutat + " (" + nomPais + ")"
                            + "\n Temperatura: " + df.format(temperatura) + " °C"
                            + "\n Sensació termica: " + df.format(sensacio) + " °C"
                            + "\n Humitat: " + humitat + "%"
                            + "\n Descripció: " + descripcio
                            + "\n vent: " + velocitat + "m/s (metres per segon)"
                            + "\n Nubols: " + nubols + "%"
                            + "\n Presió: " + presio + " hPa";

                    //Mostrem el resultat
                    tvInfoCiutat.setVisibility(View.VISIBLE);
                    tvInfoCiutat.setText(output);
                } catch (JSONException e) {

                    e.printStackTrace();
                    tvInfoCiutat.setVisibility(View.GONE);

                }

            }, error -> Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show());

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);

        }

    }


}









