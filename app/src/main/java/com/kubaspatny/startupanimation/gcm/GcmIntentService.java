package com.kubaspatny.startupanimation.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.kubaspatny.startupanimation.JSONUtil.Message;
import com.kubaspatny.startupanimation.R;
import com.kubaspatny.startupanimation.activity.DrawerActivity;
import com.kubaspatny.startupanimation.data.NuntiusContentProvider;
import com.kubaspatny.startupanimation.data.NuntiusDataContract;
import com.kubaspatny.startupanimation.fragment.PrefsFragment;
import com.kubaspatny.startupanimation.network.NetworkUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kuba on 30/8/2014.
 */

public class GcmIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    private static final String DEBUG_TAG = "GcmIntentService";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle

            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {

                sendNotification("Send error: " + extras.toString());

            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {

                sendNotification("Deleted messages on server: " + extras.toString());

            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) { // If it's a regular GCM message, do some work.

                SharedPreferences settings = getSharedPreferences(PrefsFragment.PREFS_NAME, 0);

                if(settings.getBoolean(PrefsFragment.SHOW_NOTIFICATIONS, true)){
                    sendNotification(extras.getString("message"));
                }

                syncData();
                Log.i(DEBUG_TAG, "Received: " + extras.toString());

            }
        }

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("New message")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setAutoCancel(true)
                        .setContentText(msg);

        Intent resultIntent = new Intent(this, DrawerActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        Notification notification = mBuilder.build();
        notification.defaults|= Notification.DEFAULT_SOUND;
        notification.defaults|= Notification.DEFAULT_LIGHTS;
        notification.defaults|= Notification.DEFAULT_VIBRATE;

        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void syncData(){ //TODO: GET RID OF CODE DUPLICATION - here vs. that asynctask

        try {
            URL url = new URL("http://resttime-kubaspatny.rhcloud.com/rest/msg/latest");
            String result = NetworkUtils.getHTTP(url);
            Gson gson = new Gson();
            Message[] messages = gson.fromJson(result, Message[].class);
            List<String> text_list = new ArrayList<String>();

            getContentResolver().delete(NuntiusContentProvider.CONTENT_URI, null, null); // delete previous messages

            for(Message m : messages){
                text_list.add(m.getmMessageBody());

                // add message to DB;

                ContentValues values = new ContentValues();
                values.put(NuntiusDataContract.MessageEntry.COLUMN_NAME_TEXT, m.getmMessageBody());
                values.put(NuntiusDataContract.MessageEntry.COLUMN_NAME_TIMESTAMP, m.getTimestampString());
                getContentResolver().insert(NuntiusContentProvider.CONTENT_URI, values);

            }

        } catch(MalformedURLException e){
            Log.e(DEBUG_TAG, e.getLocalizedMessage());
        } catch(Exception e){
            String message = (e.getLocalizedMessage() == null) ? "Error downloading messages." : e.getLocalizedMessage();
            Log.e(DEBUG_TAG, message);
        }

    }
}
