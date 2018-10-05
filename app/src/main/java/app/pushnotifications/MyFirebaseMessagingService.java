package app.pushnotifications;

/**
 * Created by ernestnyumbu on 7/5/18.
 */

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;


import app.agrishare.MainActivity;
import app.agrishare.MyApplication;
import app.agrishare.R;

import static app.agrishare.Constants.KEY_BOOKING_ID;
import static app.agrishare.Constants.KEY_NOTIFICATION_ID;
import static app.agrishare.Constants.KEY_PostId;
import static app.agrishare.Constants.KEY_REVIEW_NOTIFICATION;
import static app.agrishare.Constants.KEY_SEEKER;
import static app.agrishare.Constants.KEY_UserId;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static int NOTIFICATION_ID = 1;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());


            long bookingId = 0;
            long userID = 0;
            boolean seeker = false;
            boolean isReviewNotification = false;

            if(remoteMessage.getData().containsKey("category")){
                if (remoteMessage.getData().get("category").equals("app.agrishare.category.NewBooking")) {
                    userID = Long.parseLong(remoteMessage.getData().get("UserId"));
                    bookingId = Long.parseLong(remoteMessage.getData().get("BookingId"));
                    seeker = false;
                }
                else if (remoteMessage.getData().get("category").equals("app.agrishare.category.BookingConfirmed")) {
                    userID = Long.parseLong(remoteMessage.getData().get("UserId"));
                    bookingId = Long.parseLong(remoteMessage.getData().get("BookingId"));
                    seeker = true;
                }
                else if (remoteMessage.getData().get("category").equals("app.agrishare.category.BookingCancelled")) {
                    userID = Long.parseLong(remoteMessage.getData().get("UserId"));
                    bookingId = Long.parseLong(remoteMessage.getData().get("BookingId"));
                    seeker = true;
                }
                else if (remoteMessage.getData().get("category").equals("app.agrishare.category.ServiceComplete")) {
                    userID = Long.parseLong(remoteMessage.getData().get("UserId"));
                    bookingId = Long.parseLong(remoteMessage.getData().get("BookingId"));
                    seeker = false;
                }
                else if (remoteMessage.getData().get("category").equals("app.agrishare.category.NewReview")) {
                    userID = Long.parseLong(remoteMessage.getData().get("UserId"));
                    bookingId = Long.parseLong(remoteMessage.getData().get("BookingId"));
                    seeker = false;
                    isReviewNotification = true;
                }
                else if (remoteMessage.getData().get("category").equals("app.agrishare.category.PaymentReceived")) {
                    userID = Long.parseLong(remoteMessage.getData().get("UserId"));
                    bookingId = Long.parseLong(remoteMessage.getData().get("BookingId"));
                    seeker = false;
                }

                String message = remoteMessage.getData().get("message");
                sendNotification(message, userID, bookingId, seeker, isReviewNotification);

            }


          /*  if (/* Check if data needs to be processed by long running job */ //true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
          /*      scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            } */

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
     /*   FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob); */
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody, long userId, long bookingId, boolean seeker, boolean isReviewNotification) {
      Intent intent = new Intent(this,  MainActivity.class);
      intent.putExtra(KEY_SEEKER, seeker);
      intent.putExtra(KEY_REVIEW_NOTIFICATION, isReviewNotification);
      intent.putExtra(KEY_NOTIFICATION_ID, NOTIFICATION_ID);
      Log.d("NOTIFICATION", "BOOKING ID: " + bookingId + " USERID: " + userId);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (bookingId != 0)
            intent.putExtra(KEY_BOOKING_ID, bookingId);
        else if (userId != 0)
            intent.putExtra(KEY_UserId, userId);
      PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("AgriShare")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setLights(Color.BLUE, 2000, 5000)  //setLights (int argb, int onMs, int offMs).
                        .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE);

       // MyApplication.notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            MyApplication.notificationManager.createNotificationChannel(channel);
        }


        Random random = new Random();
        NOTIFICATION_ID = random.nextInt(9999 - 1000) + 1000;
        MyApplication.notificationManager.notify(NOTIFICATION_ID , notificationBuilder.build());
    }
}