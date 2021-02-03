package apps.vip.clippy;

import android.content.Context;
import android.content.Intent;
import android.content.*;

import androidx.core.content.ContextCompat;

public class serviceControl {
    public void startService(Context context) {
        Intent serviceIntent = new Intent(context, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
        ContextCompat.startForegroundService(context, serviceIntent);
    }

    public void killService(Context context) {
        Intent serviceIntent = new Intent(context, ForegroundService.class);
        context.stopService(serviceIntent);
    }
}
