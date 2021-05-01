package apps.vip.clippy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

public class main_page extends AppCompatActivity {
    public static TextView connectionStatus = null;
    public static TextView PCname = null;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Context context = this;
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        PCname = findViewById(R.id.PCname);
        connectionStatus = findViewById(R.id.connectionStatus);
        findViewById(R.id.media).setOnClickListener(v -> startActivity(new Intent(context, media_control.class)));
        findViewById(R.id.screenshot_page).setOnClickListener(v -> startActivity(new Intent(context, screenshot_page.class)));
        connectionStatus = findViewById(R.id.connectionStatus);
        connectionStatus.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (connectionStatus.getText().toString().equals("Connected")) {
                    connectionStatus.setTextColor(Color.GREEN);

                } else if(connectionStatus.getText().toString().equals("Connecting")){
                    connectionStatus.setTextColor(Color.parseColor("#FF4500"));
                } else{
                    connectionStatus.setTextColor(Color.RED);

                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        PCname.setText(ForegroundService.PCname);
        if (ForegroundService.main != null && ForegroundService.started) {
            if (ForegroundService.main.isConnected()) {
                connectionStatus.setText("Connected");
                //TODO maybe remove this part
                try {
                    ForegroundService.getPCName();
                } catch (Exception e) {
                    System.err.println(e);
                }
                /////
            } else {
                connectionStatus.setText("Connecting");
            }
        }else {
            connectionStatus.setText("Not Connected");
        }

        PCname.setOnClickListener(v -> connectORdisconnect());
        PCname.setOnLongClickListener(v -> {
            forgetPC(editor, String.valueOf(PCname.getText()));
            return true;
        });

        Button shutdown = findViewById(R.id.shutdown);
        Button reboot = findViewById(R.id.reboot);
        shutdown.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Do you really want to shutdown the PC?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        Toast.makeText(context, "Sent", Toast.LENGTH_SHORT).show();
                        ForegroundService.sendCommand((ClipboardManager) getSystemService(CLIPBOARD_SERVICE), "shutdown");
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        });
        reboot.setOnClickListener(v->{
            new AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Do you really want to reboot the PC?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        Toast.makeText(context, "Sent", Toast.LENGTH_SHORT).show();
                        ForegroundService.sendCommand((ClipboardManager) getSystemService(CLIPBOARD_SERVICE), "reboot");
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        });
        Button sleep=findViewById(R.id.sleep);
        sleep.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Do you really want put the PC to sleep?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        Toast.makeText(context, "Sent", Toast.LENGTH_SHORT).show();
                        ForegroundService.sendCommand((ClipboardManager) getSystemService(CLIPBOARD_SERVICE), "sleep");
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        });
        Button hibernate=findViewById(R.id.hibernate);
        hibernate.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Do you really want to hibernate the PC?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        Toast.makeText(context, "Sent", Toast.LENGTH_SHORT).show();
                        ForegroundService.sendCommand((ClipboardManager) getSystemService(CLIPBOARD_SERVICE), "hibernate");
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        });
        Button sendAFile=findViewById(R.id.send_file);
        sendAFile.setOnClickListener(v -> {
            String url="http://"+ForegroundService.url+":"+ForegroundService.flask_port+"/static/upload.html";
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(context, Uri.parse(url));
        });
        Button remoteControlPage=findViewById(R.id.remote_control_page);
        remoteControlPage.setOnClickListener(v -> startActivity(new Intent(context,RemoteControl.class)));


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ForegroundService.main != null && ForegroundService.started) {
            if (ForegroundService.main.isConnected()) {
                connectionStatus.setText("Connected");
                //TODO maybe remove this part
                try {
                    ForegroundService.getPCName();
                } catch (Exception e) {
                    System.err.println(e);
                }
                /////
            } else {
                connectionStatus.setText("Connecting");
            }
        }else {
            connectionStatus.setText("Not Connected");
        }
    }

    private void connectORdisconnect() {
        if (ForegroundService.started) {
            disconnect();
        }else{
            connect();
        }
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.main_menu_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.forget:
                SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                TextView PCname=findViewById(R.id.PCname);
                forgetPC(editor, String.valueOf(PCname.getText()));
                break;
            case R.id.connectORdisconnect:
                connectORdisconnect();
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    private void connect() {
        Context context=this;
        new AlertDialog.Builder(context)
                .setTitle("Do you want to connect?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    new serviceControl().startService(context);
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void disconnect() {
        new AlertDialog.Builder(this)
                .setTitle("Do you want to disconnect?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> new serviceControl().killService(this))
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void forgetPC(SharedPreferences.Editor editor,String PCname) {
        new AlertDialog.Builder(this)
                .setTitle("Forget "+PCname+" ?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    new serviceControl().killService(this);
                    editor.clear();
                    editor.apply();
                    startActivity(new Intent(this,MainActivity.class));
                })
                .setNegativeButton(android.R.string.no, null).show();
    }
}