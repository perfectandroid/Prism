package com.perfect.prism.Activity;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.perfect.prism.R;
import com.perfect.prism.Utility.Config;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends  FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {
    private LocationRequest locationRequest;
    private static final int REQUEST_TURN_ON_LOCATION_SETTINGS = 100;
    private static final int OPEN_SETTINGS = 200;
    private Animation mComboAnim;
    private ImageView mImgLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;

    private GoogleMap mMap;
    Double lat,longi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        askPermissionForLocation();
        mImgLocation = findViewById(R.id.imgLocation);
        mComboAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.combo);
        mImgLocation.startAnimation(mComboAnim);

        mGoogleApiClient = new GoogleApiClient.Builder( this )
                .addApi(LocationServices.API).addConnectionCallbacks(this).build();


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void askPermissionForLocation(){
        if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION )
                != PackageManager.PERMISSION_GRANTED ){
            if ( ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.ACCESS_COARSE_LOCATION )  ){
                gotoSettings();
            }else if ( ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.ACCESS_FINE_LOCATION )  ){
                gotoSettings();
            }else {
                ActivityCompat.requestPermissions( this, new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            }
        }else
            promptRequest();
    }

    private void gotoSettings(){
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setMessage("Please grant permission to continue with this section")
                .setCancelable( false )
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LocationActivity.this.finish();
                    }
                })
                .setPositiveButton( "Yes", (new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", LocationActivity.this.getPackageName(), null);
                        intent.setData(uri);
                        LocationActivity.this.startActivityForResult(intent, OPEN_SETTINGS);
                    }
                }));
        AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if ( requestCode == 100 ){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText( this,"Permission granted", Toast.LENGTH_LONG ).show();
                promptRequest();
            }else {
                Toast.makeText( this,"App must need permission", Toast.LENGTH_LONG ).show();
            }
        }
    }

    private void promptRequest(){

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        SettingsClient client = LocationServices.getSettingsClient( LocationActivity.this );
        Task<LocationSettingsResponse> task = client.checkLocationSettings( builder.build() );
        task.addOnSuccessListener( LocationActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        });

        task.addOnFailureListener( LocationActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(LocationActivity.this,
                                REQUEST_TURN_ON_LOCATION_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (  requestCode == REQUEST_TURN_ON_LOCATION_SETTINGS ){
            if ( resultCode == RESULT_OK ){
                if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }else {
            askPermissionForLocation();
        }
    }

    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission( this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission( this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText( this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        try{
            mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
        }catch ( Exception e ){
            Log.e("e", e.toString() );
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        startLocationUpdates();


    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText( getApplicationContext(), "ss", Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText( getApplicationContext(), "gge", Toast.LENGTH_SHORT ).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void sendLocation( double lat, double longi ){
        String getAddress = "";
        try{
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(lat, longi, 1);
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
          /*  String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();*/

            getAddress = address + "." + city ;

        }catch ( IOException e ){
            if ( Config.DEBUG ){
                Log.e("Dbg", e.toString() );
            }
        }
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putDouble( Config.LONGI, longi );
        bundle.putDouble( Config.LATI, lat );
        bundle.putString( Config.ADDRESS, getAddress );
        intent.putExtras( bundle);
        setResult( RESULT_OK, intent );
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (final Location location : locationResult.getLocations()) {
                    if ( location != null ){

                        lat = location.getLatitude();
                        longi = location.getLongitude();

                        LatLng locat = new LatLng(lat, longi);

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(locat);
                        LatLngBounds bounds = builder.build();

                        int padding = 0; // offset from edges of the map in pixels
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mMap.animateCamera(cu);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                sendLocation( location.getLatitude(), location.getLongitude() );

                            }
                        }, 3500);
                        break;
                    }
                }
            }
        };


    }
}

/*
package com.perfect.prism.Activity;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.perfect.prism.R;
import com.perfect.prism.Utility.Config;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private LocationRequest locationRequest;
    private static final int REQUEST_TURN_ON_LOCATION_SETTINGS = 100;
    private static final int OPEN_SETTINGS = 200;
    private Animation mComboAnim;
    private ImageView mImgLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        askPermissionForLocation();
        mImgLocation = findViewById(R.id.imgLocation);
        mComboAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.combo);
        mImgLocation.startAnimation(mComboAnim);

        mGoogleApiClient = new GoogleApiClient.Builder( this )
                .addApi(LocationServices.API).addConnectionCallbacks(this).build();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if ( location != null ){
                        sendLocation( location.getLatitude(), location.getLongitude() );
                        break;
                    }
                }
            }
        };
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


    }

    private void askPermissionForLocation(){
        if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION )
                != PackageManager.PERMISSION_GRANTED ){
            if ( ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.ACCESS_COARSE_LOCATION )  ){
                gotoSettings();
            }else if ( ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.ACCESS_FINE_LOCATION )  ){
                gotoSettings();
            }else {
                ActivityCompat.requestPermissions( this, new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            }
        }else
            promptRequest();
    }
    private void gotoSettings(){
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setMessage("Please grant permission to continue with this section")
                .setCancelable( false )
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LocationActivity.this.finish();
                    }
                })
                .setPositiveButton( "Yes", (new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", LocationActivity.this.getPackageName(), null);
                        intent.setData(uri);
                        LocationActivity.this.startActivityForResult(intent, OPEN_SETTINGS);
                    }
                }));
        AlertDialog alert = builder.create();
        alert.show();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if ( requestCode == 100 ){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText( this,"Permission granted", Toast.LENGTH_LONG ).show();
                promptRequest();
            }else {
                Toast.makeText( this,"App must need permission", Toast.LENGTH_LONG ).show();
            }
        }
    }
    private void promptRequest(){

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        SettingsClient client = LocationServices.getSettingsClient( LocationActivity.this );
        Task<LocationSettingsResponse> task = client.checkLocationSettings( builder.build() );
        task.addOnSuccessListener( LocationActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        });

        task.addOnFailureListener( LocationActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(LocationActivity.this,
                                REQUEST_TURN_ON_LOCATION_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (  requestCode == REQUEST_TURN_ON_LOCATION_SETTINGS ){
            if ( resultCode == RESULT_OK ){
                if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }else {
            askPermissionForLocation();
        }
    }
    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission( this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission( this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText( this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        try{
            mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
        }catch ( Exception e ){
            Log.e("e", e.toString() );
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        startLocationUpdates();


    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText( getApplicationContext(), "ss", Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText( getApplicationContext(), "gge", Toast.LENGTH_SHORT ).show();
    }
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }
    private void sendLocation( double lat, double longi ){
        String getAddress = "";
        try{
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(lat, longi, 1);
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
          */
/*  String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();*//*


            getAddress = address + "." + city ;

        }catch ( IOException e ){
            if ( Config.DEBUG ){
                Log.e("Dbg", e.toString() );
            }
        }


        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putDouble( Config.LONGI, longi );
        bundle.putDouble( Config.LATI, lat );
        bundle.putString( Config.ADDRESS, getAddress );
        intent.putExtras( bundle);
        setResult( RESULT_OK, intent );
        finish();
    }
}*/
