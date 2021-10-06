package com.perfect.prism.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import com.perfect.prism.Retrofit.ApiInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.BuildConfig;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.perfect.prism.R;
import com.perfect.prism.Utility.Config;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class PinLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEdtMpin;
    private Button btone,bttwo,btthree,btfour,btfive,btsix,btseven,bteight,btnine,btzero,btclear,btback;
    private LinearLayout  llback;
    TextView txtv_logout;


    protected double latitude;
    protected double longitude;
    protected String locName;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pin_login);

        checkLocationPermission();
        initialisation();
        btone.setOnClickListener(this);
        bttwo.setOnClickListener(this);
        btthree.setOnClickListener(this);
        btfour.setOnClickListener(this);
        btfive.setOnClickListener(this);
        btsix.setOnClickListener(this);
        btseven.setOnClickListener(this);
        bteight.setOnClickListener(this);
        btnine.setOnClickListener(this);
        btzero.setOnClickListener(this);
        btclear.setOnClickListener(this);
        btback.setOnClickListener(this);
        llback.setOnClickListener(this);
        txtv_logout.setOnClickListener(this);
    }

    private void initialisation(){
        mEdtMpin = findViewById(R.id.edtMpin);
        btone = findViewById(R.id.one);
        bttwo = findViewById(R.id.two);
        btthree = findViewById(R.id.three);
        btfour = findViewById(R.id.four);
        btfive = findViewById(R.id.five);
        btsix = findViewById(R.id.six);
        btseven = findViewById(R.id.seven);
        bteight = findViewById(R.id.eight);
        btnine = findViewById(R.id.nine);
        btzero = findViewById(R.id.zero);
        btclear = findViewById(R.id.clear);
        btback = findViewById(R.id.btback);
        llback = findViewById(R.id.back);
        txtv_logout = findViewById(R.id.txtv_logout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.one:
                mEdtMpin.setText(mEdtMpin.getText().toString()+"1");
                if(mEdtMpin.length() == 4){
                    validateMpin(mEdtMpin.getText().toString());
                }
                break;

            case R.id.two:
                mEdtMpin.setText(mEdtMpin.getText().toString()+"2");
                if(mEdtMpin.length() == 4){
                    validateMpin(mEdtMpin.getText().toString());
                }
                break;

            case R.id.three:
                mEdtMpin.setText(mEdtMpin.getText().toString()+"3");
                if(mEdtMpin.length() == 4){
                    validateMpin(mEdtMpin.getText().toString());
                }
                break;

            case R.id.four:
                mEdtMpin.setText(mEdtMpin.getText().toString()+"4");
                if(mEdtMpin.length() == 4){
                    validateMpin(mEdtMpin.getText().toString());
                }
                break;

            case R.id.five:
                mEdtMpin.setText(mEdtMpin.getText().toString()+"5");
                if(mEdtMpin.length() == 4){
                    validateMpin(mEdtMpin.getText().toString());
                }
                break;

            case R.id.six:
                mEdtMpin.setText(mEdtMpin.getText().toString()+"6");
                if(mEdtMpin.length() == 4){
                    validateMpin(mEdtMpin.getText().toString());
                }
                break;

            case R.id.seven:
                mEdtMpin.setText(mEdtMpin.getText().toString()+"7");
                if(mEdtMpin.length() == 4){
                    validateMpin(mEdtMpin.getText().toString());
                }
                break;

            case R.id.eight:
                mEdtMpin.setText(mEdtMpin.getText().toString()+"8");
                if(mEdtMpin.length() == 4){
                    validateMpin(mEdtMpin.getText().toString());
                }
                break;

            case R.id.nine:
                mEdtMpin.setText(mEdtMpin.getText().toString()+"9");
                if(mEdtMpin.length() == 4){
                    validateMpin(mEdtMpin.getText().toString());
                }
                break;

            case R.id.zero:
                mEdtMpin.setText(mEdtMpin.getText().toString()+"0");
                if(mEdtMpin.length() == 4){
                    validateMpin(mEdtMpin.getText().toString());
                }
                break;

            case R.id.back:
                if (mEdtMpin.length()>0){
                    String str = mEdtMpin.getText().toString();
                    str = str.substring ( 0, str.length() - 1 );
                    // Now set this Text to your edit text
                    mEdtMpin.setText ( str );
                }
                if(mEdtMpin.length() == 4){
                    validateMpin(mEdtMpin.getText().toString());
                }
                break;

            case R.id.btback:
                if (mEdtMpin.length()>0){
                    String str = mEdtMpin.getText().toString();
                    str = str.substring ( 0, str.length() - 1 );
                    // Now set this Text to your edit text
                    mEdtMpin.setText ( str );
                }
                if(mEdtMpin.length() == 4){
                    validateMpin(mEdtMpin.getText().toString());
                }
                break;

            case R.id.clear:
                mEdtMpin.setText("");
                break;

            case R.id.txtv_logout:
                doLogout();
                break;
        }
    }


    private void validateMpin(String mPin) {
        SharedPreferences pref2 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF2, 0);
        String storedPin= pref2.getString("PIN", null);
        try{
            if ( storedPin.equals( mPin ) ){
                startActivity( new Intent( this, HomeActivity.class ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) );
                finish();
            }else {

                Toast.makeText( this, "Incorrect pin", Toast.LENGTH_SHORT ).show();
            }
        }catch ( NullPointerException e ){
            //do nothing
        }

    }



    private void doLogout() {
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PinLoginActivity.this);
            LayoutInflater inflater1 = (LayoutInflater) PinLoginActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.logout_popup, null);
            LinearLayout ok = (LinearLayout) layout.findViewById(R.id.checkout_back_ok);
            LinearLayout cancel = (LinearLayout) layout.findViewById(R.id.checkout_back_cancel);
            builder.setView(layout);
            final android.app.AlertDialog alertDialog = builder.create();
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    dologoutchanges();
                    startActivity(new Intent(PinLoginActivity.this,WelcomeActivity.class));
                    finish();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        finishAffinity();
                    }
                }
            });
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dologoutchanges() {
        try {
            SharedPreferences Loginpref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
            SharedPreferences.Editor logineditor = Loginpref.edit();
            logineditor.putString("loginsession", "No");
            logineditor.commit();

            SharedPreferences agentnameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF1, 0);
            SharedPreferences.Editor agentnameEditer = agentnameSP.edit();
            agentnameEditer.putString("agentName", "");
            agentnameEditer.commit();

            SharedPreferences.Editor editor = this.getSharedPreferences(Config.PREFERENCE, 0).edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(this, LoginActivity.class));
            this.finish();

        } catch (NullPointerException e) {
        }
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
                        PinLoginActivity.this.startActivity(intent);
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
                                ActivityCompat.requestPermissions(PinLoginActivity.this,
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
                                    Geocoder geocoder = new Geocoder(PinLoginActivity.this, Locale.getDefault());
                                    List<Address> addresses = null;
                                    try {
                                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    locName = addresses.get(0).getAddressLine(0);
                                    Log.e("nnn",""+locName);

                                    String Locality = addresses.get(0).getLocality();
                                    String stateName = addresses.get(0).getAddressLine(1);
                                    String countryName = addresses.get(0).getAddressLine(2);
                                    locationDetails();

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
//                        Geocoder geocoder = new Geocoder(PinLoginActivity.this, Locale.getDefault());
//                        List<Address> addresses = null;
//                        try {
//                            if (geocoder!= null ) {
//                                addresses = geocoder.getFromLocation(latitude, longitude, 1);
//                                locName = addresses.get(0).getAddressLine(0);
//                                Log.e("addresssssssss",""+addresses);
//                                String Locality = addresses.get(0).getLocality();
//                                String stateName = addresses.get(0).getAddressLine(1);
//                                String countryName = addresses.get(0).getAddressLine(2);
//                            }
//
//
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            Log.e("sss",""+e);
//                        }
//
//                        locationDetails();
//                    }
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
                        Geocoder geocoder = new Geocoder(PinLoginActivity.this, Locale.getDefault());
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



                        locationDetails();
                    }
                    else {
                        showSettingsAlert();
                    }
                }
            });
        }
    }


    private void locationDetails() {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(getSSLSocketFactory())
                    .hostnameVerifier(getHostnameVerifier())
                    .build();
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Config.BASEURL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
            ApiInterface apiService = retrofit.create(ApiInterface.class);
            final JSONObject requestObject1 = new JSONObject();
            try {
                SharedPreferences pref1 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF3, 0);
                SharedPreferences pref2 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF4, 0);

                requestObject1.put("LoginMode","17");
                requestObject1.put("Agent_ID", pref1.getString("Agent_ID", null));
                requestObject1.put("Token", pref2.getString("TOKEN", null));
                requestObject1.put("Location_Latitude",String.valueOf(longitude));
                requestObject1.put("Location_Longitude",String.valueOf(longitude));
                requestObject1.put("Location_Name",locName);

            }
            catch (Exception e) {
                e.printStackTrace();
//                progressDialog.dismiss();
            }
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
            Call<String> call = apiService.getAgentLoginLocationDet(body);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
