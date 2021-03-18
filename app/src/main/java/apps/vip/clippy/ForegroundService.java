package apps.vip.clippy;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class ForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static Connection main = null;
    public static String url = "192.168.0.40";
    public static String port = "8765";
    public static String flask_port = "5000";
    public static boolean connected = false;
    public static boolean started = false;
    public static Context context=null;




    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        started = true;
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setSound(null)
                .build();
        startForeground(1, notification);
        ClipboardManager clipboardManager=(ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        context=this;
        String path = "get";
        main = new Connection(clipboardManager, getURI(url, port, path));
        main.setAsMain();
        clipboardManager.addPrimaryClipChangedListener(new ClipboardListener());
        //do heavy work on a background thread
        //stopSelf();
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        main.close();
        started = false;

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_MIN
            );
            serviceChannel.setShowBadge(false);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private static URI getURI(String url, String port, String path) {
        URI uri = null;
        try {
            uri = new URI("ws://" + url + ":" + port + "/" + path + "");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return uri;
    }

    public static void sendCommand(ClipboardManager clipBoard, String command) {
        new Connection(clipBoard, getURI(url, port, "send"), "command", command);
    }
    public static void getScreenshot(ImageView img){
        new Connection((ClipboardManager) MainActivity.context.getSystemService(CLIPBOARD_SERVICE), getURI(url, port, "send"), "get_screenshot", img);
    }

    private static  Connection remoteControlConnection=null;
    public static void sendMouseCommand(String command) {
        if (remoteControlConnection==null || !remoteControlConnection.isConnected()) {
            remoteControlConnection=new Connection((ClipboardManager) MainActivity.context.getSystemService(CLIPBOARD_SERVICE), getURI(url, port, "send"), "mouse_input", command);
        }else {
            remoteControlConnection.sendRemoteControlCommand("mouse_input", command);

        }

    }
    public static void sendKeyboardKey(String key) {
        if (remoteControlConnection==null || !remoteControlConnection.isConnected()){
            remoteControlConnection=new Connection((ClipboardManager) MainActivity.context.getSystemService(CLIPBOARD_SERVICE), getURI(url, port, "send"),"keyboard_input",key);
        }else {
            remoteControlConnection.sendRemoteControlCommand("keyboard_input", key);
        }
    }
    public static void closeRemoteControlConnection(){
        System.out.println("close remote control connection");
        if (remoteControlConnection!=null && remoteControlConnection.isConnected()) {
            remoteControlConnection.close();
            remoteControlConnection=null;
        }

        }
    public static void getInfo(ClipboardManager clipBoard, String command) {
        new Connection(clipBoard, getURI(url, port, "send"), "info", command);
    }
    public static void createLinksNotification(ArrayList<String> links) {
        if(links.size()==0){
            return;
        }

        String CHANNEL_ID = "Link";
        CharSequence name = "link";
        String Description = "for links from PC";
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        //create the channel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setDescription(Description);
            mNotificationManager.createNotificationChannel(mChannel);
        }
        if (links.size()==1) {
            for (int i = 0; i < links.size(); i++) {
                String link = links.get(i);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, browserIntent, 0);
                Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle("Link Received")
                        .setContentText(link)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build();
                mNotificationManager.notify(i + 2, notification);
            }
        }else{
            Intent notificationIntent = new Intent(context, LinksPage.class);


            LinksPage.links=links;
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0, notificationIntent, 0);
            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("Links Received")
                    .setContentText("open to view links")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
            mNotificationManager.notify(99, notification);
        }

    }

    private class ClipboardListener implements ClipboardManager.OnPrimaryClipChangedListener {

        @Override
        public void onPrimaryClipChanged() {
            ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            CharSequence pasteData = "";
            if (clipBoard.getPrimaryClip() != null) {
                ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
                pasteData = item.getText();
                if (Connection.lastRecieved.compareTo(String.valueOf(pasteData)) != 0) {
                    new Connection(clipBoard, getURI(url, port, "send"), "clipboard", String.valueOf(pasteData));
                }
            }
        }
    }
}
