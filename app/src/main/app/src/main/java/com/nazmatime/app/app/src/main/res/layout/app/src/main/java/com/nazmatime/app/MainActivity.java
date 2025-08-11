package com.nazmatime.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView clockText, dateText, tempText;
    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clockText = findViewById(R.id.clockText);
        dateText = findViewById(R.id.dateText);
        tempText = findViewById(R.id.tempText);
        webView = findViewById(R.id.webView);

        // ওয়েবসাইট লোড
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://namaztime365.blogspot.com");

        // সময় আপডেট
        updateClock();

        // তারিখ দেখানো
        String date = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("bn", "BD"))
                .format(new Date());
        dateText.setText(date);

        // তাপমাত্রা আনা
        getTemperature();
    }

    private void updateClock() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                String time = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(new Date());
                clockText.setText(time);
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void getTemperature() {
        new Thread(() -> {
            try {
                URL url = new URL("https://api.open-meteo.com/v1/forecast?latitude=23.8103&longitude=90.4125&current_weather=true");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                Scanner in = new Scanner(conn.getInputStream());
                StringBuilder response = new StringBuilder();
                while (in.hasNext()) {
                    response.append(in.nextLine());
                }
                in.close();

                JSONObject json = new JSONObject(response.toString());
                double temp = json.getJSONObject("current_weather").getDouble("temperature");

                runOnUiThread(() -> tempText.setText(temp + "°C"));

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> tempText.setText("তাপমাত্রা আনতে সমস্যা হয়েছে"));
            }
        }).start();
    }
}
