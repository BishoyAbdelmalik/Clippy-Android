package apps.vip.clippy;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static apps.vip.clippy.ForegroundService.CHANNEL_ID;

public class downloadFile {
    downloadFile(String url, Context context) {
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
        MainActivity.verifyStoragePermissions((Activity) context);

        DownloadManager downloadmanager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        String filename=url.substring(url.lastIndexOf('\\')+1);

        Uri uri = Uri.parse(url);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("File Received: "+filename);
        request.setDescription("downloading...");
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            //save in downloads folder
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,filename);
        } else{
            // save in shared storage in folder called clippy
            request.setDestinationInExternalPublicDir("clippy",filename);

        }
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setVisibleInDownloadsUi(false);

        downloadmanager.enqueue(request);
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                Toast.makeText(context, "File Received", Toast.LENGTH_SHORT).show();
            }
        };
        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

}
