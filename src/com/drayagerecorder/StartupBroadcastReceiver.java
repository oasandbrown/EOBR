//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
//Author: Andrew Browning
//Date:August 4th, 2011
//File: StartupBroadcastReceiver
//Version: 1.0
//Notes: This allows the application to launch once the
//kernel has booted
//%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&%&
package com.drayagerecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.drayagerecorder.LoginActivity;

public class StartupBroadcastReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent){
		Intent startUpIntent = new Intent(context, LoginActivity.class); //do this at launch instead of main screen
		startUpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(startUpIntent);
	}

}
