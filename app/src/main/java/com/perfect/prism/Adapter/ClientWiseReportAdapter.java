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

public class ClientWiseReportAdapter extends RecyclerView.Adapter {

    JSONArray jsonArray;
    JSONObject jsonObject=null;
    Context context;
    String FirstCategoryName, ID_CategoryFirst;

    public ClientWiseReportAdapter(Context context, JSONArray jsonArray) {
        this.context=context;
        this.jsonArray=jsonArray;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.agentwise_report_detail_new, parent, false);
        vh = new MainViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            jsonObject=jsonArray.getJSONObject(position);
            if (holder instanceof MainViewHolder) {
//                ((MainViewHolder)holder).txt_product_name.setText((position+1)+") "+jsonObject.getString("ClientName"));
//                ((MainViewHolder)holder).txt_total_ticket.setText(jsonObject.getString("TotalTicket"));
//                ((MainViewHolder)holder).txt_opening_ticket.setText(jsonObject.getString("OpenedTickets"));
//                ((MainViewHolder)holder).txt_closed_ticket.setText(jsonObject.getString("ClosedTickets"));
//                ((MainViewHolder)holder).txt_resolved_ticket.setText(jsonObject.getString("ResolvedTickets"));
//                ((MainViewHolder)holder).txt_software_pending.setText(jsonObject.getString("SoftwarePending"));

                ((MainViewHolder)holder).tv_ClientName.setText((position+1)+") "+jsonObject.getString("ClientName"));
                ((MainViewHolder)holder).tv_TotalTicket.setText(jsonObject.getString("TotalTicket"));
                        ((MainViewHolder)holder).tv_OpenedTickets.setText(jsonObject.getString("OpenedTickets"));
                        ((MainViewHolder)holder). tv_ResolvedTickets.setText(jsonObject.getString("ResolvedTickets"));
                        ((MainViewHolder)holder). tv_ClosedTickets.setText(jsonObject.getString("ClosedTickets"));
                        ((MainViewHolder)holder).tv_ClientOverDueTickets.setText(jsonObject.getString("ClientOverDueTickets"));
                        ((MainViewHolder)holder).tv_SoftwarePending.setText(jsonObject.getString("SoftwarePending"));
                        ((MainViewHolder)holder).tv_ID_Client.setText(jsonObject.getString("ID_Client"));

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
//        TextView txt_product_name,txt_total_ticket,txt_opening_ticket,txt_closed_ticket,txt_resolved_ticket,txt_software_pending;
        TextView tv_ClientName,
        tv_TotalTicket,
                tv_OpenedTickets,
        tv_ResolvedTickets,
                tv_ClosedTickets,
        tv_ClientOverDueTickets,
                tv_SoftwarePending,
        tv_ID_Client;
        public MainViewHolder(View v) {
            super(v);
//            txt_product_name=v.findViewById(R.id.txt_product_name);
//            txt_total_ticket=v.findViewById(R.id.txt_total_ticket);
//            txt_opening_ticket=v.findViewById(R.id.txt_opening_ticket);
//            txt_closed_ticket=v.findViewById(R.id.txt_closed_ticket);
//            txt_resolved_ticket=v.findViewById(R.id.txt_resolved_ticket);
//            txt_software_pending=v.findViewById(R.id.txt_software_pending);

            tv_ClientName=v.findViewById(R.id.tv_ClientName);
                    tv_TotalTicket=v.findViewById(R.id.tv_TotalTicket);
            tv_OpenedTickets=v.findViewById(R.id.tv_OpenedTickets);
                    tv_ResolvedTickets=v.findViewById(R.id.tv_ResolvedTickets);
            tv_ClosedTickets=v.findViewById(R.id.tv_ClosedTickets);
                    tv_ClientOverDueTickets=v.findViewById(R.id.tv_ClientOverDueTickets);
            tv_SoftwarePending=v.findViewById(R.id.tv_SoftwarePending);
                    tv_ID_Client=v.findViewById(R.id.tv_ID_Client);
        }
    }


}
