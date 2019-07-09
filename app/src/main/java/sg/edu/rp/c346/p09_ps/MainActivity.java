package sg.edu.rp.c346.p09_ps;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class MainActivity extends AppCompatActivity {

    Button btnStart, btnStop, btnCheck;
    TextView tvLat, tvLng;
    FusedLocationProviderClient client;
    String folderLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLat = findViewById(R.id.tvLat);
        tvLng = findViewById(R.id.tvLng);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnCheck = findViewById(R.id.btnCheck);

        client = LocationServices.getFusedLocationProviderClient(this);
        checkPermission();
        checkPermission1();

        if (!(checkPermission() && checkPermission1())) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            return;
        } else {
            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(MainActivity.this, location -> {
                if (location != null) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    tvLat.setText("Latitude: " + lat);
                    tvLng.setText("Longtitude: " + lng);
                } else {
                    String msg = "No Last Known Location Found";
                    Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
                }
            });
            String folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/P09";
            File folder = new File(folderLocation);
            if (folder.exists() == false){
                boolean result = folder.mkdir();
                if (result == true){
                    Log.d("FileRead/Write", "Folder created");
                }
            }
        }


        btnStart.setOnClickListener((v)->{
            Intent i = new Intent(MainActivity.this, MyService.class);
            startService(i);
        });

        btnStop.setOnClickListener((v) ->{
            Intent i = new Intent(MainActivity.this, MyService.class);
            stopService(i);
        });

        btnCheck.setOnClickListener(v -> {
            folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/P09";
            File targetFile = new File(folderLocation, "location.txt");
            if (targetFile.exists()) {
                String data = "";
                try {
                    FileReader reader = new FileReader(targetFile);
                    BufferedReader br = new BufferedReader(reader);
                    String line = br.readLine();
                    while (line != null) {
                        data += line + "\n";
                        line = br.readLine();
                    }
                    br.close();
                    reader.close();
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Failed to read", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                Log.d("content00", data);
                Toast.makeText(getBaseContext(), data, Toast.LENGTH_LONG).show();
            }
            else{

            }
        });

    }

    private boolean checkPermission(){
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                && permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            return false;
        }
    }

    private boolean checkPermission1(){
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            return false;
        }
    }
}
