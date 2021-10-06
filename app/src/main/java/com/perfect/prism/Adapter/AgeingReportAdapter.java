package com.perfect.prism.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.perfect.prism.R;
import com.perfect.prism.Retrofit.ApiInterface;
import com.perfect.prism.Utility.Config;

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
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AgeingReportAdapter extends RecyclerView.Adapter {

    JSONArray jsonArray;
    JSONObject jsonObject=null;
    Context context;
    String FirstCategoryName, ID_CategoryFirst,FromDate,ToDate,ID_Product,AgeingCount,ID_Client;
    private ProgressDialog progressDialog;

    public AgeingReportAdapter(Context context, JSONArray jsonArray,String FromDate,String ToDate,String ID_Product,String AgeingCount) {
        this.context=context;
        this.jsonArray=jsonArray;
        this.FromDate=FromDate;
        this.ToDate=ToDate;
        this.ID_Product=ID_Product;
        this.AgeingCount=AgeingCount;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.activity_agring_list, parent, false);
        vh = new MainViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            jsonObject=jsonArray.getJSONObject(position);
            if (holder instanceof MainViewHolder) {
                ((MainViewHolder)holder).tvPrdName.setText((position+1)+") "+jsonObject.getString("ClientName"));
                ((MainViewHolder)holder).ticket_total_count.setText(jsonObject.getString("TotalTickets"));

                ((MainViewHolder)holder).lnLayout.setTag(position);
                ((MainViewHolder)holder).lnLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            jsonObject = jsonArray.getJSONObject(position);
                            ID_Client = jsonObject.getString("ID_Client");
                            ageingReport(ID_Client,jsonObject.getString("ClientName"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    private class MainViewHolder extends RecyclerView.ViewHolder {
        LinearLayout lnLayout;
        TextView tvPrdName, ticket_total_count;
        public MainViewHolder(View v) {
            super(v);
            lnLayout= v.findViewById(R.id.lnLayout);
            tvPrdName=v.findViewById(R.id.tvPrdName);
            ticket_total_count=v.findViewById(R.id.ticket_total_count);
        }
    }

    private void ageingReport(String ID_Client, final String clientName){

        progressDialog = new ProgressDialog(context, R.style.Progress);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(context.getResources()
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
            try {
                requestObject1.put("LoginMode", "11");
                requestObject1.put("FK_Company", Config.FK_Company);
                requestObject1.put("FromDate", FromDate);
                requestObject1.put("ToDate", ToDate);
                requestObject1.put("ID_Product", ID_Product);
                requestObject1.put("ID_Client", ID_Client);
                requestObject1.put("AgeingCount", AgeingCount);

            } catch (Exception e) {

                e.printStackTrace();
                progressDialog.dismiss();
            }
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
            Call<String> call = apiService.getAgeingCountReport(body);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        progressDialog.dismiss();
                        JSONObject jObject = new JSONObject(response.body());
                        String StatusCode = jObject.getString("StatusCode");

                        if (StatusCode.equals("0")) {


                            JSONObject jmember = jObject.getJSONObject("SelectAgeingCountWiseProductDetailsInfo");
                            JSONArray jsonArray = jmember.getJSONArray("AgeingCountWiseProductDetailsList");
                            showResult(jsonArray,clientName,AgeingCount);


                        } else {

                        }

                    } catch (Exception e) {
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
        InputStream caInput = context.getResources().openRawResource(R.raw.my_cert); // File path: app\src\main\res\raw\your_cert.cer
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

    private  void showResult(JSONArray jarray,String clientName,String ageingDays){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        LayoutInflater inflater1 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater1.inflate(R.layout.ageing_report_popup, null);
        Button ok = layout.findViewById(R.id.back_ok);
        RecyclerView recy_ageing_report = layout.findViewById(R.id.recy_ageing_details_rprt);
        TextView txt_header_client_name = layout.findViewById(R.id.txt_header_client_name);
        TextView txt_aging_days = layout.findViewById(R.id.txt_aging_days);

        GridLayoutManager lLayout = new GridLayoutManager(context, 1);
        recy_ageing_report.setLayoutManager(lLayout);
        recy_ageing_report.setHasFixedSize(true);
        AgeingClientWiseReportAdapter adapter = new AgeingClientWiseReportAdapter(context, jarray);
        recy_ageing_report.setAdapter(adapter);

        txt_header_client_name.setText(clientName);
        if(ageingDays.equals("")){
            txt_aging_days.setText("Ageing with 0 days");
        }
        else {
            txt_aging_days.setText("Ageing with "+ ageingDays+" days");
        }
        builder.setView(layout);
        final android.app.AlertDialog alertDialog = builder.create();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }


}
