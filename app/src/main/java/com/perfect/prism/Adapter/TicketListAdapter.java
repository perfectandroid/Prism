package com.perfect.prism.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.perfect.prism.Activity.TicketDetailsActivity;
import com.perfect.prism.Activity.TicketListActivity;
import com.perfect.prism.Model.ProductModel;
import com.perfect.prism.Model.TicketModel;
import com.perfect.prism.R;
import com.perfect.prism.Utility.Config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TicketListAdapter extends BaseAdapter {

	String TAG="TicketListAdapter";
	String startingStatus;
	Context mContext;
	LayoutInflater inflater;
	private ArrayList<TicketModel> arraylist;
	private String mLoginSubmode;
	private final ClickHandler mClickHandler;
	private final List<String> toStartTicketList = new ArrayList<>();
	private final List<String> toStopTicketList = new ArrayList<>();
	Boolean isclicked=false;

	public TicketListAdapter(Context context, ArrayList<TicketModel> arraylist, ClickHandler handler, String loginSubmode) {
		mContext = context;
		inflater = LayoutInflater.from(mContext);
		this.arraylist = arraylist;
		this.mClickHandler = handler;
		this.mLoginSubmode = loginSubmode;
	}

	public class ViewHolder {
		Button btnTicket_No;
		TextView txtSubject, txtDate, txtSlNo, txt_inbox_count, txt_outbox_count;
		LinearLayout lnrDataTicket;
		CheckBox chk_start_stop;
	}

	@Override
	public int getCount() {
		return arraylist.size();
	}

	@Override
	public TicketModel getItem(int position) {
		return arraylist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.ticket_data_view, null);
			holder.btnTicket_No =  view.findViewById(R.id.btnTicket_No);
			holder.txtSubject =  view.findViewById(R.id.txtSubject);
			holder.txtDate =  view.findViewById(R.id.txtDate);
			holder.txtSlNo =  view.findViewById(R.id.txtSlNo);
			holder.txt_inbox_count =  view.findViewById(R.id.txt_inbox_count);
			holder.txt_outbox_count =  view.findViewById(R.id.txt_outbox_count);
			holder.lnrDataTicket =  view.findViewById(R.id.lnrDataTicket);
			holder.chk_start_stop =  view.findViewById(R.id.chk_start_stop);


			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.btnTicket_No .setText(arraylist.get(position).getTickNo());
		holder.txtSubject .setText(arraylist.get(position).getTickSubject());

		String str =arraylist.get(position).getTickDate();
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String formattedDate = new SimpleDateFormat("dd MMM yy hh:mm a").format(date);
		holder.txtDate .setText(formattedDate);
		holder.txtSlNo .setText(arraylist.get(position).getSlNo());
		holder.txt_inbox_count .setText(arraylist.get(position).getUserCount());
		holder.txt_outbox_count .setText(arraylist.get(position).getAgentCount());

		startingStatus = arraylist.get(position).getStartingstatus();
		Log.e(TAG,"startingStatus  "+startingStatus);
		String caption;
		switch (startingStatus){
			case "0":
				caption = "Start work";
				holder.chk_start_stop.setText(caption);
				break;
			case "1":
				caption = "Stop work";
				holder.chk_start_stop.setText(caption);
				break;
			case "2":
				caption = "Resume work";
				holder.chk_start_stop.setText(caption);
				break;
			default:
				holder.chk_start_stop.setVisibility(View.INVISIBLE);
				break;
		}

        holder.chk_start_stop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				startingStatus = arraylist.get(position).getStartingstatus();
				if ( isChecked ){
					switch (startingStatus) {
						case "0":
						case "2":
							toStartTicketList.add(arraylist.get(position).getID_Tickets());
							break;
						case "1":
							toStopTicketList.add(arraylist.get(position).getID_Tickets());
							break;
						default:
							holder.chk_start_stop.setVisibility(View.GONE);
							break;
					}
				}else {
					switch ( startingStatus ) {
						case "0":
						case "2":
							toStartTicketList.remove( arraylist.get(position).getID_Tickets());
							break;
						case "1":
							toStopTicketList.remove(arraylist.get(position).getID_Tickets());
							break;
						default:
							holder.chk_start_stop.setVisibility(View.GONE);
							break;
					}
				}

				mClickHandler.startStopBtnColorChanger( toStartTicketList.size(), toStopTicketList.size() );
			}
		});
		if ( mLoginSubmode.equals(Config.CLOSED_LOGINMODE ) ){
			holder.chk_start_stop.setVisibility( View.GONE);
		}

		holder.lnrDataTicket.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, TicketDetailsActivity.class);
				intent.putExtra("ID_Tickets",arraylist.get(position).getID_Tickets());
				intent.putExtra("CliName",arraylist.get(position).getCliName());
				intent.putExtra("Startingstatus",arraylist.get(position).getStartingstatus());
//				intent.putExtra("FK_Product",arraylist.get(position).getFK_Product());
				mContext.startActivity(intent);


			}
		});
		return view;
	}

	public void refreshItem( ArrayList<TicketModel> ticketsDetailModelList ){
		if ( ticketsDetailModelList != null ){
			arraylist = ticketsDetailModelList;
			notifyDataSetChanged();
		}
	}
	public List<String> getStartStopList( String startStopFlag ){
		switch (startStopFlag) {
			case "1":
				return toStartTicketList;
			case "2":
				return toStopTicketList;
			default:
				return new ArrayList<>();
		}
	}

	public interface ClickHandler{
		void onClick( TicketModel ticketsDetailModel );
		void startStopBtnColorChanger( int toStartLength, int toStopLength);
	}

	@Override
	public int getViewTypeCount() {
		return getCount();
	}

	@Override
	public int getItemViewType(int position) {
		return position;
	}

}