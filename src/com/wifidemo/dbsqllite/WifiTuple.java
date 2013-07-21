

package com.wifidemo.dbsqllite; 
 
public class WifiTuple {
     
    //private variables
    int rowId;

	int rssi;
    String ssid;

	String bssid; // mac address
    String timeStamp;
    String location; 
    
    double latitude;
	double longitude;
    
    public WifiTuple(int rssi, String ssid, String bssid, String timeStamp,
			double latitude, double longitude) {
		super();
		this.rssi = rssi;
		this.ssid = ssid;
		this.bssid = bssid;
		this.timeStamp = timeStamp;
		this.latitude = latitude;
		this.longitude = longitude;
	}

    // Empty constructor
    public WifiTuple(){
         
    }
    

	public WifiTuple(int rssi, String ssid, String bssid, String timeStamp) {
		super();
		this.rssi = rssi;
		this.ssid = ssid;
		this.bssid = bssid;
		this.timeStamp = timeStamp;
	}


	public WifiTuple(int rowId, int rssi, String ssid, String bssid,
			String timeStamp) {
		super();
		this.rowId = rowId;
		this.rssi = rssi;
		this.ssid = ssid;
		this.bssid = bssid;
		this.timeStamp = timeStamp;
	}
	
	public WifiTuple(int rowId, int rssi, String ssid, String bssid,
			String timeStamp, String location) {
		super();
		this.rowId = rowId;
		this.rssi = rssi;
		this.ssid = ssid;
		this.bssid = bssid;
		this.timeStamp = timeStamp;
		this.location = location;
	}


	public int getRowId() {
		return rowId;
	}


	public void setRowId(int rowId) {
		this.rowId = rowId;
	}


	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
    public String getSsid() {
		return ssid;
	}
    


	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}


	

}