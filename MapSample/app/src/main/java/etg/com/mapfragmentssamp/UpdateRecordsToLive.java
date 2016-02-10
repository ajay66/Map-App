package etg.com.mapfragmentssamp;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.util.Log;

public class UpdateRecordsToLive extends Service {
	int mStartMode;

	IBinder mBinder;
	ConnectivityReceiver conn;
	JSONArray CategoryArray;

	boolean mAllowRebind;

	@Override
	public void onCreate() {

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		conn = new ConnectivityReceiver(getApplicationContext());
		

		if (conn.checkInternetConnection()) {


				new InsertMultipleEmpStagesTask().execute();

		}

		long currentTimeMillis = System.currentTimeMillis(); 
		long nextUpdateTimeMillis = currentTimeMillis + 1// mins
				* DateUtils.MINUTE_IN_MILLIS;//
		Intent serviceIntent = new Intent(this, UpdateRecordsToLive.class); 
		PendingIntent pi = PendingIntent.getService(this, 131313,
				serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		// //
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE); 
		am.set(AlarmManager.RTC_WAKEUP, nextUpdateTimeMillis, pi);

		return mStartMode;
	}

	/** A client is binding to the service with bindService() */
	@Override
	public IBinder onBind(Intent intent) {

		return mBinder;
	}

	/** Called when all clients have unbound with unbindService() */
	@Override
	public boolean onUnbind(Intent intent) {
		return mAllowRebind;
	}

	/** Called when a client is binding to the service with bindService() */
	@Override
	public void onRebind(Intent intent) {

	}

	/** Called when The service is no longer used and is being destroyed */
	@Override
	public void onDestroy() {

	}

	class InsertMultipleEmpStagesTask extends
			AsyncTask<String, Integer, String> {

		ProgressDialog pDialog;
		JSONObject jObject;
		String strJson;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(String... params) {

			System.out.println("called orders1");

			try {

					JSONObject Transactions = new JSONObject();
					CategoryArray = new JSONArray();

//					if (cursor1.moveToFirst()) {

//						do {
							JSONObject Categories = new JSONObject();

							/*Categories.put(
									"empid",
									cursor1.getString(
											cursor1.getColumnIndex("userid"))
											.toString());

							Categories
									.put("date",
											cursor1.getString(
													cursor1.getColumnIndex("time_stamp"))
													.toString());

							Categories
									.put("latitude",
											cursor1.getString(
													cursor1.getColumnIndex("lattitude"))
													.toString());

							Categories
									.put("longitude",
											cursor1.getString(
													cursor1.getColumnIndex("longitude"))
													.toString());

							Categories
									.put("status",
											cursor1.getString(
													cursor1.getColumnIndex("boarding_status"))
													.toString());*/

							CategoryArray.put(Categories);

//						} while (cursor1.moveToNext());

						Transactions.put("employes", CategoryArray);

						strJson = CategoryArray.toString();
//					}

			} catch (Exception e) {
				Log.e("exception : ", e.getMessage());
			}

			return conn.PostJSONString("getEmployeOfflineStages", strJson);
		}
 
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			try {
				Log.e("Result : ", result);
				if (null == result || result.length() == 0) {

				} else {
					Log.e("Result service : ", result);

					@SuppressWarnings("unused")
					JSONObject mainObject = new JSONObject(result);

					/*if (Integer.parseInt(mainObject.optString("errcode")) == 0) {
						helper.updateEmpStagingrows();

					}*/

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}

