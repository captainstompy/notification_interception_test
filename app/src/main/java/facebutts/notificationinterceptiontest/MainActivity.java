package facebutts.notificationinterceptiontest;

        import android.app.Activity;
        import android.app.Notification;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.os.Bundle;
        import android.support.v4.app.NotificationCompat;
        import android.view.View;
        import android.widget.TextView;

        import java.util.Calendar;

public class MainActivity extends Activity {

    private TextView txtView;
    private NotificationReceiver nReceiver;
    private static BroadcastReceiver tickReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtView = (TextView) findViewById(R.id.textView);
        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("facebutts.notificationinterceptiontest.NOTIFICATION_LISTENER_EXAMPLE");
        registerReceiver(nReceiver,filter);

        //Create a broadcast receiver to handle change in time
        tickReceiver=new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().compareTo(Intent.ACTION_TIME_TICK)==0)
                {
                    Calendar cal = Calendar.getInstance();
                    int minute = cal.get(Calendar.MINUTE);
                    int hour = cal.get(Calendar.HOUR);
                    String time = String.format("%d:%02d", hour, minute);
                    Notify("time passed", "I AM AN AUTO ALERT (from "+time+")");
                    if (minute % 2 == 0) {
                        Notify("even time", "This is an even-minute message");
                    } else {
                        Notify("odd time", "This is a strange time to get a message");
                    }
                }

            }
        };
        registerReceiver(tickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nReceiver);
    }

    private void Notify(String notificationTitle, String notificationMessage){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this,NotificationView.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 1, notificationIntent, 0);
        Notification notification = new Notification.Builder(MainActivity.this)
                .setAutoCancel(false)
                .setOngoing(true)
                .setNumber(100)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setContentIntent(pendingIntent)
                .build();

        notificationManager.notify(11, notification);
    }



    public void buttonClicked(View v){

        if(v.getId() == R.id.btnCreateNotify){
            NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder ncomp = new NotificationCompat.Builder(this);
            ncomp.setContentTitle("My Notification");
            ncomp.setContentText("Notification Listener Service Example");
            ncomp.setTicker("Notification Listener Service Example");
            ncomp.setSmallIcon(R.drawable.ic_launcher);
            ncomp.setAutoCancel(true);
            nManager.notify((int)System.currentTimeMillis(),ncomp.build());
        }
        else if(v.getId() == R.id.btnClearNotify){
            Intent i = new Intent("facebutts.notificationinterceptiontest.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
            i.putExtra("command","clearall");
            sendBroadcast(i);
        }
        else if(v.getId() == R.id.btnListNotify){
            Intent i = new Intent("facebutts.notificationinterceptiontest.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
            i.putExtra("command","list");
            sendBroadcast(i);
        }
        else if(v.getId() == R.id.btnGrant){
            Intent i = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(i);
        }


    }

    class NotificationReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String temp = intent.getStringExtra("notification_event") + "\n" + txtView.getText();
            txtView.setText(temp);
        }
    }



}