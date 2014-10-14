//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
//Author: Andrew Browning
//Date:August 4th, 2011
//File: LoginActivity.java
//Version: 1.0
//Notes: This is the first screen the user sees when
//the application is launched. The user must choose
//the login button to begin the logging process, at
//which point the user will be taken to the
//OriginRestinationActivity
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&

package com.drayagerecorder;

import java.io.File;
import java.util.Random;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class LoginActivity extends Activity{

//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
//Data initialization
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
	
	private static final String tag = "LoginActivity";
	public static String currentTripName = "";
	private final DecimalFormat sevenSigDigits = new DecimalFormat("0.#######");
	private static final String DOUBLE_QUOTE = "\"";
	public static final String Test_Trip = "Test Trip"; 

//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
//Called when the activity is first created. Buttons 
//are enabled and disabled here	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//This disables the lock
		KeyguardManager mKeyGuardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
		KeyguardLock mLock = mKeyGuardManager.newKeyguardLock("LoginActivity");
		mLock.disableKeyguard();	 
		setContentView(R.layout.login);	
    Button button = (Button)findViewById(R.id.ButtonStart);
    button.setOnClickListener(mStartListener);  
    button = (Button)findViewById(R.id.ButtonStop);
    button.setOnClickListener(mStopListener); 
    button.setEnabled(false);

    
	}
	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
//Called when the activity is restarted. Buttons are
//enabled and disabled here	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
	
	@Override
	public void onRestart(){
		super.onRestart(); 
		Button button = (Button)findViewById(R.id.ButtonStart);
		button.setEnabled(false);
    button.setOnClickListener(mStartListener); 
    button = (Button)findViewById(R.id.ButtonStop);
    button.setEnabled(true);
    button.setOnClickListener(mStopListener); 
	}

//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
//Called when the activity is destroyed. Buttons are
//enabled and disabled here	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
	@Override
	public void onDestroy(){
		KeyguardManager mKeyGuardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
		KeyguardLock mLock = mKeyGuardManager.newKeyguardLock("LoginActivity");
		mLock.reenableKeyguard();	 
	}

//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
//Called when the activity is paused.
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
	
	@Override
	protected void onPause(){
		super.onPause();
		saveState();	
	}
	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
//Called when the Activity is killed.
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&		
	
	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		saveState();			
	}

//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
//Called when the Activity is resumed.
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
	@Override
	protected void onResume(){
		super.onResume();
	}

//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
//Called to save the state of the activity.
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
	 private void saveState() {
		 while(MainService.isInstanceCreated()){
		 	Button button = (Button)findViewById(R.id.ButtonStart);
			button.setEnabled(false);
	    button.setOnClickListener(mStartListener); 
	    button = (Button)findViewById(R.id.ButtonStop);
	    button.setEnabled(true);
	    button.setOnClickListener(mStopListener);  		 
		 }
 }
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
//Create the listener,start the main service in a worker 
//thread and launch the OriginDestinationActivity. 
//Buttons are enabled and disabled here	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
	
	public OnClickListener mStartListener = new OnClickListener() {
    public void onClick(View v)
    {
    		doNewTrip();
  		    startService(new Intent(LoginActivity.this,
               MainService.class));
        Button button = (Button)findViewById(R.id.ButtonStart);
        button.setEnabled(false);
        button = (Button)findViewById(R.id.ButtonStop);
        button.setEnabled(true);      
        Intent i = new Intent(LoginActivity.this, OriginDestinationActivity.class);
      	i.putExtra("com.drayagerecorder.LoginActivity.currentTripName", currentTripName);
      	LoginActivity.this.startActivity(i); 
    	}
	};

