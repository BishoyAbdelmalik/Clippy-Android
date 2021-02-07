package apps.vip.clippy;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

//        if (ForegroundService.connected) {
//            connectionStatus.setText("Connected");
//        } else {
//            connectionStatus.setText("Not Connected");
//        }
    }
}