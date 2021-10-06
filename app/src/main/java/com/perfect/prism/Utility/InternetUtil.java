package com.perfect.prism.Utility;

import android.content.Context;
import android.net.ConnectivityManager;

public class InternetUtil {

	Context context;

	public InternetUtil(Context context) {
		this.context = context;
	}

	String TAG = "InternetUtil";

	public final boolean isInternetOn() {
		ConnectivityManager connec = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED
				|| connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING
				|| connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING
				|| connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
			return true;
		} else if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED
				|| connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
			return false;
		}
		return false;
	}

	public Boolean isOnline() {
		try {
			Process p1 = Runtime.getRuntime().exec("ping -c 1 www.google.com");
			int returnVal = p1.waitFor();
			boolean reachable = (returnVal == 0);
			return reachable;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
