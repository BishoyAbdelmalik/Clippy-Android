package apps.vip.clippy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    boolean scan = false;
    Context context = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        verifyStoragePermissions(this);
        EditText ip = findViewById(R.id.ip);
        EditText port = findViewById(R.id.port);
        ProgressBar bar=findViewById(R.id.progressBar2);
        bar.setVisibility(View.GONE);
        LinearLayout choice=findViewById(R.id.choice);
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String ipAdd=sharedPref.getString("ip","-1");
        String portNumber=sharedPref.getString("port","-1");
        String flaskPort=sharedPref.getString("flaskPort","-1");
        System.out.println(ipAdd);
        System.out.println(portNumber);
        System.out.println(flaskPort);
        boolean havesaved=!ipAdd.equals("-1") && !portNumber.equals("-1") && !flaskPort.equals("-1");
        if(havesaved){
            System.out.println("ip "+ ipAdd+ " port "+portNumber+" flask "+flaskPort);
            ForegroundService.main = null;
            ForegroundService.connected = false;
            ForegroundService.started = false;
            ForegroundService.context=null;
            ip.setText(ipAdd);
            port.setText(portNumber);
            ForegroundService.flask_port=flaskPort;
            bar.setVisibility(View.VISIBLE);
            findViewById(R.id.choice).setVisibility(View.GONE);

            openMainPage(this);
        }else {
            choice.setVisibility(View.VISIBLE);
            Button manual_input_choice = findViewById(R.id.manual_input_choice);
            manual_input_choice.setOnClickListener(v -> {
                findViewById(R.id.manual_input).setVisibility(View.VISIBLE);
                Button save = findViewById(R.id.saveIP);
                save.setOnClickListener(view -> openMainPage(context));
                manual_input_choice.setVisibility(View.GONE);
            });
            scan = false;

            ip.setText(ForegroundService.url);
            port.setText(ForegroundService.port);
            Button scan = findViewById(R.id.scan);
            scan.setOnClickListener(v -> openScanner());
            if (ForegroundService.started) {
                if (ForegroundService.main == null || !ForegroundService.main.isConnected()) {
                    new serviceControl().killService(context);
                } else {
                    Toast.makeText(getApplicationContext(), "Service is running", Toast.LENGTH_SHORT).show();
                    Intent activity = new Intent(context, main_page.class);
                    activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(activity);
                }
            }
        }



    }
    private void openMainPage(Context context) {
        EditText ip = findViewById(R.id.ip);
        EditText port = findViewById(R.id.port);
        ForegroundService.url = ip.getText().toString();
        ForegroundService.port = port.getText().toString();
        new serviceControl().startService(context);
        Intent activity = new Intent(context, main_page.class);
        activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EditText ip = findViewById(R.id.ip);
        EditText port = findViewById(R.id.port);
        ip.setText(ForegroundService.url);
        port.setText(ForegroundService.port);
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
                    ForegroundService.url = String.valueOf(jsonData.get("ip"));
                    ForegroundService.port = String.valueOf(jsonData.get("port"));
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