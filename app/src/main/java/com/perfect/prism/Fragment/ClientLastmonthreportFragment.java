package com.perfect.prism.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.perfect.prism.Activity.AgentTicketReportListActivity;
import com.perfect.prism.Activity.AgentWiseReportListActivity;
import com.perfect.prism.Activity.ClientTicketReportListActivity;
import com.perfect.prism.Activity.ClientWiseReportListActivity;
import com.perfect.prism.Adapter.SearchClientAdapter;
import com.perfect.prism.Adapter.SearchProductAdapter;
import com.perfect.prism.Model.ClientModel;
import com.perfect.prism.Model.ProductModel;
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

public class ClientLastmonthreportFragment extends Fragment implements View.OnClickListener {

    LinearLayout llchoosePrdt, llchooseClient,submit_lastMonth_client,reset_lastMonth_client;
    ListView list_view;
    EditText etsearch,edt_num_month;
    SearchProductAdapter sadapter;
    SearchClientAdapter adapter;
    TextView txt_product,txt_client, tv_popuptitle;
    int textlength = 0;
    ArrayList<ProductModel> searchNamesArrayList = new ArrayList<>();
    public static ArrayList<ProductModel> array_sort= new ArrayList<>();
    ArrayList<ClientModel> clientsearchNamesArrayList = new ArrayList<>();
    public static ArrayList<ClientModel> clientarray_sort= new ArrayList<>();

