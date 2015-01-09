package org.dklisiaris.downtown.downloader;

import org.dklisiaris.downtown.R;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

public class NotificationHelper {
    private Context mContext;
    private int NOTIFICATION_ID = 1;
    private Notification mNotification;
    private NotificationManager mNotificationManager;
    private PendingIntent mContentIntent;
    private CharSequence mContentTitle;
    public NotificationHelper(Context context)
    {
        mContext = context;
    }

    /**
     * Put the notification into the status bar
     */
    @SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void createNotification() {
        //get the notification manager
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        //create the notification
        int icon = R.drawable.ic_launcher;
        CharSequence tickerText = mContext.getString(R.string.download); //Initial text that appears in the status bar
        long when = System.currentTimeMillis();
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mNotification = new Notification.Builder(mContext)
            .setContentTitle(tickerText)
            .setContentText(tickerText)
            .setSmallIcon(icon)
            .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
            .setWhen(when)
            .build();
        } else {
        	mNotification = new Notification(icon, tickerText, when);
        }*/

        mNotification = new Notification(icon, tickerText, when);
        //create the content which is shown in the notification pulldown
        mContentTitle = "Ενημέρωση Downtown"; //Full title of the notification in the pull down
        CharSequence contentText = "0% ολοκληρώθηκε."; //Text of the notification in the pull down

        //you have to set a PendingIntent on a notification to tell the system what you want it to do when the notification is selected
        //I don't want to use this here so I'm just creating a blank one
        Intent notificationIntent = new Intent();
        mContentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);

        //add the additional content and intent to the notification
        mNotification.setLatestEventInfo(mContext, mContentTitle, contentText, mContentIntent);
        
        //make this notification appear in the 'Ongoing events' section
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        //show the notification
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    /**
     * Receives progress updates from the background task and updates the status bar notification appropriately
     * @param percentageComplete
     */
    public void progressUpdate(int percentageComplete) {
        //build up the new status message
        CharSequence contentText = percentageComplete + "% ολοκληρώθηκε";
        //publish it to the status bar
        mNotification.setLatestEventInfo(mContext, mContentTitle, contentText, mContentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    /**
     * called when the background task is complete, this removes the notification from the status bar.
     * We could also use this to add a new task completed notification
     */
    public void completed()    {
        //remove the notification from the status bar

        mNotificationManager.cancel(NOTIFICATION_ID);
    }
}
