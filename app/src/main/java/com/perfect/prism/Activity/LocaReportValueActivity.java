package com.perfect.prism.Activity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.perfect.prism.Adapter.LocationClientListAdapter;
import com.perfect.prism.Adapter.LocationReportAdapter;
import com.perfect.prism.Model.AgentDetailsModel;
import com.perfect.prism.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LocaReportValueActivity extends AppCompatActivity {

    String jobjString, fromDate;
    private LinearLayout mLnrReport;
    private AgentDetailsModel mAgentDetails;
    private RecyclerView rcyAgentLocation;
    //private AgentLocationAdapter adapter;
    private TextView mTxtDate;
    private CardView mHeader;
    private static final String AGENT_ID = "agent_id";
    RecyclerView rcyLocation;
    TextView txtDate;
    String type;
    LinearLayout lnr_assign_ticket,lnr_closed_ticket,lnr_software_pending,lnr_balance;
    View a,b,c,d,e,f,g,vel,vet;
    LinearLayout ticketnumber,ll_status,ll_asssigntime,ll_actual_time,lnr_end_location,lnr_end_time;
    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loca_report_value);
        rcyLocation=findViewById(R.id.rcyLocation);
        Intent in = getIntent();
        jobjString = in.getStringExtra("jobjString");
        fromDate = in.getStringExtra("date");
        type = in.getStringExtra("type");

        txtDate=findViewById(R.id.txtDate);



        //liner layouts
        lnr_assign_ticket=findViewById(R.id.lnr_assign_ticket);
        lnr_closed_ticket=findViewById(R.id.lnr_closed_ticket);
        lnr_software_pending=findViewById(R.id.lnr_software_pending);
        lnr_balance=findViewById(R.id.lnr_balance);

        ticketnumber=findViewById(R.id.ticketnumber);
        ll_status=findViewById(R.id.ll_status);
        ll_asssigntime=findViewById(R.id.ll_asssigntime);
        ll_actual_time=findViewById(R.id.ll_actual_time);

        //view
        a=findViewById(R.id.a);
        b=findViewById(R.id.b);
        c=findViewById(R.id.c);
        d=findViewById(R.id.d);

        e=findViewById(R.id.e);
        f=findViewById(R.id.f);
        g=findViewById(R.id.g);

//transid
        if (type.equals("0")){
            e.setVisibility(View.VISIBLE);
            f.setVisibility(View.VISIBLE);

            ticketnumber.setVisibility(View.VISIBLE);
            ll_status.setVisibility(View.VISIBLE);
            ll_asssigntime.setVisibility(View.GONE);
            ll_actual_time.setVisibility(View.GONE);


            a.setVisibility(View.GONE);
            b.setVisibility(View.GONE);
            c.setVisibility(View.GONE);
            d.setVisibility(View.GONE);
            g.setVisibility(View.GONE);

            lnr_assign_ticket.setVisibility(View.GONE);
            lnr_closed_ticket.setVisibility(View.GONE);
            lnr_software_pending.setVisibility(View.GONE);
            lnr_balance.setVisibility(View.GONE);


        }
        else if (type.equals("1")){
            e.setVisibility(View.VISIBLE);
            f.setVisibility(View.VISIBLE);

            ticketnumber.setVisibility(View.VISIBLE);
            ll_status.setVisibility(View.VISIBLE);
            ll_asssigntime.setVisibility(View.GONE);
            ll_actual_time.setVisibility(View.GONE);


            a.setVisibility(View.GONE);
            b.setVisibility(View.GONE);
            c.setVisibility(View.GONE);
            d.setVisibility(View.GONE);
            g.setVisibility(View.GONE);

            lnr_assign_ticket.setVisibility(View.GONE);
            lnr_closed_ticket.setVisibility(View.GONE);
            lnr_software_pending.setVisibility(View.GONE);
            lnr_balance.setVisibility(View.GONE);
        }

        Log.d("Result of location report",jobjString);
      //  Toast.makeText(getApplicationContext(),"Result:"+jobjString,Toast.LENGTH_LONG).show();
        try {
            JSONObject jObject = new JSONObject(jobjString);
            JSONArray jarray = jObject.getJSONArray("AgentLocationDetailsList");
            rcyLocation.setLayoutManager(new LinearLayoutManager(LocaReportValueActivity.this));
            LocationReportAdapter LocReportAdapter = new LocationReportAdapter(LocaReportValueActivity.this,jarray ,type);
            rcyLocation.setAdapter(LocReportAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sourceDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sourceDateFormat.parse(fromDate);
            SimpleDateFormat targetDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            txtDate.setText("Date : "+targetDateFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
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
                Intent i = new Intent(LocaReportValueActivity.this, HomeActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

