package com.perfect.prism.Activity;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
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
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.BuildConfig;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.perfect.prism.Adapter.SearchProductAdapter;
import com.perfect.prism.Model.ProductModel;
import com.perfect.prism.R;
import com.perfect.prism.Retrofit.ApiInterface;
import com.perfect.prism.Utility.Config;
import com.perfect.prism.Utility.InternetUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    String TAG="HomeActivity";
    private ProgressDialog progressDialog;
    TextView refreshTime,tvUsername, tvSoftwarePending, tvResolved, tvClosed, tvPending, tvOpen,tvclientSidewaiting;
    ImageView txtvQR,txtRefresh;
    LinearLayout llopen,llPending, llClosed, llresolved,llsoftwarepending,llclientSidewaiting;
    String latitude,longitude;
    String password;
    protected double lattitude;
    protected double longittude;
    protected String locName;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private FusedLocationProviderClient fusedLocationClient;
    private String reCalculate="false";
    String str_char;
    StringBuilder sb1;
    String str_ascvalues;
    String str_ascii;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        checkLocationPermission();
        initiateViews();
        setRegViews();
        getHomeData();
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF1, 0);
        String name = "Welcome "+pref.getString("agentName", null);
        tvUsername.setText(name);
         refreshView();
//        SharedPreferences pref1 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
//        String refreshtime = "Last Refresh : "+pref1.getString("refreshtime", null);
//        Log.v("response","time:"+refreshtime);
//        refreshTime.setText(refreshtime);


     //   getQRcode("12345");

    }

    private void refreshView() {
        SharedPreferences pref1 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String refreshtime = "Last Refresh : "+pref1.getString("refreshtime", null);

        String time=pref1.getString("refreshtime", null);

        if (time==null)
        {
            refreshTime.setVisibility(View.GONE);
            Log.v("response","time1:"+time);
        }
        else
        {
            Log.v("response","time2::"+time);
            refreshTime.setVisibility(View.VISIBLE);
            refreshTime.setText(refreshtime);
        }

//        Log.v("response","time::"+time);
//        if (refreshtime.equals(null))
//        {
//            Log.v("response","time:"+refreshtime);
//            refreshTime.setText(refreshtime);
//        }
//        else
//        {
//            Log.v("response","time:"+refreshtime);
//            refreshTime.setText(refreshtime);
//        }



    }


    public void getHomeData(){
        if (new InternetUtil(HomeActivity.this).isInternetOn()) {
            try{
                progressDialog = new ProgressDialog(HomeActivity.this, R.style.Progress);
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
                try {

                    SharedPreferences pref1 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF3, 0);
                    SharedPreferences pref2 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF4, 0);


                    requestObject1.put("LoginMode", "3");
                    requestObject1.put("FK_Company", Config.FK_Company);
                    requestObject1.put("Agent_ID", pref1.getString("Agent_ID", null));
                    requestObject1.put("Token", pref2.getString("TOKEN", null));
                    requestObject1.put("latitude", lattitude);
                    requestObject1.put("longitude", longittude);
                    requestObject1.put("location_name", locName);
//                    requestObject1.put("longitude", "256");
//                    requestObject1.put("latitude", "256");
//                    requestObject1.put("location_name", "Calicut");

                    Log.e(TAG,"requestObject1 162   "+requestObject1);
                    Log.v("response","object=   "+requestObject1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getHomeNotificationcount(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            NavigationView navigationView = findViewById(R.id.nav_view);
                            navigationView.setNavigationItemSelectedListener(HomeActivity.this);
                            Menu navMenus = navigationView.getMenu();
                            progressDialog.dismiss();
                            Log.e(TAG,"onResponse 176   "+response.body());
                            JSONObject jObject = new JSONObject(response.body());
                            JSONObject jobj = jObject.getJSONObject("AgentTicketNotificationInfo");
                            if(jObject.getString("StatusCode").equals("0")){
                                tvSoftwarePending.setText(""+jobj.getInt("SoftwarePending"));
                                tvclientSidewaiting.setText(""+jobj.getInt("ClientSideWaiting"));
//                                tvResolved.setText(""+jobj.getInt("ResTickets"));
                                tvResolved.setText(""+jobj.getInt("SPClientReplied"));
                                tvClosed.setText(""+jobj.getInt("ClosedTickets"));
                                tvPending.setText(""+jobj.getInt("Tasks"));
                                tvOpen.setText(""+jobj.getInt("OpnTkts"));

                                SharedPreferences IsAccesSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF5, 0);
                                SharedPreferences.Editor IsAccesEditer = IsAccesSP.edit();
                                IsAccesEditer.putString("IsAcces",jobj.getString("IsAcces"));
                                IsAccesEditer.commit();

                                if(jobj.getString("IsAcces").equals("False")){
                                    navMenus.findItem(R.id.navReport).setVisible(false);
                                    navMenus.findItem(R.id.navLocationReport).setVisible(false);
                                    navMenus.findItem(R.id.navLocationTracking).setVisible(false);
                                    navMenus.findItem(R.id.navTotalTicket).setVisible(false);
                                    navMenus.findItem(R.id.navAssignTicket).setVisible(false);

                                }
                                else if(jobj.getString("IsAcces").equals("True"))
                                {
                                    navMenus.findItem(R.id.navReport).setVisible(true);
                                    navMenus.findItem(R.id.navLocationReport).setVisible(true);
                                    navMenus.findItem(R.id.navLocationTracking).setVisible(true);
                                    navMenus.findItem(R.id.navTotalTicket).setVisible(true);
                                    navMenus.findItem(R.id.navAssignTicket).setVisible(true);
                                }
                            } else if(jObject.getString("StatusCode").equals("-1"))
                            {
                                Toast.makeText( getApplicationContext(),jobj.getString("ResponseMessage") , Toast.LENGTH_LONG ).show();
                                dologoutchanges();
                                startActivity(new Intent(HomeActivity.this,WelcomeActivity.class));
                                finish();
                            }else
                            {

                                navMenus.findItem(R.id.navReport).setVisible(false);
                                navMenus.findItem(R.id.navLocationReport).setVisible(false);
                                navMenus.findItem(R.id.navLocationTracking).setVisible(false);
                                navMenus.findItem(R.id.navTotalTicket).setVisible(false);
                                navMenus.findItem(R.id.navAssignTicket).setVisible(false);
                                Snackbar.make(findViewById(R.id.home_view), jobj.getString("ResponseMessage") , Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        } catch (JSONException e) {
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
            } catch (Exception e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }
        }else {
            Toast.makeText(this,"No internet connection",Toast.LENGTH_SHORT).show();
        }
    }

    private void getRefresh() {
        if (new InternetUtil(HomeActivity.this).isInternetOn()) {
            try
            {
                progressDialog = new ProgressDialog(HomeActivity.this, R.style.Progress);
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
                //314400

                try {
                    SharedPreferences pref1 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF3, 0);
                    SharedPreferences pref2 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF4, 0);

                    requestObject1.put("AgCode",  pref1.getString("Agent_ID", null));
                    requestObject1.put("FK_Company", Config.FK_Company);
                    requestObject1.put("Recalculate",reCalculate );
                    Log.v("response","refresh data=   "+requestObject1);
                }


                catch (Exception ee)
                {
                    ee.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getRefreshNotificationcount(body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        try{
                            progressDialog.dismiss();
                            JSONObject jObject = new JSONObject(response.body());
                            Log.v("response refresh","data=   "+response.body());
                            JSONObject jobj = jObject.getJSONObject("AgentTicketNotificationInfo");
                            if(jObject.getString("StatusCode").equals("0"))
                            {
                                tvPending.setText(""+jobj.getInt("Tasks"));
                                tvOpen.setText(""+jobj.getInt("OpnTkts"));
                                tvResolved.setText(""+jobj.getInt("SPClientReplied"));
                                tvclientSidewaiting.setText(""+jobj.getInt("ClientSideWaiting"));

                                Date date = Calendar.getInstance().getTime();
                                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm a");
                                String formattedDate = df.format(date);

                                SharedPreferences logintimeSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
                                SharedPreferences.Editor logintimeEditer = logintimeSP.edit();
                                logintimeEditer.putString("refreshtime", formattedDate);
                                logintimeEditer.commit();
                                Log.v("response","refresh time=   "+formattedDate);

//                                Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
//                                startActivity(intent);
                                refreshView();
                            }
                            else if (jObject.getString("StatusCode").equals("-1"))
                            {
                                Toast.makeText( getApplicationContext(),jobj.getString("ResponseMessage") , Toast.LENGTH_LONG ).show();
                            }


                        }
                        catch (Exception e)
                        {
                            Log.v("response","response catch==   "+e);
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.v("response","onFailure=   "+t);
                        progressDialog.dismiss();
                        String g = t.toString();

                    }
                });


            }
            catch (Exception e)
            {
                progressDialog.dismiss();
            }

        }
        else
        {
            Toast.makeText(this,"No internet connection",Toast.LENGTH_SHORT).show();

        }
    }


    private void initiateViews() {
        refreshTime=findViewById(R.id.refreshTime);
        tvSoftwarePending=findViewById(R.id.tvSoftwarePending);
        tvUsername=findViewById(R.id.tvUsername);
        tvResolved=findViewById(R.id.tvResolved);
        tvClosed=findViewById(R.id.tvClosed);
        tvPending=findViewById(R.id.tvPending);
        tvOpen=findViewById(R.id.tvOpen);
        tvclientSidewaiting=findViewById(R.id.tvclientSidewaiting);
        llopen=findViewById(R.id.llopen);
        llPending=findViewById(R.id.llPending);
        llClosed=findViewById(R.id.llClosed);
        llresolved=findViewById(R.id.llresolved);
        llsoftwarepending=findViewById(R.id.llsoftwarepending);
        llclientSidewaiting=findViewById(R.id.llclientSidewaiting);
        txtvQR=findViewById(R.id.txtvQR);
        txtRefresh=findViewById(R.id.txtRefresh);
    }

    private void setRegViews() {
        llopen.setOnClickListener(this);
        llPending.setOnClickListener(this);
        llClosed.setOnClickListener(this);
        llresolved.setOnClickListener(this);
        llsoftwarepending.setOnClickListener(this);
        llclientSidewaiting.setOnClickListener(this);
        txtvQR.setOnClickListener(this);
        txtRefresh.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            showBackPopup();
        }
    }

    private void showBackPopup() {
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HomeActivity.this);
            LayoutInflater inflater1 = (LayoutInflater) HomeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.quit_app_popup, null);
            LinearLayout ok = layout.findViewById(R.id.quit_app_ok);
            LinearLayout cancel =  layout.findViewById(R.id.quit_app_cancel);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.navHome) {
        } else if (id == R.id.navReport) {
            getReport();
        } else if (id == R.id.navLocationReport) {
            //startActivity(new Intent(HomeActivity.this, LocationReportActivity.class));
            startActivity(new Intent(HomeActivity.this, LocaReportActivity.class));
        }else if (id == R.id.navLocationTracking) {
            startActivity(new Intent(HomeActivity.this, LocationTrackingActivity.class));
        } else if (id == R.id.navTotalTicket) {
            startActivity(new Intent(HomeActivity.this, TotalTicketCountActivity.class));
        } else if (id == R.id.navAssignTicket) {
            startActivity(new Intent(HomeActivity.this, TicketCreationActivity.class));

        } else if (id == R.id.scanqr) {
            IntentIntegrator integrator = new IntentIntegrator(HomeActivity.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.setPrompt("Scan");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(false);
            integrator.setBarcodeImageEnabled(false);
            integrator.initiateScan();

        }
        else if (id == R.id.navPinchange) {
            callChangePinDialog();
        }

        else if (id == R.id.txtv_pswdgen) {
           pswdgen();
        } else if (id == R.id.navLogout) {
            doLogout();
        }else if (id == R.id.navQuit) {
            showBackPopup();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void pswdgen() {
        DateFormat formatter = new SimpleDateFormat("ddMMyyyy");
        Calendar obj = Calendar.getInstance();
        String str = formatter.format(obj.getTime());
        int num = Integer.valueOf(str);
        System.out.println("Num: "+num );
        getSum(num);
        System.out.println("Addition"+getSum(num));
        int num1=getSum(num);
        getSum1(num1);
        int num2=getSum1(num1);
        System.out.println("Addition2 "+num2);
        String numval = String.valueOf(getSum1(num1));
        System.out.println("Numbers "+ numval);


        try
        {
            SharedPreferences sharedPreferences2 = getApplicationContext().getSharedPreferences(Config.USER_NAME, 0);
            String name = sharedPreferences2.getString("user_name", "");


          //  String name ="Prayag";
            StringBuilder sb = new StringBuilder();
            sb1 = new StringBuilder();

            for (char c : name.toCharArray())
            {
                int d=c;
                int sumascii=c+num2;
                sb1.append(sumascii+" ");
                sb.append((int)c+" ");
            }
            System.out.println("ASCII value of name1 "+sb);
            System.out.println("ASCII value of names "+sb1);

        }   catch(NumberFormatException e)
        {
            System.out.println("Exception"+e.toString());
        }

        str_ascvalues=sb1.toString();
        str_ascvalues=str_ascvalues.replace(" ", "");


        if (!numval.isEmpty()) {
            char c = numval.charAt(0);
            if (c%2==0)
            {
                str_char="#";


                // Toast.makeText(HomeActivity.this, c + " is even", Toast.LENGTH_LONG).show();
            }

            else
            {
                str_char="@";
                //Toast.makeText(HomeActivity.this, c + " is odd.", Toast.LENGTH_LONG).show();
            }
            password=str_char+str_ascvalues;
            System.out.println("PASSWORD "+password);


            showPasswd(password);

        }


    }

    private void showPasswd(String password) {


            try {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HomeActivity.this);
                LayoutInflater inflater1 = (LayoutInflater) HomeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater1.inflate(R.layout.activity_pass, null);
                LinearLayout ok = layout.findViewById(R.id.ll_ok);
                TextView txtv_pass=layout.findViewById(R.id.txtv_pass);
                txtv_pass.setText("Password: "+password);
               // LinearLayout cancel =  layout.findViewById(R.id.quit_app_cancel);
                builder.setView(layout);
                final android.app.AlertDialog alertDialog = builder.create();

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
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

    private int getSum1(int num1) {
        int sum1;

        /* Single line that calculates sum */
        for (sum1 = 0; num1 > 0; sum1 += num1 % 10,
                num1 /= 10);

        return sum1;

    }

    private int getSum(int num) {

        int sum;

        /* Single line that calculates sum */
        for (sum = 0; num > 0; sum += num % 10,
                num /= 10);

        return sum;
    }


    private void getReport() {
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HomeActivity.this);
            LayoutInflater inflater1 = (LayoutInflater) HomeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.agent_popup, null);
            final RadioGroup rgPrice = (RadioGroup) layout.findViewById(R.id.rgPrice);
            builder.setView(layout);
            final android.app.AlertDialog alertDialog = builder.create();
            rgPrice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    View radioButton = rgPrice.findViewById(checkedId);
                    int index = rgPrice.indexOfChild(radioButton);
                    switch (index) {
                        case 0:
                            startActivity(new Intent(HomeActivity.this, AgentwiseReportActivity.class));
                            alertDialog.dismiss();
                            break;
                        case 1:
                            startActivity(new Intent(HomeActivity.this, ClientwiseReportActivity.class));
                            alertDialog.dismiss();
                            break;
                        case 2:
                            startActivity(new Intent(HomeActivity.this, AgeingReportActivity.class));
                            alertDialog.dismiss();
                            break;
                    }
                }
            });
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doLogout() {
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HomeActivity.this);
            LayoutInflater inflater1 = (LayoutInflater) HomeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                    startActivity(new Intent(HomeActivity.this,WelcomeActivity.class));
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

    private void callChangePinDialog() {
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HomeActivity.this);
            LayoutInflater inflater1 = (LayoutInflater) HomeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.change_pin_layout, null);
            Button btn_cancel =layout.findViewById(R.id.btn_cancel);
            Button btn_ok =  layout.findViewById(R.id.btn_ok);
            final EditText edtCurrPin             = layout.findViewById(R.id.txtCurrentPin);
            final EditText edtNewPin              = layout.findViewById(R.id.txtNewPin);
            final EditText edtConfPin             = layout.findViewById(R.id.txtConfirmPin);
            builder.setView(layout);
            final android.app.AlertDialog alertDialog = builder.create();
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SharedPreferences pref2 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF2, 0);
                    String storedPin= pref2.getString("PIN", null);
                    String currPin  = edtCurrPin.getText().toString();
                    String NewPin   = edtNewPin.getText().toString();
                    String ConfPin  = edtConfPin.getText().toString();
                    if (currPin.isEmpty()||!(currPin.length()==4)) {
                        Toast.makeText(HomeActivity.this,"Enter 4-Digit Current Pin",Toast.LENGTH_LONG).show();
                    }else if(!storedPin.equals(currPin)){
                        Toast.makeText(HomeActivity.this,"Please enter correct mPin",Toast.LENGTH_LONG).show();
                    }else{
                        if(NewPin.isEmpty()||!(NewPin.length()==4)){
                            Toast.makeText(HomeActivity.this,"Enter 4-Digit New Pin",Toast.LENGTH_LONG).show();
                        }else if(ConfPin.isEmpty()||!(ConfPin.length()==4)){
                            Toast.makeText(HomeActivity.this,"Re-Enter Your  New Pin",Toast.LENGTH_LONG).show();
                        }
                        else if(!NewPin.equals(ConfPin)){
                            Toast.makeText(HomeActivity.this,"MisMatch Pin Number",Toast.LENGTH_LONG).show();
                        }else if(NewPin.equals(currPin)) {
                            Toast.makeText(HomeActivity.this,"Same as Current Pin ",Toast.LENGTH_LONG).show();
                        }else{
                            SharedPreferences spPin = getApplicationContext().getSharedPreferences(Config.SHARED_PREF2, 0);
                            SharedPreferences.Editor pinEditor = spPin.edit();
                            pinEditor.putString("PIN", NewPin);
                            pinEditor.commit();
                            Toast.makeText(HomeActivity.this,"Your PIN updated successfully.",Toast.LENGTH_LONG).show();
                            alertDialog.cancel();

                        }
                    }

                }
            });
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
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
    public void onClick(View v){
        switch (v.getId()){
            case R.id.llopen:
                if(tvOpen.getText().toString().equals("0")){
                    Snackbar.make(v, "No ticket to show", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else {
                    SharedPreferences agentnameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF6, 0);
                    SharedPreferences.Editor agentnameEditer = agentnameSP.edit();
                    agentnameEditer.putString("IsAssign", "1");
                    agentnameEditer.commit();

                    SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF5, 0);
//                    Snackbar.make(v, pref.getString("IsAcces", null), Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
                    if(pref.getString("IsAcces", null).equals("True")){
                        getProductList("2");
                    }else{
                        Intent intent = new Intent(HomeActivity.this, ClientActivity.class);
                        intent.putExtra("subMode", "2");
                        intent.putExtra("ID_Product", "");
                        startActivity(intent);
                    }
                }
                break;
            case R.id.llPending:

                if(tvPending.getText().toString().equals("0")){
                    Snackbar.make(v, "No ticket to show", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else {

                    SharedPreferences agentnameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF6, 0);
                    SharedPreferences.Editor agentnameEditer = agentnameSP.edit();
                    agentnameEditer.putString("IsAssign", "1");
                    agentnameEditer.commit();
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF5, 0);
                    if(pref.getString("IsAcces", null).equals("True")){

                        Log.e(TAG,"IsAcces True 600");
                        getProductList("1");
                    }else{
                        Log.e(TAG,"IsAcces false 603");
                        Intent intent = new Intent(HomeActivity.this, ClientActivity.class);
                        intent.putExtra("subMode", "1");
                        intent.putExtra("ID_Product", "");
                        startActivity(intent);
                    }
                }
                break;
            case R.id.txtvQR:

                IntentIntegrator integrator = new IntentIntegrator(HomeActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
                break;

            case R.id.txtRefresh:
                    Toast.makeText(getApplicationContext(),"Refresh",Toast.LENGTH_SHORT).show();
                    getRefresh();

                break;
            case R.id.llClosed:
                if(tvClosed.getText().toString().equals("0")){
                    Snackbar.make(v, "No ticket to show", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else {
                    SharedPreferences agentnameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF6, 0);
                    SharedPreferences.Editor agentnameEditer = agentnameSP.edit();
                    agentnameEditer.putString("IsAssign", "0");
                    agentnameEditer.commit();
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF5, 0);
                    if(pref.getString("IsAcces", null).equals("True")){
                        getProductList("3");
                    }else{
                        Intent intent = new Intent(HomeActivity.this, ClientActivity.class);
                        intent.putExtra("subMode", "3");
                        intent.putExtra("ID_Product", "");
                        startActivity(intent);
                    }
                }
                break;
            case R.id.llresolved:
                if(tvResolved.getText().toString().equals("0")){
                    Snackbar.make(v, "No ticket to show", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else {
                    SharedPreferences agentnameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF6, 0);
                    SharedPreferences.Editor agentnameEditer = agentnameSP.edit();
                    agentnameEditer.putString("IsAssign", "1");
                    agentnameEditer.commit();
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF5, 0);
                    if(pref.getString("IsAcces", null).equals("True")){
                        getProductList("18");
                    }else{
                        Intent intent = new Intent(HomeActivity.this, ClientActivity.class);
                        intent.putExtra("subMode", "18");
                        intent.putExtra("ID_Product", "");
                        startActivity(intent);
                    }
                }
                break;
            case R.id.llsoftwarepending:
                if(tvSoftwarePending.getText().toString().equals("0")){
                    Snackbar.make(v, "No ticket to show", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else {
                    SharedPreferences agentnameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF6, 0);
                    SharedPreferences.Editor agentnameEditer = agentnameSP.edit();
                    agentnameEditer.putString("IsAssign", "1");
                    agentnameEditer.commit();
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF5, 0);
                    if(pref.getString("IsAcces", null).equals("True")){
                        getProductList("4");
                    }else{
                        Intent intent = new Intent(HomeActivity.this, ClientActivity.class);
                        intent.putExtra("subMode", "4");
                        intent.putExtra("ID_Product", "");
                        startActivity(intent);
                    }
                }
            case R.id.llclientSidewaiting:
              //  Toast.makeText(getApplicationContext(),"Client ",Toast.LENGTH_SHORT).show();
                if(tvclientSidewaiting.getText().toString().equals("0")){
                    Snackbar.make(v, "No Waiting to show", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else {
                    SharedPreferences agentnameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF6, 0);
                    SharedPreferences.Editor agentnameEditer = agentnameSP.edit();
                    agentnameEditer.putString("IsAssign", "1");
                    agentnameEditer.commit();
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF5, 0);
                    if(pref.getString("IsAcces", null).equals("True")){
                        getProductList("6");
                    }else{
                        Intent intent = new Intent(HomeActivity.this, ClientActivity.class);
                        intent.putExtra("subMode", "6");
                        intent.putExtra("ID_Product", "");
                        startActivity(intent);
                    }
                }

                break;
        }
    }


    public void getProductList(final String submode){
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
                    requestObject1.put("LoginMode", "14");
                    requestObject1.put("FK_Company", Config.FK_Company);
                    requestObject1.put("LoginsubMode",submode);
                    requestObject1.put("Agent_ID",pref1.getString("Agent_ID", null));

                    requestObject1.put("Token",pref2.getString("TOKEN", null));

                    requestObject1.put("Location_Latitude", lattitude);
                    requestObject1.put("Location_Longitude", longittude);
//                    requestObject1.put("Location_Latitude","");
//                    requestObject1.put("Location_Longitude","");
                    requestObject1.put("ID_Department","");
                    requestObject1.put("ID_Product","");
                    requestObject1.put("ID_Client","");
                    requestObject1.put("Module","PROD");



                    Log.e(TAG," requestObject1 700     "+requestObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getProductDet(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            progressDialog.dismiss();
                            Log.e(TAG," response 741     "+response.body());
                            JSONObject jObject = new JSONObject(response.body());
                            JSONObject jobj = jObject.getJSONObject("agentProductDetailsInfo");
                            if (jobj.getString("productDetailsList").equals("null")) {
                            } else {
                                JSONArray jarray = jobj.getJSONArray("productDetailsList");
                                Log.e(TAG,"productDetailsList   "+jarray.length());
                                if (jarray.length()>1){
                                    Intent i = new Intent(HomeActivity.this, ProductActivity.class);
                                    i.putExtra("subMode", submode);
                                    startActivity(i);
                                }
                                else {
                                    JSONObject jsonObject = jarray.getJSONObject(0);
                                    Intent intent = new Intent(HomeActivity.this, ClientActivity.class);
                                    intent.putExtra("subMode", submode);
                                    intent.putExtra("ID_Product", jsonObject.getString("ID_Product"));
                                    startActivity(intent);
                                }


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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
                        HomeActivity.this.startActivity(intent);
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
                                ActivityCompat.requestPermissions(HomeActivity.this,
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

                                    lattitude = location.getLatitude();
                                    longittude = location.getLongitude();
                                    Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
                                    List<Address> addresses = null;
                                    try {
                                        addresses = geocoder.getFromLocation(lattitude, longittude, 1);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    locName = addresses.get(0).getAddressLine(0);

                                    String Locality = addresses.get(0).getLocality();
                                    String stateName = addresses.get(0).getAddressLine(1);
                                    String countryName = addresses.get(0).getAddressLine(2);
                                    locationDetails();
//                                    Toast.makeText( getApplicationContext(),"latitude : " + latitude + ",\n longitude : " + longitude + ",\n locName : " + locName , Toast.LENGTH_LONG ).show();

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
//                        Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
//                        List<Address> addresses = null;
//                        try {
//                            if (geocoder!= null ) {
//                                addresses = geocoder.getFromLocation(latitude, longitude, 1);
//                                locName = addresses.get(0).getAddressLine(0);
//                                String Locality = addresses.get(0).getLocality();
//                                String stateName = addresses.get(0).getAddressLine(1);
//                                String countryName = addresses.get(0).getAddressLine(2);
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
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
                        lattitude = location.getLatitude();
                        longittude = location.getLongitude();
                        Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(lattitude, longittude, 1);
                            locName = addresses.get(0).getAddressLine(0);
                            String Locality = addresses.get(0).getLocality();
                            String stateName = addresses.get(0).getAddressLine(1);
                            String countryName = addresses.get(0).getAddressLine(2);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

//                        Toast.makeText( getApplicationContext(),"latitude : " + latitude + ",\n longitude : " + longitude + ",\n locName : " + locName , Toast.LENGTH_LONG ).show();

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
                requestObject1.put("Location_Latitude",lattitude);
                requestObject1.put("Location_Longitude",longittude);
                requestObject1.put("Location_Name",locName);
//                Toast.makeText( getApplicationContext(),"latitude : " + latitude + ",\n longitude : " + longitude + ",\n locName : " + locName , Toast.LENGTH_LONG ).show();

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.e("Scan*******", "Cancelled scan");

            } else {
                Log.e("Scan", "Scanned");
                String qr = result.getContents();
               // tv_qr_readTxt.setText(result.getContents());
              //  Toast.makeText(this, "Scanned: " + qr, Toast.LENGTH_LONG).show();
                getQRcode(qr);
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getQRcode(String qr) {

            if (new InternetUtil(this).isInternetOn()) {
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

                        requestObject1.put("Agent_ID", pref1.getString("Agent_ID", null));
                        requestObject1.put("QRCode", qr);

                        Log.e("TAG", "requestObject1  171   " + requestObject1);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                    Call<String> call = apiService.agentqrlogin(body);
                    call.enqueue(new Callback<String>() {
                        @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                            try {
                                JSONObject jObject = new JSONObject(response.body());
                                Log.i("Responsescanner",response.body());
                                String statuscode = jObject.getString("StatusCode");

                                if(statuscode.equals("0"))
                                {
                                    JSONObject jobj = jObject.getJSONObject("AgentQRLogin");

                                    String response2 = jobj.getString("ResponseMessage");
                                    if (response2.equals("null")) {
                                    } else {
                                       // alertMessage1("", response2);
                                        Toast.makeText(getApplicationContext(),response2,Toast.LENGTH_LONG).show();

                                    }

                                }
                                else
                                {
                                  //  alertMessage1("", jObject.getString("EXMessage"));
                                    Toast.makeText(getApplicationContext(),jObject.getString("EXMessage"),Toast.LENGTH_LONG).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(Call<String> call, Throwable t) {}
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(this,"No internet connection",Toast.LENGTH_SHORT).show();
            }





    }

//    private void alertMessage1(String msg1, final String msg2) {
//
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this);
//
//        LayoutInflater inflater =this.getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.alert_layout, null);
//        dialogBuilder.setView(dialogView);
//
//        final AlertDialog alertDialog = dialogBuilder.create();
//        TextView tv_share =  dialogView.findViewById(R.id.tv_share);
//        TextView tv_msg =  dialogView.findViewById(R.id.txt1);
//        //  TextView tv_msg2 =  dialogView.findViewById(R.id.txt2);
//        if(msg1.equals(""))
//        {
//            tv_msg.setText(msg2);
//        }
//        else
//        {
//            tv_msg.setText(msg1);
//        }
//        //      tv_msg.setText(msg1);
//        //  tv_msg2.setText(msg2);
//        TextView tv_cancel =  dialogView.findViewById(R.id.tv_cancel);
//        tv_share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // displaypdf(msg2);
//          /*     Intent viewDownloadsIntent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
//                startActivity(viewDownloadsIntent);*/
//
//
//                alertDialog.dismiss();
//
//            }
//        });
//        tv_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                //  finishAffinity();
//
//            }
//        });
//        alertDialog.show();
//    }
}
