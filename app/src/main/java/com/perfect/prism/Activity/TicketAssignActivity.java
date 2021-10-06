package com.perfect.prism.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.perfect.prism.Adapter.SearchProductAdapter;
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

public class TicketAssignActivity extends AppCompatActivity implements View.OnClickListener {

    String TAG="TicketAssignActivity";
    LinearLayout ll_department,llchooseProduct,ll_team,ll_agent,llsearch,ll_time, assign_ticket,reset_assignTicket;
    ListView list_view;
    TextView tv_popuptitle,txt_department,txt_product ,txt_team,txt_agent,txt_time;
    EditText et_AgentNote,etsearch;
    SearchProductAdapter sadapter;
    public static ArrayList<ProductModel> array_sort= new ArrayList<>();
    String ID_Department="",ID_Product="",ID_Team="",ID_Agent="",ID_Ticket,TM_Complete="";
    private ProgressDialog progressDialog;

    SearchProductAdapter spadapter;
    ArrayList<ProductModel> searchNamesArrayList = new ArrayList<>();
    public static ArrayList<ProductModel> product_array_sort= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_assign);
        Intent in = getIntent();
        ID_Ticket  = in.getStringExtra("ID_Ticket");
        initialisation();
        setRegViews();
    }

    private void initialisation() {
        ll_department       = findViewById( R.id.llchooseDepartnment );
        llchooseProduct     = findViewById( R.id.llchooseProduct );
        ll_team             = findViewById(R.id.llchooseteam);
        ll_agent            = findViewById(R.id.llchooseAgent);
        ll_time             = findViewById(R.id.llchooseTime);

        assign_ticket       = findViewById(R.id.ll_assignTicket);
        reset_assignTicket  = findViewById(R.id.reset_assignTicket);
        txt_department      = findViewById(R.id.txt_department);
        txt_product         = findViewById(R.id.txt_product);
        txt_team            = findViewById(R.id.txt_team);
        txt_agent           = findViewById(R.id.txt_agent);
        txt_time           = findViewById(R.id.txt_time);
        et_AgentNote        = findViewById(R.id.et_subject);
    }

    private void setRegViews() {
        ll_department.setOnClickListener(this);
        llchooseProduct.setOnClickListener(this);
        ll_agent.setOnClickListener(this);
        ll_time.setOnClickListener(this);
        ll_team.setOnClickListener(this);
        assign_ticket.setOnClickListener(this);
        reset_assignTicket.setOnClickListener(this);
    }

    private void reset() {
        ID_Department="";
        ID_Product="";
        ID_Team="";
        ID_Agent="";
        txt_department.setText("");
        txt_product.setText("");
        txt_team.setText("");
        txt_agent.setText("");
        txt_agent.setText("");
        et_AgentNote.setText("");
    }

    private void getDepartmentAndTeam(String from,View v) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater1 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.product_popup, null);
            list_view =layout.findViewById(R.id.list_view);
            llsearch =layout.findViewById(R.id.llsearch);
            llsearch.setVisibility(View.GONE);
            tv_popuptitle = layout.findViewById(R.id.tv_popuptitle);
            if(from.equals("Dep")){
                tv_popuptitle.setText("Select Department");

            } else if (from.equals("Team")){
                tv_popuptitle.setText("Select Team");

            }
            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();
            getDepartmentAndTeamList(alertDialog,from,v);
            alertDialog.show();
        }catch (Exception e){e.printStackTrace();}
    }

    public void getDepartmentAndTeamList(final AlertDialog alertDialog, final String from,final View v){
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
                    requestObject1.put("ResponseType", "1");
                    requestObject1.put("FK_Company", Config.FK_Company);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getDepartmentAndTeam(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            progressDialog.dismiss();

                            JSONObject jObject = new JSONObject(response.body());
                            JSONObject jobj = jObject.getJSONObject("dropdownFillInfo");
                            if(jObject.getString("StatusCode").equals("0")){

                                if(from.equals("Dep")){
                                    JSONObject jobjDep = jobj.getJSONObject("DepartmentDropDownFillList");
                                    if (jobjDep.getString("DropdownFillDetailsList").equals("null")) {
                                        alertDialog.dismiss();
                                        Snackbar.make(v,"Department list is null" , Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();

                                    } else {
                                        JSONArray jarray = jobjDep.getJSONArray("DropdownFillDetailsList");
                                        array_sort = new ArrayList<>();
                                        for (int k = 0; k < jarray.length(); k++) {
                                            JSONObject jsonObject = jarray.getJSONObject(k);
                                            array_sort.add(new ProductModel(jsonObject.getString("ValueField"),jsonObject.getString("TextField")));
                                        }
                                        sadapter = new SearchProductAdapter(TicketAssignActivity.this, array_sort);
                                        list_view.setAdapter(sadapter);
                                        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                ID_Team="";
                                                ID_Agent="";
                                                txt_team.setText("");
                                                txt_agent.setText("");
                                                txt_time.setText("");

                                                txt_department.setText(array_sort.get(position).getProductName());
                                                ID_Department=array_sort.get(position).getID_Product();
                                                alertDialog.dismiss();
                                            }
                                        });
                                    }
                                }
                                else if(from.equals("Team")){
                                    JSONObject jobjDep = jobj.getJSONObject("TeamDropDownFillList");
                                    if (jobjDep.getString("DropdownFillDetailsList").equals("null")) {
                                        alertDialog.dismiss();
                                        Snackbar.make(v,"Team list is null" , Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();

                                    } else {
                                        JSONArray jarray = jobjDep.getJSONArray("DropdownFillDetailsList");
                                        array_sort = new ArrayList<>();
                                        for (int k = 0; k < jarray.length(); k++) {
                                            JSONObject jsonObject = jarray.getJSONObject(k);
                                            array_sort.add(new ProductModel(jsonObject.getString("ValueField"),jsonObject.getString("TextField")));
                                        }
                                        sadapter = new SearchProductAdapter(TicketAssignActivity.this, array_sort);
                                        list_view.setAdapter(sadapter);
                                        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                ID_Agent="";
                                                txt_agent.setText("");
                                                txt_time.setText("");

                                                txt_team.setText(array_sort.get(position).getProductName());
                                                ID_Team=array_sort.get(position).getID_Product();
                                                alertDialog.dismiss();
                                            }
                                        });

                                    }
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

    private void getProducts(View v) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater1 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.product_popup, null);
            list_view = (ListView) layout.findViewById(R.id.list_view);
