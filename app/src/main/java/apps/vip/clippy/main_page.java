package apps.vip.clippy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class main_page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Button mediaPage = findViewById(R.id.media);
        Context context = this;
        mediaPage.setOnClickListener(v -> startActivity(new Intent(context, media_control.class)));
    }
}