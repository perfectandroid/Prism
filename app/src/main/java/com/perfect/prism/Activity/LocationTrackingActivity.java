package com.perfect.prism.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.perfect.prism.R;
import com.perfect.prism.Retrofit.ApiInterface;
import com.perfect.prism.Utility.Config;
import com.perfect.prism.Utility.InternetUtil;

import org.json.JSONArray;
import org.json.JSONException;
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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class LocationTrackingActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener{

    String TAG="LocationTrackingActivity";
    private ProgressDialog progressDialog;
    private GoogleMap mMap;
    ImageView imBack;
    TextView tv_popuptitle;
    TextView  namee,mobilee ,location_address ;
    ImageView iv_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_map);

        try{
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
        }



        imBack = findViewById(R.id.imBack);
        imBack.setOnClickListener(this);
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
    public void onMapReady(final GoogleMap googleMap) {

        if (new InternetUtil(LocationTrackingActivity.this).isInternetOn()) {
            try{
                progressDialog = new ProgressDialog(LocationTrackingActivity.this, R.style.Progress);
                progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.setIndeterminateDrawable(LocationTrackingActivity.this.getResources().getDrawable(R.drawable.progress));
                progressDialog.show();

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
                    requestObject1.put("LoginMode", "18");
                    requestObject1.put("Agent_ID", pref1.getString("Agent_ID", null));

                    Log.e(TAG,"requestObject1  133  "+requestObject1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getRptAgentLocationDetails(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            progressDialog.dismiss();

                            JSONObject jObject = new JSONObject(response.body());
                            Log.e("akn","location"+jObject);
                            if(jObject.getString("StatusCode").equals("0")){
                                JSONObject jobj = jObject.getJSONObject("RptAgentLocationDetails");
                                final JSONArray jsonArray = jobj.getJSONArray("RptAgentLocationDetailsList");
                                JSONArray jary = new JSONArray();
                                for(int i = 0 ; i < jsonArray.length() ; i++) {
                                    mMap = googleMap;
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String lat = jsonObject.getString("LocationLatitude");
                                    String lon = jsonObject.getString("LocationLongitude");
                                    if (lat.equals("")   && lon.equals("") ){
                                        String name = jsonObject.getString("AgentName");
                                    }
                                    else {
                                        String name = jsonObject.getString("AgentName");
                                        String mob = jsonObject.getString("AgentMobile");
                                        String location = jsonObject.getString("LocationName");
                                        // Add a marker in Sydney and move the camera

                                        try{
                                            LatLng loc = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
//                                            mMap.addMarker(new MarkerOptions().position(loc).title(name+", Mob Numb : "+mob+", Location : "+location).snippet(name));
//                                            name = "alkjlh";
//                                            mob = "9497093212";
//                                            location = "kozhikode";
                                            Log.e("akn","name"+name);
                                            Log.e("akn","mob"+mob);
                                            Log.e("akn","loc"+location);
                                            Log.e("akn","latlong"+loc);


                                            MarkerOptions markerOptions = new MarkerOptions();
                                            markerOptions.position(loc);
                                            markerOptions.title(name);
                                            markerOptions.snippet(name+"-akn-"+mob+"-akn-"+location);

                                            Marker locationMarker = mMap.addMarker(markerOptions);
                                            locationMarker.showInfoWindow();
//                                            locationMarker.isInfoWindowShown();

//                                            mMap.addMarker(new MarkerOptions().position(loc).snippet(name+"-akn-"+mob+"-akn-"+location).title(name));
                                            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                                            float zoomLevel = (float) 18.0;
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, zoomLevel));


                                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                                @Override
                                                public boolean onMarkerClick(Marker marker) {
                                                    marker.showInfoWindow();
                                                 String snippet =   marker.getSnippet();
                                                    String[] parts = snippet.split("-akn-", 3);
                                                    String name = parts[0];  //
                                                    String mob = parts[1]; //
                                                    String loc = parts[2];
                                                    getdetails(name,mob,loc);
//                                                    Toast.makeText(LocationTrackingActivity.this, name+"...."+mob+"...... "+loc, Toast.LENGTH_SHORT).show();
                                                    return false;
                                                }
                                            });




                                            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                                                @Override
                                                public View getInfoWindow(Marker arg0) {
                                                    return null;
                                                }

                                                @Override
                                                public View getInfoContents(Marker marker) {

                                                    Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.

                                                    LinearLayout info = new LinearLayout(context);
                                                    info.setOrientation(LinearLayout.VERTICAL);

                                                    TextView title = new TextView(context);
                                                    title.setTextColor(Color.BLACK);
                                                    title.setGravity(Gravity.CENTER);
                                                    title.setTypeface(null, Typeface.BOLD);
                                                    title.setText(marker.getTitle());

                                                    TextView snippet = new TextView(context);
                                                    snippet.setTextColor(Color.GRAY);
//                                                    snippet.setText(marker.getSnippet());

                                                    info.addView(title);
//                                                    info.addView(snippet);

                                                    return info;
                                                }
                                            });



                                        }
                                        catch (Exception e) {
                                            e.printStackTrace();
                                            Log.e("akn","eeee"+e);
                                            Toast.makeText( getApplicationContext(),"Something went wrong. Please try again later." , Toast.LENGTH_LONG ).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                }
                            }

                            else  {
                                JSONObject jobj = jObject.getJSONObject("RptAgentLocationDetails");
                                Toast.makeText( getApplicationContext(),jobj.getString("ResponseMessage") , Toast.LENGTH_LONG ).show();
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();

                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        progressDialog.dismiss();
                        String g = t.toString();
                    }
                });
            }
            catch (Exception e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }
        }else {
            Toast.makeText(this,"No internet connection",Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imBack:
                finish();
        }
    }

    private void getdetails(String name, String mob, String loc) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater1 = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.map_popup_details, null);
            tv_popuptitle = (TextView) layout.findViewById(R.id.tv_popuptitle);
            namee = (TextView) layout.findViewById(R.id.namee);
            mobilee = (TextView) layout.findViewById(R.id.mobilee);
            location_address = (TextView) layout.findViewById(R.id.location_address);
            iv_close = layout.findViewById(R.id.iv_close);
            tv_popuptitle.setText("Agent Details");
            namee.setText(name);
            mobilee.setText(mob);
            location_address.setText(loc);

            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();

        }catch (Exception e){e.printStackTrace();}
    }
}