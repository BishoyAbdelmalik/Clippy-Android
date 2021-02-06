package apps.vip.clippy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipboardManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;

public class media_control extends AppCompatActivity {
    public static TextView playingTxt = null;
    public static ImageView playingThumb = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_control);
        playingThumb = (ImageView) findViewById(R.id.playingThumb);
        playingTxt = findViewById(R.id.playing);
        findViewById(R.id.playPause).setOnClickListener(v -> ForegroundService.sendCommand((ClipboardManager) getSystemService(CLIPBOARD_SERVICE), "playPause"));
        findViewById(R.id.volumeUp).setOnClickListener(v -> ForegroundService.sendCommand((ClipboardManager) getSystemService(CLIPBOARD_SERVICE), "volumeUp"));
        findViewById(R.id.volumeDown).setOnClickListener(v -> ForegroundService.sendCommand((ClipboardManager) getSystemService(CLIPBOARD_SERVICE), "volumeDown"));
        AsyncTask.execute(() -> {
            ForegroundService.getInfo((ClipboardManager) getSystemService(CLIPBOARD_SERVICE), "media");
        });


    }



}