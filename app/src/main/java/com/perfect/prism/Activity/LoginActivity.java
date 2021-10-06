package com.perfect.prism.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.perfect.prism.R;
import com.perfect.prism.Retrofit.ApiInterface;
import com.perfect.prism.Utility.Config;

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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    String TAG ="LoginActivity";
    private ProgressDialog progressDialog;
    private EditText edtUsername,edtPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        initialisation();
        btnLogin.setOnClickListener( this );
    }
    private void initialisation(){
        edtUsername   = findViewById( R.id.edt_user_name );
        edtPassword   = findViewById(R.id.edt_password);
        btnLogin      = findViewById(R.id.btn_login);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                checkValidation();
                break;
        }

    }

    private void checkValidation() {

        if(edtUsername.getText().toString().isEmpty()){
            edtUsername.setError("Please enter valid username");
        }else if(edtPassword.getText().toString().isEmpty()){
            edtPassword.setError("Please enter valid password");
        }else {
            login(edtUsername.getText().toString(),edtPassword.getText().toString());
        }

    }

    private void login(String userName ,String password) {

        progressDialog = new ProgressDialog(this, R.style.Progress);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(this.getResources()
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

                requestObject1.put("AgUserName", userName);
                requestObject1.put("AgPassword", password);
                requestObject1.put("LoginMode", "1");

                Log.e(TAG,"requestObject1  130  "+requestObject1);
                Log.e(TAG,"requestObject1  130  "+Config.BASEURL);

            } catch (Exception e) {

                e.printStackTrace();
                progressDialog.dismiss();
            }
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestObject1.toString());
            Call<String> call = apiService.getLogin(body);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        progressDialog.dismiss();
                        Log.e(TAG,"requestObject1   144    "+response.body());
                        JSONObject jObject = new JSONObject(response.body());
                        String StatusCode = jObject.getString("StatusCode");
                        if (StatusCode.equals("1")) {
                            JSONObject jmember = jObject.getJSONObject("LogInfo");
                            SharedPreferences agentnameSP = getApplicationContext().getSharedPreferences(Config.SHARED_PREF1, 0);
                            SharedPreferences.Editor agentnameEditer = agentnameSP.edit();
                            agentnameEditer.putString("agentName", jmember.getString("Agent_Name"));
                            agentnameEditer.commit();

                            Intent i = new Intent(LoginActivity.this, OtpEnterActivity.class);
                            i.putExtra("Agent_ID", jmember.getString("Agent_ID"));
                            i.putExtra("OTPRefNo", jmember.getString("OTPRefNo"));
                            i.putExtra("Agent_Name", jmember.getString("Agent_Name"));
                            i.putExtra("Token", jmember.getString("Token"));
                            startActivity(i);

                        } else {
                            setAlert("Invalid username or password");
                            edtPassword.setText("");
                            edtUsername.setText("");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG,"Exception   169    "+e.toString());
                        progressDialog.dismiss();
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.e(TAG,"requestObject1   176    "+t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
            Log.e(TAG,"Exception   182    "+e.toString());
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

    public void setAlert(String strMsg){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage(strMsg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //clearall();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
