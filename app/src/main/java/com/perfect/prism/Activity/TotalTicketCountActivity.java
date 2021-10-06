package com.perfect.prism.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class TotalTicketCountActivity extends AppCompatActivity {

    String TAG = "TotalTicketCountActivity";
    ProgressDialog progressDialog;
    TextView tvResolvedticket,tvClosedTicket,tvswpending,tvReturned,tvTotalTickets,tvPrivilegeClients,tvclientOverDue;
    TextView tvSiteCommitted,tvSiteVisitOverDue,tvOpenBefore2020,tvResolvedBefore2020,tvSoftwarePendingBefore2020,tvClosedBefore2020;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_ticket_count);
        initiateViews();
        setRegViews();
        getTicketCount();
    }

    private void initiateViews() {
        tvResolvedticket=findViewById(R.id.tvResolvedticket);
        tvClosedTicket=findViewById(R.id.tvClosedTicket);
        tvswpending=findViewById(R.id.tvswpending);
        tvReturned=findViewById(R.id.tvReturned);
        tvTotalTickets=findViewById(R.id.tvTotalTickets);
        tvPrivilegeClients=findViewById(R.id.tvPrivilegeClients);
        tvclientOverDue=findViewById(R.id.tvclientOverDue);
        tvSiteCommitted = findViewById(R.id.tvSiteCommitted);
        tvSiteVisitOverDue = findViewById(R.id.tvSiteVisitOverDue);
        tvOpenBefore2020 = findViewById(R.id.tvOpenBefore2020);
        tvResolvedBefore2020 = findViewById(R.id.tvResolvedBefore2020);
        tvSoftwarePendingBefore2020 = findViewById(R.id.tvSoftwarePendingBefore2020);
        tvClosedBefore2020 = findViewById(R.id.tvClosedBefore2020);
    }

    private void setRegViews() {
    }


    private void getTicketCount() {
        if (new InternetUtil(this).isInternetOn()) {
            try {
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
                        requestObject1.put("LoginMode", "3");
                        requestObject1.put("FK_Company", Config.FK_Company);
                        requestObject1.put("Agent_ID",pref1.getString("Agent_ID", null));

                        Log.e(TAG,"requestObject1   115  "+requestObject1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                    Call<String> call = apiService.getTicketCount(body);
                    call.enqueue(new Callback<String>() {
                        @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                            try {
                                progressDialog.dismiss();
                                Log.e(TAG,"response   128   "+response.body());
                                JSONObject jObject = new JSONObject(response.body());
                                JSONObject jobj = jObject.getJSONObject("TicketCountSummaryDetails");
                                if(jObject.getString("StatusCode").equals("0")){
                                    tvResolvedticket.setText("Resolved : "+jobj.getInt("ResolvedTickets"));
                                    tvClosedTicket.setText("Closed Tickets : "+jobj.getInt("ClosedTickets"));
                                    tvswpending.setText("software Pending : "+jobj.getInt("SoftwarePending"));
                                    tvReturned.setText("Returned : "+jobj.getInt("Returned"));
                                    tvTotalTickets.setText("Total Tickets : "+jobj.getInt("TotalTickets"));
                                    tvPrivilegeClients.setText("Privilege Clients : "+jobj.getInt("PrivilegeClients"));
                                    tvclientOverDue.setText("Client OverDue : "+jobj.getInt("ClientOverDue"));
                                    tvSiteCommitted.setText("Site Visit Next 4 days : "+jobj.getInt("SiteCommitted"));
                                    tvSiteVisitOverDue.setText("Site Visit Over Due : "+jobj.getInt("SiteVisitOverDue"));
                                    tvOpenBefore2020.setText("Open Before 2020 : "+jobj.getInt("OpenBefore2020"));
                                    tvResolvedBefore2020.setText("Resolved Before 2020 : "+jobj.getInt("OpenBefore2020"));
                                    tvSoftwarePendingBefore2020.setText("S/W Pendind Before 2020 : "+jobj.getInt("SoftwarePendingBefore2020"));
                                    tvClosedBefore2020.setText("Closed Before 2020 : "+jobj.getInt("ClosedBefore2020"));


                                }else{
                                    Toast.makeText(getApplicationContext(),"Something went wrong!!",Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                            }
                        }
                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            progressDialog.dismiss();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Something went wrong!",Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"No internet connection!!",Toast.LENGTH_SHORT).show();
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
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item:
                Intent i = new Intent(TotalTicketCountActivity.this, HomeActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
