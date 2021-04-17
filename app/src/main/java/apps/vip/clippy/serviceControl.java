package apps.vip.clippy;

import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

public class serviceControl {
    public void startService(Context context) {
        if(!ForegroundService.started) {
            System.out.println("starting service");
            ForegroundService.context=context;
            Intent serviceIntent = new Intent(context, ForegroundService.class);
            serviceIntent.putExtra("inputExtra", "Running");
            ContextCompat.startForegroundService(context, serviceIntent);
        }
    }

    public void killService(Context context) {
        if(ForegroundService.started) {
            System.out.println("stopping service");
            Intent serviceIntent = new Intent(context, ForegroundService.class);
            context.stopService(serviceIntent);
            if (main_page.connectionStatus != null) {
                main_page.connectionStatus.setText("Not Connected");
            }
        }

    }
}
