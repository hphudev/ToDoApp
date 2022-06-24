package com.example.todo.view.service;

import static com.example.todo.view.service.MyApplication.CHANNEL_ID;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.todo.R;
import com.example.todo.view.MainActivity;
import com.example.todo.view.TaskListActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyService extends Service {
    private AlarmManager alarmManager;
    @Override
    public void onCreate() {
        super.onCreate();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Log.e("hphudev", "Service created");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FirebaseFirestore.getInstance().collection("nhiemvu")
                .whereEqualTo("Email", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                Log.e("vndo", "listen");
                                if (error != null)
                                    return;
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                                int count = 0;
                                assert value != null;
                                for(QueryDocumentSnapshot documentSnapshot : value) {
                                    if (documentSnapshot.getString("NhacNho").equals("null"))
                                        continue;
//                                    Log.e("VnDO", "entrance");
                                    Intent intent = new Intent(MyService.this, AlarmReceiver.class);
                                    intent.putExtra("NameTask", documentSnapshot.getString("TenNV"));
                                    count++;
                                    Date date = null;
                                    try {
                                        date = simpleDateFormat.parse(documentSnapshot.getString("NhacNho"));
                                        assert date != null;
                                        if (date.before(new Date()))
                                        {
                                            Log.e("VnDO", "continue");
                                            continue;
                                        }
                                        else
                                            Log.e("vndo", "setting alarm");
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(MyService.this, count, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        if (Build.VERSION.SDK_INT >= 23) {
                                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                                                    date.getTime(), pendingIntent);
                                        } else if (Build.VERSION.SDK_INT >= 19) {
                                            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                                                    date.getTime(), pendingIntent);
                                        } else {
                                            alarmManager.set(AlarmManager.RTC_WAKEUP,
                                                    date.getTime(), pendingIntent);
                                        }
//                                        alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
        sendNotifycation();
        return START_STICKY;
    }

    private void sendNotifycation() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_app);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.star_on)
                .setLargeIcon(bitmap)
                .setSound(uri)
                .setContentTitle("Quản lý công việc")
                .setContentText("VnDo đã đồng bộ dữ liệu.")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Đang thống kê công việc hôm nay..."));
        startForeground(1, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("hphudev", "Service destroyed");
    }
}
