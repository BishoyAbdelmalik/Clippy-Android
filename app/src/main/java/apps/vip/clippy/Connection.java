package apps.vip.clippy;

import android.annotation.SuppressLint;
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
                    String type = obj.getString("type");
                    String data = obj.getString("data");
                    if (type.compareTo("clipboard") == 0) {
                        lastRecieved = data;
                        ClipData clipData = ClipData.newPlainText("text", data);
                        clipboardManager.setPrimaryClip(clipData);
                    }
                    if (type.compareTo("info") == 0) {
                        JSONObject jsonData = new JSONObject(data);
                        if (jsonData.has("type")) {
                            type = jsonData.getString("type");
                            if (type.compareTo("media") == 0) {
                                String title = jsonData.getString("title");
                                String thumb = jsonData.getString("thumbnail");
                                if (media_control.playingTxt != null) {
                                    media_control.playingTxt.setText(title);
                                    media_control.playingTXT = title;
                                } else {
                                    media_control.playingTXT = title;

                                }
                                System.out.println("playing title from connection " + title);
                            } else {

                            }
                        }

                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onOpen( ServerHandshake handshake ) {
                System.out.println("opened connection");
                ForegroundService.connected = true;
                if (main_page.connectionStatus != null) {
                    main_page.connectionStatus.setText("Connected");
                }
                //this.send("test");

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onClose(int code, String reason, boolean remote ) {
                System.out.println("closed connection code:" + code + " reason:" + reason);
                ForegroundService.connected = false;
                if (main_page.connectionStatus != null) {
                    main_page.connectionStatus.setText("Not Connected");
                }
            }

            @Override
            public void onError(Exception ex) {
                ForegroundService.connected = false;
                ex.printStackTrace();
            }

        };
        //open websocket
        mWs.connect();
    }

    public boolean isConnected() {
        return mWs.getReadyState() == ReadyState.OPEN;
    }

    public Connection(ClipboardManager clipBoard, URI uri, String type, String data) {
        this(clipBoard, uri);
        while (!isConnected()) ;
        if (type.compareTo("clipboard") == 0) {
            sendClipboard(data);
        } else if (type.compareTo("command") == 0) {
            sendCommand(data);
        }
        if (type.compareTo("info") == 0) {
            getInfo(data);
        } else {

        }

    }

    private void getInfo(String command) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", "info");
            obj.put("data", command);
            Log.d("send", "sendCommand: " + obj.toString());
            String msg = obj.toString();
            mWs.send(msg);
        } catch (JSONException e) {
            e.printStackTrace();
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
