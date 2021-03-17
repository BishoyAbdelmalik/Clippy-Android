package apps.vip.clippy;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ClipboardManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URL;


public class media_control extends AppCompatActivity {
    public static ImageView playingThumb = null;
    public static String thumbnailUrl = "https://memesr.com/meme-templates/doge-meme.png";
    public static TextView playingTxt = null;
    public static String playingTXT = "Nothing Playing";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_control);
        playingThumb = findViewById(R.id.playingThumb);
        try {
            URL url = new URL(thumbnailUrl);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try  {
                        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        playingThumb.setImageBitmap(bmp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } catch (Exception e) {
            System.err.println("lmao get fukd user");
        }
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