//            etsearch = (EditText) layout.findViewById(R.id.etsearch);
            llsearch =layout.findViewById(R.id.llsearch);
            llsearch.setVisibility(View.GONE);
            tv_popuptitle = (TextView) layout.findViewById(R.id.tv_popuptitle);
            tv_popuptitle.setText("Select Product");
            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();
            getProductList(alertDialog,v);
            alertDialog.show();
        }catch (Exception e){e.printStackTrace();}
    }

    public void getProductList(final AlertDialog alertDialog,final View v){
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
                    requestObject1.put("LoginMode", "15");
                    requestObject1.put("ID_Department", ID_Department);
                    requestObject1.put("FK_Company", Config.FK_Company);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getDepProducts(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            JSONObject jObject = new JSONObject(response.body());
                            JSONObject jobj = jObject.getJSONObject("DepartmentProductInfo");
                            if (jobj.getString("productDetailsList").equals("null")) {
                                alertDialog.dismiss();
                                Snackbar.make(v,"Product list is null" , Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();

                            } else {
                                JSONArray jarray = jobj.getJSONArray("productDetailsList");
                                product_array_sort = new ArrayList<>();
                                for (int k = 0; k < jarray.length(); k++) {
                                    JSONObject jsonObject = jarray.getJSONObject(k);

                                    searchNamesArrayList.add(new ProductModel(jsonObject.getString("ID_Product"),jsonObject.getString("ProdName")));
                                    product_array_sort.add(new ProductModel(jsonObject.getString("ID_Product"),jsonObject.getString("ProdName")));
                                }
                                sadapter = new SearchProductAdapter(getApplicationContext(), product_array_sort);
                                list_view.setAdapter(sadapter);
                                list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        txt_product.setText(product_array_sort.get(position).getProductName());
                                        ID_Product=product_array_sort.get(position).getID_Product();
                                        alertDialog.dismiss();
                                    }
                                });

                            }
