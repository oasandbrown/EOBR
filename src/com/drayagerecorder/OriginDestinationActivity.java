//%&%&%&%&%&%&%&%&%%&%&%&%&%&%&%&&%%%&%&%&%&%&%&%&%&%&%&%&%&%&%
//Author: Andrew Browning
//Filename: OriginDestinationActivity.java
//Date: August 4th, 2011
//Notes:This activity records the origin, destination and type, 
//i.e. whether it is a pickup or delivery, of each trip
//%&%&%%&%&%&%%%&%%%&%&%&&%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&

package com.drayagerecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class OriginDestinationActivity extends Activity{
	
//%&%&%&%&&%%&%&%&%&%%&%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%
//Data Initialization
//%&%&%&%&%&%&%%%&%&%&%&&%%&%&%&%&&%&%&%%&%&%&%%%&%&%&%&%&%&%&%
	
	public static String ORIGIN;
	public static String DESTINATION;
	public static String TRIP_TYPE;	
	public static String TRIPNAME;
	public static final String fileName = "ODA";
	public static final String DATABASE_NAME = "OriginDestination";
	public static final String TRIP_TABLE_NAME = "ORIGIN_DESTINATION_INFORMATION";
	private SQLiteDatabase db;
	private static final String TAG = "OriginDestinationActivity";
	
	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%%&%&%
//Called when the activity is first created. This is where we
//create the spinners to select the origin and destination
//locations	and create the buttons
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%%		
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras(); 
		TRIPNAME=bundle.getString("com.drayagerecorder.LoginActivity.currentTripName"); 
		initDatabase();
		doNewTrip();
		
		//This disables the lock
		KeyguardManager mKeyGuardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
		KeyguardLock mLock = mKeyGuardManager.newKeyguardLock("OriginDestinationActivity");
		mLock.disableKeyguard();
		
		setContentView(R.layout.origindestination);	
		
		
		
		Spinner o_spinner = (Spinner)findViewById(R.id.origin);
    ArrayAdapter<CharSequence> o_adapter = ArrayAdapter.createFromResource(
    		this, R.array.origin_array, android.R.layout.simple_spinner_item);
    o_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    o_spinner.setAdapter(o_adapter);
    
    Spinner d_spinner = (Spinner)findViewById(R.id.destination);
    ArrayAdapter<CharSequence> d_adapter = ArrayAdapter.createFromResource(
    		this, R.array.destination_array, android.R.layout.simple_spinner_item);
    d_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    d_spinner.setAdapter(d_adapter);
    
    Spinner t_spinner = (Spinner)findViewById(R.id.triptype);
    ArrayAdapter<CharSequence> t_adapter = ArrayAdapter.createFromResource(
    		this, R.array.triptype_array, android.R.layout.simple_spinner_item);
    t_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    t_spinner.setAdapter(t_adapter);  
    
    o_spinner.setOnItemSelectedListener(new MyOriginItemSelectedListener());
    d_spinner.setOnItemSelectedListener(new MyDestinationItemSelectedListener());
    t_spinner.setOnItemSelectedListener(new MyTriptypeItemSelectedListener());
    
    Button button = (Button)findViewById(R.id.next);
    button.setEnabled(false); 
    button.setOnClickListener(mTripInformationListener); 
 
    
    button= (Button)findViewById(R.id.save);
    button.setOnClickListener(mSave);
	}

