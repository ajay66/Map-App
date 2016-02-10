package etg.com.mapfragmentssamp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends FragmentActivity {

    String latitude="17.456325",longitude="78.235263",name="babu",imei="1223334444666";


    GoogleMap gmap;
    Boolean isgpsenable,isnetworkenable;
    android.location.LocationListener ll;
    Double cur_lat=0.0,cur_lng=0.0;
    TextView tvtitle,tvLat,tvLng,txt_val;
    Geocoder geocoder;
    String uri = "http://172.16.0.128:8080/Android_Map_location/StoreLatLng.jsp?";
    //	ArrayList<StringBuilder> addrlist=new ArrayList<StringBuilder>();
    @SuppressWarnings("unused")

    ProgressDialog pDialog;


    LatLngBounds.Builder builder;
    CameraUpdate cu;
    LocationManager manager;
    BitmapDescriptor current_marker_bitmap=null,marker_bitmap=null;
    List<Marker> markersList;
    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();
    int flg=1;
    Location loc;
    ProgressDialog prgDialog;
    static final int progress_bar_type=0;

    ConnectivityReceiver conn;

    String resultStrng;

    TelephonyManager telephonyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imei = telephonyManager.getDeviceId();

        txt_val=(TextView)findViewById(R.id.txt_val);

        final int[] map_types={GoogleMap.MAP_TYPE_NORMAL,GoogleMap.MAP_TYPE_SATELLITE,GoogleMap.MAP_TYPE_TERRAIN};
        final String[] mtypes={"Normal","Satellite","Terrain"};
        if (gmap == null) {
            MapFragment frag = (MapFragment) getFragmentManager().findFragmentById(R.id.map_frag);
            gmap = frag.getMap();
        }
        marker_bitmap=null;
        current_marker_bitmap=null;
        marker_bitmap=	BitmapDescriptorFactory.fromResource(R.drawable.pink_pin);
        current_marker_bitmap=	BitmapDescriptorFactory.fromResource(R.drawable.green_pin);

        conn = new ConnectivityReceiver(getApplicationContext());

        txt_val.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                AlertDialog.Builder builders = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                // builder.setInverseBackgroundForced(true);
                builders.setTitle("MapTypes List");
                // Collections.sort(dateslist);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        getApplicationContext(), android.R.layout.simple_list_item_1,mtypes) {

                    public View getView(int position, View convertView,
                                        android.view.ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                        text.setTextColor(Color.BLACK);

                        return view;
                    }
                };
                builders.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {



                        if(conn.checkInternetConnection())
                        {
							/*gmap.clear();
							flg=3;
							new RefreshServicesData().execute();*/
//							initializeTimerTask();
                            startTimer();
                            gmap.setMapType(map_types[item]);

							/*	 CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(cur_lat, cur_lng)).zoom(36).build();
								gmap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

								builder = new LatLngBounds.Builder();

								for (Marker m : markersList) {
									builder.include(m.getPosition());
								}
								*//**initialize the padding for map boundary*//*
								int padding = 55;
								*//**create the bounds from latlngBuilder to set into map camera*//*
								LatLngBounds bounds = builder.build();
								*//**create the camera with bounds and padding to set into map*//*
								cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
								gmap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
									@Override
									public void onMapLoaded() {
										*//**set animated zoom camera into map*//*
										gmap.animateCamera(cu);

									}
								});*/



                        }
                        else
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage(
                                    "Sorry, Network is not available. Please try again later")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            finish();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    }
                });

                AlertDialog alert = builders.create();
                alert.show();
            }
        });

        if (conn.checkInternetConnection()) {
            ll=new MyLocationListener();
            manager=(LocationManager) getSystemService(LOCATION_SERVICE);
            isgpsenable=manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isnetworkenable=manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);



            if(isnetworkenable)
            {
                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 0, ll);
                loc=manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(loc!=null)
                {
                    cur_lat=loc.getLatitude();
                    cur_lng=loc.getLongitude();

                    Toast.makeText(getApplicationContext(), "Lat : " + cur_lat + ", Longitude : " + cur_lng, Toast.LENGTH_LONG).show();

//					new RefreshServicesData().execute();

                    startTimer();
                }

            }
            else
            {
//					Toast.makeText(MainActivity.this, "network provider not available", 1).show();


                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

                dialog.setMessage("This app requires your location.");

                dialog.setPositiveButton("Ok",

                        new DialogInterface.OnClickListener() {



                            @Override

                            public void onClick(

                                    DialogInterface paramDialogInterface,

                                    int paramInt) {

                                // TODO Auto-generated method stub

                                finish();

                                startActivity(new Intent(

                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                            }

                        });

                dialog.setNegativeButton("Cancel",

                        new DialogInterface.OnClickListener() {



                            @Override

                            public void onClick(

                                    DialogInterface paramDialogInterface,

                                    int paramInt) {

                                // TODO Auto-generated method stub

                                finish();

                            }

                        });

                dialog.show();

            }

        }

        else
        {
//			conn.showCustomDialog(MainActivity.this);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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





    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 6000, 60000); //
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {
        if(conn.checkInternetConnection())
        {

            timerTask = new TimerTask() {
                public void run() {

                    //use a handler to run a toast that shows the current timestamp
                    handler.post(new Runnable() {
                        public void run() {
                            //get the current timeStamp
//						Calendar calendar = Calendar.getInstance();
//						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
//						final String strDate = simpleDateFormat.format(calendar.getTime());
//
//						//show the toast
//						int duration = Toast.LENGTH_SHORT;
//						Toast toast = Toast.makeText(getApplicationContext(), strDate, duration);
//						toast.show();
                            gmap.clear();
                            flg=3;
//						refreshData();
                            new RefreshServicesData().execute();
                        }
                    });
                }
            };
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        marker_bitmap=null;
        current_marker_bitmap=null;
        stoptimertask();

    }
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        stoptimertask();
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }
    private void setMap() {
        // TODO Auto-generated method stub
        //geo
        geocoder=new Geocoder(MainActivity.this, Locale.ENGLISH);


		 CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(cur_lat, cur_lng)).zoom(15).build();
			gmap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

//        builder = new LatLngBounds.Builder();

//        for (Marker m : markersList) {
//            builder.include(m.getPosition());
//        }
//        /**initialize the padding for map boundary*/
//        int padding = 55;
//        /**create the bounds from latlngBuilder to set into map camera*/
//        LatLngBounds bounds = builder.build();
//        /**create the camera with bounds and padding to set into map*/
//        cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//        gmap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
//            @Override
//            public void onMapLoaded() {
//                /**set animated zoom camera into map*/
//                gmap.animateCamera(cu);
//
//            }
//        });



        gmap.setInfoWindowAdapter(new InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @SuppressLint("InflateParams")
            @Override
            public View getInfoContents(Marker arg0) {


                View v = getLayoutInflater().inflate(R.layout.info_window, null);

                if (conn.checkInternetConnection()) {

                    tvtitle = (TextView) v.findViewById(R.id.title);
                    tvLat = (TextView) v.findViewById(R.id.address1);
                    tvLng = (TextView) v.findViewById(R.id.address2);
                    // Returning the view containing InfoWindow contents
                    txt_val.setText("");
//				                Toast.makeText(getApplicationContext(),"size of addrlist"+addrlist.size(),1).show();
				               /* double dlat =arg0.getPosition().latitude;
				                double dlon =arg0.getPosition().longitude;
				                String slat = String.valueOf(dlat);
				                String slon = String.valueOf(dlon);*/

                    tvtitle.setText(""+getAddress(arg0.getPosition().latitude,arg0.getPosition().longitude).toString());
                }
                else
                {
//		                	conn.showCustomDialog();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

                return v;
            }
        });

    }
    private class RefreshServicesData extends
            AsyncTask<String, String, String> {


		/*String locationId = "", languageId = "";

		public SlideImagesJSONParse(String locId, String langid) {
			// TODO Auto-generated constructor stub
			this.locationId = locId;
			this.languageId = langid;
		}*/

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//			if(flg==1){
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
//			}
        }

        @Override
        protected String doInBackground(String... args) {

			/*
			 * return WebServiceCalls.getJSONString(Constants.Base_url +
			 * "getSlideImages/" + Constants.locationCode + "/" + languageId);
			 */
//			refreshData();

			/*for (int i = 0; i <1000; i++) {

			}*/

//            String jsonString = null;
//
//            try {
//
//                HttpClient client = new DefaultHttpClient();
//                HttpPost post = new HttpPost(uri);
//
//                List<NameValuePair> pairs = new ArrayList<NameValuePair>();
//                post.setEntity(new UrlEncodedFormEntity(pairs));
//                HttpResponse response = client.execute(post);
//                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//                    HttpEntity entity = response.getEntity();
//                    String re = EntityUtils.toString(entity, HTTP.UTF_8);
//                    jsonString = re.trim();
//                }else{
//                    jsonString = "";
//                }
//
//            } catch (Exception e) {
//                Log.e("", "" + e);
//            }
//            return jsonString;

            HttpClient httpclient = new DefaultHttpClient();
//			HttpPost httppost = new HttpPost(uri);
            HttpGet httpget = new HttpGet(uri+"latitude="+cur_lat+"&longitude="+cur_lng+"&name="+Login.globalVar+"&imei="+imei);
            String result = null;
            try {
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity, HTTP.UTF_8).trim();
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }
            return result;





        }

        @Override
        protected void onPostExecute(String jsonResult) {
//			if(flg==1){
//			 pDialog.dismiss();
//			}
            Toast.makeText(getApplicationContext(),"Result : "+jsonResult,Toast.LENGTH_SHORT).show();
            System.out.println(" result " + jsonResult);


            markersList = new ArrayList<Marker>();

            MarkerOptions marker = new MarkerOptions().position(new LatLng(cur_lat,cur_lng));
            marker.icon(current_marker_bitmap);

            Marker m1=gmap.addMarker(marker);
            markersList.add(m1);

            try {
                JSONObject jsonObject = new JSONObject(jsonResult);

                JSONArray jsonArray = jsonObject.getJSONArray("latitudeNlongitude");

//                for (int i = 0; i < jsonArray.length(); i++){
//
//                    JSONObject newJson = jsonArray.getJSONObject(i);
//                    Toast.makeText(getApplicationContext(), "Lat : " + newJson.getString("latitude") + ", Longitude : " + newJson.getString("longitude"), Toast.LENGTH_LONG).show();
//
//                    Double latVal = Double.parseDouble(newJson.getString("latitude"));
//                    Double lngVal = Double.parseDouble(newJson.getString("longitude"));
//                MarkerOptions marker2 = new MarkerOptions().position(new LatLng(latVal, lngVal));
//				marker2.icon(marker_bitmap);
//
//
//                    Marker m2=gmap.addMarker(marker2);
//
//
//                    markersList.add(m2);
//                }
            }catch (Exception e){

            }

//			try {
//				try
//				{
//







//				MarkerOptions marker2 = new MarkerOptions().position(AMEERPET);
//				marker2.icon(marker_bitmap);
//
//				MarkerOptions marker3 = new MarkerOptions().position(SEC);
//				marker3.icon(marker_bitmap);
//
//
//
//				MarkerOptions gachibowli_marker3 = new MarkerOptions().position(gachibowli);
//				gachibowli_marker3.icon(marker_bitmap);
//
//				MarkerOptions kondapur_marker3 = new MarkerOptions().position(kondapur);
//				kondapur_marker3.icon(marker_bitmap);
//
//				MarkerOptions nizampet_marker3 = new MarkerOptions().position(nizampet);
//				nizampet_marker3.icon(marker_bitmap);
//
//				MarkerOptions adibhatla_marker3 = new MarkerOptions().position(adibhatla);
//				adibhatla_marker3.icon(marker_bitmap);
//
//				MarkerOptions hyt_marker3 = new MarkerOptions().position(hyt);
//				hyt_marker3.icon(marker_bitmap);
//
//				MarkerOptions lbnagar_marker3 = new MarkerOptions().position(lbnagar);
//				lbnagar_marker3.icon(marker_bitmap);
//
//				MarkerOptions bjh_marker3 = new MarkerOptions().position(bjh);
//				bjh_marker3.icon(marker_bitmap);
//
//				MarkerOptions mik_marker3 = new MarkerOptions().position(mik);
//				mik_marker3.icon(marker_bitmap);
//
//				MarkerOptions dsn_marker3 = new MarkerOptions().position(dsn);
//				dsn_marker3.icon(marker_bitmap);
//
//

//				Marker m3=gmap.addMarker(marker3);
//
//				Marker m4=gmap.addMarker(gachibowli_marker3);
//				Marker m5=gmap.addMarker( kondapur_marker3);
//				Marker m6=gmap.addMarker(nizampet_marker3);
//
//				Marker m7=gmap.addMarker(adibhatla_marker3);
//				Marker m8=gmap.addMarker(hyt_marker3);
//				Marker m9=gmap.addMarker(lbnagar_marker3);
//
//				Marker m10=	gmap.addMarker(bjh_marker3);
//				Marker m11=	gmap.addMarker(mik_marker3);
//				Marker m12=	gmap.addMarker(dsn_marker3);
//
//
//
//
//
//
//
//
//
//
//				/*Marker Delhi = gmap.addMarker(new MarkerOptions().position(new LatLng(
//						28.61, 77.2099)).title("Delhi").icon(marker_bitmap));
//				Marker Chaandigarh = gmap.addMarker(new MarkerOptions().position(new LatLng(
//						30.75, 76.78)).title("Chandigarh").icon(marker_bitmap));
//				Marker SriLanka = gmap.addMarker(new MarkerOptions().position(new LatLng(
//						7.000, 81.0000)).title("Sri Lanka").icon(marker_bitmap));
//				*/
//				/**Put all the markers into arraylist*/
//

//				markersList.add(m3);
//
//				markersList.add(m4);
//				markersList.add(m5);
//				markersList.add(m6);
//				markersList.add(m7);
//				markersList.add(m8);
//				markersList.add(m9);
//				markersList.add(m10);
//				markersList.add(m11);
//				markersList.add(m12);
//
//			/*	markersList.add(gmap.addMarker(gachibowli_marker3));
//				markersList.add(gmap.addMarker( kondapur_marker3));
//				markersList.add(gmap.addMarker(nizampet_marker3));
//				markersList.add(gmap.addMarker(adibhatla_marker3));
//				markersList.add(gmap.addMarker(hyt_marker3));
//				markersList.add(gmap.addMarker(lbnagar_marker3));
//
//				markersList.add(gmap.addMarker(bjh_marker3));
////				markersList.add(gmap.addMarker(srn_marker3));
//				markersList.add(gmap.addMarker(dsn_marker3));*/
//
//
            setMap();
            pDialog.dismiss();
//				}
//				catch (Exception e) {
//					// TODO: handle exception
//				}
//
//			} catch (Exception e) {
//				// TODO: handle exception
//				e.printStackTrace();
////				if(flg==1){
            if (pDialog.isShowing() && pDialog != null) {
                pDialog.dismiss();
            }
////				 }
////				}
//			}
        }
    }


    // Show Dialog Box with Progress bar
   /* @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case progress_bar_type:
            prgDialog = new ProgressDialog(this);
            prgDialog.setMessage("Please wait...");
            prgDialog.setIndeterminate(false);
            prgDialog.setMax(100);
            prgDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            prgDialog.setCancelable(false);
            prgDialog.show();
            return prgDialog;
        default:
            return null;
        }
    }*/
    // Async Task Class



    public StringBuilder getAddress(double lat,double lng)
    {
        StringBuilder straddress=null;

        try {
//            List<Address> addresslist=geocoder.getFromLocation(lat, lng, 1);
//            Address address=addresslist.get(0);

//            for(int i=0;i<address.getMaxAddressLineIndex();i++)
//            {
//                straddress.append(address.getAddressLine(i)).append("\n");
////				st=straddress.toString();
////				Toast.makeText(getApplicationContext(), ""+i+"--->"+address.getAddressLine(i), 1).show();
//            }

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            straddress=new StringBuilder();
//            for(int i=0;i<addresses.getMaxAddressLineIndex();i++){
//                straddress = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            straddress.append(addresses.get(0).getAddressLine(0)).append("\n");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return straddress;
    }

    class MyLocationListener implements android.location.LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        stoptimertask();
//		if (pDialog.isShowing() && pDialog != null) {
//			 pDialog.dismiss();
//			}
    }


}
