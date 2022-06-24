package com.example.todo.view.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import java.util.Random;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.todo.R;
import com.example.todo.view.TaskListActivity;

public class AlarmReceiver extends BroadcastReceiver {
    private  String CHANNEL_ID = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("vndo", "vndo");
        Random rand = new Random(System.currentTimeMillis()); //instance of random class
        CHANNEL_ID = String.valueOf(rand.nextFloat());
        Toast.makeText(context, intent.getStringExtra("NameTask"), Toast.LENGTH_LONG).show(); // For example
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "phu";
            String description = "description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
//
        Bundle bundle = new Bundle();
        bundle.putString("id", "Today");
        bundle.putString("title", "Ngày của tôi");
        bundle.putInt("backgroundColor", Color.parseColor("#1582D8"));
        bundle.putInt("titleColor", Color.WHITE);
        Intent intent_direction = new Intent(context, TaskListActivity.class);
        intent_direction.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent_direction, 0);
//
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_app);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.star_on)
                .setLargeIcon(bitmap)
                .setSound(uri)
                .setContentTitle("Nhiệm vụ")
                .setContentText("VnDo nhắc nhở nhiệm vụ.")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(intent.getStringExtra("NameTask")));
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(14, builder.build());
    }
}
