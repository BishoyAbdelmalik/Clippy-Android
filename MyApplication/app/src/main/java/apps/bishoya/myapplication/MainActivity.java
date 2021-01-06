package apps.bishoya.myapplication;

import android.os.Bundle;
import android.os.StrictMode;

import androidx.appcompat.app.AppCompatActivity;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        URI uri= null;
        try {
            uri = new URI( "ws://192.168.0.40:8765/0" );
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        WebSocketClient mWs = new WebSocketClient(uri)
        {


            @Override
            public void onMessage( String message ) {
                System.out.println(message);
//                JSONObject obj = null;
//                try {
//                    obj = new JSONObject(message);
//                    String channel = obj.getString("channel");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

            }

            @Override
            public void onOpen( ServerHandshake handshake ) {
                System.out.println( "opened connection" );
                this.send("test");

            }

            @Override
            public void onClose( int code, String reason, boolean remote ) {
                System.out.println( "closed connection" );
            }

            @Override
            public void onError( Exception ex ) {
                ex.printStackTrace();
            }

        };
        //open websocket
        mWs.connect();
//        JSONObject obj = new JSONObject();
//        try {
//            obj.put("event", "addChannel");
//            obj.put("channel", "ok_btccny_ticker");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        String message = obj.toString();
        //send message
//        mWs.send(message);

    }
}