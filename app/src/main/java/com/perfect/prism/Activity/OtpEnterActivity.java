package com.perfect.prism.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.perfect.prism.R;
import com.perfect.prism.Retrofit.ApiInterface;
import com.perfect.prism.Utility.Config;
import com.perfect.prism.Utility.GPSTracker;

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
import java.text.DateFormat;
import java.util.Date;
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

public class OtpEnterActivity extends AppCompatActivity implements View.OnClickListener {
    String TAG="OtpEnterActivity";
    private EditText mEdtOtp;
    private Button btnSubmit;
    private String agentId,otpRefNo,cusName,Token,stringLatitude,stringLongitude,city;
    private ProgressDialog progressDialog;
//    GPSTracker gpsTracker;
//    LocationResult locationResult;


    private String mLastUpdateTime;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private Boolean mRequestingLocationUpdates;
    Geocoder geocoder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_otp_enter);
        initialisation();
        Intent in = getIntent();
        agentId  = in.getStringExtra("Agent_ID");
        otpRefNo = in.getStringExtra("OTPRefNo");
        cusName  = in.getStringExtra("Agent_Name");
        Token  = in.getStringExtra("Token");
        btnSubmit.setOnClickListener(this);
//        gpsTracker = new GPSTracker(this);
//        gpsTrack();

        init();
        restoreValuesFromBundle(savedInstanceState);
        geocoder = new Geocoder(this, Locale.getDefault());
