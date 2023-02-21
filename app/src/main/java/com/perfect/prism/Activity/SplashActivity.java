package com.perfect.prism.Activity;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.BuildConfig;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import com.perfect.prism.Utility.Config;
import android.view.WindowManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.perfect.prism.R;
import android.location.Location;
import android.app.AlertDialog;

import java.io.IOException;
import java.util.List;
import java.util.Locale;



public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;

    protected double latitude;
    protected double longitude;
    protected String locName;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private FusedLocationProviderClient fusedLocationClient;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        checkLocationPermission();




        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences Loginpref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                if(Loginpref.getString("loginsession", null) == null ){
                    Intent i = new Intent(SplashActivity.this, WelcomeActivity.class);
                    startActivity(i);
                    finish();
                }else if(Loginpref.getString("loginsession", null) != null && !Loginpref.getString("loginsession", null).isEmpty()&&Loginpref.getString("loginsession", null).equals("Yes")){
                    Intent i = new Intent(SplashActivity.this, PinLoginActivity.class);
                    startActivity(i);
                    finish();

                }else if(Loginpref.getString("loginsession", null) != null && !Loginpref.getString("loginsession", null).isEmpty()&&Loginpref.getString("loginsession", null).equals("No")){
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }



            }
        }, SPLASH_TIME_OUT);

    }



    public void showSettingsPermissionAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Location Permission")
                .setMessage("For Enable Location Permission, Go to settings .")
                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                        startActivity(i);
                        finish();
                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("Cancel",  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

//                        finishAffinity();
//                        System.exit(0);

                    }
                })
                .show();
    }

    public void showSettingsAlert() {
        new AlertDialog.Builder(this)
                .setTitle("SETTINGS")
                .setMessage("Enable Location Provider! Go to settings menu? ")
                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        SplashActivity.this.startActivity(intent);
                        dialog.cancel();
                        finish();
                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("Cancel",  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

//                        finishAffinity();
//                        System.exit(0);

                    }
                })
                .show();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new AlertDialog.Builder(this)
                        .setTitle("ALERT")
                        .setMessage("Please allow location permission to access your location .")
                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(SplashActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("Quit",  new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


//                                finishAffinity();
//                                System.exit(0);

                            }
                        })
                        .show();


            }
            else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                    Geocoder geocoder = new Geocoder(SplashActivity.this, Locale.getDefault());
                                    List<Address> addresses = null;
                                    try {
                                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                        locName = addresses.get(0).getAddressLine(0);
                                        Log.e("loca",""+locName);
                                        String Locality = addresses.get(0).getLocality();
                                        String stateName = addresses.get(0).getAddressLine(1);
                                        String countryName = addresses.get(0).getAddressLine(2);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }



                                    SharedPreferences Loginpref = getApplicationContext().getSharedPreferences(Config.LONGI, 0);
                                    SharedPreferences.Editor logineditor = Loginpref.edit();
                                    logineditor.putString("LONGI", String.valueOf(longitude));
                                    logineditor.commit();

                                    SharedPreferences spPin = getApplicationContext().getSharedPreferences(Config.LATI, 0);
                                    SharedPreferences.Editor pinEditor = spPin.edit();
                                    pinEditor.putString("LATI", String.valueOf(latitude));
                                    pinEditor.commit();

                                    SharedPreferences spAgentIg = getApplicationContext().getSharedPreferences(Config.ADDRESS, 0);
                                    SharedPreferences.Editor spAgentIgEditor = spAgentIg.edit();
                                    spAgentIgEditor.putString("ADDRESS", locName);
                                    spAgentIgEditor.commit();
                                }
                                else {
                                    showSettingsAlert();
                                }
                            }
                        });
                    }
                }
                else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    showSettingsPermissionAlert();
                }
                return;
            }

        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                @Override
//                public void onSuccess(Location location) {
//                    // Got last known location. In some rare situations this can be null.
//                    if (location != null) {
//                        // Logic to handle location object
//                        latitude = location.getLatitude();
//                        longitude = location.getLongitude();
//                        Geocoder geocoder = new Geocoder(SplashActivity.this, Locale.getDefault());
//                        List<Address> addresses = null;
//                        try {
//                            addresses = geocoder.getFromLocation(latitude, longitude, 1);
//                            locName = addresses.get(0).getAddressLine(0);
//                            String Locality = addresses.get(0).getLocality();
//                            String stateName = addresses.get(0).getAddressLine(1);
//                            String countryName = addresses.get(0).getAddressLine(2);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//
//
//                        SharedPreferences Loginpref = getApplicationContext().getSharedPreferences(Config.LONGI, 0);
//                        SharedPreferences.Editor logineditor = Loginpref.edit();
//                        logineditor.putString("LONGI", String.valueOf(longitude));
//                        logineditor.commit();
//
//                        SharedPreferences spPin = getApplicationContext().getSharedPreferences(Config.LATI, 0);
//                        SharedPreferences.Editor pinEditor = spPin.edit();
//                        pinEditor.putString("LATI", String.valueOf(latitude));
//                        pinEditor.commit();
//
//                        SharedPreferences spAgentIg = getApplicationContext().getSharedPreferences(Config.ADDRESS, 0);
//                        SharedPreferences.Editor spAgentIgEditor = spAgentIg.edit();
//                        spAgentIgEditor.putString("ADDRESS", locName);
//                        spAgentIgEditor.commit();                    }
//                    else {
//                        showSettingsAlert();
//                    }
//                }
//            });
//        }
//    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Geocoder geocoder = new Geocoder(SplashActivity.this, Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            locName = addresses.get(0).getAddressLine(0);
                            String Locality = addresses.get(0).getLocality();
                            String stateName = addresses.get(0).getAddressLine(1);
                            String countryName = addresses.get(0).getAddressLine(2);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }




                        SharedPreferences Loginpref = getApplicationContext().getSharedPreferences(Config.LONGI, 0);
                        SharedPreferences.Editor logineditor = Loginpref.edit();
                        logineditor.putString("LONGI", String.valueOf(longitude));
                        logineditor.commit();

                        SharedPreferences spPin = getApplicationContext().getSharedPreferences(Config.LATI, 0);
                        SharedPreferences.Editor pinEditor = spPin.edit();
                        pinEditor.putString("LATI", String.valueOf(latitude));
                        pinEditor.commit();

                        SharedPreferences spAgentIg = getApplicationContext().getSharedPreferences(Config.ADDRESS, 0);
                        SharedPreferences.Editor spAgentIgEditor = spAgentIg.edit();
                        spAgentIgEditor.putString("ADDRESS", locName);
                        spAgentIgEditor.commit();                    }
                    else {
                        showSettingsAlert();
                    }
                }
            });
        }
    }

}
