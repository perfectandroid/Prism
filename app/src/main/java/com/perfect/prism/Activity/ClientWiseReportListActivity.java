package com.perfect.prism.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.perfect.prism.Adapter.AgentWiseReportAdapter;
import com.perfect.prism.Adapter.ClientWiseReportAdapter;
import com.perfect.prism.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientWiseReportListActivity extends AppCompatActivity {

    String responsearray,FromDate,ToDate,Month,Year,Months;
    JSONArray jarray;
    RecyclerView recy_agentwise_report;
    TextView contentDate,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_wise_report_list);
        recy_agentwise_report=findViewById(R.id.recy_agentWise_details_rprt);
        contentDate=findViewById(R.id.txt_days);
        name=findViewById(R.id.name);
        name.setText("Client Name");
        try {
            Intent in = getIntent();
            responsearray  = in.getStringExtra("jarray");
            jarray = new JSONArray(responsearray);
            if(in.getStringExtra("From").equals("Date Sort"))
            {
                FromDate  = in.getStringExtra("FromDate");
                ToDate  = in.getStringExtra("ToDate");

                SimpleDateFormat spf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date newFromDate = null, newToDate= null;
                try {
                    newFromDate = spf.parse(FromDate);
                    newToDate = spf.parse(ToDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                spf= new SimpleDateFormat("dd MMM yyyy");
                contentDate.setText("Report from " + spf.format(newFromDate) + " to "+ spf.format(newToDate));
            }
            else if(in.getStringExtra("From").equals("Specific Month"))
            {
                Month  = in.getStringExtra("Month");
                Year  = in.getStringExtra("Year");

                contentDate.setText("Report on "+Month +" - "+Year);
            }
            else if(in.getStringExtra("From").equals("Last Month"))
            {
                Months  = in.getStringExtra("Months");

                contentDate.setText("Report of last " +Months+ " month");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    getReport(jarray);
    }

    private void getReport(JSONArray jarray) {
        GridLayoutManager lLayout = new GridLayoutManager(ClientWiseReportListActivity.this, 1);
        recy_agentwise_report.setLayoutManager(lLayout);
        recy_agentwise_report.setHasFixedSize(true);
        ClientWiseReportAdapter adapter = new ClientWiseReportAdapter(ClientWiseReportListActivity.this, jarray);
        recy_agentwise_report.setAdapter(adapter);

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
                Intent i = new Intent(ClientWiseReportListActivity.this, HomeActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
