package apps.vip.clippy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText ip = findViewById(R.id.ip);
        EditText port = findViewById(R.id.port);
        ip.setText("192.168.0.40");
        port.setText("8765");
        Button save = findViewById(R.id.saveIP);
        Button stop = findViewById(R.id.stop);
        Context context = this;
        save.setOnClickListener(v -> {
            ForegroundService.url = ip.getText().toString();
            ForegroundService.port = port.getText().toString();
            new serviceControl().startService(context);
            Intent activity = new Intent(context, main_page.class);
            startActivity(activity);
        });
        stop.setOnClickListener(v -> new serviceControl().killService(context));


    }


}