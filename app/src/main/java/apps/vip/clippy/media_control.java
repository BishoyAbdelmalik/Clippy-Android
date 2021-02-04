package apps.vip.clippy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipboardManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

public class media_control extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_control);
        findViewById(R.id.playPause).setOnClickListener(v -> ForegroundService.sendCommand((ClipboardManager) getSystemService(CLIPBOARD_SERVICE), "playPause"));
        findViewById(R.id.volumeUp).setOnClickListener(v -> ForegroundService.sendCommand((ClipboardManager) getSystemService(CLIPBOARD_SERVICE), "volumeUp"));
        findViewById(R.id.volumeDown).setOnClickListener(v -> ForegroundService.sendCommand((ClipboardManager) getSystemService(CLIPBOARD_SERVICE), "volumeDown"));


    }
}