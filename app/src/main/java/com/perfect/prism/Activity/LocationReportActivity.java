package com.perfect.prism.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.perfect.prism.Adapter.LocationClientListAdapter;
import com.perfect.prism.Adapter.LocationReportAdapter;
import com.perfect.prism.Adapter.SearchAgentAdapter;
import com.perfect.prism.Adapter.SearchProductAdapter;
import com.perfect.prism.DB.DBHandler;
import com.perfect.prism.Model.AgentDetailsModel;
import com.perfect.prism.Model.AgentModel;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

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

@Deprecated
public class LocationReportActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressDialog progressDialog;
    LinearLayout  llchooseAgent, llresult;
    ListView list_view;
    EditText etsearch;
    SearchProductAdapter sadapter;
    SearchAgentAdapter adapter;
    TextView txt_agent, tv_popuptitle, txtDate;
    int textlength = 0;
    ArrayList<AgentModel> agentsearchNamesArrayList = new ArrayList<>();
    public static ArrayList<AgentModel> agentarray_sort= new ArrayList<>();
    String agentID;
    ImageView imclr;
    Button search_location_report;
    ArrayList<String> ClientNamesArray = new ArrayList<>();
    HashSet<String> hashSet = new HashSet<String>();
    LocationClientListAdapter ClintAdapter;
    RecyclerView rv_client;
    ArrayList<AgentDetailsModel> AgentDetailArrayList = new ArrayList<>();
    ArrayList<ArrayList> FullAgentDetailArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_report);
        initiateViews();
        setRegViews();
    }
    private void initiateViews() {
        rv_client=findViewById(R.id.rv_client);
        llchooseAgent=findViewById(R.id.llchooseAgent);
        txt_agent=findViewById(R.id.txt_agent);
        search_location_report=findViewById(R.id.search_location_report);
        txtDate=findViewById(R.id.txtDate);
        llresult=findViewById(R.id.llresult);
        imclr=findViewById(R.id.imclr);
    }

    private void setRegViews() {
        llchooseAgent.setOnClickListener(this);
        search_location_report.setOnClickListener(this);
        imclr.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llchooseAgent:
                getAgents();
                break;
            case R.id.search_location_report:
                getLocationReport();
                break;
            case R.id.imclr:
                txt_agent.setText("");
                agentID="";
                break;
        }
    }
    private void getLocationReport(){
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
                        requestObject1.put("LoginMode", "12");
                        requestObject1.put("FK_Company", Config.FK_Company);
                        requestObject1.put("Rpt_AgentID", agentID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                    Call<String> call = apiService.getLocationReport(body);
                    call.enqueue(new Callback<String>() {
                        @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                            try {
                                progressDialog.dismiss();
                                JSONObject jObject = new JSONObject(response.body());
                                JSONObject jobj = jObject.getJSONObject("SelectAgentLocationDetailsInfo");
                                if (jobj.getString("AgentLocationDetailsList").equals("null")) {
                                    Toast.makeText(LocationReportActivity.this,jobj.getString("ResponseMessage"),Toast.LENGTH_SHORT).show();
                                    llresult.setVisibility(View.GONE);
                                } else {
                                    llresult.setVisibility(View.VISIBLE);
                                    Date c = Calendar.getInstance().getTime();
                                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                                    String formattedDate = df.format(c);
                                    String date = "Location Report "+" :"+formattedDate;
                                    txtDate.setText(date);

                                    JSONArray jarray = jobj.getJSONArray("AgentLocationDetailsList");

                                    DBHandler db=new DBHandler(LocationReportActivity.this);
                                    db.clearall();
                                    for(int i=0; i<jarray.length();i++){
                                        JSONObject jobjt =  jarray.getJSONObject(i);
                                        db.addLocationReport(new AgentDetailsModel(
                                                jobjt.getString("AgentName"),
                                                jobjt.getString("ID_Client"),
                                                jobjt.getString("Site"),
                                                jobjt.getString("Location"),
                                                jobjt.getString("AssignedTicket"),
                                                jobjt.getString("SoftwarePending"),
                                                jobjt.getString("ClosedTicket"),
                                                jobjt.getString("Balance"),
                                                jobjt.getString("ID_Agent")
                                        ));
                                    }
                                    hashSet.clear();
                                    ClientNamesArray = new ArrayList<>();
                                    for (int k = 0; k < jarray.length(); k++) {
                                        JSONObject jsonObject = jarray.getJSONObject(k);
                                        ClientNamesArray.add(jsonObject.getString("Site")+" idClient "+jsonObject.getString("ID_Client"));
                                    }
                                    hashSet.addAll(ClientNamesArray);
                                    ClientNamesArray.clear();
                                    ClientNamesArray.addAll(hashSet);

                                    ClintAdapter = new LocationClientListAdapter(LocationReportActivity.this,ClientNamesArray );
                                    rv_client.setLayoutManager(new LinearLayoutManager(LocationReportActivity.this));
                                    rv_client.setAdapter(ClintAdapter);
                                    }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                            }
                        }
                        @Override
                        public void onFailure(Call<String> call, Throwable t) {}
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }catch (Exception e) {
                e.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Something went wrong!",Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"No internet connection!!",Toast.LENGTH_SHORT).show();
        }

    }

    private void getAgents() {
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            LayoutInflater inflater1 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.product_popup, null);
            list_view = (ListView) layout.findViewById(R.id.list_view);
            etsearch = (EditText) layout.findViewById(R.id.etsearch);
            tv_popuptitle = (TextView) layout.findViewById(R.id.tv_popuptitle);
            tv_popuptitle.setText("Select Agent");
            builder.setView(layout);
            final android.app.AlertDialog alertDialog = builder.create();
            getAgentList(alertDialog);
            alertDialog.show();
        }catch (Exception e){e.printStackTrace();}
    }

    public void getAgentList(final AlertDialog alertDialog){
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
                    requestObject1.put("LoginMode", "2");
                    requestObject1.put("FK_Company", Config.FK_Company);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getAgent(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            JSONObject jObject = new JSONObject(response.body());
                            JSONObject jobj = jObject.getJSONObject("AgentDetails");
                            if (jobj.getString("agentDetailsList").equals("null")) {
                            } else {
                                JSONArray jarray = jobj.getJSONArray("agentDetailsList");
                                agentarray_sort = new ArrayList<>();
                                for (int k = 0; k < jarray.length(); k++) {
                                    JSONObject jsonObject = jarray.getJSONObject(k);

                                    agentsearchNamesArrayList.add(new AgentModel(jsonObject.getString("ID_Agent"),jsonObject.getString("AgentName")));
                                    agentarray_sort.add(new AgentModel(jsonObject.getString("ID_Agent"),jsonObject.getString("AgentName")));
                                }
                                adapter = new SearchAgentAdapter(LocationReportActivity.this, agentarray_sort);
                                list_view.setAdapter(adapter);
                                list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        txt_agent.setText(agentarray_sort.get(position).getAgentName());
                                        agentID = agentarray_sort.get(position).getID_Agent();
                                        alertDialog.dismiss();
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
                                    agentarray_sort.clear();
                                    for (int i = 0; i < agentsearchNamesArrayList.size(); i++) {
                                        if (textlength <= agentsearchNamesArrayList.get(i).getAgentName().length()) {
                                            if (agentsearchNamesArrayList.get(i).getAgentName().toLowerCase().trim().contains(
                                                    etsearch.getText().toString().toLowerCase().trim())) {
                                                agentarray_sort.add(agentsearchNamesArrayList.get(i));
                                            }
                                        }
                                    }
                                    adapter = new SearchAgentAdapter(LocationReportActivity.this, agentarray_sort);
                                    list_view.setAdapter(adapter);
                                }
                            });
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