    String Months ,ID_Product="",ID_Client="";
    private ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_clientlastmont, container,
                false);

        initiateViews(v);
        setRegViews();
        return v;
    }
    private void initiateViews(View v) {
        llchoosePrdt=v.findViewById(R.id.llchoosePrdt);
        llchooseClient=v.findViewById(R.id.llchooseClient);
        txt_product=v.findViewById(R.id.txt_product);
        txt_client=v.findViewById(R.id.txt_client);
        edt_num_month=v.findViewById(R.id.edt_num_month);
        reset_lastMonth_client=v.findViewById(R.id.reset_lastMonth_client);
        submit_lastMonth_client=v.findViewById(R.id.submit_lastMonth_client);
    }

    private void setRegViews() {
        llchoosePrdt.setOnClickListener(this);
        llchooseClient.setOnClickListener(this);
        reset_lastMonth_client.setOnClickListener(this);
        submit_lastMonth_client.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llchoosePrdt:
                searchNamesArrayList.clear();
                array_sort.clear();
                getProducts();
                break;
            case R.id.llchooseClient:
                clientsearchNamesArrayList.clear();
                clientarray_sort.clear();
                getClient();
                break;
            case R.id.reset_lastMonth_client:
                reSet();
                break;
            case R.id.submit_lastMonth_client:
                Months=edt_num_month.getText().toString();
                if(Months.equals("")){
                    Snackbar.make(v, "Please Insert Number Of Months...", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else {
                    getClientWiseReportList(v);
                }
                break;
        }
    }

    public void reSet(){
        ID_Client = "";
        ID_Product= "";
        Months="";
        txt_product.setText("");
        txt_client.setText("");
        edt_num_month.setText("");
    }
    public void getClientWiseReportList(final View v){
        if (new InternetUtil(getContext()).isInternetOn()) {
            try{
                progressDialog = new ProgressDialog(getContext(), R.style.Progress);
                progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.setIndeterminateDrawable(getContext().getResources()
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
                    SharedPreferences spAgentIg = getContext().getSharedPreferences(Config.SHARED_PREF3, 0);
                    String Agent_ID = spAgentIg.getString("Agent_ID",null);

                    requestObject1.put("LoginMode", "4");
                    requestObject1.put("LoginsubMode", "3");
                    requestObject1.put("FK_Company", Config.FK_Company);
                    requestObject1.put("ID_Client", ID_Client);
                    requestObject1.put("FromDate", "2017-7-18 00:00:00");
                    requestObject1.put("ToDate", "2019-7-18 00:00:00");
                    requestObject1.put("ID_Product", ID_Product);
                    requestObject1.put("Month", "0");
                    requestObject1.put("Year", "0");
                    requestObject1.put("MonthCount", Months);
                    requestObject1.put("Agent_ID", Agent_ID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getClientwiseTicketCount(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject jObject = new JSONObject(response.body());
                            JSONObject jobj = jObject.getJSONObject("SelectClientWiseTicketDetails");
                            if (jObject.getString("StatusCode").equals("0")) {
                                JSONArray jarray = jobj.getJSONArray("ClientWiseTicketDetailsInfo");
                                if(jarray.length()>1){
                                    Intent i = new Intent(getContext(), ClientWiseReportListActivity.class);
                                    i.putExtra("jarray", String.valueOf(jarray));
                                    i.putExtra("From", "Last Month");
                                    i.putExtra("Months", Months);
                                    startActivity(i);
                                }
                                else {
                                    Intent i = new Intent(getContext(), ClientTicketReportListActivity.class);
                                    i.putExtra("jarray", String.valueOf(jarray));
                                    i.putExtra("From", "Last Month");
                                    i.putExtra("Months", Months);

                                    startActivity(i);
                                }

                            } else {
                                Snackbar.make(v, jobj.getString("ResponseMessage"), Snackbar.LENGTH_LONG)
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
            Toast.makeText(getContext(),"No internet connection",Toast.LENGTH_SHORT).show();
        }
    }

    private void getProducts() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater1 = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.product_popup, null);
            list_view = (ListView) layout.findViewById(R.id.list_view);
            etsearch = (EditText) layout.findViewById(R.id.etsearch);
            tv_popuptitle = (TextView) layout.findViewById(R.id.tv_popuptitle);
            tv_popuptitle.setText("Select Product");
            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();
            getProductList(alertDialog);
            alertDialog.show();
        }catch (Exception e){e.printStackTrace();}
    }

    public void getProductList(final AlertDialog alertDialog){
        if (new InternetUtil(getContext()).isInternetOn()) {
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getProducts(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            JSONObject jObject = new JSONObject(response.body());
                            JSONObject jobj = jObject.getJSONObject("agentProductDetailsInfo");
                            if (jobj.getString("productDetailsList").equals("null")) {
                            } else {
                                JSONArray jarray = jobj.getJSONArray("productDetailsList");
                                array_sort = new ArrayList<>();
                                for (int k = 0; k < jarray.length(); k++) {
                                    JSONObject jsonObject = jarray.getJSONObject(k);

                                    searchNamesArrayList.add(new ProductModel(jsonObject.getString("ID_Product"),jsonObject.getString("ProdName")));
                                    array_sort.add(new ProductModel(jsonObject.getString("ID_Product"),jsonObject.getString("ProdName")));
                                }
                                sadapter = new SearchProductAdapter(getContext(), array_sort);
                                list_view.setAdapter(sadapter);
                                list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        txt_product.setText(array_sort.get(position).getProductName());
                                        ID_Product=array_sort.get(position).getID_Product();

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
                                    array_sort.clear();
                                    for (int i = 0; i < searchNamesArrayList.size(); i++) {
                                        if (textlength <= searchNamesArrayList.get(i).getProductName().length()) {
                                            if (searchNamesArrayList.get(i).getProductName().toLowerCase().trim().contains(
                                                    etsearch.getText().toString().toLowerCase().trim())) {
                                                array_sort.add(searchNamesArrayList.get(i));
                                            }
                                        }
                                    }
                                    sadapter = new SearchProductAdapter(getContext(), array_sort);
                                    list_view.setAdapter(sadapter);
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
            Toast.makeText(getContext(),"No internet connection",Toast.LENGTH_SHORT).show();
        }
    }

    private void getClient() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater1 = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.product_popup, null);
            list_view = (ListView) layout.findViewById(R.id.list_view);
            etsearch = (EditText) layout.findViewById(R.id.etsearch);
            tv_popuptitle = (TextView) layout.findViewById(R.id.tv_popuptitle);
            tv_popuptitle.setText("Select Client");
            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();
            getClientList(alertDialog);
            alertDialog.show();
        }catch (Exception e){e.printStackTrace();}
    }

    public void getClientList(final AlertDialog alertDialog){
        if (new InternetUtil(getContext()).isInternetOn()) {
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
                    requestObject1.put("LoginMode", "1");
                    requestObject1.put("FK_Company", Config.FK_Company);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getClient(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            JSONObject jObject = new JSONObject(response.body());
                            JSONObject jobj = jObject.getJSONObject("ClientDetails");
                            if (jobj.getString("clientDetailsList").equals("null")) {
                            } else {
                                JSONArray jarray = jobj.getJSONArray("clientDetailsList");
                                clientarray_sort = new ArrayList<>();
                                for (int k = 0; k < jarray.length(); k++) {
                                    JSONObject jsonObject = jarray.getJSONObject(k);

                                    clientsearchNamesArrayList.add(new ClientModel(jsonObject.getString("ID_Client"),jsonObject.getString("ClientName")));
                                    clientarray_sort.add(new ClientModel(jsonObject.getString("ID_Client"),jsonObject.getString("ClientName")));
                                }
                                adapter = new SearchClientAdapter(getContext(), clientarray_sort);
                                list_view.setAdapter(adapter);
                                list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        txt_client.setText(clientarray_sort.get(position).getClientName());
                                        ID_Client=clientarray_sort.get(position).getID_Client();

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
                                    clientarray_sort.clear();
                                    for (int i = 0; i < clientsearchNamesArrayList.size(); i++) {
                                        if (textlength <= clientsearchNamesArrayList.get(i).getClientName().length()) {
                                            if (clientsearchNamesArrayList.get(i).getClientName().toLowerCase().trim().contains(
                                                    etsearch.getText().toString().toLowerCase().trim())) {
                                                clientarray_sort.add(clientsearchNamesArrayList.get(i));
                                            }
                                        }
                                    }
                                    adapter = new SearchClientAdapter(getContext(), clientarray_sort);
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
            Toast.makeText(getContext(),"No internet connection",Toast.LENGTH_SHORT).show();
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