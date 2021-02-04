package apps.vip.clippy;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import org.json.*;
import java.net.URI;
import java.net.URISyntaxException;

public class Connection {
    private final WebSocketClient mWs;
    public static String lastRecieved = "";

    Connection(ClipboardManager clipboardManager, URI uri) {


        mWs = new WebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                System.out.println(message);
                Log.d("testing connection", "onMessage: " + message);
                JSONObject obj = null;
                try {
                    obj = new JSONObject(message);
                   String type=obj.getString("type");
                   String data=obj.getString("data");
                   if (type.compareTo("clipboard")==0){
                       lastRecieved=data;
                       ClipData clipData = ClipData.newPlainText("text",data);
                       clipboardManager.setPrimaryClip(clipData);
                   }else {

                   }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onOpen( ServerHandshake handshake ) {
                System.out.println( "opened connection" );
                //this.send("test");

            }

            @Override
            public void onClose( int code, String reason, boolean remote ) {
                System.out.println( "closed connection" );
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }

        };
        //open websocket
        mWs.connect();
    }

    public Connection(ClipboardManager clipBoard, URI uri, String type, String data) {
        this(clipBoard, uri);
        while (mWs.getReadyState() != ReadyState.OPEN) ;
        if (type.compareTo("clipboard") == 0) {
            sendClipboard(data);
        } else if (type.compareTo("command") == 0) {
            sendCommand(data);
        } else {

        }

    }

    private void sendCommand(String command) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", "command");
            obj.put("data", command);
            Log.d("send", "sendCommand: " + obj.toString());
            String msg = obj.toString();
            mWs.send(msg);
            mWs.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendClipboard(String text) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", "clipboard");
            obj.put("data", text);
            Log.d("send", "sendClipboard: " + obj.toString());
            String msg = obj.toString();
            mWs.send(msg);
            mWs.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        mWs.close();
    }
}
