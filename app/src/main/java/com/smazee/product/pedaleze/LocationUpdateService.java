package com.smazee.product.pedaleze;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import static com.smazee.product.pedaleze.MapsActivity.inBackground;
import static com.smazee.product.pedaleze.MapsActivity.isTracking;
import static com.smazee.product.pedaleze.MapsActivity.lat1;
import static com.smazee.product.pedaleze.MapsActivity.lat2;
import static com.smazee.product.pedaleze.MapsActivity.lon1;
import static com.smazee.product.pedaleze.MapsActivity.lon2;
import static com.smazee.product.pedaleze.MapsActivity.prevDist;

public class LocationUpdateService extends Service {

    LocationListener locationListener;
    public static MapsActivity mapsActivity;
    private static final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";
    static Notification notification;
    int numMessages;
    Location mLastLocation;

    public LocationUpdateService(){}

    public LocationUpdateService(MapsActivity activity) {
        mapsActivity = activity;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {

        Log.d("ServiceIntent--->","onStartCommand called");
        startForegroundService();
        super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
    }

    private void startForegroundService() {
        Log.d(TAG_FOREGROUND_SERVICE, "Start foreground service.");

        // Create notification default intent.
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Create notification builder.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Make notification show big text.
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("PedalEze");
        bigTextStyle.bigText("Location Updates: Open App");
        // Set big text style.
        builder.setStyle(bigTextStyle);

        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_travel);
        builder.setLargeIcon(largeIconBitmap);
        // Make the notification max priority.
        builder.setPriority(Notification.PRIORITY_MAX);
        // Make head-up notification.
        builder.setFullScreenIntent(pendingIntent, true);

        // Build the notification.
        notification = builder.build();

        // Start foreground service.
        startForeground(1, notification);
    }

    private void stopForegroundService() {
        Log.d(TAG_FOREGROUND_SERVICE, "Stop foreground service.");

        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        this.locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, this.locationListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForegroundService();
    }

    private class MyLocationListener implements LocationListener {


        @Override
        public void onLocationChanged(final Location location) {
            lat2 = location.getLatitude();
            lon2 = location.getLongitude();
            Log.d("ServiceIntent--->", "Updating location...");
            if (lat1 == 0 && lon1 == 0 && mapsActivity.mMap != null) {
                lat1 = lat2;
                lon1 = lon2;
                mLastLocation = location;
                LatLng latLng = new LatLng(lat1, lon1);
                mapsActivity.mMap.addMarker(new MarkerOptions().position(latLng).title("Start Point"));
                mapsActivity.mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
            if (isTracking) {
                float dist[] = new float[3];
                Location.distanceBetween(lat1, lon1, lat2, lon2, dist);
                Log.d("Service-->", prevDist + "----" + dist[0]);
                float d;
                d=dist[0];
                dist[0] += prevDist;
                if (dist[0] != 0 && dist[0] > prevDist && !inBackground) {//
                    Log.d("ServiceIn-->", prevDist + "----" + dist[0]);
                    mapsActivity.distTxt.setText(String.format("Dist.: %.2f", dist[0] / 1000) + " km");
                    /*mapsActivity.mMap.addPolyline(new PolylineOptions()
                            .add(new LatLng(prevLat, prevLong), new LatLng(lat2, lon2))
                            .width(5)
                            .color(Color.RED));*/
                    prevDist = dist[0];
                    double speed = 0;
                    if (mLastLocation != null) {
                        speed = Math.sqrt(Math.pow(lon2 - lon1, 2) + Math.pow(lat2 - lat1, 2));
                        Log.d("Speed(dist)-->", String.valueOf(speed)+"/"+(location.getTime() - mLastLocation.getTime()));
                        speed = d/((location.getTime() - mLastLocation.getTime())/60000);
                    }
                    mapsActivity.distTxt.append("\n Speed: " + speed*3.6 + "km/h");
                    mLastLocation = location;
                    lat1 = lat2;
                    lon1 = lon2;
                }




                //update Notification
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                // Sets an ID for the notification, so it can be updated
                int notifyID = 1;
                NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(LocationUpdateService.this)
                        .setContentTitle("PedalEze")
                        .setContentText("You've received new messages.")
                        .setSmallIcon(R.drawable.ic_discount);
                numMessages = 0;
                // Start of a loop that processes data and then notifies the user...
                mNotifyBuilder.setContentText(String.format("Distance: %.2f", dist[0] / 1000) + " km")
                        .setNumber(++numMessages);
                // Because the ID remains unchanged, the existing notification is
                // updated.
                mNotificationManager.notify(
                        notifyID,
                        mNotifyBuilder.build());
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    }
}