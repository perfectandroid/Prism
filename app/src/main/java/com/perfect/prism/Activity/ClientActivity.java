package com.perfect.prism.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.perfect.prism.Adapter.SearchClientHomeAdapter;
import com.perfect.prism.Model.ClientHomeModel;
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

public class ClientActivity extends AppCompatActivity {

    String TAG="ClientActivity";

    ProgressDialog progressDialog;
    String subMode, ID_Product;
    ListView list_view;
    EditText etsearch;
    int textlength = 0;
    ArrayList<ClientHomeModel> clientsearchNamesArrayList = new ArrayList<>();
    public static ArrayList<ClientHomeModel> clientarray_sort= new ArrayList<>();
    SearchClientHomeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        Log.e(TAG,"Start");
        Intent in = getIntent();
        subMode = in.getStringExtra("subMode");
        ID_Product = in.getStringExtra("ID_Product");

        initiateViews();
        setRegViews();
        getClientList(subMode);
    }
    private void initiateViews() {
        list_view = findViewById(R.id.list_view);
        etsearch = findViewById(R.id.etsearch);
    }

    private void setRegViews() {
    }

    public void getClientList(String submode){
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
                    requestObject1.put("ID_Product",ID_Product);
                    requestObject1.put("Agent_ID",pref1.getString("Agent_ID", null));

                    requestObject1.put("Token",pref2.getString("TOKEN", null));
                    requestObject1.put("Location_Latitude","");
                    requestObject1.put("Location_Longitude","");
                    requestObject1.put("ID_Department","");
                    requestObject1.put("ID_Client","");
                    requestObject1.put("Module","Client");

                    Log.e(TAG,"requestObject1  138   "+requestObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getTicketsCountWiseClient(body);
               // Call<String> call = apiService.getClient(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            Log.e(TAG,"Response  148    "+response.body());
                            progressDialog.dismiss();
                            Log.e(TAG,"   150  ");
                            JSONObject jObject = new JSONObject(response.body());
                            Log.e(TAG,"   152  ");
//                            JSONObject jobj = jObject.getJSONObject("ClientDetails");
                            JSONObject jobj = jObject.getJSONObject("ClientProductDetailsInfo");
                            Log.e(TAG,"   154  ");
                            if (jobj.getString("clientDetailsList").equals("null")) {
                                Log.e(TAG,"   153  ");
                            } else {
                                Log.e(TAG,"   155  ");
                                JSONArray jarray = jobj.getJSONArray("clientDetailsList");
                                clientarray_sort = new ArrayList<>();
                                for (int k = 0; k < jarray.length(); k++) {
                                    JSONObject jsonObject = jarray.getJSONObject(k);

                                    clientsearchNamesArrayList.add(new ClientHomeModel(jsonObject.getString("ID_Client"),jsonObject.getString("ClientName"),jsonObject.getString("Count"),jsonObject.getString("FK_Product")));
                                    clientarray_sort.add(new ClientHomeModel(jsonObject.getString("ID_Client"),jsonObject.getString("ClientName"),jsonObject.getString("Count"),jsonObject.getString("FK_Product")));
                                }
                                Log.e(TAG,"clientarray_sort     "+clientarray_sort.size());
                                adapter = new SearchClientHomeAdapter(ClientActivity.this, clientarray_sort);
                                list_view.setAdapter(adapter);
                                list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Intent intent = new Intent(ClientActivity.this, TicketListActivity.class);
                                        intent.putExtra("subMode", subMode);
//                                      intent.putExtra("ID_Product", ID_Product);
                                        ID_Product =  clientarray_sort.get(position).getFK_Product();
                                        intent.putExtra("ID_Product", ID_Product);
                                        intent.putExtra("ID_Client", clientarray_sort.get(position).getID_Client());
//                                      intent.putExtra("FK_Product",clientarray_sort.get(position).getFK_Product());

                                        Log.e("maaa",""+clientarray_sort.get(position).getFK_Product());
                                        startActivity(intent);
                                    }
                                });

                            }
                            etsearch.addTextChangedListener(new TextWatcher() {
                                public void afterTextChanged(Editable s) {
                                }
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    list_view.setVisibility(View.VISIBLE);
                                    textlength = etsearch.getText().length();
                                    clientarray_sort.clear();
                                    for (int i = 0; i < clientsearchNamesArrayList.size(); i++) {
                                        if (textlength <= clientsearchNamesArrayList.get(i).getClientName().length()) {
                                            if (clientsearchNamesArrayList.get(i).getClientName().toLowerCase().trim().contains(
                                                    etsearch.getText().toString().toLowerCase().trim())) {
                                                clientarray_sort.add(clientsearchNamesArrayList.get(i));
                                            }
                                        }
                                    }
                                    adapter = new SearchClientHomeAdapter(ClientActivity.this, clientarray_sort);
                                    list_view.setAdapter(adapter);
                                }
                            });
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
                Intent i = new Intent(ClientActivity.this, HomeActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
