package com.example;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.wifidemo.dbsqllite.DatabaseHandler;
import com.wifidemo.dbsqllite.WifiTuple;

import android.R.bool;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


public class WiFiDemo extends Activity {
	private static final String TAG = "WiFiDemo";
	private WifiManager wifi;
	private static BroadcastReceiver receiver;
	private static List<ScanResult> results;
	private static TextView textStatus;
	private static Button buttonScan;
	private static Button buttonLog;
	private static ScrollView ScrollView01;
	private static DatabaseHandler db; 

	private static StringBuilder textDisplay;

	private static LocationManager locationManager;
	private static LocationListener locationListener;
	private static Location curlocation;
	private static SimpleDateFormat dateFormat;
	private static boolean locationChange = false;
	private boolean scanRequested = false;
	public static final int NETWORK_UPDATE = 7000;
	public static final int GPS_UPDATE = 10000;
	public static final int SCAN_INTERVAL = 15000;

	private static Timer scanTimer;

	public WiFiDemo getActivityObject(){return this;}
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Setup UI
		buttonScan = (Button) findViewById(R.id.buttonScan);
		buttonLog = (Button) findViewById(R.id.buttonLog);
		ScrollView01 = (ScrollView)findViewById(R.id.ScrollView01);
		textStatus = (TextView) findViewById(R.id.textStatus);

		textDisplay = new StringBuilder();

		// Setup DB
		db = new DatabaseHandler(this);

		// Setup WiFi
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		curlocation = new Location(LOCATION_SERVICE);
		dateFormat = new SimpleDateFormat("dd-MM-yyyy | HH:mm:ss");

		// Register Broadcast Receiver
		if (receiver == null)
			receiver = new WiFiScanReceiver(this);

		registerReceiver(receiver, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

		//scanAndPrint();

		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		locationListener = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				if(!sameLocation(curlocation, location)){
					locationChange = true;
					curlocation.setLatitude(location.getLatitude());
					curlocation.setLongitude(location.getLongitude());
					curlocation.setProvider(location.getProvider());
				}

				//Toast.makeText(WiFiScanReceiver.getWiFiDemo(),"Location Provider : "+location.getProvider(), Toast.LENGTH_LONG).show();
				Log.d(TAG, "Time : "+ dateFormat.format(new Date(location.getTime()))+" Provider :" + location.getProvider()+" Latitude : " + location.getLatitude() +" Longitude : "+location.getLongitude());

			}
		};

		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, NETWORK_UPDATE, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_UPDATE, 0, locationListener);

		//set up timer
		scanTimer = new Timer();
		scanTimer.schedule(new TimerTask() {			
			@Override
			public void run() {
				periodicScan();
			}

		}, 15000, SCAN_INTERVAL);

	}


	// perform scan	
	public void periodicScan(){

		Log.d(TAG, "periodicScan() wifi.startScan()");
		scanRequested = wifi.startScan();

	}

	public void scanAndPrint() {

		String date = dateFormat.format(new Date());

		textStatus = (TextView) findViewById(R.id.textStatus);
		textStatus.setText("");

		String prov;
		Double lat,lon;
		lat = curlocation.getLatitude();
		lon = curlocation.getLongitude();
		prov = curlocation.getProvider();


		textStatus.append("\nTimeStamp : [ " + date + " ]\n");
		textStatus.append("Latitude : [ " + lat+" ] Longitude : [ "+ lon +" ]\n");
		textStatus.append("Provider : "+ prov + "\n");

		if(locationChange)
			textStatus.append("Location : [ NEW ]\n");
		else
			textStatus.append("Location : [ SAME ]\n");

		locationChange = false;

		// Scan result
		results = wifi.getScanResults();
		int i = 1;
		for(ScanResult result:results) {

			String ssid = result.SSID;
			int rssi = result.level;
			String bssid = result.BSSID;
			String rssiString = String.valueOf(rssi);
			textStatus.append("\n--------------- [" + Integer.toString(i)+"]-------------------\n"+ssid + "	|| " +bssid+"\nRSSI : " + rssiString + "\n\n");

			Log.d(TAG + " : scanAndPrint() :","Inserting WifiTuple...");
			db.addWifiTuple(new WifiTuple(rssi, ssid, bssid, date,lat,lon));

			i++;
		}

	}

	public void onClickScan(View view) {

		if (view.getId() == R.id.buttonScan) {

			Log.d(TAG, "onClick() wifi.startScan()");
			scanRequested = wifi.startScan();
		}


	}

	public boolean isScanRequested() {
		return scanRequested;
	}

	public void setScanRequested(boolean scanRequested) {
		this.scanRequested = scanRequested;
	}

	public void onClickLog(View view) {

		if(view.getId() == R.id.buttonLog){

			processDB();
		}

	}

	public void processDB(){

		Log.d(TAG + " : processDB : ", "Reading all contacts.."); 

		//Toast.makeText(this,"processDB", Toast.LENGTH_LONG).show();

		List<WifiTuple> wifituples = db.getAllWifiTuples();       
		Toast.makeText(this,"DB Count : " + String.valueOf(db.getWifiTuplesCount()), Toast.LENGTH_LONG).show();


		for (WifiTuple wt : wifituples) {

			db.deleteWifiTuple(wt);
			String log = "Latitude: " + wt.getLatitude() + " ,Longitude: " + wt.getLongitude() + " ,TimeStamp: " + wt.getTimeStamp() +" ,BSSID : " + wt.getBssid() + " ,SSID: " + wt.getSsid() + " ,RSSI: " + wt.getRssi() + "\n";
			// Writing Contacts to log
			//Log.d("<TUPLE>", log);
			appendLog(log);
		}



	}

	public void appendLog(String text)
	{       
		File logFile = new File("/sdcard/log.txt");
		if (!logFile.exists())
		{
			try
			{
				logFile.createNewFile();
			} 
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try
		{
			//BufferedWriter for performance, true to set append to file flag
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
			buf.append(text);
			buf.newLine();
			buf.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean sameLocation(Location previous,Location current){

		return ((previous.getLatitude() == current.getLatitude()) && (previous.getLongitude() == current.getLongitude())); 

	}

	@Override
	public void onStop() {
		Log.w(TAG, "App Stopped");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.w(TAG, "App destroyed");
		unregisterReceiver(receiver);
		super.onDestroy();
	}






}
