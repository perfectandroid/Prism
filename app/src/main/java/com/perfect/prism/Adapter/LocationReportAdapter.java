package com.perfect.prism.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.perfect.prism.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LocationReportAdapter extends RecyclerView.Adapter {

    JSONArray jsonArray;
    JSONObject jsonObject=null;
    Context context;
    String FirstCategoryName, ID_CategoryFirst;
    String type;

    public LocationReportAdapter(Context context, JSONArray jsonArray,String type) {
        this.context=context;
        this.jsonArray=jsonArray;
        this.type = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.location_wise_detail, parent, false);
        vh = new MainViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            jsonObject=jsonArray.getJSONObject(position);
            if (holder instanceof MainViewHolder) {
                ((MainViewHolder)holder).txt_agent_name.setText(jsonObject.getString("AgentName"));
                ((MainViewHolder)holder).txt_location.setText(jsonObject.getString("Location"));
                ((MainViewHolder)holder).txt_assign_ticket.setText(jsonObject.getString("AssignedTicket"));
                ((MainViewHolder)holder).txt_closed_ticket.setText(jsonObject.getString("ClosedTicket"));
                ((MainViewHolder)holder).txt_software_pending.setText(jsonObject.getString("SoftwarePending"));
                ((MainViewHolder)holder).txt_balance.setText(jsonObject.getString("Balance"));
                ((MainViewHolder)holder).txt_client_name.setText(jsonObject.getString("Site"));
                ((MainViewHolder)holder).txt_stime.setText(jsonObject.getString("StartTime"));

            //single todo
                ((MainViewHolder)holder).txt_ticketno.setText(jsonObject.getString("Ticketno"));
                ((MainViewHolder)holder).txt_ststus.setText(jsonObject.getString("status"));

                ((MainViewHolder)holder).txt_assisttime.setText(jsonObject.getString("StartTime"));
                ((MainViewHolder)holder).txt_actual_time.setText(jsonObject.getString("StartTime"));

                ((MainViewHolder)holder).txt_end_location.setText(jsonObject.getString("EndLocation"));
                ((MainViewHolder)holder).txt_etime.setText(jsonObject.getString("TicketEndTime"));

                Log.e("type","         "+type);
                if (type.equals("0")){
                    ((MainViewHolder)holder).ll_ticketno.setVisibility(View.VISIBLE);
                    ((MainViewHolder)holder).ll_status.setVisibility(View.VISIBLE);
                    ((MainViewHolder)holder).ll_assisttime.setVisibility(View.GONE);
                    ((MainViewHolder)holder).ll_actual_time.setVisibility(View.GONE);

                    ((MainViewHolder)holder).lnr_assign_ticket.setVisibility(View.GONE);
                    ((MainViewHolder)holder).lnr_closed_ticket.setVisibility(View.GONE);
                    ((MainViewHolder)holder).lnr_software_pending.setVisibility(View.GONE);
                    ((MainViewHolder)holder).lnr_balance.setVisibility(View.GONE);

                    ((MainViewHolder)holder).d.setVisibility(View.VISIBLE);
                    ((MainViewHolder)holder).e.setVisibility(View.VISIBLE);
                    ((MainViewHolder)holder).f.setVisibility(View.VISIBLE);

                    ((MainViewHolder)holder).a.setVisibility(View.GONE);
                    ((MainViewHolder)holder).b.setVisibility(View.GONE);
                    ((MainViewHolder)holder).c.setVisibility(View.GONE);

                    ((MainViewHolder)holder).lnr_end_location.setVisibility(View.VISIBLE);
                    ((MainViewHolder)holder).ll_end_time.setVisibility(View.VISIBLE);
                }
                else if (type.equals("1")){

                    ((MainViewHolder)holder).ll_ticketno.setVisibility(View.VISIBLE);
                    ((MainViewHolder)holder).ll_status.setVisibility(View.VISIBLE);
                    ((MainViewHolder)holder).ll_assisttime.setVisibility(View.GONE);
                    ((MainViewHolder)holder).ll_actual_time.setVisibility(View.GONE);

                    ((MainViewHolder)holder).lnr_assign_ticket.setVisibility(View.GONE);
                    ((MainViewHolder)holder).lnr_closed_ticket.setVisibility(View.GONE);
                    ((MainViewHolder)holder).lnr_software_pending.setVisibility(View.GONE);
                    ((MainViewHolder)holder).lnr_balance.setVisibility(View.GONE);

                    ((MainViewHolder)holder).d.setVisibility(View.VISIBLE);
                    ((MainViewHolder)holder).e.setVisibility(View.VISIBLE);
                    ((MainViewHolder)holder).f.setVisibility(View.VISIBLE);

                    ((MainViewHolder)holder).a.setVisibility(View.GONE);
                    ((MainViewHolder)holder).b.setVisibility(View.GONE);
                    ((MainViewHolder)holder).c.setVisibility(View.GONE);

                    ((MainViewHolder)holder).lnr_end_location.setVisibility(View.VISIBLE);
                    ((MainViewHolder)holder).ll_end_time.setVisibility(View.VISIBLE);

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    private class MainViewHolder extends RecyclerView.ViewHolder {
        TextView txt_agent_name,txt_location,txt_assign_ticket,txt_closed_ticket,txt_software_pending,txt_balance,txt_client_name,txt_stime;
        LinearLayout ll_ticketno,ll_status,ll_assisttime,ll_actual_time;
        TextView txt_ticketno,txt_ststus,txt_assisttime,txt_actual_time,txt_end_location,txt_etime;
        View a,b,c,d,e,f;
        LinearLayout lnr_assign_ticket,lnr_closed_ticket,lnr_software_pending,lnr_balance,lnr_end_location,ll_end_time;
        public MainViewHolder(View v) {
            super(v);
            lnr_end_location=v.findViewById(R.id.lnr_end_location);
            ll_end_time=v.findViewById(R.id.ll_end_time);
            txt_end_location=v.findViewById(R.id.txt_end_location);
            txt_etime=v.findViewById(R.id.txt_etime);


            txt_agent_name=v.findViewById(R.id.txt_agent_name);
            txt_location=v.findViewById(R.id.txt_location);
            txt_assign_ticket=v.findViewById(R.id.txt_assign_ticket);
            txt_closed_ticket=v.findViewById(R.id.txt_closed_ticket);
            txt_software_pending=v.findViewById(R.id.txt_software_pending);
            txt_balance=v.findViewById(R.id.txt_balance);
            txt_client_name=v.findViewById(R.id.txt_client_name);
            txt_stime=v.findViewById(R.id.txt_stime);


            a=v.findViewById(R.id.a);
            b=v.findViewById(R.id.b);
            c=v.findViewById(R.id.c);
            d=v.findViewById(R.id.d);
            e=v.findViewById(R.id.e);
            f=v.findViewById(R.id.f);

            ll_ticketno=v.findViewById(R.id.ll_ticketno);
            ll_status=v.findViewById(R.id.ll_status);
            ll_assisttime=v.findViewById(R.id.ll_assisttime);
            ll_actual_time=v.findViewById(R.id.ll_actual_time);

            txt_ticketno=v.findViewById(R.id.txt_ticketno);
            txt_ststus=v.findViewById(R.id.txt_ststus);
            txt_assisttime=v.findViewById(R.id.txt_assisttime);
            txt_actual_time=v.findViewById(R.id.txt_actual_time);


            lnr_assign_ticket=v.findViewById(R.id.lnr_assign_ticket);
            lnr_closed_ticket=v.findViewById(R.id.lnr_closed_ticket);
            lnr_software_pending=v.findViewById(R.id.lnr_software_pending);
            lnr_balance=v.findViewById(R.id.lnr_balance);

        }
    }


}
