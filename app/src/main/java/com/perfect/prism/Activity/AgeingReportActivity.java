package com.perfect.prism.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

public class AgeingReportActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout llchoosePrdt, llfromdate, lltodate, llSubmit, llRest;
    ListView list_view;
    EditText etsearch, edt_ageing_month;
    SearchProductAdapter sadapter;
    TextView txt_product, tv_popuptitle, txt_from_date, txt_to_date;
    int textlength = 0;
    ArrayList<ProductModel> searchNamesArrayList = new ArrayList<>();
    public static ArrayList<ProductModel> array_sort= new ArrayList<>();
    private int mYear, mMonth, mDay;
    String FromDate, ToDate, ID_Product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ageing_report);
        initiateViews();
        setRegViews();
        emptyDates();
        setDate();
    }

    private void initiateViews() {
        llchoosePrdt=findViewById(R.id.llchoosePrdt);
        txt_product=findViewById(R.id.txt_product);
        llfromdate =findViewById(R.id.llfromdate);
        lltodate=findViewById(R.id.lltodate);
        txt_from_date=findViewById(R.id.txt_from_date);
        txt_to_date=findViewById(R.id.txt_to_date);
        edt_ageing_month=findViewById(R.id.edt_ageing_month);
        llSubmit=findViewById(R.id.llSubmit);
        llRest=findViewById(R.id.llRest);
    }

    private void setRegViews() {
        llchoosePrdt.setOnClickListener(this);
        llfromdate.setOnClickListener(this);
        lltodate.setOnClickListener(this);
        llSubmit.setOnClickListener(this);
        llRest.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llchoosePrdt:
                getProducts();
                break;
            case R.id.llfromdate:
                dateSelector("from");
                break;
            case R.id.lltodate:
                dateSelector("to");
                break;
            case R.id.llSubmit:
                getAgeingReport(v);
                break;
            case R.id.llRest:
                reSet();
                break;
        }
    }

    public void reSet(){
        ID_Product= "";
        emptyDates();
        txt_product.setText("");
/*        txt_from_date.setText("");
*//*        txt_to_date.setText("");*/
        edt_ageing_month.setText("");
        setDate();
    }

    private void getProducts() {
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            LayoutInflater inflater1 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.product_popup, null);
            list_view = (ListView) layout.findViewById(R.id.list_view);
            etsearch = (EditText) layout.findViewById(R.id.etsearch);
            tv_popuptitle = (TextView) layout.findViewById(R.id.tv_popuptitle);
            tv_popuptitle.setText("Select Product");
            builder.setView(layout);
            final android.app.AlertDialog alertDialog = builder.create();
            getProductList(alertDialog);
            alertDialog.show();
        }catch (Exception e){e.printStackTrace();}
    }

    public void getProductList(final AlertDialog alertDialog){
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
                                sadapter = new SearchProductAdapter(AgeingReportActivity.this, array_sort);
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
                                    sadapter = new SearchProductAdapter(AgeingReportActivity.this, array_sort);
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

    public void dateSelector(final String strdate){
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(AgeingReportActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        if(strdate.equals("from")){
                            txt_from_date.setText(dayOfMonth + "-" + (monthOfYear + 1)+ "-" + year);
                            FromDate= year+ "-" + (monthOfYear + 1)+ "-" + dayOfMonth+" 00:00:00";
                        }
                        if(strdate.equals("to")){
                        txt_to_date.setText(dayOfMonth + "-" + (monthOfYear + 1)+ "-" + year);
                            ToDate= year+ "-" + (monthOfYear + 1)+ "-" + dayOfMonth+" 00:00:00";
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void getAgeingReport(final View view){
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
                    requestObject1.put("LoginMode", "9");
                    requestObject1.put("FK_Company", Config.FK_Company);
                    requestObject1.put("FromDate",FromDate );
                    requestObject1.put("ToDate", ToDate);
                    requestObject1.put("ID_Product",ID_Product);
                    requestObject1.put("AgeingCount",edt_ageing_month.getText().toString() );

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.geAgeingReport(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {

                        try {
                            JSONObject jObject = new JSONObject(response.body());
                            String StatusCode = jObject.getString("StatusCode");
                            if (StatusCode.equals("0")) {
                                JSONObject jobj = jObject.getJSONObject("SelectClientWiseTicketAgeingDetailsInfo");
                                JSONArray jarray=  jobj.getJSONArray("ClientWiseTicketAgeingDetailsList");

                                Intent i = new Intent(AgeingReportActivity.this, AgeingReportListActivity.class);
                                i.putExtra("response", jarray.toString());
                                i.putExtra("FromDate", FromDate);
                                i.putExtra("ToDate", ToDate);
                                i.putExtra("ID_Product", ID_Product);
                                i.putExtra("AgeingCount", edt_ageing_month.getText().toString());
                                i.putExtra("ProductName", txt_product.getText().toString());
                                startActivity(i);
                            }else{
                                Snackbar.make(view, "No data found", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
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
            Snackbar.make(view, "No internet connection", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    public void emptyDates(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get( Calendar.YEAR ) ;
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        FromDate  = ( year-2 ) +"-" + (month + 1) + "-" + dayOfMonth +" 00:00:00";
        ToDate  = ( year ) +"-" + (month+1) + "-" + dayOfMonth+ " 00:00:00";
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
                Intent i = new Intent(AgeingReportActivity.this, HomeActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setDate(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get( Calendar.YEAR ) ;
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        txt_from_date.setText(dayOfMonth +"-" + (month -1) + "-" + ( year ));
        txt_to_date.setText(dayOfMonth +"-" + (month +1) + "-" + ( year ));
    }
}
