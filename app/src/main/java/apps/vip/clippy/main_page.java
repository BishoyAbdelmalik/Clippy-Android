package apps.vip.clippy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

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
        shutdown.setOnClickListener(v->ForegroundService.sendCommand((ClipboardManager) getSystemService(CLIPBOARD_SERVICE), "shutdown"));
        reboot.setOnClickListener(v->ForegroundService.sendCommand((ClipboardManager) getSystemService(CLIPBOARD_SERVICE), "reboot"));
        Button sendAFile=findViewById(R.id.send_file);
        sendAFile.setOnClickListener(v -> {
            String url="http://"+ForegroundService.url+":5000/static/upload.html";
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(context, Uri.parse(url));
        });


    }
}