package com.perfect.prism.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.perfect.prism.Adapter.SearchHomeProductAdapter;
import com.perfect.prism.Adapter.TicketListAdapter;
import com.perfect.prism.Model.ProductModel;
import com.perfect.prism.Model.TicketModel;
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
import java.util.ArrayList;
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

public class TicketListActivity extends AppCompatActivity implements View.OnClickListener {

    String TAG="TicketListActivity";
    String subMode, ID_Product, ID_Client,FK_Product;
    ProgressDialog progressDialog;
    ListView list_view;
    EditText etsearch;
    int textlength = 0;
    protected String locName;
    LinearLayout activity_search;
    SharedPreferences sharedPreferences;
    private LinearLayout constrEmpty;
    private Button mBtnStart;
    private Button mBtnStop;
    private String mStartStopStatus = "";
    private static final int START_LOCATION = 100;
    private FusedLocationProviderClient fusedLocationClient;
    ArrayList<TicketModel> searchNamesArrayList = new ArrayList<>();
    public static ArrayList<TicketModel> array_sort= new ArrayList<>();
    TicketListAdapter sadapter;
    protected double latitude;
    protected double longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_list);
        Intent in = getIntent();
        subMode = in.getStringExtra("subMode");
        ID_Product = in.getStringExtra("ID_Product");
        ID_Client = in.getStringExtra("ID_Client");
