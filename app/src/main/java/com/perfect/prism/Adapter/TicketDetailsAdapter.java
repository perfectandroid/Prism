package com.perfect.prism.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.perfect.prism.Activity.DownloadActivity;
import com.perfect.prism.Activity.TicketDetailsActivity;
import com.perfect.prism.R;
import com.perfect.prism.Utility.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TicketDetailsAdapter extends RecyclerView.Adapter {

    JSONArray jsonArray;
    JSONObject jsonObject=null;
    Context context;
    String Attachment;
    public TicketDetailsAdapter(Context context, JSONArray jsonArray) {
        this.context=context;
        this.jsonArray=jsonArray;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.ticket_chat_details, parent, false);
        vh = new MainViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        try {
            if (holder instanceof MainViewHolder) {
                jsonObject=jsonArray.getJSONObject(position);
                Log.e("TicketDetailsAdapter","jsonObject   "+jsonObject);
                if(jsonObject.getInt("UserCode")>0){
                    Log.e("TicketDetailsAdapter","jsonObject   62");
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View inflatedLayout = inflater.inflate(R.layout.chatsender, null, false);
                    ((MainViewHolder)holder).llChat.addView(inflatedLayout);
                    TextView textView = (TextView) inflatedLayout.findViewById(R.id.tv_chat_meessage);
                    textView.setText(jsonObject.getString("Description"));
                    TextView tv_chat_client = (TextView) inflatedLayout.findViewById(R.id.tv_chat_client);
                    tv_chat_client.setText(jsonObject.getString("CliName"));
                    TextView tv_chat_time = (TextView) inflatedLayout.findViewById(R.id.tv_chat_time);
                    tv_chat_time.setText(setDateFormat(jsonObject.getString("EnteredOn")));
                  //  tv_chat_time.setText(jsonObject.getString("EnteredOn"));
                    TextView tv_status = (TextView) inflatedLayout.findViewById(R.id.tv_status);
                    if(!jsonObject.getString("TickStatus").isEmpty()){
                    tv_status.setText("Status : "+jsonObject.getString("TickStatus"));
                    }else {tv_status.setVisibility(View.GONE);}
                    TextView tv_hrtaken = (TextView) inflatedLayout.findViewById(R.id.tv_hrtaken);
                    if(!jsonObject.getString("HourTaken").isEmpty()) {
                        tv_hrtaken.setText("Hour Taken : " + jsonObject.getString("HourTaken"));
                    }else {tv_hrtaken.setVisibility(View.GONE);}
                    TextView tv_attach = (TextView) inflatedLayout.findViewById(R.id.tv_attach);
                    TextView tv_attachtitle = (TextView) inflatedLayout.findViewById(R.id.tv_attachtitle);
                    if(!jsonObject.getString("AttachmentDetails").isEmpty()) {
                        Attachment = jsonObject.getString("AttachmentDetails");
                        Attachment = Attachment.substring(Attachment.indexOf(">") + 1);
                        Attachment = Attachment.substring(0, Attachment.indexOf("<"));
                        tv_attach.setText(Attachment);
                    }else {
                        tv_attach.setVisibility(View.GONE);
                        tv_attachtitle.setVisibility(View.GONE);
                    }
                    TextView tv_AgentNotes = (TextView) inflatedLayout.findViewById(R.id.tv_AgentNotes);
                    if(!jsonObject.getString("AgentNotes").isEmpty()) {
                        tv_AgentNotes.setText("Agent Notes : " + jsonObject.getString("AgentNotes"));
                    }else {tv_AgentNotes.setVisibility(View.GONE);}
                    tv_attach.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    tv_attach.setTextColor(ContextCompat.getColor(context, R.color.perple));
                    tv_attach.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i=new Intent(context, DownloadActivity.class);
                            i.putExtra("attachedfile", Config.IMAGE_URL+Attachment);
                            context.startActivity(i);
                        }
                    });
                }
                if (jsonObject.getInt("AgentFrom")!=0&&jsonObject.getInt("AgentTo")!=0){
                    Log.e("TicketDetailsAdapter","jsonObject   108");
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View inflatedLayout = inflater.inflate(R.layout.chat_assign, null, false);
                    ((MainViewHolder) holder).llChat.addView(inflatedLayout);
                    TextView tvAssignby = (TextView) inflatedLayout.findViewById(R.id.tvAssignby);
                    tvAssignby.setText(jsonObject.getString("AgentFromName"));
                    TextView tvAssignto = (TextView) inflatedLayout.findViewById(R.id.tvAssignto);
                    tvAssignto.setText(jsonObject.getString("AgentToName"));
                    TextView tvDate = (TextView) inflatedLayout.findViewById(R.id.tvDate);

                    TextView tvTime = (TextView) inflatedLayout.findViewById(R.id.tvTime);
                    tvTime.setText("Time To Complete : "+jsonObject.getString("TimeToComplete"));

                   tvDate.setText(setDateFormat(jsonObject.getString("EnteredOn")));
               //  tvDate.setText(jsonObject.getString("EnteredOn"));
                    TextView tvAgentNote = (TextView) inflatedLayout.findViewById(R.id.tvAgentNote);
                    if(!jsonObject.getString("AgentNotes").isEmpty()) {
                        tvAgentNote.setText(jsonObject.getString("AgentNotes"));
                    }else {tvAgentNote.setVisibility(View.GONE);}
                }
                if(jsonObject.getInt("AgentCode")>0&&jsonObject.getInt("AgentFrom")==0&&jsonObject.getInt("AgentTo")==0) {
                    Log.e("TicketDetailsAdapter","jsonObject   126");
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View inflatedLayout = inflater.inflate(R.layout.chat_receiver, null, false);
                    ((MainViewHolder) holder).llChat.addView(inflatedLayout);
                    TextView textView = (TextView) inflatedLayout.findViewById(R.id.tv_chat_clmeessage);
                    textView.setText(jsonObject.getString("Description"));
                    TextView tv_chat_you = (TextView) inflatedLayout.findViewById(R.id.tv_chat_you);
                    tv_chat_you.setText(jsonObject.getString("AgentName"));
                    TextView tv_chat_ctime = (TextView) inflatedLayout.findViewById(R.id.tv_chat_ctime);
                    //tv_chat_ctime.setText(jsonObject.getString("EnteredOn"));
                    tv_chat_ctime.setText(setDateFormat(jsonObject.getString("EnteredOn")));
                    TextView tv_status = (TextView) inflatedLayout.findViewById(R.id.tv_status);
                    if(!jsonObject.getString("TickStatus").isEmpty()) {
                    tv_status.setText("Status : " + jsonObject.getString("TickStatus"));
                    }else {tv_status.setVisibility(View.GONE);}
                    TextView tv_hrtaken = (TextView) inflatedLayout.findViewById(R.id.tv_hrtaken);
                    if(!jsonObject.getString("HourTaken").isEmpty()) {
                    tv_hrtaken.setText("Hour Taken : " + jsonObject.getString("HourTaken"));
                    }else {tv_hrtaken.setVisibility(View.GONE);}
                    TextView tv_attach = (TextView) inflatedLayout.findViewById(R.id.tv_attach);
                    TextView tv_attachtitle = (TextView) inflatedLayout.findViewById(R.id.tv_attachtitle);
                    if(!jsonObject.getString("AttachmentDetails").isEmpty()) {
                        Attachment = jsonObject.getString("AttachmentDetails");
                        Attachment = Attachment.substring(Attachment.indexOf(">") + 1);
                        Attachment = Attachment.substring(0, Attachment.indexOf("<"));
                    tv_attach.setText(Attachment);
                    }else {
                        tv_attach.setVisibility(View.GONE);
                        tv_attachtitle.setVisibility(View.GONE);}
                    tv_attach.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    tv_attach.setTextColor(ContextCompat.getColor(context, R.color.perple));
                    TextView tv_AgentNotes = (TextView) inflatedLayout.findViewById(R.id.tv_AgentNotes);
                    if(!jsonObject.getString("AgentNotes").isEmpty()) {
                    tv_AgentNotes.setText("Agent Notes : " + jsonObject.getString("AgentNotes"));
                    }else {tv_AgentNotes.setVisibility(View.GONE);}
                    tv_attach.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i=new Intent(context, DownloadActivity.class);
                            i.putExtra("attachedfile", Config.IMAGE_URL+Attachment);
                            context.startActivity(i);
                        }
                    });
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
        TextView tv_chat_clmeessage;
        LinearLayout llChat;
        public MainViewHolder(View v) {
            super(v);
            llChat=v.findViewById(R.id.llChat);
        }
    }

    public String setDateFormat(String dateinput){

        DateFormat inputFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy hh:mm a");

        Date date = null;
        try {
            date = inputFormat.parse(dateinput);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dateoutput = outputFormat.format(date);

        return dateoutput;
    }
}
