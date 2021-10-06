package com.perfect.prism.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.perfect.prism.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AgentWiseReportAdapter extends RecyclerView.Adapter {

    JSONArray jsonArray;
    JSONObject jsonObject=null;
    Context context;
    String FirstCategoryName, ID_CategoryFirst;

    public AgentWiseReportAdapter(Context context, JSONArray jsonArray) {
        this.context=context;
        this.jsonArray=jsonArray;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.agentwise_report_detail, parent, false);
        vh = new MainViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            jsonObject=jsonArray.getJSONObject(position);
            if (holder instanceof MainViewHolder) {
                ((MainViewHolder)holder).txt_product_name.setText((position+1)+") "+jsonObject.getString("AgentName"));
                ((MainViewHolder)holder).txt_total_ticket.setText(jsonObject.getString("TotalTicket"));
                    ((MainViewHolder)holder).txt_opening_ticket.setText(jsonObject.getString("OpenedTickets"));
                ((MainViewHolder)holder).txt_closed_ticket.setText(jsonObject.getString("ClosedTickets"));
                ((MainViewHolder)holder).txt_resolved_ticket.setText(jsonObject.getString("ResolvedTickets"));
                ((MainViewHolder)holder).txt_software_pending.setText(jsonObject.getString("SoftwarePending"));

                ((MainViewHolder)holder).txt_open_balance.setText(jsonObject.getString("OpeningBalance"));
                ((MainViewHolder)holder).txt_receive.setText(jsonObject.getString("Received"));
                ((MainViewHolder)holder).txt_assigned.setText(jsonObject.getString("Assigned"));
                ((MainViewHolder)holder).txt_resolved.setText(jsonObject.getString("Resolve"));
                ((MainViewHolder)holder).txt_retrnd.setText(jsonObject.getString("Returned"));
                ((MainViewHolder)holder).txt_sftwr_pending.setText(jsonObject.getString("SoftwarePending"));
                ((MainViewHolder)holder).txt_client_wait.setText(jsonObject.getString("ClientSideWaiting"));
                ((MainViewHolder)holder).txt_closed.setText(jsonObject.getString("ClosedTickets"));
                ((MainViewHolder)holder).txt_agent_pend.setText(jsonObject.getString("AgentPending"));
                ((MainViewHolder)holder).txt_actual_pend.setText(jsonObject.getString("ActualPending"));

               // txt_open_balance,txt_receive,txt_assigned,txt_resolved,txt_retrnd,txt_sftwr_pending,txt_client_wait
                //txt_closed,txt_agent_pend,txt_actual_pend

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
        TextView txt_product_name,txt_total_ticket,txt_opening_ticket,txt_closed_ticket,txt_resolved_ticket,txt_software_pending;
        TextView txt_open_balance,txt_receive,txt_assigned,txt_resolved,txt_retrnd,txt_sftwr_pending,txt_client_wait,txt_closed,txt_agent_pend,txt_actual_pend;
        public MainViewHolder(View v) {
            super(v);
            txt_product_name=v.findViewById(R.id.txt_product_name);
            txt_total_ticket=v.findViewById(R.id.txt_total_ticket);
            txt_opening_ticket=v.findViewById(R.id.txt_opening_ticket);
            txt_closed_ticket=v.findViewById(R.id.txt_closed_ticket);
            txt_resolved_ticket=v.findViewById(R.id.txt_resolved_ticket);
            txt_software_pending=v.findViewById(R.id.txt_software_pending);

            txt_open_balance=v.findViewById(R.id.txt_open_balance);
            txt_receive=v.findViewById(R.id.txt_received);
            txt_assigned=v.findViewById(R.id.txt_assigned);
            txt_resolved=v.findViewById(R.id.txt_resolved);
            txt_retrnd=v.findViewById(R.id.txt_retrnd);
            txt_client_wait=v.findViewById(R.id.txt_client_wait);
            txt_sftwr_pending=v.findViewById(R.id.txt_sftwr_pending);
            txt_client_wait=v.findViewById(R.id.txt_client_wait);
            txt_closed=v.findViewById(R.id.txt_closed);
            txt_agent_pend=v.findViewById(R.id.txt_agent_pend);
            txt_actual_pend=v.findViewById(R.id.txt_actual_pend);

            // txt_open_balance,txt_receive,txt_assigned,txt_resolved,txt_retrnd,txt_sftwr_pending,txt_client_wait
            //txt_closed,txt_agent_pend,txt_actual_pend
        }
    }


}
