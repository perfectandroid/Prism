package com.perfect.prism.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.perfect.prism.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AgentTicketReportListActivity extends AppCompatActivity {

    String responsearray,FromDate,ToDate,Month,Year,Months;
    JSONArray jarray;
    TextView contentDate,tvTotalTicket,tvOpenedTicket,tvClosedTicket,tvResolvedTickets,tvSoftwarePending,txt_agent_name;
    TextView tvOpeningBalance,tvReceived,tvAssigned,tvResolve,tvReturned,tvClientSideWaiting,tvAgentPending,tvActualPending;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_ticket_report_list);
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

        //akn new
        tvOpeningBalance =findViewById(R.id.tvOpeningBalance);
        tvReceived =findViewById(R.id.tvReceived);
        tvAssigned =findViewById(R.id.tvAssigned);
        tvResolve =findViewById(R.id.tvResolve);
        tvReturned =findViewById(R.id.tvReturned);
        tvClientSideWaiting =findViewById(R.id.tvClientSideWaiting);
        tvAgentPending =findViewById(R.id.tvAgentPending);
        tvActualPending =findViewById(R.id.tvActualPending);

    }

    private void viewSettings() {
        try {
            txt_agent_name.setText(jarray.getJSONObject(0).getString("AgentName"));
            tvTotalTicket.setText(":  "+ jarray.getJSONObject(0).getString("TotalTicket"));
            tvOpenedTicket.setText(":  "+ jarray.getJSONObject(0).getString("OpenedTickets"));
            tvClosedTicket.setText(":  "+ jarray.getJSONObject(0).getString("ClosedTickets"));
            tvResolvedTickets.setText(":  "+ jarray.getJSONObject(0).getString("ResolvedTickets"));
            tvSoftwarePending.setText(":  "+ jarray.getJSONObject(0).getString("SoftwarePending"));

            //akn

            tvOpeningBalance.setText(":  "+ jarray.getJSONObject(0).getString("OpeningBalance"));
            tvReceived.setText(":  "+ jarray.getJSONObject(0).getString("Received"));
            tvAssigned.setText(":  "+ jarray.getJSONObject(0).getString("Assigned"));
            tvResolve.setText(":  "+ jarray.getJSONObject(0).getString("Resolve"));
            tvReturned.setText(":  "+ jarray.getJSONObject(0).getString("Returned"));
            tvClientSideWaiting .setText(":  "+ jarray.getJSONObject(0).getString("ClientSideWaiting"));
            tvAgentPending.setText(":  "+ jarray.getJSONObject(0).getString("AgentPending"));
            tvActualPending.setText(":  "+ jarray.getJSONObject(0).getString("ActualPending"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
