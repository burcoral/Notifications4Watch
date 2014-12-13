package com.devatma.wear.notifications4watch;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    public final static String EXTRA_MESSAGE = "com.devatma.wear.notifications4watch.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        //return true;
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //method for button action clicked and will start the display message
    public void push4Action(View view) {

/*
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
*/
        Log.v(TAG, "pushed");
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText =  (EditText) findViewById(R.id.textToSend);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        showStackNotifications(message);
        showPageNotifications();

    }


    //
    // Stacks implementation modified after
    // http://android-developers.blogspot.com/2014/05/stacking-notifications-for-android-wear.html
    //
    public void showStackNotifications(String message) {
        Bitmap bitmapLargeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

        // Nuke all previous notifications and generate unique ids
        NotificationManagerCompat.from(this).cancelAll();
        int notificationId = 0;

        // String to represent the group all the notifications will be a part of
        final String GROUP_KEY_MESSAGES = "group_key_messages";

        // Group notification that will be visible on the phone
        Notification summaryNotification = new NotificationCompat.Builder(this)
                .setContentTitle("Burc's Meetups...")
                .setContentText("Update RSVP")
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(bitmapLargeIcon)
                .setGroup(GROUP_KEY_MESSAGES)
                .setGroupSummary(true)
                .setSound(RingtoneManager.getDefaultUri((RingtoneManager.TYPE_RINGTONE))) //added for ringtone
                .build();

        // Separate notifications that will be visible on the watch
        Intent viewIntent1 = new Intent(this, MainActivity.class);
        PendingIntent viewPendingIntent1 =
                PendingIntent.getActivity(this, notificationId+1, viewIntent1, 0);
        Notification notification1 = new NotificationCompat.Builder(this)
                .addAction(R.drawable.ic_launcher, "Meetup Stuff", viewPendingIntent1)
                .setContentTitle("Message from Burc Oral")
                .setContentText("Are you coming to the meetup?"
                        + "If not, update your RSVP to NO...Please.")
                .setSmallIcon(R.drawable.ic_launcher)
                .setGroup(GROUP_KEY_MESSAGES)
                .build();

        Intent viewIntent2 = new Intent(this, MainActivity.class);
        PendingIntent viewPendingIntent2 =
                PendingIntent.getActivity(this, notificationId+2, viewIntent2, 0);
        Notification notification2 = new NotificationCompat.Builder(this)
                .addAction(R.drawable.ic_launcher, "Another Message from Burc", viewPendingIntent2)
                .setContentTitle("Knock knock from Burc Oral")
                .setContentText(message+"****update RSVP, s'il vous plaît")
                .setSmallIcon(R.drawable.ic_launcher)
                .setGroup(GROUP_KEY_MESSAGES)
                .build();

        // Issue the group notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId+0, summaryNotification);

        // Issue the separate wear notifications
        notificationManager.notify(notificationId+2, notification2);
        notificationManager.notify(notificationId+1, notification1);
    }

    //
    // Pages implementation modified after
    // http://android-developers.blogspot.com/2014/05/another-easy-sample-for-notification.html
    //
    public void showPageNotifications() {
        // Nuke all previous notifications and generate unique ids
        NotificationManagerCompat.from(this).cancelAll();
        int notificationId = 0;

        // Titles, authors, and overdue status of some books to display
        String[] titles = { "How to survive with no food",
                "Sailing around the world",
                "Navigation on the high seas",
                "Avoiding sea monsters",
                "Salt water distillation",
                "Sail boat maintenance" };
        String[] authors = { "I. M. Hungry",
                "F. Magellan",
                "E. Shackleton",
                "K. Kracken",
                "U. R. Thirsty",
                "J. Macgyver" };
        Boolean[] overdue = { true, true, true, true, true, false };
        List extras = new ArrayList();

        // Extra pages of information for the notification that will
        // only appear on the wearable
        int numOverdue = 0;
        for (int i = 0; i < titles.length; i++) {
            if (!overdue[i]) continue;
            BigTextStyle extraPageStyle = new NotificationCompat.BigTextStyle();
            extraPageStyle.setBigContentTitle("Overdue Book " + (i+1))
                    .bigText("Title: " + titles[i] + ", Author: " + authors[i]);
            Notification extraPageNotification = new NotificationCompat.Builder(this)
                    .setStyle(extraPageStyle)
                    .build();
            extras.add(extraPageNotification);
            numOverdue++;
        }

        // Main notification that will appear on the phone handset and the wearable
        Intent viewIntent1 = new Intent(this, MainActivity.class);
        PendingIntent viewPendingIntent1 =
                PendingIntent.getActivity(this, notificationId+1, viewIntent1, 0);
        Notification notification1 = new NotificationCompat.Builder(this)
                .addAction(R.drawable.ic_launcher, "Returned", viewPendingIntent1)
                .setContentTitle("Books Overdue")
                .setContentText("You have " + numOverdue + " books due at the library")
                .setSmallIcon(R.drawable.ic_launcher)
                .extend(new NotificationCompat.WearableExtender().addPages(extras))
                .build();

        // Issue the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId+1, notification1);
    }

}
