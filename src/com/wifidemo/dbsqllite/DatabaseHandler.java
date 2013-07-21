

package com.wifidemo.dbsqllite;
 
import java.util.ArrayList;
import java.util.List;
 
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
public class DatabaseHandler extends SQLiteOpenHelper {
 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;
 
    // Database Name
    private static final String DATABASE_NAME = "tupleManager";
 
    // WifiTuples table name
    private static final String TABLE_TUPLES = "tuples";
 
    // WifiTuples Table Columns names
    private static final String KEY_ID = "row_id";
    private static final String KEY_RSSI = "rssi";
    private static final String KEY_SSID = "name";
    private static final String KEY_BSSID = "mac_address";
    private static final String KEY_TIMESTAMP = "time_stamp";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LON = "longitude";

 
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_TUPLES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_RSSI + " INTEGER," + KEY_SSID + " TEXT,"
                + KEY_BSSID + " TEXT," + KEY_TIMESTAMP + " TEXT," + KEY_LAT + " REAL,"+ KEY_LON + " REAL" + ")" ;
        
        db.execSQL(CREATE_CONTACTS_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TUPLES);
 
        // Create tables again
        onCreate(db);
    }
 
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
 
    // Adding new tuple
    public void addWifiTuple(WifiTuple tuple) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_RSSI, tuple.getRssi()); // WifiTuple RSSI
        values.put(KEY_BSSID, tuple.getBssid()); // WifiTuple BSSID
        values.put(KEY_SSID, tuple.getSsid()); // WifiTuple SSID
        values.put(KEY_TIMESTAMP,tuple.getTimeStamp()); // WifiTuple TimeStamp
        values.put(KEY_LAT,tuple.getLatitude()); //latitude
        values.put(KEY_LON,tuple.getLongitude());//longitude
 
        // Inserting Row
        db.insert(TABLE_TUPLES, null, values);
        db.close(); // Closing database connection
    }
 
    // Getting single tuple
    public WifiTuple getWifiTuple(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_TUPLES, new String[] { KEY_ID,
                KEY_BSSID, KEY_RSSI,KEY_SSID,KEY_TIMESTAMP }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        
        //faulty 
        if (cursor != null)
            cursor.moveToFirst();
 
        WifiTuple tuple = new WifiTuple(Integer.parseInt(cursor.getString(0)),
                Integer.parseInt(cursor.getString(1)), cursor.getString(2),cursor.getString(3),cursor.getString(4));
        // return tuple
        return tuple;
    }
     
    // Getting All WifiTuples
    public List<WifiTuple> getAllWifiTuples() {
        List<WifiTuple> tupleList = new ArrayList<WifiTuple>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TUPLES;
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                WifiTuple tuple = new WifiTuple();
                //tuple.setRowId(Integer.parseInt(cursor.getString(0)));
                tuple.setRssi(Integer.parseInt(cursor.getString(1)));
                tuple.setBssid(cursor.getString(2));
                tuple.setSsid(cursor.getString(3));
                tuple.setTimeStamp(cursor.getString(4));
                tuple.setLatitude(Double.parseDouble(cursor.getString(5)));
                tuple.setLongitude(Double.parseDouble(cursor.getString(6)));

                
                // Adding tuple to list
                tupleList.add(tuple);
            } while (cursor.moveToNext());
        }
 
        // return tuple list
        return tupleList;
    }
 
    // Updating single tuple
    public int updateWifiTuple(WifiTuple tuple) {
    	
    	int ret;
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_RSSI, tuple.getRssi()); // WifiTuple RSSI
        values.put(KEY_BSSID, tuple.getBssid()); // WifiTuple BSSID
        values.put(KEY_SSID, tuple.getSsid()); // WifiTuple SSID
        values.put(KEY_TIMESTAMP,tuple.getTimeStamp()); // WifiTuple TimeStamp
        values.put(KEY_LAT,tuple.getLatitude()); //latitude
        values.put(KEY_LON,tuple.getLongitude());//longitude
 
        // updating row
        ret = db.update(TABLE_TUPLES, values, KEY_ID + " = ?",
                new String[] { String.valueOf(tuple.getRowId()) });
        
        db.close();
        return ret;
    }
 
    // Deleting single tuple
    public void deleteWifiTuple(WifiTuple tuple) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TUPLES, KEY_RSSI + " = ?",
                new String[] { String.valueOf(tuple.getRssi() ) });
        db.close();
    }
 
 
    // Getting tuples Count
    public int getWifiTuplesCount() {
    	
    	int count;
        String countQuery = "SELECT  * FROM " + TABLE_TUPLES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        
        count = cursor.getCount();
        cursor.close();
 
        // return count
        return count;
    }
 
}