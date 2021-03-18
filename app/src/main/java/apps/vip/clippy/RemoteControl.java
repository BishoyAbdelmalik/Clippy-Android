package apps.vip.clippy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;

public class RemoteControl extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);
        EditText keyboard_input=findViewById(R.id.keyboard_input);
        keyboard_input.setOnKeyListener((v, keyCode, event) -> {
            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
            if(keyCode == KeyEvent.KEYCODE_DEL && event.getAction()==KeyEvent.ACTION_UP) {
                //this is for backspace
                ForegroundService.sendKeyboardKey("backspace");
            }
            return false;
        });
        keyboard_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println(s);
                for (char c:s.toString().toCharArray()){
                    ForegroundService.sendKeyboardKey(String.valueOf(c));
                }
                if(s.length() != 0) {
                    keyboard_input.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Vibrator vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        Button mouse_up=findViewById(R.id.mouse_up);
        Button mouse_down=findViewById(R.id.mouse_down);
        Button mouse_left=findViewById(R.id.mouse_left);
        Button mouse_right=findViewById(R.id.mouse_right);
        Button click=findViewById(R.id.click);
        mouse_up.setOnClickListener(v -> {
            ForegroundService.sendMouseCommand("up");
            vibe.vibrate(1);

        });
        mouse_down.setOnClickListener(v -> {
            ForegroundService.sendMouseCommand("down");
            vibe.vibrate(1);


        });
        mouse_left.setOnClickListener(v -> {
            ForegroundService.sendMouseCommand("left");
            vibe.vibrate(1);

        });
        mouse_right.setOnClickListener(v -> {
            ForegroundService.sendMouseCommand("right");
            vibe.vibrate(1);

        });
        click.setOnClickListener(v -> {
            ForegroundService.sendMouseCommand("click");
            vibe.vibrate(1);

        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ForegroundService.closeRemoteControlConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ForegroundService.closeRemoteControlConnection();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ForegroundService.closeRemoteControlConnection();
    }
}