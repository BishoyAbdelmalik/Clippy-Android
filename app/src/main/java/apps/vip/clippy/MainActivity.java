package apps.vip.clippy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    String ip="192.168.0.40";
    String port = "8765";
    boolean scan = false;
    static Context context = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        verifyStoragePermissions(this);

        //TODO if info exist attempt to connect first
        LinearLayout choice=findViewById(R.id.choice);
        choice.setVisibility(View.VISIBLE);
        Button manual_input_choice=findViewById(R.id.manual_input_choice);
        manual_input_choice.setOnClickListener(v-> {
            findViewById(R.id.manual_input).setVisibility(View.VISIBLE);
            Button save = findViewById(R.id.saveIP);
            save.setOnClickListener(view -> openMainPage(context));
            manual_input_choice.setVisibility(View.GONE);
        });
        scan = false;
        EditText ip = findViewById(R.id.ip);
        EditText port = findViewById(R.id.port);
        ip.setText(this.ip);
        port.setText(this.port);
        Button scan = findViewById(R.id.scan);
        scan.setOnClickListener(v -> openScanner());
        if (ForegroundService.started) {
            if (ForegroundService.main == null || !ForegroundService.main.isConnected()) {
                new serviceControl().killService(context);
            } else {
                Toast.makeText(getApplicationContext(), "Service is running", Toast.LENGTH_SHORT).show();
                Intent activity = new Intent(context, main_page.class);
                startActivity(activity);
            }
        }
        //TODO only hide it if ip and port not saved
        ProgressBar bar=findViewById(R.id.progressBar2);
        bar.setVisibility(View.GONE);

    }
    private void openMainPage(Context context) {
        EditText ip = findViewById(R.id.ip);
        EditText port = findViewById(R.id.port);
        ForegroundService.url = ip.getText().toString();
        ForegroundService.port = port.getText().toString();
        new serviceControl().startService(context);
        Intent activity = new Intent(context, main_page.class);
        startActivity(activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EditText ip = findViewById(R.id.ip);
        EditText port = findViewById(R.id.port);
        ip.setText(this.ip);
        port.setText(this.port);
        if (scan) {
            openMainPage(this);
        }
    }

    private void openScanner() {
        if (ForegroundService.started) {
            if (ForegroundService.main != null || ForegroundService.main.isConnected()) {
                try{
                    ForegroundService.main.close();
                }catch (Exception e){
                    System.out.println("Error in closing connection maybe");
                }
                ForegroundService.main=null;
            }
            new serviceControl().killService(context);


        }
        try {

            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

            startActivityForResult(intent, 0);

        } catch (Exception e) {

            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
            startActivity(marketIntent);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {

                String contents = data.getStringExtra("SCAN_RESULT");
                System.out.println("qr "+contents);
                try {
                    JSONObject jsonData = new JSONObject(contents);
                    System.out.println("qr " + jsonData.get("ip"));
                    System.out.println("qr " + jsonData.get("port"));
                    ip = String.valueOf(jsonData.get("ip"));
                    port = String.valueOf(jsonData.get("port"));
                    ForegroundService.flask_port=String.valueOf(jsonData.get("flask_port"));
                    scan = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if (resultCode == RESULT_CANCELED) {
                //handle cancel
                System.out.println("qr cancelled");

            }
        }
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}