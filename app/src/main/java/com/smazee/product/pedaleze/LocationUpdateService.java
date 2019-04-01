package com.smazee.product.pedaleze;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
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
import android.os.Build;
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

import java.util.Calendar;

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
    long time=0;
    Calendar calendar;
    String NOTIFICATION_CHANNEL_ID = "com.smazee.product.pedaleze";
    NotificationManager manager;

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
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelName = "My Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

        }*/

        // Create notification default intent.
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Create notification builder.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);

        // Make notification show big text.
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("PedalEze");
        bigTextStyle.bigText("Location Updates: Open App");
        // Set big text style.
        builder.setStyle(bigTextStyle);

        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.battery);
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

    private void startMyOwnForeground(){
            /*String channelName = "My Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.icon_1)
            .setContentTitle("App is running in background")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build();
            */

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle("PedalEze");
            bigTextStyle.bigText("Location Updates: Open App");
            // Set big text style.
            notificationBuilder.setStyle(bigTextStyle);

            notificationBuilder.setWhen(System.currentTimeMillis());
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
            Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_arrow);
            notificationBuilder.setLargeIcon(largeIconBitmap);
            // Build the notification.
            notification = notificationBuilder.build();
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
            calendar = Calendar.getInstance();
            lat2 = location.getLatitude();
            lon2 = location.getLongitude();
            Log.d("ServiceIntent--->", "Updating location...");
            if (lat1 == 0 && lon1 == 0 && mapsActivity.mMap != null) {
                lat1 = lat2;
                lon1 = lon2;
                mLastLocation = location;
                time=mLastLocation.getTime();
                LatLng latLng = new LatLng(lat1, lon1);
                mapsActivity.mMap.addMarker(new MarkerOptions().position(latLng).title("Start Point"));
                mapsActivity.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17.0f));
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
                    mapsActivity.distTxt.setText(String.format("%.2f", dist[0] / 1000) + " km");
                    /*mapsActivity.mMap.addPolyline(new PolylineOptions()
                            .add(new LatLng(prevLat, prevLong), new LatLng(lat2, lon2))
                            .width(5)
                            .color(Color.RED));*/
                    prevDist = dist[0];
                    double speed = 0;



                    if (mLastLocation != null) {
                        calendar.setTimeInMillis(location.getTime() - time);
                        int s=calendar.get(Calendar.SECOND);
                        Log.d("Service Time--->",location.getTime()+"  "+mLastLocation.getTime()+"  "+s);
                        speed = Math.sqrt(Math.pow(lon2 - lon1, 2) + Math.pow(lat2 - lat1, 2));
                        Log.d("Speed(dist)-->", String.valueOf(speed)+"/"+(s));
                        speed = d/s;
                    }

                    if(speed*3.6<61)
                        mapsActivity.goBtn.setText(String.format("%.0f\nkmph", speed*3.6));
                    mLastLocation = location;
                    time=mLastLocation.getTime();
                    lat1 = lat2;
                    lon1 = lon2;
                }




                //update Notification
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                // Sets an ID for the notification, so it can be updated
                int notifyID = 1;
                NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(LocationUpdateService.this,NOTIFICATION_CHANNEL_ID)
                        .setContentTitle("PedalEze")
                        .setContentText("You've received new messages.")
                        .setSmallIcon(R.drawable.ic_arrow);
                numMessages = 0;
                // Start of a loop that processes data and then notifies the user...
                mNotifyBuilder.setContentText(String.format("Distance: %.2f", dist[0] / 1000) + " km")
                        .setNumber(++numMessages);
                // Because the ID remains unchanged, the existing notification is
                // updated.
                mNotificationManager.notify(
                        notifyID,
                        mNotifyBuilder.build());
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    manager.notify(notifyID, mNotifyBuilder.build());

                }*/
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