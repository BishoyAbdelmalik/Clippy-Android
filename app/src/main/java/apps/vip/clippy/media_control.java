package apps.vip.clippy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipboardManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class media_control extends AppCompatActivity {
    public static ImageView playingThumb = null;
    public static TextView playingTxt = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_control);
        Vibrator vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        playingThumb = findViewById(R.id.playingThumb);
        try {
            // Using an Async solution from SO
            // https://stackoverflow.com/questions/2471935/how-to-load-an-imageview-by-url-in-android
            new Connection.DownloadImageTask((ImageView) playingThumb)
                    .execute(ForegroundService.thumbnailUrl);
        } catch (Exception e) {
            System.err.println("lmao get fukd user");
        }
        playingTxt = findViewById(R.id.playing);
        playingTxt.setText(ForegroundService.playingTXT);
        findViewById(R.id.playPause).setOnClickListener(v -> {
            ForegroundService.sendCommand((ClipboardManager) getSystemService(CLIPBOARD_SERVICE), "playPause");
            vibe.vibrate(1);
        });
        Button volumeUp = findViewById(R.id.volumeUp);
        Button volumeDown = findViewById(R.id.volumeDown);
        Button mute = findViewById(R.id.mute);
        volumeUp.setOnClickListener(v -> {
            ForegroundService.sendCommand((ClipboardManager) getSystemService(CLIPBOARD_SERVICE), "volumeUp");
            vibe.vibrate(1);

        });
        volumeDown.setOnClickListener(v -> {
            ForegroundService.sendCommand((ClipboardManager) getSystemService(CLIPBOARD_SERVICE), "volumeDown");
            vibe.vibrate(1);

        });
        mute.setOnClickListener(v -> {
            ForegroundService.sendCommand((ClipboardManager) getSystemService(CLIPBOARD_SERVICE), "volumeMute");
            vibe.vibrate(1);
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home) {
            finish();
        }
        return true;
    }
}

