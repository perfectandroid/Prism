package com.perfect.prism.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.perfect.prism.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientTicketReportListActivity extends AppCompatActivity {

    String responsearray,FromDate,ToDate,Month,Year,Months;
    JSONArray jarray;
    TextView contentDate,tvTotalTicket,tvOpenedTicket,tvClosedTicket,tvResolvedTickets,txt_agent_name;
    TextView  tvSoftwarePending, tvClientOverDueTickets, tvID_Client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_ticket_report_list_new);
        try {
            Intent in = getIntent();
            responsearray  = in.getStringExtra("jarray");
            jarray = new JSONArray(responsearray);

            initiateViews();

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
        viewSettings();
    }
    private void initiateViews() {
        contentDate=findViewById(R.id.txt_agent_days);
        tvTotalTicket=findViewById(R.id.tvTotalTicket);
        tvOpenedTicket=findViewById(R.id.tvOpenedTicket);
        tvClosedTicket=findViewById(R.id.tvClosedTicket);
        tvResolvedTickets=findViewById(R.id.tvResolvedTickets);
        tvSoftwarePending =findViewById(R.id.tvSoftwarePending);
        txt_agent_name =findViewById(R.id.txt_agent_name);
        tvClientOverDueTickets =findViewById(R.id.tvClientOverDueTickets);
        tvID_Client =findViewById(R.id.tvID_Client);
    }

    private void viewSettings() {
        try {
            txt_agent_name.setText(jarray.getJSONObject(0).getString("ClientName"));
            tvTotalTicket.setText(":  "+ jarray.getJSONObject(0).getString("TotalTicket"));
            tvOpenedTicket.setText(":  "+ jarray.getJSONObject(0).getString("OpenedTickets"));
            tvClosedTicket.setText(":  "+ jarray.getJSONObject(0).getString("ClosedTickets"));
            tvResolvedTickets.setText(":  "+ jarray.getJSONObject(0).getString("ResolvedTickets"));
            tvSoftwarePending.setText(":  "+ jarray.getJSONObject(0).getString("SoftwarePending"));
            tvClientOverDueTickets.setText(":  "+ jarray.getJSONObject(0).getString("ClientOverDueTickets"));
            tvID_Client.setText(":  "+ jarray.getJSONObject(0).getString("ID_Client"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
