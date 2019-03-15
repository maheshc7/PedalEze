package com.smazee.product.pedaleze;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Calendar;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    protected GoogleMap mMap;
    static double lon1 = 0, lon2 = 0, lat1 = 0, lat2 = 0;
    //LocationManager locationManager;
    TextView distTxt;
    public static boolean isTracking = false, inBackground=false;
    Button goBtn;
    RadioGroup radioGroup;
    static float prevDist=0;
    static Intent toService;
    Calendar startTime,stopTime;
    TextView heart_rate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        distTxt = findViewById(R.id.distText);
        goBtn = findViewById(R.id.goBtn);
        heart_rate = findViewById(R.id.heart_rate_map);
        radioGroup = findViewById(R.id.mode_group);



    }

    @Override
    protected void onResume() {
        super.onResume();
        inBackground=false;
        if(isTracking){
            goBtn.setText("STOP!");
        }
        else{
            goBtn.setText("START!");
            distTxt.setText("0.0 km");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        inBackground=true;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            Log.d("MapActivity-->","Give Permission");
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        /*locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 20, MapsActivity.this);
        }
        catch(Exception e){
            Log.d("MapActivity-->",e.getMessage());
        }*/


    }

    @Override
    public void onLocationChanged(Location location) {

        /*lat2 = location.getLatitude();
        lon2 = location.getLongitude();
        if(lat1==0 && lon1==0){
            lat1=lat2;
            lon1=lon2;
        }
        if (isTracking) {
            float dist[] = new float[3];
            Location.distanceBetween(lat1,lon1,lat2,lon2,dist);
            if(dist[0]!=0 && dist[0]>prevDist)
                distTxt.setText(String.format("%.2f", dist[0]/1000) + " km");
            prevDist=dist[0];
        }*/


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("MapActivity-->","Please enable "+provider);
    }

    public void startTrack(View view) {
        if (!isTracking) {
            isTracking = true;
            goBtn.setText("STOP!");
            LocationUpdateService locationUpdateService = new LocationUpdateService(this);
            toService = new Intent(MapsActivity.this,LocationUpdateService.class);
            startService(toService);

        }
        else{
            isTracking = false;
            stopService(toService);
            goBtn.setText("START!");
            distTxt.setText("0.0 km");
            mMap.clear();
        }
    }

    public void onSelection(View view){
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton mode = findViewById(selectedId);
        if(selectedId==-1){
            Toast.makeText(MapsActivity.this,"Nothing selected", Toast.LENGTH_SHORT).show();
        }
        else{
            LinearLayout selection_layout = findViewById(R.id.selection_layout);
            LinearLayout mode_layout = findViewById(R.id.mode_layout);
            selection_layout.setVisibility(View.GONE);
            mode_layout.setVisibility(View.GONE);
            Toast.makeText(MapsActivity.this,mode.getText()+" Mode ON!", Toast.LENGTH_SHORT).show();
        }
    }
}