//                        progressDialog.dismiss();
                        JSONObject jObject = new JSONObject(response.body());
                        String StatusCode = jObject.getString("StatusCode");
                        if (StatusCode.equals("1")) {
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

                    } catch (Exception e) {
                        e.printStackTrace();
//                        progressDialog.dismiss();
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
//                    progressDialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
//            progressDialog.dismiss();
        }


    }



    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }

    private TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
        return new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return originalTrustManager.getAcceptedIssuers();
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0){
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkClientTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("checkClientTrusted", e.toString());
                        }
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0){
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkServerTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("checkServerTrusted", e.toString());
                        }
                    }
                }
        };
    }

    private SSLSocketFactory getSSLSocketFactory()
            throws CertificateException, KeyStoreException, IOException,
            NoSuchAlgorithmException, KeyManagementException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = getResources().openRawResource(R.raw.my_cert); // File path: app\src\main\res\raw\your_cert.cer
        Certificate ca = cf.generateCertificate(caInput);
        caInput.close();
        KeyStore keyStore = KeyStore.getInstance("BKS");
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);
        TrustManager[] wrappedTrustManagers = getWrappedTrustManagers(tmf.getTrustManagers());
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, wrappedTrustManagers, null);
        return sslContext.getSocketFactory();
    }
}
