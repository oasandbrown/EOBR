//%&%&%&%&%&%&%&%&%%&%&%&%&%&%&%&&%%%&%&%&%&%&%&%&%&%&%&%&%&%&%&
//Author: Andrew Browning
//Filename: MainService.java
//Date: August 4th, 2011
//Notes:This service accesses the LocationListener and 
//LocationManager to record the user's Longitude, Latitude 
//and Time Stamp. There is also a binder that accesses trip 
//information from the TripInformationActivity.
//%&%&%%&%&%&%%%&%%%&%&%&&%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%*/

package com.drayagerecorder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import com.drayagerecorder.MainService;
import android.R.string;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MainService extends IntentService{

public MainService(String name) {
		super("MainService");
	}

@Override
protected void onHandleIntent(Intent intent) {
}


//%&%&%&%&&%%&%&%&%&%%&%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
//Data initialization
//%&%&%&%&%&%&%%%&%&%&%&&%%&%&%&%&&%&%&%%&%&%&%%%&%&%&%&%&%&%&
	public static final String DATABASE_NAME = "LOGGERDB";
	public static final String POINTS_TABLE_NAME = "LOCATION_POINTS";
	public static final String TRIPS_TABLE_NAME = "TRIPS";
	public static final String USER_LATITUDE = "user.lat";
	public static final String USER_LONGITUDE = "user.lon";
	public static final String USER_SHARED_PREFERENCES = "user.prefs";
	private final DateFormat timestampFormat = new SimpleDateFormat("yyyyMMddHHmmss");

	private LocationManager lm;
	private LocationListener locationListener;
	private SQLiteDatabase db;
	
	private static long minTimeMillis = 30000;//Get a location update every 30 seconds //0
	private static long minDistanceMeters = 100;
	private static float minAccuracyMeters = 100;//5
	
	private int lastStatus = 0;
	private static boolean showingDebugToast = false;
	private static final String TAG = "MainService";
	private final static MainService instance = null;

//%&%&%&%&%&%&%&%&%&%&%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%
//Service business logic, i.e. this is where the service
//is started	
//%&%%&&%%&%&%%&%&%&%&%&%&%&%&%&%&%%&%%%&%&%&%%%&%&%%&%&%&%&%&	

	private void startMainService() {
	lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	locationListener = new MyLocationListener();
	lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
			minTimeMillis, 
			minDistanceMeters,
			locationListener);
	initDatabase();

	}

//%&%&%&%&%&%&%&%&%&%&%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%
//This is where the database is initialized
//%&%%&&%%&%&%%&%&%&%&%&%&%&%&%&%&%%&%%%&%&%&%%%&%&%%&%&%&%&%&	

	private void initDatabase() {
	db = this.openOrCreateDatabase(DATABASE_NAME, SQLiteDatabase.OPEN_READWRITE, null);//No Database Error Handler
	db.execSQL("CREATE TABLE IF NOT EXISTS " +
			POINTS_TABLE_NAME + " (GMTTIMESTAMP VARCHAR, LATITUDE REAL, LONGITUDE REAL," +
					"TRIP_INFORMATION STRING);");//Create table with binding arguments
	db.close();//Close the database
	Log.i(TAG, "LOCATION_POINTS database created");
}

//%&%&%&%&%&%&%&%&%&%&%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%
//Shut down the main service and remove updates
//%&%%&&%%&%&%%&%&%&%&%&%&%&%&%&%&%%&%%%&%&%&%%%&%&%%&%&%&%&%&

private void shutdownMainService() {
}

//%&%&%&%&%&%&%&%&%&%&%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%
//This is where the work is done using LocationListener
//%&%%&&%%&%&%%&%&%&%&%&%&%&%&%&%&%%&%%%&%&%&%%%&%&%%&%&%&%&%&

