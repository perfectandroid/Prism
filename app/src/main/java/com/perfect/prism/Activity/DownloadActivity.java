package com.perfect.prism.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.perfect.prism.R;

public class DownloadActivity extends AppCompatActivity {

    String TAG="DownloadActivity";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        Intent in = getIntent();
        String attachedfile = in.getStringExtra("attachedfile");
        Log.e(TAG,"attachedfile    "+attachedfile);

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        progressDialog = new ProgressDialog(DownloadActivity.this, R.style.Progress);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(getResources()
                .getDrawable(R.drawable.progress));
        progressDialog.show();
        Log.e("akn","image"+attachedfile);
        if(attachedfile.indexOf(".jpg")!=-1||attachedfile.indexOf(".jpeg")!=-1||attachedfile.indexOf(".png")!=-1||attachedfile.indexOf(".gif")!=-1) {
            webView.setWebViewClient(new SSLTolerentWebViewClient());
            webView.loadUrl(attachedfile);
        }else {
            String url = "http://docs.google.com/viewer?embedded=true&url=" + attachedfile;
            webView.setWebViewClient(new SSLTolerentWebViewClient());
            webView.loadUrl(url);
        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                progressDialog.dismiss();
            }
        }, 4000);
    }

    private class SSLTolerentWebViewClient extends WebViewClient {
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    }



}