//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
//This is where we set the listeners and assign the 
//selected data to their respective variables	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
	
	public class MyOriginItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> origin,
        View view, int pos, long id) {
    		ORIGIN = origin.getItemAtPosition(pos).toString();
     }	
    public void onNothingSelected(AdapterView<?> parent) {
      // Do nothing.
    }   
}
	
	public class MyDestinationItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> destination,
        View view, int pos, long id) {
    		DESTINATION = destination.getItemAtPosition(pos).toString();
     }	
    public void onNothingSelected(AdapterView<?> parent) {
      // Do nothing.
    }   
}	

	public class MyTriptypeItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> triptype,
        View view, int pos, long id) {
    		TRIP_TYPE = triptype.getItemAtPosition(pos).toString();
     }	
    public void onNothingSelected(AdapterView<?> parent) {
      // Do nothing.
    }   
}	
	
	private OnClickListener mSave = new OnClickListener() {
		public void onClick(View v){
			recordTripType();
			Button button= (Button)findViewById(R.id.next);
  		button.setEnabled(true);
  		button= (Button)findViewById(R.id.save);
  		button.setEnabled(false);
	}
};
			
	private OnClickListener mTripInformationListener = new OnClickListener() {
	  public void onClick(View v){
	  		Intent i = new Intent(OriginDestinationActivity.this, TripInformationActivity.class);
	  		OriginDestinationActivity.this.startActivity(i);
	  		//doUnbindService();
	  		try {
					exportDB();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
	  		finish();			  		
	  }

	};
	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
//This is where we initialize the TRIP_TYPE database
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&		
	private void initDatabase() {
		db = this.openOrCreateDatabase(DATABASE_NAME, SQLiteDatabase.OPEN_READWRITE, null);//No Database Error Handler
		db.execSQL("CREATE TABLE IF NOT EXISTS " +
				TRIP_TABLE_NAME + " (TRIPNAME STRING, ORIGIN STRING, DESTINATION STRING, TRIPTYPE STRING);");//Create table with binding arguments
		db.close();//Close the database
		Log.i(TAG, "Database created");
	}
	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
//Create the database for the new trip
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	  
	  
	private void doNewTrip() {
			SQLiteDatabase db = null;
			try {
				db = openOrCreateDatabase(DATABASE_NAME, SQLiteDatabase.OPEN_READWRITE, null);
				db.execSQL("DELETE FROM "+TRIP_TABLE_NAME);
			}finally{
	    		if (db != null && db.isOpen())
	    			db.close();
	    }	
	}
	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
//This is where we record the trip type
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
	
	private void recordTripType(){
		try {
			StringBuffer queryBuf = new StringBuffer();
			queryBuf.append("INSERT INTO "+TRIP_TABLE_NAME+
				" (TRIPNAME,ORIGIN,DESTINATION,TRIPTYPE) VALUES (" +"'"+TRIPNAME+"',"+
				"'"+ORIGIN+"',"+"'"+DESTINATION +"',"+" '"+TRIP_TYPE+"');");
			db = openOrCreateDatabase(DATABASE_NAME, SQLiteDatabase.OPEN_READWRITE, null);
			db.execSQL(queryBuf.toString());
			Log.i(TAG, queryBuf.toString());
		} catch (Exception e) {
		Log.e(TAG, e.toString());
		} finally {
			if (db.isOpen())
				db.close();
		}
	}

//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%%%&
//This where we get the binder to connect to the main service
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%			
private MainService mBoundService;
	private boolean mIsBound;
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.i("INFO", "Service bound ");
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service.  Because we have bound to a explicit
			// service that we know is running in our own process, we can
			// cast its IBinder to a concrete class and directly access it.
			mBoundService = ((MainService.LocalBinder)service).getService();

		}


		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			// Because it is running in our same process, we should never
			// see this happen.
			mBoundService = null;
		}
	};


	void doBindService() {
		// Establish a connection with the service.  We use an explicit
		// class name because we want a specific service implementation that
		// we know will be running in our own process (and thus won't be
		// supporting component replacement by other applications).
		bindService(new Intent(com.drayagerecorder.OriginDestinationActivity.this, 
        MainService.class), mConnection, Context.BIND_AUTO_CREATE);
				mIsBound = true;
}

	void doUnbindService() {
		if (mIsBound) {
			// Detach our existing connection.
			unbindService(mConnection);
			mIsBound = false;
		}
	}
	
	 // Local database
void exportDB() throws IOException {	
  InputStream input = new FileInputStream("/data/data/com.drayagerecorder/databases/OriginDestination");

  // create directory for backup
  File dir = new File("/sdcard/OriginDestination");
  dir.mkdirs();

  // Path to the external backup
  OutputStream output = new FileOutputStream("/sdcard/OriginDestination/"+TRIPNAME+"_ODAdb");

  // transfer bytes from the Input File to the Output File
  byte[] buffer = new byte[1024];
  int length;
  while ((length = input.read(buffer))>0) {
      output.write(buffer, 0, length);
  }

  output.flush();
  output.close();
  input.close();
 }
}

	



	
	

