package apps.vip.clippy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class openDialer extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String phone = getIntent().getStringExtra("phone");

        Intent intent = new Intent(Intent.ACTION_DIAL);
//        intent.setData(Uri.parse("tel:8183509566"));
        intent.setData(Uri.fromParts("tel", phone, null));
        startActivity(intent);
        finish();

    }

    @Override
    protected void onResume() {
        super.onResume();
        finish();
    }
}