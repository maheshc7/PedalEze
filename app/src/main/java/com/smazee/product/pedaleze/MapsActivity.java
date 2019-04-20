package com.smazee.product.pedaleze;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.smazee.product.pedaleze.model.DataParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    protected GoogleMap mMap;
    ConstraintLayout dist_layout, allset_layout;
    LinearLayout mode_layout, selection_layout, dest_layout;
    ImageView set_img;
    static double lon1 = 0, lon2 = 0, lat1 = 0, lat2 = 0;
    Polyline polyline;
    List<Polyline> polylineList;
    //LocationManager locationManager;
    TextView distTxt, destText;
    public static boolean isTracking = false, inBackground = false, destVisible = false;
    Button goBtn, destBtn, sosBtn;
    RadioGroup radioGroup;
    static float prevDist = 0;
    static Intent toService;
    String bpm, phno;
    static TextView heart_rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        dist_layout = findViewById(R.id.dist_layout);
        mode_layout = findViewById(R.id.mode_layout);
        selection_layout = findViewById(R.id.selection_layout);
        allset_layout = findViewById(R.id.set_layout);
        dest_layout = findViewById(R.id.dest_layout);
        dist_layout.animate().translationY(600).alpha(0.0f);
        distTxt = findViewById(R.id.distText);
        destText = findViewById(R.id.destText);
        goBtn = findViewById(R.id.goBtn);
        destBtn = findViewById(R.id.map_dest);
        sosBtn = findViewById(R.id.map_sos);
        heart_rate = findViewById(R.id.heart_rate_map);
        radioGroup = findViewById(R.id.mode_group);
        set_img = findViewById(R.id.set_img);
        Intent intent = getIntent();
        bpm = intent.getStringExtra("bpm");
        phno = intent.getStringExtra("phno");
        heart_rate.setText(bpm);
        heart_rate.setVisibility(View.INVISIBLE);
        dest_layout.setVisibility(View.INVISIBLE);


    }

    @Override
    protected void onResume() {
        super.onResume();
        inBackground = false;
        if (isTracking) {
            goBtn.setText("STOP!");
        } else {
            goBtn.setText("START!");
            distTxt.setText("0.0 km");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        inBackground = true;
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
            Log.d("MapActivity-->", "Give Permission");
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(22.2695345, 75.7330751)));
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
        Log.d("MapActivity-->", "Please enable " + provider);
    }

    public static void update(String bpm) {
        heart_rate.setText(bpm + " bpm");
    }

    public void startTrack(View view) {
        if (!isTracking) {
            isTracking = true;
            goBtn.setText("STOP!");
            heart_rate.setVisibility(View.VISIBLE);
            heart_rate.setText("0 BPM");
            dist_layout.animate().translationY(0).setDuration(2000);
            LocationUpdateService locationUpdateService = new LocationUpdateService(this);
            toService = new Intent(MapsActivity.this, LocationUpdateService.class);
            startService(toService);


        } else {
            isTracking = false;
            stopService(toService);
            goBtn.setText("START!");
            distTxt.setText("0.0 km");
            mMap.clear();
            dist_layout.animate().translationY(600);
            mode_layout.animate().translationY(0).alpha(1.0f);
            selection_layout.animate().translationY(0).alpha(1.0f);
            heart_rate.setVisibility(View.INVISIBLE);
        }
    }

    public void onSelection(View view) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton mode = findViewById(selectedId);
        if (selectedId == -1) {
            Toast.makeText(MapsActivity.this, "Nothing selected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MapsActivity.this, mode.getText() + " Mode ON!", Toast.LENGTH_SHORT).show();
            dist_layout.setVisibility(View.VISIBLE);
            mode_layout.animate().translationY(-1000).alpha(0.0f).setDuration(1500);
            selection_layout.animate().translationY(-200).alpha(0.0f).setDuration(500).setStartDelay(500);
            allset_layout.animate().alpha(1).setDuration(2000).withEndAction(new Runnable() {
                @Override
                public void run() {
                    allset_layout.animate().alpha(0).setDuration(2000);
                    dist_layout.animate().translationY(goBtn.getHeight() + 40).alpha(1.0f).setDuration(2000);
                    goBtn.animate().rotationBy(360).setDuration(2000);
                }
            });
            set_img.animate().rotation(360).setDuration(3000);
        }
    }

    public void sendSOS(View view) {
        phno = "+919543610925";
        String msg="HELP! I'm in Danger!";

        //For Sending SOS Text
        //TO:DO using MSG91


        //For making an SOS call
        Intent call = new Intent(Intent.ACTION_CALL);
        call.setData(Uri.parse("tel:" + phno));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(call);
    }

    public void getDest(View view){
        if(destVisible){
            destVisible=false;
            dest_layout.setVisibility(View.INVISIBLE);
            destText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((keyCode == KeyEvent.KEYCODE_ENTER)) {
                        // Perform action on key press
                        setRoute(new LatLng(0,0),new LatLng(0,0));
                        destVisible=false;
                        dest_layout.setVisibility(View.INVISIBLE);
                        return true;
                    }
                    return false;
                }
            });
        }
        else{
            destVisible=true;
            dest_layout.setVisibility(View.VISIBLE);
        }
    }

    public void setRoute(LatLng origin, LatLng dest) {
        dest = new LatLng(13.0508684, 80.2497857);
        origin = new LatLng(13.042787, 80.265635);

        String url = getUrl(origin, dest);
        FetchUrl FetchUrl = new FetchUrl();

        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);
    }

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    // Fetches data from url passed
    class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }

        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();
                Log.d("downloadUrl", data);
                br.close();

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }
    }

    class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0]);
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(R.color.colorAccent);

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                polyline=mMap.addPolyline(lineOptions);
                polylineList.add(polyline);

            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }
}
