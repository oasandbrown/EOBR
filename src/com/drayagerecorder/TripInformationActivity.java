//%&%&%&%&%&%&%&%&%%&%&%&%&%&%&%&&%%%&%&%&%&%&%&%&%&%&%&%&%&%&%&
//Author: Andrew Browning
//Filename: TripInformationActivity.java
//Date: August 4th, 2011
//Notes:This activity records information about each trip, such
//as delays and traffic conditions.
//%&%&%%&%&%&%%%&%%%&%&%&&%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%*/

package com.drayagerecorder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TripInformationActivity extends Activity{

//%%&%&%&%&&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
//Data initialization
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	 
	
	public static String TRIP_INFO;
	public static long LATITUDE;
	public static long LONGITUDE;
	public static final String DATABASE_NAME = "TRIPINFODB";
	public static final String TRIPS_TABLE_NAME = "TRIPS";
	public static final String USER_LATITUDE = "user.lat";
	public static final String USER_LONGITUDE = "user.lon";
	public static final String USER_SHARED_PREFERENCES = "user.prefs";
	//private static final String TAG = "TripInformationActivity";
	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
//Called when the activity is first created. We also
//create the buttons here	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tripinformation);	
		doBindService();  
    Button button = (Button)findViewById(R.id.terminaltrafficorigin);
    button.setOnClickListener(mTerminalTrafficOriginListener); 
    
    button = (Button)findViewById(R.id.terminaltrafficdestination);
    button.setOnClickListener(mTerminalTrafficDestinationListener); 
    
    button = (Button)findViewById(R.id.roadtraffic);
    button.setOnClickListener(mTrafficListener); 
    
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
//These are our spinners
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%%&%%&%&%&%&%%&%&%&%&%&%&%&%&%&%&%&%&    
	/**	Spinner io_spinner = (Spinner)findViewById(R.id.waitinginsidethegateorigin);
    ArrayAdapter<CharSequence> io_adapter = ArrayAdapter.createFromResource(
    		this, R.array.inside_origin_array, android.R.layout.simple_spinner_item);
    io_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    io_spinner.setAdapter(io_adapter);
    
		Spinner id_spinner = (Spinner)findViewById(R.id.waitinginsidethegatedestination);
    ArrayAdapter<CharSequence> id_adapter = ArrayAdapter.createFromResource(
    		this, R.array.inside_destination_array, android.R.layout.simple_spinner_item);
    id_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    id_spinner.setAdapter(id_adapter);
    
    io_spinner.setOnItemSelectedListener(new MyInsideOriginItemSelectedListener());
    id_spinner.setOnItemSelectedListener(new MyInsideDestinationItemSelectedListener());*/

//%&%%&%&%&%&%&%&%&%&%&%&%%&%&&%&%&%&%&%&%&%&%&%&%&%&%&%%&%&%&%&%&%%&%&%&%&%&%
//More Buttons
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&    
    
    button = (Button)findViewById(R.id.LocatingFreightOrigin);
    button.setOnClickListener(mLocatingFreightOriginListener);
    
    button = (Button)findViewById(R.id.LocatingFreightDestination);
    button.setOnClickListener(mLocatingFreightDestinationListener);
    
    button = (Button)findViewById(R.id.refueling);
    button.setOnClickListener(mRefuelingListener); 
    
    button = (Button)findViewById(R.id.paperworkorigin);
    button.setOnClickListener(mPaperworkOriginListener);  
    
    button = (Button)findViewById(R.id.paperworkdestination);
    button.setOnClickListener(mPaperworkDestinationListener); 
    
    button = (Button)findViewById(R.id.equipment);
    button.setOnClickListener(mEquipmentListener); 
    
    button = (Button)findViewById(R.id.notes);
    button.setOnClickListener(mNotesListener); 
    //button.setEnabled(false);
    
    //button = (Button)findViewById(R.id.camera);
   // button.setOnClickListener(mCameraListener);
    
    //Return to the Origin Destination Activity
    button = (Button)findViewById(R.id.home);
    button.setOnClickListener(new OnClickListener(){
    	public void onClick(View v){
    		showDialog();
    	}
    });
}
	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
//This is where we set the listeners and gather the trip 
//information and also provide a button to launch 
//the VoiceRecorderActivity	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
	
	private OnClickListener mTerminalTrafficOriginListener = new OnClickListener() {
		public void onClick(View v)
		{
			TRIP_INFO = "'Waiting Outside The Gate - Origin'";
		}	
	};
	private OnClickListener mTerminalTrafficDestinationListener = new OnClickListener() {
		public void onClick(View v)
		{
			TRIP_INFO = "'Waiting Outside The Gate - Destination'";
		}	
	};
		
	private OnClickListener mTrafficListener = new OnClickListener() {
		public void onClick(View v)	
		{
			TRIP_INFO = "'Road Traffic'";
		}	
	};
	
	//private OnClickListener mCameraListener = new OnClickListener(){
	//	public void onClick(View v){
	//		 Intent i = new Intent(TripInformationActivity.this, CameraActivity.class);
  //  	 TripInformationActivity.this.startActivity(i); 		
	//	}
	//};
	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
