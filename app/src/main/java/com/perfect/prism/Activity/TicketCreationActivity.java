package com.perfect.prism.Activity;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.perfect.prism.Adapter.SearchClientAdapter;
import com.perfect.prism.Adapter.SearchProductAdapter;
import com.perfect.prism.Adapter.SearchTopicAdapter;
import com.perfect.prism.Model.ClientModel;
import com.perfect.prism.Model.ProductModel;
import com.perfect.prism.Model.TopicModel;
import com.perfect.prism.R;
import com.perfect.prism.Retrofit.ApiInterface;
import com.perfect.prism.Utility.Config;
import com.perfect.prism.Utility.FileUtils;
import com.perfect.prism.Utility.InternetUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class TicketCreationActivity extends AppCompatActivity implements View.OnClickListener {

    String TAG ="TicketCreationActivity";
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1;
    ProgressDialog progressDialog;
    private LinearLayout mBtnSubmit, llreset;
    ListView list_view;
    private ImageView imgClient, imgProduct, imgTopic, mImgAttchment;
    private TextView txt_client,txt_product,txt_topic,mTxtAttchment,tv_popuptitle,txt_ticket_no,txt_date;
    SearchClientAdapter adapter;
    SearchProductAdapter sadapter;
    SearchTopicAdapter topicadapter;
    ArrayList<ClientModel> clientsearchNamesArrayList = new ArrayList<>();
    public static ArrayList<ClientModel> clientarray_sort= new ArrayList<>();
    int textlength = 0;
    ArrayList<ProductModel> searchNamesArrayList = new ArrayList<>();
    public static ArrayList<ProductModel> array_sort= new ArrayList<>();
    ArrayList<TopicModel> searchTopicArrayList = new ArrayList<>();
    public static ArrayList<TopicModel> topic_array_sort= new ArrayList<>();
    String ID_Product="",ID_Client="",ID_Topic="";
    String imagePath;
    EditText et_subject, et_Dis,etsearch;
    private Bitmap bitmap;
    private File destination = null;
    private InputStream inputStreamImg;
    private String imgPath = null;
    private String selectedFilePath = "";
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2, PICK_DOCUMRNT_GALLERY = 3;
    private static final int PERMISSION_CAMERA = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_creation);
        initiateViews();
        setRegViews();
        Log.e(TAG,"Start   ");
        ticketNumberGenerate();

        txt_date.setText(getDateTime("dd-MM-yyyy"));
    }

    private void initiateViews() {
        imgClient = findViewById(R.id.imgClient);
        imgProduct = findViewById(R.id.imgProduct);
        imgTopic = findViewById(R.id.imgTopic);
        mImgAttchment = findViewById(R.id.impic);
        mBtnSubmit = findViewById(R.id.submit_datewise);
        txt_ticket_no = findViewById(R.id.txt_ticket_no);
        txt_date = findViewById(R.id.txt_date);
        txt_client = findViewById(R.id.txt_client);
        txt_product = findViewById(R.id.txt_product);
        txt_topic = findViewById(R.id.txt_topic);
        mTxtAttchment = findViewById(R.id.txt_attachment);
        et_subject = findViewById(R.id.et_subject);
        et_Dis = findViewById(R.id.et_Dis);
        llreset = findViewById(R.id.llreset);
    }

    private void setRegViews() {
        imgClient.setOnClickListener(this);
        imgProduct.setOnClickListener(this);
        imgTopic.setOnClickListener(this);
        mImgAttchment.setOnClickListener(this);
        mBtnSubmit.setOnClickListener(this);
        llreset.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.imgClient:
                clientsearchNamesArrayList.clear();
                clientarray_sort.clear();
                getClient();
                break;
            case R.id.imgProduct:
                searchNamesArrayList.clear();
                array_sort.clear();
                getProducts();
                break;
            case R.id.imgTopic:
                searchTopicArrayList.clear();
                topic_array_sort.clear();
                getTopics();
                break;
            case R.id.llreset:
                reset();
                break;
            case R.id.impic:
                selectImage();
                break;
            case R.id.submit_datewise:
                if(txt_client.getText().toString().isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Please choose client",Snackbar.LENGTH_SHORT).show();
                }
                else {
                    Log.e(TAG,"submit_datewise    189");
                    if(txt_product.getText().toString().isEmpty()){
                        Log.e(TAG,"submit_datewise   190 ");
                        Snackbar.make(findViewById(android.R.id.content),"Please choose Product ", Snackbar.LENGTH_SHORT).show();
                    } else {
                        if(txt_topic.getText().toString().isEmpty()){
                            Log.e(TAG,"submit_datewise   195 ");

                            Snackbar.make(findViewById(android.R.id.content),"Please choose Topic", Snackbar.LENGTH_SHORT).show();
                        }
                        else{
                            if (et_subject.getText().toString().isEmpty()){
                                Log.e(TAG,"submit_datewise   201 ");
                                Snackbar.make(findViewById(android.R.id.content),"Please  provide subject.", Snackbar.LENGTH_SHORT).show();
                            }
                            else{
                                Log.e(TAG,"submit_datewise   205 ");
                                if (et_Dis.getText().toString().isEmpty()){
                                    Snackbar.make(findViewById(android.R.id.content),"Please  provide discription.", Snackbar.LENGTH_SHORT).show();
                                }
                                else {
                                    Log.e(TAG,"submit_datewise   210 ");
                                    if(destination==null){
                                        Log.e(TAG,"submit_datewise   212 ");
                                        createTicketWithoutImg();
                                    }
                                    else {
                                        Log.e(TAG,"submit_datewise   215 ");

                                        SharedPreferences pref1 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF3, 0);

                                        JSONObject jdata = new JSONObject();
                                        try {
//                                            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), destination);
//                                            MultipartBody.Part body = MultipartBody.Part.createFormData("ImgFile", destination.getName(),requestFile );
                                            jdata.put("TicketNo", txt_ticket_no.getText().toString());
                                            jdata.put("TicketDate",  getDateTime("yyyy-MM-dd"));
                                            jdata.put("TicketSubject", et_subject.getText().toString());
                                            jdata.put("TicketDescription", et_Dis.getText().toString());
                                            jdata.put("ID_Product", ID_Product);
                                            jdata.put("FK_Topic",  ID_Topic);
                                            jdata.put("Agent_ID", pref1.getString("Agent_ID", null));
                                            jdata.put("FK_Company", Config.FK_Company);
                                            jdata.put("ID_Client", ID_Client);
                                            //jdata.put("XmlAttachment", body);

                                        }
                                        catch (JSONException e) {
                                            Log.e(TAG,"submit_datewise   233 ");
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                        Log.e(TAG,"submit_datewise   237 ");
                                        createTicketWithImg(destination, jdata.toString());
                                    }

                                }

                            }
                        }
                    }
                }
                break;
        }
    }

    private void getTopics() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater1 = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.product_popup, null);
            list_view = (ListView) layout.findViewById(R.id.list_view);
            etsearch = (EditText) layout.findViewById(R.id.etsearch);
            tv_popuptitle = (TextView) layout.findViewById(R.id.tv_popuptitle);
            tv_popuptitle.setText("Select Topic");
            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();
            getTopicList(alertDialog);
            alertDialog.show();
        }catch (Exception e){e.printStackTrace();}
    }

    public void getTopicList(final AlertDialog alertDialog){
        if (new InternetUtil(getApplicationContext()).isInternetOn()) {
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
                    requestObject1.put("LoginMode", "16");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getTicketTopicDetails(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            JSONObject jObject = new JSONObject(response.body());
                            JSONObject jobj = jObject.getJSONObject("TicketTopicDetails");
                            if (jobj.getString("TicketTopicDetailsList").equals("null")) {
                            } else {
                                JSONArray jarray = jobj.getJSONArray("TicketTopicDetailsList");
                                topic_array_sort = new ArrayList<>();
                                for (int k = 0; k < jarray.length(); k++) {
                                    JSONObject jsonObject = jarray.getJSONObject(k);
                                    searchTopicArrayList.add(new TopicModel(jsonObject.getString("ID_Topic"),jsonObject.getString("TopicName")));
                                    topic_array_sort.add(new TopicModel(jsonObject.getString("ID_Topic"),jsonObject.getString("TopicName")));
                                }
                                topicadapter = new SearchTopicAdapter(getApplicationContext(), topic_array_sort);
                                list_view.setAdapter(topicadapter);
                                list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        txt_topic.setText(topic_array_sort.get(position).getTopicName());
                                        ID_Topic=topic_array_sort.get(position).getID_Topic();

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
                                    topic_array_sort.clear();
                                    for (int i = 0; i < searchTopicArrayList.size(); i++) {
                                        if (textlength <= searchTopicArrayList.get(i).getTopicName().length()) {
                                            if (searchTopicArrayList.get(i).getTopicName().toLowerCase().trim().contains(
                                                    etsearch.getText().toString().toLowerCase().trim())) {
                                                topic_array_sort.add(searchTopicArrayList.get(i));
                                            }
                                        }
                                    }
                                    topicadapter = new SearchTopicAdapter(getApplicationContext(), topic_array_sort);
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
            Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
        }
    }

    private void getProducts() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater1 = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        if (new InternetUtil(getApplicationContext()).isInternetOn()) {
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
                    requestObject1.put("LoginMode", "13");
                    requestObject1.put("ID_Client", ID_Client);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getAgentProductDetails(body);
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
            }
            catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
        }
    }

    private void getClient() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater1 = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        if (new InternetUtil(getApplicationContext()).isInternetOn()) {
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

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getClientDetails(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            JSONObject jObject = new JSONObject(response.body());
                            Log.e("myyyy",""+jObject);
                            JSONObject jobj = jObject.getJSONObject("ClientDetails");
                            if (jobj.getString("ClientDetailsList").equals("null")) {
                            } else {
                                JSONArray jarray = jobj.getJSONArray("ClientDetailsList");
                                clientarray_sort = new ArrayList<>();
                                for (int k = 0; k < jarray.length(); k++) {
                                    JSONObject jsonObject = jarray.getJSONObject(k);
                                    clientsearchNamesArrayList.add(new ClientModel(jsonObject.getString("ID_Client"),jsonObject.getString("ClientName")));
                                    clientarray_sort.add(new ClientModel(jsonObject.getString("ID_Client"),jsonObject.getString("ClientName")));
                                }
                                adapter = new SearchClientAdapter(getApplicationContext(), clientarray_sort);
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
                                    adapter = new SearchClientAdapter(getApplicationContext(), clientarray_sort);
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
            Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
        }
    }

    private void createTicketWithoutImg() {
        if (new InternetUtil(TicketCreationActivity.this).isInternetOn()) {
            try{
                progressDialog = new ProgressDialog(TicketCreationActivity.this, R.style.Progress);
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
                    requestObject1.put("TicketNo", txt_ticket_no.getText().toString());
                    requestObject1.put("TicketDate", getDateTime("yyyy-MM-dd"));
                    requestObject1.put("TicketSubject", et_subject.getText().toString());
                    requestObject1.put("TicketDescription", et_Dis.getText().toString());
                    requestObject1.put("ID_Product", ID_Product);
                    requestObject1.put("FK_Topic",  ID_Topic);
                    requestObject1.put("Agent_ID", pref1.getString("Agent_ID", null));
                    requestObject1.put("FK_Company", Config.FK_Company);
                    requestObject1.put("ID_Client", ID_Client);
                    Log.e("myyy",""+requestObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getAgentTicketCreationWithoutImg(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject jObject = new JSONObject(response.body());
                            if(jObject.getString("StatusCode").equals("0")){

                                JSONObject objTicketCreation = jObject.getJSONObject("AgentTicketCreation");
                                String ticketNum = objTicketCreation.getString("TicketNumber");
                                AlertDialog.Builder builder = new AlertDialog.Builder(TicketCreationActivity.this);
                                builder.setMessage("Ticket number "+ticketNum+" has been created successfully.")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                startActivity(new Intent(TicketCreationActivity.this,HomeActivity.class));
                                                finish();                                            }
                                        });
                                // Create the AlertDialog object and return it
                                builder.create();
                                builder.show();

                            }else {
                                JSONObject objTicketCreation = jObject.getJSONObject("AgentTicketCreation");
                                String ResMsg = objTicketCreation.getString("ResponseMessage");
                                AlertDialog.Builder builder = new AlertDialog.Builder(TicketCreationActivity.this);
                                builder.setMessage(ResMsg)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                         }
                                        });
                                // Create the AlertDialog object and return it
                                builder.create();
                                builder.show();
                                Toast.makeText(TicketCreationActivity.this,"Creating ticket has been failed.",Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this,"No internet connection",Toast.LENGTH_SHORT).show();
        }
    }

    private void createTicketWithImg(File fileimage, String Jsondata) {
        Log.e(TAG,"createTicketWithImg  1");
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
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), fileimage);
//ImgFile
                    MultipartBody.Part body = MultipartBody.Part.createFormData("ImgFile", fileimage.getName(), requestFile);
                    RequestBody JsonData = RequestBody.create(MediaType.parse("text/plain"), Jsondata);

                    Log.e("jsondattaa","DATA    "+Jsondata);
                    Log.e("jsondattaa","BODY "+body);