public class MyLocationListener implements LocationListener {
	LocalBinder foo = new LocalBinder();
	public void onLocationChanged(Location loc) {
		if (loc != null) {
			//try {
			//	if (loc.hasAccuracy() && loc.getAccuracy()<= minAccuracyMeters) {
							GregorianCalendar greg = new GregorianCalendar();
							TimeZone tz = greg.getTimeZone();
							int offset = tz.getOffset(System.currentTimeMillis());
							greg.add(Calendar.SECOND, (offset/1000) * -1);
							StringBuffer queryBuf = new StringBuffer();
							queryBuf.append("INSERT INTO "+POINTS_TABLE_NAME+
															" (GMTTIMESTAMP,LATITUDE,LONGITUDE,TRIP_INFORMATION) VALUES (" +
															"'"+timestampFormat.format(greg.getTime())+"',"+
															loc.getLatitude()+","+
															loc.getLongitude()+","+
															(foo.hasTripInfo() ? foo.getTripInfo() : "NULL")+");");
															foo.deleteTripInfo();
							Log.i(TAG, queryBuf.toString());
							db = openOrCreateDatabase(DATABASE_NAME, SQLiteDatabase.OPEN_READWRITE, null);
							db.execSQL(queryBuf.toString());
				}
			//} catch (Exception e) {
			//Log.e(TAG, e.toString());
		//} finally {
			if (db.isOpen())
				db.close();
		//	}
		//}
	}

//%&%&%&%&%&%&%&%&%&%&%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%
//OnProviderDisabled send us a message
//%&%%&&%%&%&%%&%&%&%&%&%&%&%&%&%&%%&%%%&%&%&%%%&%&%%&%&%&%&%&

public void onProviderDisabled(String provider) {
	if (showingDebugToast) Toast.makeText(getBaseContext(), "onProviderDisabled: " + provider,
			Toast.LENGTH_SHORT).show();

}

//%&%&%&%&%&%&%&%&%&%&%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%
//OnProviderEnabled send us a message
//%&%%&&%%&%&%%&%&%&%&%&%&%&%&%&%&%%&%%%&%&%&%%%&%&%%&%&%&%&%&

public void onProviderEnabled(String provider) {
	if (showingDebugToast) Toast.makeText(getBaseContext(), "onProviderEnabled: " + provider,
			Toast.LENGTH_SHORT).show();

}

//%&%&%&%&%&%&%&%&%&%&%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%
//OnStatusChanged send us a message
//%&%%&&%%&%&%%&%&%&%&%&%&%&%&%&%&%%&%%%&%&%&%%%&%&%%&%&%&%&%&

	public void onStatusChanged(String provider, int status, Bundle extras) {
		String showStatus = null;
		if (status == LocationProvider.AVAILABLE)
			showStatus = "Available";
		if (status == LocationProvider.TEMPORARILY_UNAVAILABLE)
			showStatus = "Temporarily Unavailable";
		if (status == LocationProvider.OUT_OF_SERVICE)
			showStatus = "Out of Service";
		if (status != lastStatus && showingDebugToast) {
			Toast.makeText(getBaseContext(),
					"new status: " + showStatus,
					Toast.LENGTH_SHORT).show();
		}
	lastStatus = status;
	}
}

//%&%&%&%&%&%&%&%&%&%&%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%
//This is where we use a binder to access the TripInformation 
//Activity
//%&%%&&%%&%&%%&%&%&%&%&%&%&%&%&%&%%&%%%&%&%&%%%&%&%%&%&%&%&%&

private NotificationManager mNM;

/**
* Class for clients to access. Because we know this service always runs in
* the same process as its clients, we don't need to deal with IPC.
*/
public class LocalBinder extends Binder {
	MainService getService() {
		return MainService.this;
	}
	
	String getTripInfo(){
		String tripInfo = TripInformationActivity.TRIP_INFO;
		return tripInfo;
	}
	
 void deleteTripInfo(){
	 TripInformationActivity.TRIP_INFO = "'NULL'";
 }
 
	Boolean hasTripInfo(){
		if (getTripInfo() == null){
	return false;
		}else{
			return true;
		}	
	}	
	
	String getTripName(){
		String TripName = OriginDestinationActivity.TRIPNAME;
		return TripName;
	}
	
