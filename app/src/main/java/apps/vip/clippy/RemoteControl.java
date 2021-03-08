package apps.vip.clippy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class RemoteControl extends AppCompatActivity {
    Connection conn=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);
        EditText keyboard_input=findViewById(R.id.keyboard_input);
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
    }
}