package apps.vip.clippy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class openDialer extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean type = getIntent().getBooleanExtra("type", true);
        if (type) {
            phone();
        } else {
            sms();
        }
        finish();

    }

    private void sms() {
        String phone = getIntent().getStringExtra("phone");

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("sms:" + phone));
        startActivity(intent);
    }

    private void phone() {
        String phone = getIntent().getStringExtra("phone");
        Intent intent = new Intent(Intent.ACTION_DIAL);
//        intent.setData(Uri.parse("tel:8183509566"));
        intent.setData(Uri.fromParts("tel", phone, null));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        finish();
    }
}