//                            etsearch.addTextChangedListener(new TextWatcher() {
//                                public void afterTextChanged(Editable s) {
//                                }
//                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                                }
//                                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                                    list_view.setVisibility(View.VISIBLE);
////                                    textlength = etsearch.getText().length();
//                                    array_sort.clear();
//                                    for (int i = 0; i < searchNamesArrayList.size(); i++) {
//                                        if (textlength <= searchNamesArrayList.get(i).getProductName().length()) {
//                                            if (searchNamesArrayList.get(i).getProductName().toLowerCase().trim().contains(
//                                                    etsearch.getText().toString().toLowerCase().trim())) {
//                                                array_sort.add(searchNamesArrayList.get(i));
//                                            }
//                                        }
//                                    }
//                                    sadapter = new SearchProductAdapter(getApplicationContext(), array_sort);
//                                    list_view.setAdapter(sadapter);
//                                }
//                            });
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

    private void getTime(View v) {

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater1 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.product_popup, null);
            list_view =layout.findViewById(R.id.list_view);
            llsearch =layout.findViewById(R.id.llsearch);
            llsearch.setVisibility(View.GONE);
            tv_popuptitle = layout.findViewById(R.id.tv_popuptitle);
            tv_popuptitle.setText("Select Time");
            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();
            getTimeList(alertDialog,v);
            alertDialog.show();
        }catch (Exception e){e.printStackTrace();}
    }

    private void getTimeList(final AlertDialog alertDialog, View v) {
        String[] stringArray = getResources().getStringArray(R.array.time_to_complete);
        Log.e(TAG,"stringArray   "+stringArray);
        array_sort = new ArrayList<>();
        for (int k = 0; k < stringArray.length; k++) {
            array_sort.add(new ProductModel(""+k+1,stringArray[k]));
        }
        sadapter = new SearchProductAdapter(TicketAssignActivity.this, array_sort);
        list_view.setAdapter(sadapter);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                txt_time.setText(array_sort.get(position).getProductName());
                alertDialog.dismiss();
            }
        });

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
                    requestObject1.put("ID_Department", ID_Department);
                    requestObject1.put("FK_Team", ID_Team);
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
                                    sadapter = new SearchProductAdapter(TicketAssignActivity.this, array_sort);
                                    list_view.setAdapter(sadapter);
                                    list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            txt_agent.setText(array_sort.get(position).getProductName());
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

    public void assignTicket(final View v){
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
                    SharedPreferences pref1 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF3, 0);
                    SharedPreferences pref2 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF4, 0);
                    requestObject1.put("ResponseType", "1");
                    requestObject1.put("LoginMode", "6");
                    requestObject1.put("Token", pref2.getString("TOKEN", null));
                    requestObject1.put("ID_Tickets", ID_Ticket);
                    requestObject1.put("ID_Department", ID_Department);
                    requestObject1.put("ID_Product", ID_Product);
                    requestObject1.put("AgentTo", ID_Agent);
                    requestObject1.put("AgentNotes", et_AgentNote.getText().toString());
                    requestObject1.put("Agent_ID",pref1.getString("Agent_ID", null));
                    requestObject1.put("FK_Company", Config.FK_Company);
                    requestObject1.put("TimeToComplete", TM_Complete);

                Log.e(TAG,"requestObject1  "+requestObject1);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.assignTicket(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            progressDialog.dismiss();
                            Log.e(TAG,"onResponse  "+response.body());
                            JSONObject jObject = new JSONObject(response.body());
                            JSONObject jobj = jObject.getJSONObject("AgentRootTicketsIndividualSelectinfo");
                            if(jObject.getInt("StatusCode") > -1){
                                finish();
                            } else if(jObject.getString("StatusCode").equals("-1")) {
                                Toast.makeText( getApplicationContext(),jobj.getString("ResponseMessage") , Toast.LENGTH_LONG ).show();
                                dologoutchanges();
                                startActivity(new Intent(TicketAssignActivity.this,WelcomeActivity.class));
                                finish();
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
            case R.id.llchooseDepartnment:
                getDepartmentAndTeam("Dep",v);
                break;
            case R.id.llchooseProduct:
                if(ID_Department.toString().equals(""))
                {
                    Snackbar.make(v,"Please select department" , Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else {
                    getProducts(v);
                }
                break;
            case R.id.llchooseteam:
                getDepartmentAndTeam("Team",v);
                break;
            case R.id.llchooseAgent:
                if(ID_Department.toString().equals(""))
                {
                    Snackbar.make(v,"Please select department" , Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else if(ID_Product.toString().equals("")){
                    Snackbar.make(v,"Please select product" , Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else if(ID_Team.toString().equals("")){
                    Snackbar.make(v,"Please select team" , Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else {
                    getAgent(v);
                }
                break;
            case R.id.llchooseTime:
                if(ID_Department.toString().equals(""))
                {
                    Snackbar.make(v,"Please select department" , Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else if(ID_Product.toString().equals("")){
                    Snackbar.make(v,"Please select product" , Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else if(ID_Team.toString().equals("")){
                    Snackbar.make(v,"Please select team" , Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else if(ID_Agent.toString().equals("")){
                    Snackbar.make(v,"Please select Agent" , Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else {
                    getTime(v);
                    //Toast.makeText(getApplicationContext(),"Time",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ll_assignTicket:
                TM_Complete = txt_time.getText().toString();
                if(ID_Department.toString().equals(""))
                {
                    Snackbar.make(v,"Please select department" , Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else if(ID_Product.toString().equals("")){
                    Snackbar.make(v,"Please select product" , Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else if(ID_Team.toString().equals("")){
                    Snackbar.make(v,"Please select team" , Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else if(ID_Agent.toString().equals("")){
                    Snackbar.make(v,"Please select agent" , Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else if(TM_Complete.toString().equals("")){
                    Snackbar.make(v,"Please select Time" , Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else {
                    assignTicket(v);
                }
                break;
            case R.id.reset_assignTicket:
                reset();
                break;


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item:
                Intent i = new Intent(TicketAssignActivity.this, HomeActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
