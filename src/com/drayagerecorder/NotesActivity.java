//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
//Author: Andrew Browning
//Date:August 4th, 2011
//File: NotesActivity.java
//Version: 1.0
//Notes: This is the screen that allows users to enter a
//text record as means of providing a holistic analysis 
//of the entire trip.  This does not allow the official
//time-stamped record to be modified
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
package com.drayagerecorder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class NotesActivity extends Activity{
	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
//Data initialization
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
	private static final String TAG = "NotesActivity";
	private static final String tripFileName = "currentTripNotes.txt";
	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
//Called when the activity is first created. Buttons 
//are enabled and disabled here	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notes);
		Button button = (Button)findViewById(R.id.SaveNotes);
    button.setOnClickListener(mSavenotesListener); 
    button = (Button)findViewById(R.id.GoHome);
    button.setOnClickListener(mGoHomeListener);
    button.setEnabled(false);
	}	
	
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
//Set up the listeners
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&		
	
	public OnClickListener mSavenotesListener = new OnClickListener() {
    public void onClick(View v){
    	EditText editor = (EditText)findViewById(R.id.Notes);
    	String textString = editor.getText().toString();
    	if(textString != null && textString.trim().length() == 0){
				Toast.makeText(getBaseContext(), "You must enter notes",Toast.LENGTH_LONG).show();
		}else{
			try {
				saveNotes(editor.getText().toString());
			}
			catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	Button button = (Button)findViewById(R.id.GoHome);
    	button.setEnabled(true);
    	button = (Button)findViewById(R.id.SaveNotes);
    	button.setEnabled(false);
   }
	};
	
	public OnClickListener mGoHomeListener = new OnClickListener(){
		public void onClick(View v){
			try {
				notesToStorage();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	finish();
		}
	};

//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	
//Save the notes
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&	

	private void saveNotes(String Notes) throws FileNotFoundException, IOException {
		FileOutputStream fOut = openFileOutput(tripFileName,
          	MODE_PRIVATE);
		OutputStreamWriter osw = new OutputStreamWriter(fOut); 
		osw.write(Notes);
		osw.flush();
		osw.close();
		fOut.close();
	}

//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
//Save the notes to external storage
//%&%&%&%&%&%&%&%&%&%&%&%&%%&%&%&%&%&%&%&%&%&%&%&%&%%&%&%&
	private void notesToStorage() throws IOException{
		InputStream input = new FileInputStream("data/data/com.drayagerecorder/files/currentTripNotes.txt");
		LoginActivity.checkStorage();
		File sdDir = new File("/sdcard/notes");
		sdDir.mkdirs();
		OutputStream output = new FileOutputStream("/sdcard/notes/"+LoginActivity.currentTripName +".txt");
		byte[] buffer  = new byte[1024];
		int length;
		while((length = input.read(buffer))>0){
			output.write(buffer, 0, length);
		}
	}
}



