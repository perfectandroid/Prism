package com.perfect.prism.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class AgeingClientWiseReportAdapter extends RecyclerView.Adapter {

    JSONArray jsonArray;
    JSONObject jsonObject=null;
    Context context;
    String FirstCategoryName, ID_CategoryFirst;

    public AgeingClientWiseReportAdapter(Context context, JSONArray jsonArray) {
        this.context=context;
        this.jsonArray=jsonArray;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.ageing_product_wise_detail, parent, false);
        vh = new MainViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            jsonObject=jsonArray.getJSONObject(position);
            if (holder instanceof MainViewHolder) {
                ((MainViewHolder)holder).txt_product_name.setText(jsonObject.getString("ProductName"));
                ((MainViewHolder)holder).txt_total_ticket.setText(jsonObject.getString("TotalTickets"));
                ((MainViewHolder)holder).txt_opening_ticket.setText(jsonObject.getString("OpenedTickets"));
                ((MainViewHolder)holder).txt_resolved_ticket.setText(jsonObject.getString("ResolvedTickets"));
                ((MainViewHolder)holder).txt_software_pending.setText(jsonObject.getString("SoftwarePending"));

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
        TextView txt_product_name,txt_total_ticket,txt_opening_ticket,txt_resolved_ticket,txt_software_pending;
        public MainViewHolder(View v) {
            super(v);
            txt_product_name=v.findViewById(R.id.txt_product_name);
            txt_total_ticket=v.findViewById(R.id.txt_total_ticket);
            txt_opening_ticket=v.findViewById(R.id.txt_opening_ticket);
            txt_resolved_ticket=v.findViewById(R.id.txt_resolved_ticket);
            txt_software_pending=v.findViewById(R.id.txt_software_pending);
        }
    }


}
