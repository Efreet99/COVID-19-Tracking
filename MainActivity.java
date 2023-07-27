package com.skypan.covid_19;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private PowerManager.WakeLock wakeLock;

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    Handler handler = new Handler();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.item_home:
                intent = new Intent(this, MainActivity.class);
                break;
            case R.id.item_information:
                intent = new Intent(this, InformationActivity.class);
                break;
            case R.id.item_history:
                intent = new Intent(this, HistoryActivity.class);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        startActivity(intent);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "ExampleApp:Wakelock");
        wakeLock.acquire();


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        final Switch mSLooper = findViewById(R.id.switch_looper);

        SharedPreferences sharedPreferences = getSharedPreferences("com.skypan.covid_19", MODE_PRIVATE);
        mSLooper.setChecked(sharedPreferences.getBoolean("value", false));
        mSLooper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSLooper.isChecked()) {
                    SharedPreferences.Editor editor = getSharedPreferences("com.skypan.covid_19", MODE_PRIVATE).edit();
                    editor.putBoolean("Value", true);
                    editor.commit();
                    mSLooper.setChecked(true);
                    mRunnable.run();
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences("com.skypan.covid_19", MODE_PRIVATE).edit();
                    editor.putBoolean("Value", false);
                    editor.commit();
                    mSLooper.setChecked(false);
                    handler.removeCallbacks(mRunnable);
                    wakeLock.release();
                }
            }
        });
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "   " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(MainActivity.this);
                    try {
                        saveLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), Calendar.getInstance());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "   " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    try {
                        saveLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), Calendar.getInstance());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void saveLocation(double latitude, double longitude, Calendar calendar) throws IOException {

        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        List<Address> addresses;

        addresses = geocoder.getFromLocation(latitude, longitude, 1);

        String address = addresses.get(0).getAddressLine(0);

        SimpleDateFormat MdsimpleDateFormat = new SimpleDateFormat("YYYY-MMMdd");
        String MddateTime = MdsimpleDateFormat.format(calendar.getTime());
        SimpleDateFormat hmsimpleDateFormat = new SimpleDateFormat("hh:mm:ss a");
        String hmdateTime = hmsimpleDateFormat.format(calendar.getTime());

        String date = (MddateTime);
        String time = (hmdateTime);
        String location = ("Lat : " + latitude + "\nLng : " + longitude);

        History history = new History(date, time, location, address);

        db.collection(MddateTime).document(hmdateTime).set(history);

    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            fetchLastLocation();
            handler.postDelayed(this, 3600000);
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                .title("I am here.");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
        googleMap.addMarker(markerOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        App.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.activityResumed();
    }
}