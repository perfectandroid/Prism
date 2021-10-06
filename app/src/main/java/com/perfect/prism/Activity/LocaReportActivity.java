package com.perfect.prism.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.perfect.prism.Adapter.SearchAgentAdapter;
import com.perfect.prism.Adapter.SearchProductAdapter;
import com.perfect.prism.Model.AgentModel;
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
import java.util.Calendar;

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

public class LocaReportActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout llchooseProduct,llchooseAgentLoca,llChooseDate,llsearch, assign_ticket,reset_assignTicket;
    ListView list_view;
    TextView tv_popuptitle,txt_product,txt_date,txt_agentLoca;
    EditText etsearch;
    int textlength = 0;
    SearchProductAdapter sadapter;
    SearchAgentAdapter adapter;

    ArrayList<ProductModel> searchNamesArrayList = new ArrayList<>();
    public static ArrayList<ProductModel> array_sort= new ArrayList<>();
    ArrayList<AgentModel> agentsearchNamesArrayList = new ArrayList<>();
    public static ArrayList<AgentModel> agentarray_sort= new ArrayList<>();
    String ID_Product="",ID_Agent="",ID_Ticket,FromDate;
    private ProgressDialog progressDialog;
    private int mYear, mMonth, mDay;
    String typee = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_repo);
        Intent in = getIntent();
        ID_Ticket  = in.getStringExtra("ID_Ticket");
        initialisation();
        setRegViews();
    }

    private void initialisation(){
        llchooseProduct     = findViewById(R.id.llchooseProduct );
        llchooseAgentLoca   = findViewById(R.id.llchooseAgentLoca);
        llChooseDate        = findViewById(R.id.llChooseDate);
        assign_ticket       = findViewById(R.id.ll_searchLocationReport);
        reset_assignTicket  = findViewById(R.id.reset_assignTicket);
        txt_product         = findViewById(R.id.txt_product);
        txt_date            = findViewById(R.id.txt_date);
        txt_agentLoca       = findViewById(R.id.txt_agentLoca);
    }

    private void setRegViews() {
        llchooseProduct.setOnClickListener(this);
        llchooseAgentLoca.setOnClickListener(this);
        llChooseDate.setOnClickListener(this);
        assign_ticket.setOnClickListener(this);
        reset_assignTicket.setOnClickListener(this);
        setDate();
    }

    private void reset(){
        ID_Product= "";
        ID_Agent="";
        txt_product.setText("");
        setDate();
        txt_agentLoca.setText("");
    }

    public void setDate(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get( Calendar.YEAR ) ;
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        txt_date.setText(dayOfMonth +"-" + (month +1) + "-" + ( year ));
        FromDate  = ( year ) +"-" + (month + 1) + "-" + dayOfMonth;

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llchooseProduct:
                searchNamesArrayList.clear();
                array_sort.clear();
                getProducts();
                break;
            case R.id.llchooseAgentLoca:
//                if(ID_Product.equals("")){
//                    Snackbar.make(v,"Please select product" , Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                }
//                else{
                    agentsearchNamesArrayList.clear();
                    agentarray_sort.clear();
                    getAgent(v);
//                }
                break;
            case R.id.llChooseDate:
                dateSelector();
                break;

            case R.id.ll_searchLocationReport:
//                if(ID_Product.equals("")){
//                    Snackbar.make(v,"Please select product" , Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                }
//                else{
                    searchLocationReport(v);
//                }
                break;
            case R.id.reset_assignTicket:
                reset();
                break;
        }
    }
    public void searchLocationReport(final View v){
        if (new InternetUtil(this).isInternetOn()) {
            try{
                progressDialog = new ProgressDialog(this, R.style.Progress);
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
                    requestObject1.put("LoginMode", "12");
                    requestObject1.put("Rpt_AgentID", ID_Agent);
                    requestObject1.put("FK_Company", Config.FK_Company);
                    requestObject1.put("ID_Product", ID_Product);
                    requestObject1.put("ToDate", FromDate);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.agentLocationHourDetails(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject jObject = new JSONObject(response.body());
                            JSONObject jobj = jObject.getJSONObject("SelectAgentLocationDetailsInfo");
                            if(jObject.getInt("StatusCode") == 0){

                                if (ID_Agent.equalsIgnoreCase("")){
                                    typee = "1";
                                }else {
                                    typee = "0";
                                }
                                Intent intent = new Intent(LocaReportActivity.this, LocaReportValueActivity.class);
                                intent.putExtra("jobjString", String.valueOf(jobj));
                                intent.putExtra("date", FromDate);
                                intent.putExtra("type",typee);
                                startActivity(intent);
                            }
                            else{
                                Snackbar.make(v,jobj.getString("ResponseMessage") , Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        progressDialog.dismiss();
                    }
                });
            } catch (Exception e) {
                progressDialog.dismiss();
                e.printStackTrace();
            }
        }else {
            Snackbar.make(v,"No internet connection" , Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }
    private void getProducts() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater1 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        txt_agentLoca.setText("");
        ID_Agent="";
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
                                sadapter = new SearchProductAdapter(getApplicationContext(), array_sort);
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
                                    sadapter = new SearchProductAdapter(getApplicationContext(), array_sort);
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
            Toast.makeText(this,"No internet connection",Toast.LENGTH_SHORT).show();
        }
    }

    private void getAgent(View v) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater1 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.product_popup, null);
            list_view =layout.findViewById(R.id.list_view);
            llsearch =layout.findViewById(R.id.llsearch);
            llsearch.setVisibility(View.GONE);
            tv_popuptitle = layout.findViewById(R.id.tv_popuptitle);
            tv_popuptitle.setText("Select Agent");
            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();
            getAgentList(alertDialog,v);
            alertDialog.show();
        }catch (Exception e){e.printStackTrace();}
    }

    public void getAgentList(final AlertDialog alertDialog,final View v){
        if (new InternetUtil(this).isInternetOn()) {
            try{
                progressDialog = new ProgressDialog(this, R.style.Progress);
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
                    requestObject1.put("ResponseType", "0");
                    requestObject1.put("FK_Company", Config.FK_Company);
                    requestObject1.put("ID_Product", ID_Product);
//                    requestObject1.put("FK_Team", ID_Team);
                    //  requestObject1.put("Rpt_AgentID", agentID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getAgentList(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject jObject = new JSONObject(response.body());
                            JSONObject jobj = jObject.getJSONObject("dropdownFillInfo");
                            if(jObject.getString("StatusCode").equals("0")){

                                JSONObject jobjDep = jobj.getJSONObject("AgentDropDownFillList");
                                if (jobjDep.getString("DropdownFillDetailsList").equals("[]")||jobjDep.getString("DropdownFillDetailsList").equals("")) {
                                    alertDialog.dismiss();
                                    Snackbar.make(v,"There is no agent to display..." , Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                } else {
                                    JSONArray jarray = jobjDep.getJSONArray("DropdownFillDetailsList");
                                    array_sort = new ArrayList<>();
                                    for (int k = 0; k < jarray.length(); k++) {
                                        JSONObject jsonObject = jarray.getJSONObject(k);
                                        array_sort.add(new ProductModel(jsonObject.getString("ValueField"),jsonObject.getString("TextField")));
                                    }
                                    sadapter = new SearchProductAdapter(LocaReportActivity.this, array_sort);
                                    list_view.setAdapter(sadapter);
                                    list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            txt_agentLoca.setText(array_sort.get(position).getProductName());
                                            ID_Agent=array_sort.get(position).getID_Product();
                                            alertDialog.dismiss();
                                        }
                                    });
                                }
                            }
                            else{
                                alertDialog.dismiss();

                                Snackbar.make(v,jobj.getString("ResponseMessage") , Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        } catch (JSONException e) {
                            alertDialog.dismiss();
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        alertDialog.dismiss();
                        progressDialog.dismiss();
                    }
                });
            } catch (Exception e) {
                alertDialog.dismiss();
                progressDialog.dismiss();
                e.printStackTrace();
            }
        }else {
            Snackbar.make(v,"No internet connection" , Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    public void dateSelector(){
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        txt_date.setText(dayOfMonth + "-" + (monthOfYear + 1)+ "-" + year);
                        FromDate  = ( year ) +"-" + (monthOfYear + 1) + "-" + dayOfMonth;

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
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
                Intent i = new Intent(LocaReportActivity.this, HomeActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

