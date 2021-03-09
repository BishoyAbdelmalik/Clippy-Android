package apps.vip.clippy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RemoteControl extends AppCompatActivity {
    Connection conn=null;
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
        Button mouse_up=findViewById(R.id.mouse_up);
        Button mouse_down=findViewById(R.id.mouse_down);
        Button mouse_left=findViewById(R.id.mouse_left);
        Button mouse_right=findViewById(R.id.mouse_right);
        Button click=findViewById(R.id.click);
        mouse_up.setOnClickListener(v -> {
            ForegroundService.sendMouseCommand("up");
        });
        mouse_down.setOnClickListener(v -> {
            ForegroundService.sendMouseCommand("down");


        });
        mouse_left.setOnClickListener(v -> {
            ForegroundService.sendMouseCommand("left");

        });
        mouse_right.setOnClickListener(v -> {
            ForegroundService.sendMouseCommand("right");

        });
        click.setOnClickListener(v -> {
            ForegroundService.sendMouseCommand("click");

        });

    }
}