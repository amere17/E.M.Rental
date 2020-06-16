package com.example.emrental.SendNotificationPack;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.emrental.OrderActivity;
import com.example.emrental.ProfileActivity;
import com.example.emrental.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;

public class MyFireBaseMessagingService extends FirebaseMessagingService {
    private static final int NOTIFICATION_ID = 1000;
    String title, message, user, tool,toolId;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        title = remoteMessage.getData().get("Title");
        message = remoteMessage.getData().get("Message");
        user = remoteMessage.getData().get("UserID");
        tool = remoteMessage.getData().get("ToolName");
        toolId = remoteMessage.getData().get("ToolID");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Name";
            String description = "Des";
            int importance = NotificationManager.IMPORTANCE_HIGH; //Important for heads-up notification
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(this.getApplicationContext(), ProfileActivity.class);
        intent.putExtra("UserId", user);
        PendingIntent pIntent = PendingIntent.getActivity(this.getApplication(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Intent intent1 = new Intent(getApplicationContext(), OrderActivity.class);
        intent1.putExtra("ToolId", toolId);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pIntent1 = PendingIntent.getActivity(this.getApplication(), 0, intent1,
                PendingIntent.FLAG_UPDATE_CURRENT);
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.logo);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(message + " For Tool With Name " + tool)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message + " For Tool With Name " + tool))
                .setLargeIcon(bitmapdraw.getBitmap())
                .addAction(R.drawable.logo, "Open Tenant profile", pIntent)
                .addAction(R.drawable.logo, "Open ُTool Page", pIntent1)
                .setAutoCancel(true)
                .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE); //Important for heads-up notification
                 //Important for heads-up notification
        Notification buildNotification = mBuilder.build();
        NotificationManager mNotifyMgr = (NotificationManager) getBaseContext().getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(001, buildNotification);
    }

}
