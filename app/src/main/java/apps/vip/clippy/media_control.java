package apps.vip.clippy;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;

import android.os.Bundle;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class media_control extends AppCompatActivity {
    public static TextView playingTxt = null;
    public static ImageView playingThumb = null;
    public static String playingTXT = "Nothing Playing";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_control);
        playingThumb = findViewById(R.id.playingThumb);
        playingTxt = findViewById(R.id.playing);
        playingTxt.setText(playingTXT);
        findViewById(R.id.playPause).setOnClickListener(v -> ForegroundService.sendCommand((ClipboardManager) getSystemService(CLIPBOARD_SERVICE), "playPause"));
        Button volumeUp = findViewById(R.id.volumeUp);
        Button volumeDown = findViewById(R.id.volumeDown);
        Button mute = findViewById(R.id.mute);
        volumeUp.setOnClickListener(v -> ForegroundService.sendCommand((ClipboardManager) getSystemService(CLIPBOARD_SERVICE), "volumeUp"));
        volumeDown.setOnClickListener(v -> ForegroundService.sendCommand((ClipboardManager) getSystemService(CLIPBOARD_SERVICE), "volumeDown"));
        mute.setOnClickListener(v -> ForegroundService.sendCommand((ClipboardManager) getSystemService(CLIPBOARD_SERVICE), "volumeMute"));
    }



}