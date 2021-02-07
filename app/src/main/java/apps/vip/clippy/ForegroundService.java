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
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.net.URI;
import java.net.URISyntaxException;

public class ForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private Connection getClipboard;
    public static String url = "192.168.0.40";
    public static String port = "8765";
    public static boolean connected = false;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
                .build();
        startForeground(1, notification);
        ClipboardManager clipboardManager=(ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        String path = "get";
        getClipboard = new Connection(clipboardManager, getURI(url, port, path));
        clipboardManager.addPrimaryClipChangedListener(new ClipboardListener());
        //do heavy work on a background thread
        //stopSelf();
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        getClipboard.close();
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
                    NotificationManager.IMPORTANCE_DEFAULT
            );
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

    public static void getInfo(ClipboardManager clipBoard, String command) {
        new Connection(clipBoard, getURI(url, port, "send"), "info", command);
    }

    private class ClipboardListener implements ClipboardManager.OnPrimaryClipChangedListener {

        @Override
        public void onPrimaryClipChanged() {
            ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            CharSequence pasteData = "";
            ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
            pasteData = item.getText();
            if (Connection.lastRecieved.compareTo(String.valueOf(pasteData)) != 0) {
                new Connection(clipBoard, getURI(url, port, "send"), "clipboard", String.valueOf(pasteData));
            }
        }
    }
}
