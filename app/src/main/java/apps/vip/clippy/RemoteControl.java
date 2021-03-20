package apps.vip.clippy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;

import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class RemoteControl extends AppCompatActivity {
    boolean touch=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);
//        Spinner spinner = (Spinner) findViewById(R.id.spinner);
//// Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.remote_control_dropdown, android.R.layout.simple_spinner_item);
//// Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//// Apply the adapter to the spinner
//        spinner.setAdapter(adapter);



        LinearLayout arrow_control = findViewById(R.id.arrows_control);
        LinearLayout touch_control = findViewById(R.id.touch_control);
        arrow_control.setVisibility(View.GONE);
        touch_control.setVisibility(View.VISIBLE);
        Button mode_switch=findViewById(R.id.mode_switch);
        mode_switch.setText(R.string.trackpad_mode);
        touch=true;
        mode_switch.setOnClickListener((v) -> {
            if (touch){
                touch=false;
                touch_control.setVisibility(View.GONE);
                arrow_control.setVisibility(View.VISIBLE);
                mode_switch.setText(R.string.arrows_mode);
            }else {
                touch=true;
                mode_switch.setText(R.string.trackpad_mode);
                arrow_control.setVisibility(View.GONE);
                touch_control.setVisibility(View.VISIBLE);

            }
        });
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
        Button leftClick=findViewById(R.id.leftClick);
        Button rightClick=findViewById(R.id.rightClick);
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

        click.setOnClickListener(v -> leftClick(v,vibe));
        leftClick.setOnClickListener(v -> leftClick(v,vibe));
        rightClick.setOnClickListener(v -> rightClick(v,vibe));

        View touch =findViewById(R.id.touch);
        float[] originalX = {0};
        float[] originalY = {0};
        touch.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // ... Respond to touch events
                System.out.println(event);
                int action = MotionEventCompat.getActionMasked(event);

                String DEBUG_TAG = "touch";
                switch (action) {
                    case (MotionEvent.ACTION_DOWN):
                        Log.d(DEBUG_TAG, "Action was DOWN");
                        Log.d(DEBUG_TAG, "start x "+event.getX()+" y "+ event.getY());

                        originalX[0] =event.getX();
                        originalY[0] =event.getY();
                        return true;
                    case (MotionEvent.ACTION_MOVE):
                        Log.d(DEBUG_TAG, "Action was MOVE");
                        float x =event.getX();
                        float y=event.getY();
                        float distanceX=x-originalX[0];
                        float distanceY=y-originalY[0];
                        distanceX=Math.round(distanceX);
                        distanceY=Math.round(distanceY);
                        Log.d(DEBUG_TAG, "x "+distanceX+" y "+ distanceY);
                        originalX[0] =Math.round(x);
                        originalY[0] =Math.round(y);
                        if(Math.abs(distanceX)>2 && Math.abs(distanceY)>2){
                            ForegroundService.sendMouseCommand(distanceX+","+distanceY);
                        }

                        return true;
                    case (MotionEvent.ACTION_UP):
                        Log.d(DEBUG_TAG, "Action was UP");
//                        float x =event.getX();
//                        float y=event.getY();
//                        float distanceX=x-originalX[0];
//                        float distanceY=y-originalY[0];
//                        Log.d(DEBUG_TAG, "x "+distanceX+" y "+ distanceY);
//                        originalX[0] =x;
//                        originalY[0] =y;
//                        ForegroundService.sendMouseCommand(distanceX+","+distanceY);

                        return true;
                    case (MotionEvent.ACTION_CANCEL):
                        Log.d(DEBUG_TAG, "Action was CANCEL");
                        return true;
                    case (MotionEvent.ACTION_OUTSIDE):
                        Log.d(DEBUG_TAG, "Movement occurred outside bounds " +
                                "of current screen element");
                        return true;
                    default:
                        return true;
                }
            }
        });

    }




    private void rightClick(View v, Vibrator vibe) {
        ForegroundService.sendMouseCommand("right_click");
        vibe.vibrate(1);
    }

    private void leftClick(View v, Vibrator vibe) {
        ForegroundService.sendMouseCommand("click");
        vibe.vibrate(1);
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