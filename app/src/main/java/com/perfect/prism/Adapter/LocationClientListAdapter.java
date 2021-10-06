package com.perfect.prism.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.perfect.prism.Activity.LocationReportActivity;
import com.perfect.prism.DB.DBHandler;
import com.perfect.prism.Model.AgentDetailsModel;
import com.perfect.prism.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class LocationClientListAdapter extends RecyclerView.Adapter<LocationClientListAdapter.ViewHolder> {

	Context mContext;
	LayoutInflater inflater;
	private ArrayList<String> arraylist;
	ArrayList<AgentDetailsModel> locationlist = new ArrayList<>();

	public LocationClientListAdapter(Context context, ArrayList<String> arraylist) {
		mContext = context;
		inflater = LayoutInflater.from(mContext);
		this.arraylist = arraylist;

	}
	public class ViewHolder extends RecyclerView.ViewHolder {
		TextView client_name;
		LinearLayout lnLayout;
		ViewHolder(View itemView) {
			super(itemView);
			client_name = itemView.findViewById(R.id.client_name);
			lnLayout = itemView.findViewById(R.id.lnLayout);
		}
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		final View view = inflater.from(viewGroup.getContext())
				.inflate(R.layout.location_report_clients, viewGroup, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull final ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {


		String attributes =arraylist.get(i);
			String substr = " idClient ";
			String before = attributes.substring(0, attributes.indexOf(substr));
			final String after = attributes.substring(attributes.indexOf(substr) + substr.length());

		viewHolder.client_name .setText((i+1)+") "+before);
		viewHolder.lnLayout.setTag(i);
		viewHolder.lnLayout.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			DBHandler db=new DBHandler(mContext);
			locationlist = new ArrayList<>(db.getAllLocationReport(after));
			try {
				Gson gson = new Gson();
				String listString = gson.toJson(locationlist,new TypeToken<ArrayList<AgentDetailsModel>>() {}.getType());
				JSONArray jarray =  new JSONArray(listString);
				showResult(jarray, arraylist.get(i));
			}catch (Exception e){
			}

		}
	});
	}

	@Override
	public int getItemCount() {
		return arraylist.size();
	}

	private  void showResult(JSONArray jarray, String site){
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
		LayoutInflater inflater1 = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater1.inflate(R.layout.location_report_popup, null);
		Button ok = layout.findViewById(R.id.back_ok);
		RecyclerView rcyLocation = layout.findViewById(R.id.rcyLocation);
		TextView txt_header_client_name = layout.findViewById(R.id.txt_header_client_name);
		TextView txt_aging_days = layout.findViewById(R.id.txt_aging_days);
		txt_header_client_name.setText(site);
		Date c = Calendar.getInstance().getTime();
		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
		String formattedDate = df.format(c);
		String date = "Location Report "+" :"+formattedDate;
		txt_aging_days.setText(date);
		GridLayoutManager lLayout = new GridLayoutManager(mContext, 1);
		rcyLocation.setLayoutManager(lLayout);
		rcyLocation.setHasFixedSize(true);
		LocationReportAdapter adapter = new LocationReportAdapter(mContext, jarray,"1");
		rcyLocation.setAdapter(adapter);
		builder.setView(layout);
		final android.app.AlertDialog alertDialog = builder.create();
		ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				alertDialog.dismiss();
			}
		});
		alertDialog.show();

	}

}