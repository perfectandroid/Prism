package com.perfect.prism.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.perfect.prism.Adapter.AgeingClientWiseReportAdapter;
import com.perfect.prism.Adapter.TicketDetailsAdapter;
import com.perfect.prism.Adapter.TicketListAdapter;
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

public class TicketDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    String TAG="TicketDetailsActivity";
    String ID_Tickets, CliName, Startingstatus;
    ProgressDialog progressDialog;
    LinearLayout llAssign, llAddremark, ll_bottom_navigation;
    RecyclerView rv_chat;
    TextView tvTicketid, tvCliName;
    JSONArray jsnarray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_details);
        Log.e(TAG,"Start");
        tvTicketid=findViewById(R.id.tvTicketid);
        tvCliName=findViewById(R.id.tvCliName);
        llAssign=findViewById(R.id.llAssign);
        llAssign.setOnClickListener(this);
        llAddremark=findViewById(R.id.llAddremark);
        llAddremark.setOnClickListener(this);
        rv_chat=findViewById(R.id.rv_chat);
        ll_bottom_navigation=findViewById(R.id.ll_bottom_navigation);
        Intent in = getIntent();
        ID_Tickets  = in.getStringExtra("ID_Tickets");
        CliName  = in.getStringExtra("CliName");
        Startingstatus  = in.getStringExtra("Startingstatus");
        tvCliName.setText(CliName);
        getTicketDetails(ID_Tickets);
        conditionAssignRemark();
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF6, 0);
        if(pref.getString("IsAssign", null).equals("0")) {
            ll_bottom_navigation.setVisibility(View.GONE);
        }
        else{
            ll_bottom_navigation.setVisibility(View.VISIBLE);
        }
    }

    public void conditionAssignRemark(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF5, 0);
        if(pref.getString("IsAcces", null).equals("True")){
            llAssign.setVisibility(View.VISIBLE);
        }
        if(pref.getString("IsAcces", null).equals("False")){
            llAssign.setVisibility(View.GONE);
        }
        if(Startingstatus.equals("0")||Startingstatus.equals("1")){
            llAddremark.setVisibility(View.GONE);
        }
        if(Startingstatus.equals("2")){
            llAddremark.setVisibility(View.VISIBLE);
        }
        if((Startingstatus.equals("0")||Startingstatus.equals("1"))&&(pref.getString("IsAcces", null).equals("False"))){
            ll_bottom_navigation.setVisibility(View.GONE);
        }
    }

    public void getTicketDetails(String ID_Ticket){
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
                    requestObject1.put("LoginMode", "5");
                    requestObject1.put("ResponseType", "0");
                    requestObject1.put("ID_Tickets",ID_Ticket);
                    requestObject1.put("FK_Company", Config.FK_Company);
                    requestObject1.put("Token", pref2.getString("TOKEN", null));
                    requestObject1.put("Agent_ID",pref1.getString("Agent_ID", null));
                    Log.e(TAG,"requestObject1  "+requestObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getAgentTicketDetails(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        /*try {
                            progressDialog.dismiss();
                            JSONObject jObject = new JSONObject(response.body());
                            JSONObject jobj = jObject.getJSONObject("AgentRootTicketsIndividualSelectinfo");
                            JSONObject jobjt = jobj.getJSONObject("BugtypeDropdownFillDetailsList");
                            jsnarray = jobjt.getJSONArray("DropdownFillDetailsList");
                            if (jobj.getString("TicketsIndividualDetailsList").equals("null")) {
                            } else {
                                JSONArray jarray = jobj.getJSONArray("TicketsIndividualDetailsList");
                                GridLayoutManager lLayout = new GridLayoutManager(TicketDetailsActivity.this, 1);
                                rv_chat.setLayoutManager(lLayout);
                                rv_chat.setHasFixedSize(true);
                                TicketDetailsAdapter adapter = new TicketDetailsAdapter(TicketDetailsActivity.this, jarray);
                                rv_chat.setAdapter(adapter);
                            }
                        } */
                        try {
                            progressDialog.dismiss();
                            JSONObject jObject = new JSONObject(response.body());
                            Log.e("StatusCode","TicketsIndividualDetailsList   "+response.body());
                            JSONObject jobj = jObject.getJSONObject("AgentRootTicketsIndividualSelectinfo");
                            if (jobj.getString("TicketsIndividualDetailsList").equals("null")) {
                                if(jObject.getString("StatusCode").equals("-1")) {
                                    Toast.makeText( getApplicationContext(),jobj.getString("ResponseMessage") , Toast.LENGTH_LONG ).show();
                                    dologoutchanges();
                                    startActivity(new Intent(TicketDetailsActivity.this,WelcomeActivity.class));
                                    finish();
                                } else {
                                    Snackbar.make(findViewById(R.id.ticketDetails_view), jobj.getString("ResponseMessage"), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            } else {
                                JSONObject jobjt = jobj.getJSONObject("BugtypeDropdownFillDetailsList");

                                jsnarray = jobjt.getJSONArray("DropdownFillDetailsList");

                                JSONArray jarray = jobj.getJSONArray("TicketsIndividualDetailsList");

                                for(int i=0; i<jarray.length(); i++){
                                    JSONObject jsonObject = jarray.getJSONObject(i);
                                    tvTicketid.setText(jsonObject.getString("TickNo"));
                                }
                                GridLayoutManager lLayout = new GridLayoutManager(TicketDetailsActivity.this, 1);
                                rv_chat.setLayoutManager(lLayout);
                                rv_chat.setHasFixedSize(true);
                                TicketDetailsAdapter adapter = new TicketDetailsAdapter(TicketDetailsActivity.this, jarray);
                                rv_chat.setAdapter(adapter);
                            }
                        }catch (JSONException e) {
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llAssign:
                Intent i=new Intent(this, TicketAssignActivity.class);
                i.putExtra("ID_Ticket", ID_Tickets);

                startActivity(i);
                break;
            case R.id.llAddremark:
                Intent in=new Intent(this, TicketRemarkActivity.class);
                in.putExtra("ID_Ticket", ID_Tickets);
                in.putExtra("BugTypes", jsnarray.toString());
                startActivity(in);
                break;
        }
    }
    @Override
    public void onRestart(){
        super.onRestart();
        finish();
        startActivity(getIntent());
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
                Intent i = new Intent(TicketDetailsActivity.this, HomeActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
