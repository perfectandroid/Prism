package com.perfect.prism.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.perfect.prism.Adapter.AgeingReportAdapter;
import com.perfect.prism.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AgeingReportListActivity extends AppCompatActivity {

    String responsearray,FromDate,ToDate,ID_Product,AgeingCount,ProductName;
    JSONArray jarray;
    RecyclerView recy_ageing_report;
    TextView contentDate,ProdctName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ageing_report_list);
        recy_ageing_report=findViewById(R.id.recy_ageing_report);
        contentDate=findViewById(R.id.contentDate);
        ProdctName=findViewById(R.id.ProductName);
        try {
            Intent in = getIntent();
            responsearray  = in.getStringExtra("response");
            FromDate  = in.getStringExtra("FromDate");
            ToDate  = in.getStringExtra("ToDate");
            ID_Product  = in.getStringExtra("ID_Product");
            AgeingCount  = in.getStringExtra("AgeingCount");
            ProductName  = in.getStringExtra("ProductName");
            jarray = new JSONArray(responsearray);
            getReport(jarray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SimpleDateFormat spf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date newFromDate = null, newToDate= null;
        try {
            newFromDate = spf.parse(FromDate);
            newToDate = spf.parse(ToDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf= new SimpleDateFormat("dd MMM yyyy");
        if(ProductName.equals("Choose Product")){
            ProdctName.setText("");
            ProdctName.setVisibility(View.GONE);
        }
        else {
            ProdctName.setVisibility(View.VISIBLE);
            ProdctName.setText(ProductName);
        }
        contentDate.setText("Report from " + spf.format(newFromDate) + " to "+ spf.format(newToDate));

    }

    private void getReport(JSONArray jarray) {
        GridLayoutManager lLayout = new GridLayoutManager(AgeingReportListActivity.this, 1);
        recy_ageing_report.setLayoutManager(lLayout);
        recy_ageing_report.setHasFixedSize(true);
        AgeingReportAdapter adapter = new AgeingReportAdapter(AgeingReportListActivity.this, jarray,FromDate,ToDate,ID_Product,AgeingCount);
        recy_ageing_report.setAdapter(adapter);
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
                Intent i = new Intent(AgeingReportListActivity.this, HomeActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
