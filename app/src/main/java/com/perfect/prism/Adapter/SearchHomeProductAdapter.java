package com.perfect.prism.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.perfect.prism.Model.ProductHomeModel;
import com.perfect.prism.R;

import java.util.ArrayList;

public class SearchHomeProductAdapter extends BaseAdapter {

	String TAG="SearchHomeProductAdapter";
	Context mContext;
	LayoutInflater inflater;
	private ArrayList<ProductHomeModel> arraylist;

	public SearchHomeProductAdapter(Context context, ArrayList<ProductHomeModel> arraylist) {
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
	public ProductHomeModel getItem(int position) {
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
			view = inflater.inflate(R.layout.item_client_home_adaptor, null);
			holder.tvPrdName = (TextView) view.findViewById(R.id.tvPrdName);
			holder.ticket_total_count = (TextView) view.findViewById(R.id.ticket_total_count);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		Log.e(TAG,"arraylist_size  58   "+arraylist.size());
//		holder.tvPrdName.setText(arraylist.get(position).getProductName()+"\nNo of tickets : "+arraylist.get(position).getProductCount());
		holder.tvPrdName.setText(arraylist.get(position).getProductName());
		holder.ticket_total_count.setText(arraylist.get(position).getProductCount());
		return view;
	}

}