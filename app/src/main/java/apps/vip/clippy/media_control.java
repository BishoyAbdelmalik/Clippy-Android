package apps.vip.clippy;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ClipboardManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
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
        Vibrator vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        playingThumb = findViewById(R.id.playingThumb);
        try {
            // Using an Async solution from SO
            // https://stackoverflow.com/questions/2471935/how-to-load-an-imageview-by-url-in-android
            new DownloadImageTask((ImageView) playingThumb)
                    .execute(thumbnailUrl);
        } catch (Exception e) {
            System.err.println("lmao get fukd user");
        }
        playingTxt = findViewById(R.id.playing);
        playingTxt.setText(playingTXT);
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
}

class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}