	Boolean hasTripName(){
		if(getTripName() == null){
			return false;
				}else{
					return true;
				}
		}
}


//%&%&%&%&%&%&%&%&%&%&%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%
//The main service is started here and a persistent 
//notification is provided
//%&%%&&%%&%&%%&%&%&%&%&%&%&%&%&%&%%&%%%&%&%&%%%&%&%%&%&%&%&%&

@Override
public void onCreate() {
	super.onCreate();
	mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	startMainService();
	showNotification();
	}

//%&%&%&%&%&%&%&%&%&%&%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%
//We want the main service to run until explicitly shut 
//down. We do this by returning START_STICKY
//%&%%&&%%&%&%%&%&%&%&%&%&%&%&%&%&%%&%%%&%&%&%%%&%&%%&%&%&%&%&

@Override
public int onStartCommand(Intent intent, int flags, int startId) {
    Log.i("LocalService", "Received start id " + startId + ": " + intent);
    return START_STICKY;
}	

//%&%&%&%&%&%&%&%&%&%&%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%
//Shutdown the main service and cancel notification
//%&%%&&%%&%&%%&%&%&%&%&%&%&%&%&%&%%&%%%&%&%&%%%&%&%%&%&%&%&%&

@Override
	public void onDestroy() {
	super.onDestroy();
	shutdownMainService();
	mNM.cancel(R.string.local_service_started);
}

//%&%&%&%&%&%&%&%&%&%&%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%
//This is the object that receives interactions from clients. 
//See RemoteService for a more complete example.
//%&%%&&%%&%&%%&%&%&%&%&%&%&%&%&%&%%&%%%&%&%&%%%&%&%%&%&%&%&%&

private final IBinder mBinder = new LocalBinder();
@Override
public IBinder onBind(Intent intent) {
return mBinder;
}
	

//%&%&%&%&%&%&%&%&%&%&%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%
//Show Notification
//%&%%&&%%&%&%%&%&%&%&%&%&%&%&%&%&%%&%%%&%&%&%%%&%&%%&%&%&%&%&

private void showNotification() {
	// In this sample, we'll use the same text for the ticker and the
	// expanded notification
	CharSequence text = getText(R.string.local_service_started);

	// Set the icon, scrolling text and time stamp
	Notification notification = new Notification(R.drawable.globe_icon_bw,
			text, System.currentTimeMillis());

	// The PendingIntent to launch our activity if the user selects this
	// notification
	PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
			new Intent(this, MainService.class), 0);

	// Set the info for the views that show in the notification panel.
	notification.setLatestEventInfo(this, getText(R.string.service_name),
			text, contentIntent);

	// Send the notification.
	// We use a layout id because it is a unique number. We use it later to
	// cancel.
	mNM.notify(R.string.local_service_started, notification);
}	

//%&%&%&%&%&%&%&%&%&%&%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%
//Static data methods
//%&%%&&%%&%&%%&%&%&%&%&%&%&%&%&%&%%&%%%&%&%&%%%&%&%%&%&%&%&%&

	public static void setMinTimeMillis(long _minTimeMillis) {
		minTimeMillis = _minTimeMillis;
	}

	public static long getMinTimeMillis() {
		return minTimeMillis;
	}

	public static void setMinDistanceMeters(long _minDistanceMeters) {
		minDistanceMeters = _minDistanceMeters;
	}

	public static long getMinDistanceMeters() {
		return minDistanceMeters;
	}

	public static float getMinAccuracyMeters() {
		return minAccuracyMeters;
	}

	public static void setMinAccuracyMeters(float minAccuracyMeters) {
		MainService.minAccuracyMeters = minAccuracyMeters;
	}

	public static void setShowingDebugToast(boolean showingDebugToast) {
		MainService.showingDebugToast = showingDebugToast;
	}

	public static boolean isShowingDebugToast() {
		return showingDebugToast;
	}
	
	public static boolean isInstanceCreated() { 
    return instance != null; 
 }



}
//EOF