//        FK_Product = in.getStringExtra("FK_Product");
        Log.e(TAG,"subMode    "+subMode);
        initiateViews();
        setRegViews();
        getTicketList(subMode);
    }
    private void initiateViews() {
        list_view = findViewById(R.id.list_view);
        etsearch = findViewById(R.id.etsearch);
        mBtnStart = findViewById( R.id.btn_start );
        mBtnStop = findViewById( R.id.btn_stop );
        constrEmpty = findViewById( R.id.constr_empty );
        activity_search = findViewById( R.id.activity_search );
    }
    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id){

            case R.id.btn_start:
                this.mStartStopStatus = "1";
                fetchLocation();

                break;
            case R.id.btn_stop:
                this.mStartStopStatus = "2";
                getCurrentlocation();
                //     startStopWork(1,3,"");
                break;

        }

    }

    private void getCurrentlocation() {
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
                        Geocoder geocoder = new Geocoder(TicketListActivity.this, Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            locName = addresses.get(0).getAddressLine(0);
                            String Locality = addresses.get(0).getLocality();
                            String stateName = addresses.get(0).getAddressLine(1);
                            String countryName = addresses.get(0).getAddressLine(2);

                            startStopWork(latitude,longitude,locName);
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

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data ){
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == START_LOCATION ){
            if ( resultCode == RESULT_OK ){
                Bundle bundle = data.getExtras();
                if ( bundle == null )
                    return;
                double latitude = bundle.getDouble(Config.LATI);
                double longitude= bundle.getDouble( Config.LONGI );
                String address = bundle.getString( Config.ADDRESS );
                startStopWork( latitude, longitude, address);

            }
        }
    }


    private void startStopWork( double lati, double longi, String address ){
        if ( mStartStopStatus.isEmpty() || ( !mStartStopStatus.equals("1") && !mStartStopStatus.equals("2") ) )
            return;
        try {
            progressDialog = new ProgressDialog(TicketListActivity.this, R.style.Progress);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setIndeterminateDrawable(this.getResources()
                    .getDrawable(R.drawable.progress));
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
            String latitude = Double.toString( lati );
            String longitude = Double.toString( longi );
            List<String> startStopTicketList = sadapter.getStartStopList( mStartStopStatus );
            if ( startStopTicketList == null )
                return;
            String ticketsParam;
            JSONArray jsonArray = new JSONArray();
            try {

                requestObject1.put("is_start", mStartStopStatus);
                for (String ticketId : startStopTicketList ) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Ticket_ID", ticketId );
                    jsonArray.put( jsonObject );
                }
                ticketsParam = jsonArray.toString();
                requestObject1.put("jsondata",ticketsParam);
                requestObject1.put("LoginMode","8");

                if ( this.mStartStopStatus.equals("1")){
                    requestObject1.put("Location_Longitude", longitude );
                    requestObject1.put("Location_Latitude", latitude );
                    requestObject1.put("Location_Name", address);
                }

                SharedPreferences pref1 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF3, 0);
                SharedPreferences pref2 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF4, 0);
                requestObject1.put("FK_Company",Config.FK_Company);
                requestObject1.put("Token", pref2.getString("TOKEN", null));
                requestObject1.put("Agent_ID",pref1.getString("Agent_ID", null));

                Log.e(TAG,"requestObject1  204   "+requestObject1);

            }catch (Exception e){

                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
            Call<String>call = apiService.getAgentTicketTimeStatus(body);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {

                        Log.e(TAG,"requestObject1  217   "+response.body());
                        progressDialog.dismiss();
                        if(getApplicationContext()==null)
                            return;

                        JSONObject jObject = new JSONObject(response.body());
                        JSONObject jobj = jObject.getJSONObject("RootupdateTicketTimeInfo");
                        String RespondsCode = jobj.getString("ResponseCode");
                        if(jObject.getString("StatusCode").equals("-1")) {
                            dologoutchanges();
                            startActivity(new Intent(TicketListActivity.this,WelcomeActivity.class));
                            finish();
                        }
                        if(RespondsCode.equals("000")){
                            Toast.makeText(getApplicationContext(), "Successfully updated status", Toast.LENGTH_LONG ).show();
                            getTicketList(subMode);
                            mBtnStart.setVisibility( View.GONE );
                            mBtnStop.setVisibility( View.GONE );
                        }else {
                            Toast.makeText( getApplicationContext(),jobj.getString("ResponseMessage") , Toast.LENGTH_LONG ).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if ( progressDialog != null){
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    progressDialog.dismiss();
                }
            });




        }catch (Exception e){
            progressDialog.dismiss();
            e.printStackTrace();

        }
    }

    private void fetchLocation(){
        startActivityForResult( new Intent( TicketListActivity.this, LocationActivity.class ), START_LOCATION);
    }

    private void setRegViews() {
        mBtnStop.setOnClickListener( this );
        mBtnStart.setOnClickListener( this );
    }

    public void getTicketList(String submode){
        if (new InternetUtil(this).isInternetOn()) {
            progressDialog = new ProgressDialog(this, R.style.Progress);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setIndeterminateDrawable(this.getResources()
                    .getDrawable(R.drawable.progress));
            progressDialog.show();
            try{
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
                    requestObject1.put("LoginMode", "4");
                    requestObject1.put("FK_Company", Config.FK_Company);
                    requestObject1.put("LoginsubMode",submode);
                    requestObject1.put("Token", pref2.getString("TOKEN", null));
                    requestObject1.put("Agent_ID",pref1.getString("Agent_ID", null));
                    requestObject1.put("ID_Client",ID_Client);
                    requestObject1.put("ID_Product",ID_Product);
                    requestObject1.put("ID_Department","0");
//                    requestObject1.put("FK_Product",FK_Product);

                    Log.e(TAG,"requestObject1  307   "+requestObject1);
//                    Log.e(TAG,"maa1   "+FK_Product);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getAgentTicket(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            progressDialog.dismiss();
                            Log.e(TAG,"onResponse  318 "+response.body());
                            JSONObject jObject = new JSONObject(response.body());
                            JSONObject jobj = jObject.getJSONObject("AgentRootTicketsSelectinfo");
                            Log.e(TAG,"onResponse  325 "+jobj.getString("TicketsDetailsList"));
                            if (jobj.getString("TicketsDetailsList").equals("null")) {
                                if(jObject.getString("StatusCode").equals("-1")) {
                                    Toast.makeText( getApplicationContext(),jobj.getString("ResponseMessage") , Toast.LENGTH_LONG ).show();

                                    dologoutchanges();
                                    startActivity(new Intent(TicketListActivity.this,WelcomeActivity.class));
                                    finish();
                                } else {

                                    Snackbar.make(findViewById(R.id.ticketlist_view), jobj.getString("ResponseMessage"), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            } else {
                                JSONArray jarray = jobj.getJSONArray("TicketsDetailsList");
                                array_sort = new ArrayList<>();
                                for (int k = 0; k < jarray.length(); k++) {
                                    JSONObject jsonObject = jarray.getJSONObject(k);
                                    searchNamesArrayList.add(new TicketModel(jsonObject.getString("ID_Tickets"),jsonObject.getString("TickNo"),
                                            jsonObject.getString("SlNo"),jsonObject.getString("TickDate"),
                                            jsonObject.getString("TickSubject"),jsonObject.getString("TickPriority"),
                                            jsonObject.getString("TickStatus"),jsonObject.getString("UserDate"),
                                            jsonObject.getString("CliName"),jsonObject.getString("UserCount"),
                                            jsonObject.getString("AgentCount"),jsonObject.getString("Startingstatus"),
                                            jsonObject.getString("StartTime"),jsonObject.getString("EndTime"),
                                            jsonObject.getString("TotalTime")
                                    ));
                                    array_sort.add(new TicketModel(jsonObject.getString("ID_Tickets"),jsonObject.getString("TickNo"),
                                            jsonObject.getString("SlNo"),jsonObject.getString("TickDate"),
                                            jsonObject.getString("TickSubject"),jsonObject.getString("TickPriority"),
                                            jsonObject.getString("TickStatus"),jsonObject.getString("UserDate"),
                                            jsonObject.getString("CliName"),jsonObject.getString("UserCount"),
                                            jsonObject.getString("AgentCount"),jsonObject.getString("Startingstatus"),
                                            jsonObject.getString("StartTime"),jsonObject.getString("EndTime"),
                                            jsonObject.getString("TotalTime")
                                    ));
                                }
                                sadapter = new TicketListAdapter(TicketListActivity.this, array_sort, new TicketListAdapter.ClickHandler() {
                                    @Override
                                    public void onClick(TicketModel ticketsDetailModel) {
                                        Intent intent = new Intent(getApplicationContext(),TicketDetailsActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("ticketData",ticketsDetailModel.toString());
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void startStopBtnColorChanger(int toStartLength, int toStopLength) {

                                        if ( toStartLength  < 1  ){
                                            mBtnStart.setVisibility( View.GONE );
                                        }else {
                                            mBtnStart.setVisibility( View.VISIBLE );
                                        }
                                        if ( toStopLength < 1 ){
                                            mBtnStop.setVisibility( View.GONE );
                                        }else {
                                            mBtnStop.setVisibility( View.VISIBLE );
                                        }

                                    }
                                },subMode);
                                list_view.setAdapter(sadapter);
                                list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    }
                                });

                            }
                            etsearch.addTextChangedListener(new TextWatcher() {
                                public void afterTextChanged(Editable s) {
                                }
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    try {
                                        list_view.setVisibility(View.VISIBLE);
                                        textlength = etsearch.getText().length();
                                        array_sort.clear();
                                        for (int i = 0; i < searchNamesArrayList.size(); i++) {
                                            if (textlength <= searchNamesArrayList.get(i).getTickNo().length()) {
                                                if (searchNamesArrayList.get(i).getTickNo().toLowerCase().trim().contains(
                                                        etsearch.getText().toString().toLowerCase().trim())) {
                                                    array_sort.add(searchNamesArrayList.get(i));
                                                } else {

                                                    // Toast.makeText(TicketListActivity.this,"Issue 1",Toast.LENGTH_SHORT).show();
                                                }
                                            } else {

                                                //  Toast.makeText(TicketListActivity.this,"Issue 1",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        sadapter = new TicketListAdapter(TicketListActivity.this, array_sort, new TicketListAdapter.ClickHandler() {
                                            @Override
                                            public void onClick(TicketModel ticketsDetailModel) {

                                                Intent intent = new Intent(getApplicationContext(), TicketDetailsActivity.class);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("ticketData", ticketsDetailModel.toString());
                                                startActivity(intent);

                                            }

                                            @Override
                                            public void startStopBtnColorChanger(int toStartLength, int toStopLength) {

                                                if (toStartLength < 1) {
                                                    mBtnStart.setVisibility(View.GONE);
                                                } else {
                                                    mBtnStart.setVisibility(View.VISIBLE);
                                                }
                                                if (toStopLength < 1) {
                                                    mBtnStop.setVisibility(View.GONE);
                                                } else {
                                                    mBtnStop.setVisibility(View.VISIBLE);
                                                }

                                            }
                                        }, subMode);
                                        list_view.setAdapter(sadapter);
                                    }catch (Exception e){
                                        e.printStackTrace();

                                        //Toast.makeText(TicketListActivity.this,"Issue 3",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            activity_search.setVisibility(View.GONE);
                            progressDialog.dismiss();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        progressDialog.dismiss();}
                });
            } catch (Exception e) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item:
                Intent i = new Intent(TicketListActivity.this, HomeActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                        TicketListActivity.this.startActivity(intent);
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
}
