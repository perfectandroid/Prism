package com.perfect.prism.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.perfect.prism.Model.ClientHomeModel;
import com.perfect.prism.R;

import java.util.ArrayList;

public class SearchClientHomeAdapter extends BaseAdapter {

	String TAG="SearchClientHomeAdapter";
	Context mContext;
	LayoutInflater inflater;
	private ArrayList<ClientHomeModel> arraylist;

	public SearchClientHomeAdapter(Context context, ArrayList<ClientHomeModel> arraylist) {
		mContext = context;
		inflater = LayoutInflater.from(mContext);
		this.arraylist = arraylist;
	}

	public class ViewHolder {
		TextView tvPrdName;
		TextView ticket_total_count;
}

	@Override
	public int getCount() {
		return arraylist.size();
	}

	@Override
	public ClientHomeModel getItem(int position) {
		return arraylist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		if (view == null) {
			Log.e(TAG,"Start");
			holder = new ViewHolder();
//			view = inflater.inflate(R.layout.searchlist, null);
			view = inflater.inflate(R.layout.item_client_home_adaptor, null);
			holder.tvPrdName = (TextView) view.findViewById(R.id.tvPrdName);
			holder.ticket_total_count = (TextView) view.findViewById(R.id.ticket_total_count);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
//		holder.tvPrdName .setText(arraylist.get(position).getClientName() + "\nNo of Tickets : "+arraylist.get(position).getClientCount());
		holder.tvPrdName .setText(arraylist.get(position).getClientName());
		holder.ticket_total_count.setText(arraylist.get(position).getClientCount());
		return view;
	}

}