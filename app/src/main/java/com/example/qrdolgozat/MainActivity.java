package com.example.qrdolgozat;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private Button buttonElso, buttonMasodik;
    private TextView txtview;
    private Timer timer;
    private TimerTask timerTask;
    private float hosszusag; //hosszusagikor
    float szelsesseg;//szelessegikor
    private Naplozas naplozas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        buttonElso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("QR CODE SCANNING by Bence");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

        buttonMasodik.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                LocationManager locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);

                //koordinatak lekerdezese
                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        szelsesseg = (float) location.getLatitude();
                        hosszusag = (float) location.getLongitude();
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                };
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        TimerMethod();
                    }


                };
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null) {
            txtview.setText("QRvCode eredmeny:" + result.getContents());
        }
            else
            {
                Toast.makeText(this,"kiléptünk a Scannelesbol", Toast.LENGTH_SHORT).show();
                //Uri uri = Uri.parse(result.getContents());
               // Intent intent = new Intent(Intent.ACTION_VIEW,uri);
               // startActivity(intent);
            }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //idozitett keresztfuggveny
    private void TimerMethod() {
        this.runOnUiThread(Timer_Tick);
    }

    public Runnable Timer_Tick =  new Runnable() {
        @Override
        public void run() {
            try {
                naplozas.kiiras(txtview.getText().toString());
            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    };



    public  void  init()
    {
        buttonElso = findViewById(R.id.buttonElso);
        buttonMasodik = findViewById(R.id.buttonMasodik);
        txtview = findViewById(R.id.txtview);
        naplozas = new Naplozas();


    }
}
