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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.perfect.prism.Adapter.BugTypeAdapter;
import com.perfect.prism.Model.BugTypeModel;
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
import java.util.ArrayList;

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

@SuppressWarnings("ALL")
public class TicketRemarkActivity extends AppCompatActivity implements View.OnClickListener {

    String TAG="TicketRemarkActivity";
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1;
    ProgressDialog progressDialog;
    private LinearLayout mBtnSubmit, llreset;
    private ImageView mImgStatus, mImgBugType,mImgAttchment,mImgHoursTaken;
    private TextView mTxtStatus,mTxtBugType,mTxtAttchment,mTxtHoursTaken;
    String BugTypes, ID_Ticket, FKbugType, strStatus;
    BugTypeAdapter sadapter;
    ArrayList<BugTypeModel> searchNamesArrayList = new ArrayList<>();
    String imagePath;
    EditText et_AgentNote, et_Dis;
    private Bitmap bitmap;
    public File destination = null;
    private InputStream inputStreamImg;
    private String imgPath = null;
    private String selectedFilePath = "";
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2, PICK_DOCUMRNT_GALLERY = 3;
    private static final int PERMISSION_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_remark);
        Log.e(TAG,"Start");
        Intent in = getIntent();
        BugTypes  = in.getStringExtra("BugTypes");
        ID_Ticket  = in.getStringExtra("ID_Ticket");
        initiateViews();
        setRegViews();
    }

    private void initiateViews() {
        mImgStatus = findViewById(R.id.imgStatus);
        mImgBugType = findViewById(R.id.imgBugType);
        mImgAttchment = findViewById(R.id.impic);
        mImgHoursTaken = findViewById(R.id.imgHours_taken);
        mBtnSubmit = findViewById(R.id.submit_datewise);
        mTxtStatus = findViewById(R.id.txt_status);
        mTxtBugType = findViewById(R.id.txt_bug_type);
        mTxtAttchment = findViewById(R.id.txt_attachment);
        mTxtHoursTaken = findViewById(R.id.txt_hours_taken);
        et_AgentNote = findViewById(R.id.et_subject);
        et_Dis = findViewById(R.id.et_Dis);
        llreset = findViewById(R.id.llreset);
    }

    private void setRegViews() {
        mImgStatus.setOnClickListener(this);
        mImgBugType.setOnClickListener(this);
        mImgAttchment.setOnClickListener(this);
        mImgHoursTaken.setOnClickListener(this);
        mBtnSubmit.setOnClickListener(this);
        llreset.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.imgStatus:
                getStatus();
                break;
            case R.id.llreset:
                reset();
                break;
            case R.id.imgBugType:
                getBug();
                break;
            case R.id.impic:
                selectImage();
                break;
            case R.id.imgHours_taken:
                break;
            case R.id.submit_datewise:


                if(mTxtStatus.getText().toString().isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Please choose status",Snackbar.LENGTH_SHORT).show();
                } else {
                    if(mTxtBugType.getText().toString().isEmpty()){
                        Snackbar.make(findViewById(android.R.id.content),"Please choose bug type", Snackbar.LENGTH_SHORT).show();
                    } else {
                        SharedPreferences pref1 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF3, 0);
                        SharedPreferences pref2 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF4, 0);
                        SharedPreferences pref3 = getApplicationContext().getSharedPreferences(Config.LONGI, 0);
                        SharedPreferences pref4 = getApplicationContext().getSharedPreferences(Config.LATI, 0);
                        SharedPreferences pref5 = getApplicationContext().getSharedPreferences(Config.ADDRESS, 0);
                        if(destination==null){
                            addRemarkWithoutImage();
                        }else {
                            JSONObject jdata = new JSONObject();
                            try {
                                 jdata.put("LoginMode", "7");
//                                jdata.put("Mode", "7");
                                jdata.put("Token", pref2.getString("TOKEN", null));
                                jdata.put("ID_Tickets", ID_Ticket);
                                jdata.put("Agent_ID", pref1.getString("Agent_ID", null));
                                jdata.put("AgentTo", "0");
                                jdata.put("FK_BugType", FKbugType);
                                jdata.put("TraStatus", strStatus);
                                jdata.put("Description",  et_Dis.getText().toString());
                                jdata.put("AgentNotes", et_AgentNote.getText().toString());
                                jdata.put("FK_Company", Config.FK_Company);
                                jdata.put("HourTaken", "1");
                                jdata.put("Location_Latitude", pref4.getString("LATI",null));
                                // jdata.put("XmlAttachment","");
                                jdata.put("Location_Longitude", pref3.getString("LONGI",null));
                                //jdata.put("Location_Name", "Calicut");
                                jdata.put("Location_Name",  pref5.getString("ADDRESS",null));
                                Log.e(TAG,"jdata   192 "+jdata);
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            addRemark(destination, jdata.toString());
                        }

                    }
                }

               /* SharedPreferences pref1 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF3, 0);
                SharedPreferences pref2 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF4, 0);
                if(destination==null){
                    addRemarkWithoutImage();
                }else {
                    JSONObject jdata = new JSONObject();
                    try {
                        jdata.put("LoginMode", "7");
                        jdata.put("Token", pref2.getString("TOKEN", null));
                        jdata.put("ID_Tickets", ID_Ticket);
                        jdata.put("Agent_ID", pref1.getString("Agent_ID", null));
                        jdata.put("AgentTo", "0");
                        jdata.put("FK_BugType", FKbugType);
                        jdata.put("TransStatus", strStatus);
                        jdata.put("Description",  et_Dis.getText().toString());
                        jdata.put("AgentNotes", et_AgentNote.getText().toString());
                        jdata.put("FK_Company", Config.FK_Company);
                        jdata.put("HourTaken", "1");
                        jdata.put("Location_Latitude", "154566");
                        jdata.put("Location_Longitude", "65522");
                        jdata.put("Location_Name", "Calicut");
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                addRemark(destination, jdata.toString());
                }*/
                break;
        }
    }

    private void addRemarkWithoutImage() {
        if (new InternetUtil(TicketRemarkActivity.this).isInternetOn()) {
            try{
                progressDialog = new ProgressDialog(TicketRemarkActivity.this, R.style.Progress);
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
                    SharedPreferences pref3 = getApplicationContext().getSharedPreferences(Config.LONGI, 0);
                    SharedPreferences pref4 = getApplicationContext().getSharedPreferences(Config.LATI, 0);
                    SharedPreferences pref5 = getApplicationContext().getSharedPreferences(Config.ADDRESS, 0);
                     requestObject1.put("LoginMode", "7");
//                    requestObject1.put("Mode", "7");
                    requestObject1.put("Token",pref2.getString("TOKEN", null));
                    requestObject1.put("ID_Tickets", ID_Ticket);
                    requestObject1.put("Agent_ID",  pref1.getString("Agent_ID", null));
                    requestObject1.put("AgentTo", "0");
                    requestObject1.put("FK_BugType", FKbugType);
                    requestObject1.put("TransStatus", strStatus);
                    requestObject1.put("Description", et_Dis.getText().toString());
                    requestObject1.put("AgentNotes", et_AgentNote.getText().toString());
                    requestObject1.put("FK_Company",  Config.FK_Company);
                    requestObject1.put("HourTaken", "1");
                    requestObject1.put("XmlAttachment", "1");
                    requestObject1.put("Location_Latitude", pref4.getString("LATI",null));
                    requestObject1.put("Location_Longitude", pref3.getString("LONGI",null));
                    // requestObject1.put("Location_Name", "Calicut");
                    requestObject1.put("Location_Name",  pref5.getString("ADDRESS",null));

                    Log.e(TAG,"requestObject1  280  "+requestObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
                Call<String> call = apiService.addRemarkWithoutImage(body);
                call.enqueue(new Callback<String>() {
                    @Override public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject jObject = new JSONObject(response.body());
                            // JSONObject jobj = jObject.getJSONObject("UpdateTicketAssignInfo");
                            if(jObject.getString("StatusCode").equals("0")){
                                Toast.makeText(TicketRemarkActivity.this,"Remarks added successfully",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(TicketRemarkActivity.this,HomeActivity.class));
                                finish();
                            }else {
                                Toast.makeText(TicketRemarkActivity.this,"Adding remarks has been failed.",Toast.LENGTH_SHORT).show();
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

    private void getStatus() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.status_popup, null);
            ListView listView =  layout.findViewById(R.id.listView);
            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();

            String[] listItem = new String[0];

            SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF5, 0);
            if(pref.getString("IsAcces", null).equals("True")){
                listItem = getResources().getStringArray(R.array.array_technology);
            }
            else{
                listItem = getResources().getStringArray(R.array.array_technology_new);
            }
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, listItem);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    // TODO Auto-generated method stub
                    String value=adapter.getItem(position);
                    //Toast.makeText(getApplicationContext(),value,Toast.LENGTH_SHORT).show();
                    mTxtStatus.setText(value);
                    if(position==0){strStatus="1";}
                    if(position==1){strStatus="2";}
                    if(position==2){strStatus="4";}
                    if(position==3){strStatus="6";}
                    if(position==4){strStatus="3";}
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }catch (Exception e){e.printStackTrace();}
    }

    private void getBug() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater1.inflate(R.layout.bug_popup, null);
            ListView listView =  layout.findViewById(R.id.lv_bugs);
            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();
            searchNamesArrayList = new ArrayList<>();
            JSONArray jsarray = new JSONArray(BugTypes);
            for (int k = 0; k < jsarray.length(); k++) {
                JSONObject jsonObject = jsarray.getJSONObject(k);
                searchNamesArrayList.add(new BugTypeModel(jsonObject.getString("ValueField"),jsonObject.getString("TextField")));
            }
            sadapter = new BugTypeAdapter(this, searchNamesArrayList);
            listView.setAdapter(sadapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mTxtBugType.setText(searchNamesArrayList.get(position).getProductName());
                    FKbugType=searchNamesArrayList.get(position).getID_Product();
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }catch (Exception e){e.printStackTrace();}
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
                if (data != null) {
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
            if (data != null) {
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
            if (data != null) {
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

    private void addRemark(File fileimage, String Jsondata) {
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
                    //   MultipartBody.Part body = MultipartBody.Part.createFormData("XmlAttachment", fileimage.getName(), requestFile);
                    Log.i("Image",fileimage.getName());
                      MultipartBody.Part body = MultipartBody.Part.createFormData("ImgFile", fileimage.getName(), requestFile);
//                    MultipartBody.Part body = MultipartBody.Part.createFormData("XmlAttachment", fileimage.getName(), requestFile);
                    RequestBody JsonData = RequestBody.create(MediaType.parse("text/plain"), Jsondata);
                    Log.e(TAG,"JsonDatass   "+JsonData+"  "+body);

                    Call<String> call = apiService.addRemark(JsonData, body);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                            try {
                                progressDialog.dismiss();
                                Log.e(TAG,"onResponse    "+response.body());
                                JSONObject jObject = new JSONObject(response.body());
                                // JSONObject jobj = jObject.getJSONObject("UpdateTicketAssignInfo");

                                if(jObject.getString("StatusCode").equals("0")){
                                    Toast.makeText(TicketRemarkActivity.this,"Remarks added successfully",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(TicketRemarkActivity.this,HomeActivity.class));
                                    finish();
                                }else {
                                    Toast.makeText(TicketRemarkActivity.this,"Adding remarks has been failed.",Toast.LENGTH_SHORT).show();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e(TAG,"655    "+e.toString());
                                progressDialog.dismiss();
                            }
                        }
                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            progressDialog.dismiss();
                            Log.e(TAG,"662    "+t.getMessage());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Log.e(TAG,"668    "+e.toString());

                }
            }catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG,"673    "+e.toString());
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

    private void reset(){
        imagePath="";
        ID_Ticket="";
        FKbugType="";
        et_Dis.setText("");
        et_AgentNote.setText("");
        mTxtStatus.setText("");
        mTxtBugType.setText("");
        mTxtAttchment.setText("");
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
                Intent i = new Intent(TicketRemarkActivity.this, HomeActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}