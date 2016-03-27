package com.example.bink.dailyselfie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {
	private static final String TAG = "AlarmReceiver";
	private static final String MESSAGE = "Time for selfie!";

	private Intent mNotificationIntent;
	private PendingIntent mContentIntent;

	@Override
	public void onReceive(Context context, Intent intent) {
		mNotificationIntent = new Intent(context, MainActivity.class);
		mContentIntent = PendingIntent.getActivity(context, 0,
				mNotificationIntent, 0);
		Notification.Builder notificationBuilder = new Notification.Builder(
				context).setTicker(MESSAGE)
				.setSmallIcon(android.R.drawable.stat_sys_warning)
				.setAutoCancel(true).setContentTitle("Reminder")
				.setContentText(MESSAGE).setContentIntent(mContentIntent) ;
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(1,
				notificationBuilder.build());
		Log.i(TAG, "received notification at:"
				+ DateFormat.getDateTimeInstance().format(new Date()));
	}
}
