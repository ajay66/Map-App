package etg.com.mapfragmentssamp;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class ConnectivityReceiver implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static Context mContext;
	// public static String MAIN_HOST = "http://172.16.0.141/tact/webservices/";
	String MAIN_HOST = "http://202.65.147.156/sshuttle/webservices/";

	public ConnectivityReceiver(Context context) {
		mContext = context;
	}

	public boolean checkInternetConnection() {

		ConnectivityManager con_manager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (con_manager == null) {
			return false;
		} else {
			NetworkInfo[] info = con_manager.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void showCustomDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage(
				"Sorry, Network is not available. Please try again later")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();

	}

	public boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	Typeface getTf_lato_regular() {
		return Typeface.createFromAsset(mContext.getAssets(),
				"fonts/lato-regular.ttf");
	}

	public String getJSONString(String url) {

		String jsonString = null;
		url = MAIN_HOST + url;
		System.out.println("url is" + url);

		try {

			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			post.setEntity(new UrlEncodedFormEntity(pairs));
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				String re = EntityUtils.toString(entity, HTTP.UTF_8);
				jsonString = re.trim();
			}

		} catch (Exception e) {
			Log.e("", "" + e);
		}
		return jsonString;
	}

	public String PostJSONString(String url, String strJson) {

		String jsonString = null;
		url = MAIN_HOST + url;

		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();

			pairs.add(new BasicNameValuePair("employes", strJson));

			post.setEntity(new UrlEncodedFormEntity(pairs));
			HttpResponse response = client.execute(post);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				String re = EntityUtils.toString(entity, HTTP.UTF_8);
				jsonString = re.trim();
			}

		} catch (Exception e) {
			// TODO: handle exception

		}
		return jsonString;
	}
}
