package apps.vip.clippy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class main_page extends AppCompatActivity {
    public static TextView connectionStatus = null;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        TextView PCname = findViewById(R.id.PCname);
        //PCname.setTextSize((float) (PCname.getTextSize()*1.2));
        Context context = this;
        findViewById(R.id.media).setOnClickListener(v -> startActivity(new Intent(context, media_control.class)));
        findViewById(R.id.screenshot_page).setOnClickListener(v -> startActivity(new Intent(context, screenshot_page.class)));
        connectionStatus = findViewById(R.id.connectionStatus);
        if (ForegroundService.main != null) {
            if (ForegroundService.main.isConnected()) {
                connectionStatus.setText("Connected");
            } else {
                connectionStatus.setText("Not Connected");
            }
        }
        PCname.setOnClickListener(v -> {
            ForegroundService.main.close();
            startActivity(new Intent(context, MainActivity.class));
        });
        Button shutdown=findViewById(R.id.shutdown);
        Button reboot=findViewById(R.id.reboot);
        shutdown.setOnClickListener(v->{
            new AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Do you really want to shutdown the PC?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        Toast.makeText(context, "Sent", Toast.LENGTH_SHORT).show();
                        ForegroundService.sendCommand((ClipboardManager) getSystemService(CLIPBOARD_SERVICE), "shutdown");
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        });
        reboot.setOnClickListener(v->{
            new AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Do you really want to reboot the PC?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        Toast.makeText(context, "Sent", Toast.LENGTH_SHORT).show();
                        ForegroundService.sendCommand((ClipboardManager) getSystemService(CLIPBOARD_SERVICE), "reboot");
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        });
        Button sleep=findViewById(R.id.sleep);
        sleep.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Do you really want put the PC to sleep?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        Toast.makeText(context, "Sent", Toast.LENGTH_SHORT).show();
                        ForegroundService.sendCommand((ClipboardManager) getSystemService(CLIPBOARD_SERVICE), "sleep");
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        });
        Button hibernate=findViewById(R.id.hibernate);
        hibernate.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Do you really want to hibernate the PC?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        Toast.makeText(context, "Sent", Toast.LENGTH_SHORT).show();
                        ForegroundService.sendCommand((ClipboardManager) getSystemService(CLIPBOARD_SERVICE), "hibernate");
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        });
        Button sendAFile=findViewById(R.id.send_file);
        sendAFile.setOnClickListener(v -> {
            String url="http://"+ForegroundService.url+":5000/static/upload.html";
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(context, Uri.parse(url));
        });
        Button remoteControlPage=findViewById(R.id.remote_control_page);
        remoteControlPage.setOnClickListener(v -> startActivity(new Intent(context,RemoteControl.class)));


    }
}