//                    RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), Jsondata.toString());

//                    Log.e(TAG,"createTicketWithImg  8");
//                    Log.e(TAG,"createTicketWithImg1  body  "+body);
//                    Log.e(TAG,"createTicketWithImg1  JsonData   "+JsonData+"   "
//                    +"\n"+Jsondata);

                 //
//                    Call<String> call = apiService.getAgentTicketCreationWithoutImg(body);
                    Call<String> call = apiService.getAgentTicketCreation(JsonData, body);
                    Log.e(TAG,"createTicketWithImg  9");
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                            Log.e(TAG,"responseakn   "+response.body());

                            try {
                                progressDialog.dismiss();
                                JSONObject jObject = new JSONObject(response.body());
                                Log.e("responceabc",""+response.body());

                                if(jObject.getString("StatusCode").equals("0")){

                                    JSONObject objTicketCreation = jObject.getJSONObject("AgentTicketCreation");
                                    String ticketNum = objTicketCreation.getString("TicketNumber");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(TicketCreationActivity.this);
                                    builder.setMessage("Ticket number "+ticketNum+" has been created successfully.")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {

                                                    startActivity(new Intent(TicketCreationActivity.this,HomeActivity.class));
                                                    finish();                                            }
                                            });
                                    // Create the AlertDialog object and return it
                                    builder.create();
                                    builder.show();

                                }else {
                                    JSONObject objTicketCreation = jObject.getJSONObject("AgentTicketCreation");
                                    String ResMsg = objTicketCreation.getString("ResponseMessage");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(TicketCreationActivity.this);
                                    builder.setMessage(ResMsg)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                }
                                            });
                                    // Create the AlertDialog object and return it
                                    builder.create();
                                    builder.show();
                                    Toast.makeText(TicketCreationActivity.this,"Creating ticket has been failed.",Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (JSONException e) {
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
                    Log.e(TAG,"Exceptionss    "+e.toString());
                    e.printStackTrace();
                    progressDialog.dismiss();

                }
            }catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Something went wrong!",Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"No internet connection!!",Toast.LENGTH_SHORT).show();
        }
    }

    private void selectImage() {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
                    }
                }
                else {
                    final CharSequence[] options = {"Take Photo", "Choose From Gallery","Choose Document","Cancel"};
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                    builder.setTitle("Select Option");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals("Take Photo")) {
                                dialog.dismiss();
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, PICK_IMAGE_CAMERA);
                            } else if (options[item].equals("Choose From Gallery")) {
                                dialog.dismiss();
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);

                            } else if (options[item].equals("Choose Document")) {
                                browseDocuments();
                            } else if (options[item].equals("Cancel")) {
                                dialog.dismiss();
                            }
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            } else

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSION_CAMERA);
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        } else {
                            // No explanation needed; request the permission
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
                        }
                    }
                    else {
                        final CharSequence[] options = {"Take Photo", "Choose From Gallery","Choose Document","Cancel"};
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                        builder.setTitle("Select Option");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals("Take Photo")) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(intent, PICK_IMAGE_CAMERA);
                                } else if (options[item].equals("Choose From Gallery")) {
                                    dialog.dismiss();
                                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                                } else if (options[item].equals("Choose Document")) {
                                    browseDocuments();
                                } else if (options[item].equals("Cancel")) {
                                    dialog.dismiss();
                                }
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        inputStreamImg = null;
        if (requestCode == PICK_IMAGE_CAMERA) {
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
                }
            }
            else {
                if (data!= null) {
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                    destination = new File(Environment.getExternalStorageDirectory() + "/" +
                            getString(R.string.app_name), "IMG_" + System.currentTimeMillis() + ".jpg");
                    FileOutputStream fo;
                    try {
                        if (!destination.getParentFile().exists()) {
                            destination.getParentFile().mkdirs();
                        }
                        if (!destination.exists()) {
                            destination.createNewFile();
                        }
                        fo = new FileOutputStream(destination);
                        fo.write(bytes.toByteArray());
                        fo.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imgPath = destination.getAbsolutePath();
                    destination = new File(imgPath);
                    mTxtAttchment.setText(imgPath);
                }else {
                    Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if (requestCode == PICK_IMAGE_GALLERY) {
            if (data!=null) {
                Uri selectedImage = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                    imgPath = getRealPathFromURI(selectedImage);
                    destination = new File(imgPath.toString());
                    mTxtAttchment.setText(imgPath);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(this, "No image selected from gallery", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == PICK_DOCUMRNT_GALLERY) {
            if (data!= null) {
                Uri uri = data.getData();
                selectedFilePath = FileUtils.getPath(this, uri);
                destination = new File(selectedFilePath.toString());
                mTxtAttchment.setText(selectedFilePath);
            }else {
                Toast.makeText(this, "No Document selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void browseDocuments(){

//        final Intent fsIntent = new Intent();
//        fsIntent.setType("*/*");
//        fsIntent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(fsIntent, PICK_DOCUMRNT_GALLERY);

        String[] mimetypes = {
                "application/*",
                //"audio/*",
                "font/*",
                //"image/*",
                "message/*",
                "model/*",
                "multipart/*",
                "text/*",
                //"video/*"
        };
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes); //Important part here
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_DOCUMRNT_GALLERY);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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

    private void reset(){
        destination=null;
        imagePath="";
        et_Dis.setText("");
        et_subject.setText("");
        txt_client.setText("");
        txt_product.setText("");
        txt_topic.setText("");
        mTxtAttchment.setText("");
        ID_Client="";
        ID_Product="";
        ID_Topic="";
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
                Intent i = new Intent(TicketCreationActivity.this, HomeActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void ticketNumberGenerate(){
        if (new InternetUtil(getApplicationContext()).isInternetOn()) {
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
                    requestObject1.put("FK_Company", Config.FK_Company);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.getTicketNumberGenerate(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            JSONObject jObject = new JSONObject(response.body());
                            JSONObject jobj = jObject.getJSONObject("TicketNumberGenerate");
                            if (jObject.getString("StatusCode").equals("0")) {
                                txt_ticket_no.setText(jobj.getString("TicketNo"));
                            }
                            else {
                                Toast.makeText(getApplicationContext(),jobj.getString("ResponseMessage"),Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
        }
    }

    private String getDateTime(String dateForm) {
        DateFormat dateFormat = new SimpleDateFormat(dateForm);
        Date date = new Date();
        return dateFormat.format(date);
    }
}