//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
//Set the stop listener, stop the main service and export
//the database to a KML file. Buttons are enabled and
//disabled here	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
	
	private OnClickListener mStopListener = new OnClickListener() {
    public void onClick(View v)
    {   
    	stopService(new Intent(LoginActivity.this,
               MainService.class));
      	Button button = (Button)findViewById(R.id.ButtonStop);
      	button.setEnabled(false);
      	button = (Button)findViewById(R.id.ButtonStart);
      	button.setEnabled(true);  
    		try {
					exportDB();
				}catch (IOException e) {
					Toast.makeText(getBaseContext(), "Error trying to export: " + e.getMessage(), Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
      	doExport();
    }
	};	
  
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
//Create the database for the new trip
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	  
	  
	private void doNewTrip() {
			SQLiteDatabase db = null;
			try {
				db = openOrCreateDatabase(MainService.DATABASE_NAME, SQLiteDatabase.OPEN_READWRITE, null);
				db.execSQL("DELETE FROM "+MainService.POINTS_TABLE_NAME);
				final Random mRandom = new Random();
				currentTripName = String.valueOf(mRandom.nextInt());	
				 Log.i(tag,"The Current Trip Name is:" + currentTripName);
	    	} catch (Exception e) {
	    		Log.e(tag, e.toString());
	    	} finally {
	    		if (db != null && db.isOpen())
	    			db.close();
	    	}
	    }

//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
//Export the data to the SD card
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
	  
	private void doExport() {
			SQLiteDatabase db = null;
			Cursor cursor = null;
			try {
				db = openOrCreateDatabase(MainService.DATABASE_NAME, SQLiteDatabase.OPEN_READWRITE, null);
				cursor = db.rawQuery("SELECT * " +
	                    " FROM " + MainService.POINTS_TABLE_NAME +
	                    " ORDER BY GMTTIMESTAMP ASC",
	                    null);
	            int gmtTimestampColumnIndex = cursor.getColumnIndexOrThrow("GMTTIMESTAMP");
	            int latitudeColumnIndex = cursor.getColumnIndexOrThrow("LATITUDE");
	            int longitudeColumnIndex = cursor.getColumnIndexOrThrow("LONGITUDE");    
				if (cursor.moveToFirst()) {
					StringBuffer fileBuf = new StringBuffer();
					String beginTimestamp = null;
					String endTimestamp = null;
					String gmtTimestamp = null;
					initFileBuf(fileBuf, initValuesMap());
					do {
						gmtTimestamp = cursor.getString(gmtTimestampColumnIndex);
						if (beginTimestamp == null) {
							beginTimestamp = gmtTimestamp;
						}
						double latitude = cursor.getDouble(latitudeColumnIndex);
						double longitude = cursor.getDouble(longitudeColumnIndex);
						fileBuf.append("	<Placemark>\n");
						fileBuf.append("		<TimeStamp>\n");
						fileBuf.append("			<when>");
						fileBuf.append(zuluFormat(gmtTimestamp));
						fileBuf.append("</when>\n");
						fileBuf.append("		</TimeStamp>\n");
						fileBuf.append(" <styleUrl> #Truck</styleUrl>");
						fileBuf.append("		<Point>\n");
						fileBuf.append("<coordinates>");
						fileBuf.append(sevenSigDigits.format(longitude)+","+sevenSigDigits.format(latitude)+ "</coordinates>\n");
						fileBuf.append("		</Point>\n");
						fileBuf.append("	</Placemark>\n");
						
					} while (cursor.moveToNext());
					endTimestamp = gmtTimestamp;
					closeFileBuf(fileBuf, beginTimestamp, endTimestamp);
					String fileContents = fileBuf.toString();
					Log.d(tag, fileContents);
					checkStorage();
					File sdDir = new File("/sdcard/KML");
					sdDir.mkdirs();
					File file = new File("/sdcard/KML/"+currentTripName+".kml");
					try{
						FileWriter sdWriter = new FileWriter(file, false);
						sdWriter.write(fileContents);
						sdWriter.close();
					}catch(IOException e){
						e.printStackTrace();
					}				
				}
			} catch (Exception e) {
				Toast.makeText(getBaseContext(),
						"Error trying to export: " + e.getMessage(),
						Toast.LENGTH_LONG).show();
			} finally {
				if (cursor != null && !cursor.isClosed()) {
					cursor.close();
				}
				if (db != null && db.isOpen()) {
					db.close();
				}
			}
		}
	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
//Values map
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	  
	 
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private HashMap initValuesMap() {
			HashMap valuesMap = new HashMap();

			valuesMap.put("FILENAME", currentTripName);
				// use ground settings for the export
			valuesMap.put("EXTRUDE", "0");
			valuesMap.put("TESSELLATE", "1");
			valuesMap.put("ALTITUDEMODE", "clampToGround");
			return valuesMap;
		}

//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
//Open the file buffer	  
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
	  
	@SuppressWarnings("rawtypes")
	private void initFileBuf(StringBuffer fileBuf, HashMap valuesMap) {
			fileBuf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			fileBuf.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n");
			fileBuf.append("  <Document>\n");
			fileBuf.append("<Style id=");
			fileBuf.append(DOUBLE_QUOTE);
			fileBuf.append("truck");
			fileBuf.append(DOUBLE_QUOTE);
			fileBuf.append(">\n");
			fileBuf.append("<IconStyle>\n");
			fileBuf.append("<color>ff11fdfa</color>\n");
			fileBuf.append("<scale>1.1</scale>\n"	);
			fileBuf.append("<Icon>\n");
			fileBuf.append("http://maps.google.com/mapfiles/kml/shapes/truck.png\n");
			fileBuf.append("</Icon>\n");
			fileBuf.append("</IconStyle>\n");
			fileBuf.append("</Style>\n");
			fileBuf.append("    <name>"+valuesMap.get("FILENAME")+"</name>\n");	
			fileBuf.append("    <Style id=\"yellowLineGreenPoly\">\n");
			fileBuf.append("      <LineStyle>\n");
			fileBuf.append("        <color>7f00ffff</color>\n");
			fileBuf.append("        <width>4</width>\n");
			fileBuf.append("      </LineStyle>\n");
			fileBuf.append("      <PolyStyle>\n");
			fileBuf.append("        <color>7f00ff00</color>\n");
			fileBuf.append("      </PolyStyle>\n");
			fileBuf.append("    </Style>\n");
			fileBuf.append("        <extrude>"+valuesMap.get("EXTRUDE")+"</extrude>\n");
			fileBuf.append("        <tessellate>"+valuesMap.get("TESSELLATE")+"</tessellate>\n");
			fileBuf.append("        <altitudeMode>"+valuesMap.get("ALTITUDEMODE")+"</altitudeMode>\n");
		}

//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%
//Close the file buffer
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%
	  
	private void closeFileBuf(StringBuffer fileBuf, String beginTimestamp, String endTimestamp) {
			fileBuf.append("  </Document>\n");
			fileBuf.append("</kml>");
		}
	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
//Zulu time format	  
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	  	  
	  
	private String zuluFormat(String beginTimestamp) {
			// turn 20081215135500 into 2008-12-15T13:55:00Z
			StringBuffer buf = new StringBuffer(beginTimestamp);
			buf.insert(4, '-');
			buf.insert(7, '-');
			buf.insert(10, 'T');
			buf.insert(13, ':');
			buf.insert(16, ':');
			buf.append('Z');
			return buf.toString();
		}

//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
//This is where we check the state of the external 
//storage before we write to it
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
	
	static void checkStorage(){	  
	  boolean mExternalStorageAvailable = false;
	  boolean mExternalStorageWriteable = false;
	  String state = Environment.getExternalStorageState();

	  if (Environment.MEDIA_MOUNTED.equals(state)) {
	      // We can read and write the media
	      mExternalStorageAvailable = mExternalStorageWriteable = true;
	      Log.i(tag,"External Storage Avaliable:" + mExternalStorageAvailable);
	      Log.i(tag,"External Storage Writable:" + mExternalStorageWriteable);
	  } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	      // We can only read the media
	      mExternalStorageAvailable = true;
	      mExternalStorageWriteable = false;
	      Log.i(tag,"External Storage Avaliable:" + mExternalStorageAvailable);
	      Log.i(tag,"External Storage Writable:" + mExternalStorageWriteable);
	  } else {
	      // Something else is wrong. It may be one of many other states, but all we need
	      //  to know is we can neither read nor write
	      mExternalStorageAvailable = mExternalStorageWriteable = false;
	      Log.i(tag,"External Storage Avaliable:" + mExternalStorageAvailable);
	      Log.i(tag,"External Storage Writable:" + mExternalStorageWriteable);
	  }	  
	}	
	


//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
//Export the database to the SdCard
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
	
	void exportDB() throws IOException {	
		Log.i(tag, "This is the input stream " + getDatabasePath("LOGGERDB"));
	  InputStream input = new FileInputStream("/data/data/com.drayagerecorder/databases/LOGGERDB");

	  // create directory for backup
	  checkStorage();
	  File dir = new File("/sdcard/LoggerDB");
	  dir.mkdirs();
	  Log.i(tag, "Directory Created");

	  // Path to the external backup
	  OutputStream output = new FileOutputStream("/sdcard/LoggerDB/"+currentTripName+"_TIAdb");
	  Log.i(tag, "This is the ouput stream");

	  // transfer bytes from the Input File to the Output File
	  byte[] buffer = new byte[1024];
	  int length;
	  while ((length = input.read(buffer))>0) {
	      output.write(buffer, 0, length);
	  }
	  Log.i(tag, "Exporting database to SdCard");
	  Log.i(tag, "Bytes transfered");
	  output.flush();
	  Log.i(tag, "flushed");
	  output.close();
	  Log.i(tag, "output close");
	  input.close();
	  Log.i(tag, "input close");
	 }
}

//EOF	