//These are our spinner listeners
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
/**	public class MyInsideOriginItemSelectedListener implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> waitinginsidethegateorigin,
        View view, int pos, long id) {
    		TRIP_INFO = waitinginsidethegateorigin.getItemAtPosition(pos).toString();
     }	
    public void onNothingSelected(AdapterView<?> parent) {
      // Do nothing.
    }   
}
	
	public class MyInsideDestinationItemSelectedListener implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> waitinginsidethegatedestination,
        View view, int pos, long id) {
    		TRIP_INFO = waitinginsidethegatedestination.getItemAtPosition(pos).toString();
     }	
    public void onNothingSelected(AdapterView<?> parent) {
      // Do nothing.
    }   
}	*/
	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
//More listeners
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&		
	private OnClickListener mLocatingFreightOriginListener = new OnClickListener() {
		public void onClick(View v)
		{
			TRIP_INFO = "'Locating Freight - Origin'";
		}	
	};
	
	private OnClickListener mLocatingFreightDestinationListener = new OnClickListener() {
		public void onClick(View v)
		{
			TRIP_INFO = "'Locating Freight - Destination'";
		}	
	};	
	
	private OnClickListener mRefuelingListener = new OnClickListener() {
		public void onClick(View v)
		{
			TRIP_INFO = "'Refueling'";
		}	
	};
	private OnClickListener mPaperworkOriginListener = new OnClickListener() {
		public void onClick(View v)
		{
			TRIP_INFO = "'EquipmentProblem - Origin'";
		} 
	};
	private OnClickListener mPaperworkDestinationListener = new OnClickListener() {
		public void onClick(View v)
		{
			TRIP_INFO = "'EquipmentProblem - Destination'";
		} 
	};
	private OnClickListener mEquipmentListener = new OnClickListener() {
		public void onClick(View v)
		{
			TRIP_INFO = "'Equipment Problem - Road'";
		}	
	};
	
	private OnClickListener mNotesListener = new OnClickListener() {
		public void onClick(View v)
		{
			 //Intent i = new Intent(TripInformationActivity.this, NotesActivity.class);
     	 //TripInformationActivity.this.startActivity(i); 
     	 //doUnbindService();
     	 //finish();
			showDialogNotes();
		}	
	};
		
	 
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%%&%&%&%&%&%&%&%&%&%&%&%&%&%&%%%&
//	This where we get the binder to connect to the main service
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
			bindService(new Intent(com.drayagerecorder.TripInformationActivity.this, 
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

		@Override
		protected void onDestroy() {
			super.onDestroy();
			doUnbindService();
		}

//%%&%&%&%&&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
//Present an alert dialog when exiting this
//activity to go home	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&			
	
		public void showDialog(){
			AlertDialog.Builder builder = new AlertDialog.Builder(TripInformationActivity.this);
			builder.setMessage("Estas seguro?")
			       .setCancelable(false)
			       .setPositiveButton("Si", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                TripInformationActivity.this.finish();
			              	doUnbindService();
			           }
			       })
			       .setNegativeButton("No", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
		}

//%%&%&%&%&&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
//Present an alert dialog when exiting this
//activity to go to notes	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
		
		public void showDialogNotes(){
			AlertDialog.Builder builder = new AlertDialog.Builder(TripInformationActivity.this);
			builder.setMessage("Estas seguro?")
			       .setCancelable(false)
			       .setPositiveButton("Si", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			          	 		Intent i = new Intent(TripInformationActivity.this, NotesActivity.class);
			           	 		TripInformationActivity.this.startActivity(i); 
			           	 		doUnbindService();
			                TripInformationActivity.this.finish();
			           }
			       })
			       .setNegativeButton("No", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
		}
		
		
//%%&%&%&%&&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
//Custom Array Adapter For The Spinners	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&			

		/**private class MyArrayAdapter extends ArrayAdapter {

			public MyArrayAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			}

			public TextView getView(int position, View convertView, ViewGroup parent) {
			TextView v = (TextView) super.getView(position, convertView, parent);
			v.setTypeface(myFont);
			return v;
			}

			public TextView getDropDownView(int position, View convertView, ViewGroup parent) {
			TextView v = (TextView) super.getView(position, convertView, parent);
			v.setTypeface(myFont);
			return v;
			}

			}*/
	}