//        startLocation();
    }

    private void initialisation(){
        mEdtOtp = findViewById(R.id.edtOtp);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

//    private void gpsTrack(){
//        // check if GPS enabled
//        if (gpsTracker.getIsGPSTrackingEnabled())
//        {
//            stringLatitude = String.valueOf(gpsTracker.getLatitude());
//            stringLongitude = String.valueOf(gpsTracker.getLongitude());
//            String country = gpsTracker.getCountryName(this);
//            city = gpsTracker.getLocality(this);
//            String postalCode = gpsTracker.getPostalCode(this);
//
//            Toast.makeText(getApplicationContext(),"Latitude : " + stringLatitude + "Longitude : " + stringLongitude,Toast.LENGTH_SHORT).show();
//            String addressLine = gpsTracker.getAddressLine(this);
//
//        }
////        else
////        {
////            // can't get location
////            // GPS or Network is not enabled
////            // Ask user to enable GPS/network in settings
////            gpsTracker.showSettingsAlert();
////        }
//    }

    private void gpsTracking(String tocken,String agentID, String latitude,String longitude, String locName) {

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
            try{

                requestObject1.put("LoginMode","17");
                requestObject1.put("Token",tocken);
                requestObject1.put("Agent_ID",agentID);
                requestObject1.put("Location_Latitude",latitude);
                requestObject1.put("Location_Longitude",longitude);
                requestObject1.put("Location_Name",locName);

            }catch (Exception e){

                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
            Call<String> call = apiService.getAgentLoginLocationDet(body);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        progressDialog.dismiss();
                        JSONObject jObject = new JSONObject(response.body());
                        JSONObject jmember = jObject.getJSONObject("AgentLoginLocationDet");
                        String StatusCode = jObject.getString("StatusCode");
                        if(StatusCode.equals("1")){

//                            SharedPreferences spAgentIg = getApplicationContext().getSharedPreferences(Config.SHARED_PREF3, 0);
//                            SharedPreferences.Editor spAgentIgEditor = spAgentIg.edit();
//                            spAgentIgEditor.putString("Agent_ID", jmember.getString("Agent_ID"));
//                            spAgentIgEditor.commit();


                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    progressDialog.dismiss();
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
            progressDialog.dismiss();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:
                validate();
                break;
        }
    }

    private void validate() {
        if(mEdtOtp.getText().toString().isEmpty()||mEdtOtp.getText().toString().length()!=4){
            mEdtOtp.setError("Enter Valid Otp");
        }else {
            checkOtp(mEdtOtp.getText().toString());
        }
    }

    private void checkOtp(final String mOtp) {
        progressDialog = new ProgressDialog(this, R.style.Progress);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(this.getResources()
                .getDrawable(R.drawable.progress));
        progressDialog.show();
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
            try{

                requestObject1.put("LoginMode","2");
                requestObject1.put("Agent_ID",agentId);
                requestObject1.put("OTPRefNo",otpRefNo);
                requestObject1.put("OTP",mOtp);
                Log.e(TAG,"requestObject1    "+requestObject1  );
            }catch (Exception e){

                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
            Call<String> call = apiService.getOtp(body);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        progressDialog.dismiss();
                        Log.e(TAG,"onResponse    "+response.body()   );
                        JSONObject jObject = new JSONObject(response.body());
                        JSONObject jmember = jObject.getJSONObject("verInfo");
                        String StatusCode = jObject.getString("StatusCode");
                        if(StatusCode.equals("0")){
                            SharedPreferences.Editor editor = getSharedPreferences(Config.PREFERENCE,0).edit();
                            editor.putString(Config.AGNT_ID,jmember.getString("Agent_ID"));
                            editor.putString(Config.TOKEN,jmember.getString("AgToken"));
                            editor.putString(Config.PIN, mOtp );
                            editor.putString( Config.AGENT_NAME, cusName );
                            editor.apply();

                            SharedPreferences Loginpref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                            SharedPreferences.Editor logineditor = Loginpref.edit();
                            logineditor.putString("loginsession", "Yes");
                            logineditor.commit();

                            SharedPreferences spPin = getApplicationContext().getSharedPreferences(Config.SHARED_PREF2, 0);
                            SharedPreferences.Editor pinEditor = spPin.edit();
                            pinEditor.putString("PIN", mOtp);
                            pinEditor.commit();

                            SharedPreferences spAgentIg = getApplicationContext().getSharedPreferences(Config.SHARED_PREF3, 0);
                            SharedPreferences.Editor spAgentIgEditor = spAgentIg.edit();
                            spAgentIgEditor.putString("Agent_ID", jmember.getString("Agent_ID"));
                            spAgentIgEditor.commit();

                            SharedPreferences spToken = getApplicationContext().getSharedPreferences(Config.SHARED_PREF4, 0);
                            SharedPreferences.Editor spTokenEditor = spToken.edit();
                            spTokenEditor.putString("TOKEN", jmember.getString("AgToken"));
                            spTokenEditor.commit();

                            gpsTracking(jmember.getString("AgToken"),jmember.getString("Agent_ID"),stringLatitude,stringLongitude,city);

                            startActivity(new Intent(OtpEnterActivity.this,HomeActivity.class));
                            finish();
                        }
                        else {
                            setAlert(jmember.getString("ResponseMessage"));
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    progressDialog.dismiss();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            progressDialog.dismiss();
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

    public void setAlert(String strMsg){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage(strMsg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }







    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode){
                    case Activity.RESULT_OK:
                        Log.e("", "User agreed to make required location settings changes.");
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e("", "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
        }
    }

//    private void openSettings(){
//        Intent intent = new Intent();
//        intent.setAction(
//                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        Uri uri = Uri.fromParts("package",
//                BuildConfig.APPLICATION_ID, null);
//        intent.setData(uri);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//    }

    @Override
    public void onResume(){
        super.onResume();
        if (mRequestingLocationUpdates && checkPermissions()){
            startLocationUpdates();
        }
        updateLocationUI();
    }

    private boolean checkPermissions(){
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void toggleButtons() {
        if (!mRequestingLocationUpdates){
        }
    }

    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    private void startLocationUpdates(){
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i("", "All location settings are satisfied.");
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode){
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i("", "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try{
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(OtpEnterActivity.this, REQUEST_CHECK_SETTINGS);
                                }catch (IntentSender.SendIntentException sie){
                                    Log.i("", "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e("", errorMessage);
                        }
                        updateLocationUI();
                    }
                });
    }

    @SuppressLint("RestrictedApi")
    private void init(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                updateLocationUI();
            }
        };
        mRequestingLocationUpdates = false;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Restoring values from saved instance state
     */
    private void restoreValuesFromBundle(Bundle savedInstanceState){
        if (savedInstanceState != null){
            if (savedInstanceState.containsKey("is_requesting_updates")){
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
            }
            if (savedInstanceState.containsKey("last_known_location")){
                mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
            }
            if (savedInstanceState.containsKey("last_updated_on")){
                mLastUpdateTime = savedInstanceState.getString("last_updated_on");
            }
        }
        updateLocationUI();
    }

    /**
     * Update the UI displaying the location data
     * and toggling the buttons
     */
    private void updateLocationUI(){
        if (mCurrentLocation != null){
            stringLatitude = String.valueOf(mCurrentLocation.getLatitude());
            stringLongitude = String.valueOf(mCurrentLocation.getLongitude());

            Toast.makeText(getApplicationContext(),"Lat : "+ stringLatitude+"\nLon : "+ stringLongitude,Toast.LENGTH_SHORT).show();
        }
        toggleButtons();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
        outState.putParcelable("last_known_location", mCurrentLocation);
        outState.putString("last_updated_on", mLastUpdateTime);
    }

//    public void startLocation() {
//        Dexter.withActivity(this)
//                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//                .withListener(new PermissionListener() {
//                    @Override
//                    public void onPermissionGranted(PermissionGrantedResponse response){
//                        mRequestingLocationUpdates = true;
//                        startLocationUpdates();
//                    }
//                    @Override
//                    public void onPermissionDenied(PermissionDeniedResponse response){
//                        if (response.isPermanentlyDenied()){
//                            openSettings();
//                        }
//                    }
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {
//                        token.continuePermissionRequest();
//                    }
//                }).check();
//    }
}
