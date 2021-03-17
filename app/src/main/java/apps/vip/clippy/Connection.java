package apps.vip.clippy;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Patterns;
import android.widget.ImageView;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import org.json.*;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

public class Connection {
    private final WebSocketClient mWs;
    public static String lastRecieved = "";
    boolean mainConnection = false;
    Object o;
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
                        String[] arr=data.trim().split("\\s+");
                        ArrayList<String> links=new ArrayList<>();
                        for(String s:arr) {
                            if (Patterns.WEB_URL.matcher(s).matches()) {
                                links.add(s);
                            }
                        }
                        System.out.println("Links in connection "+Arrays.toString(links.toArray()));
                        ForegroundService.createLinksNotification(links);
                    } else if (type.compareTo("info") == 0) {
                        JSONObject jsonData = new JSONObject(data);
                        if (jsonData.has("type")) {
                            type = jsonData.getString("type");
                            if (type.compareTo("media") == 0) {
                                String title = jsonData.getString("title");
                                String thumb = jsonData.getString("thumbnail");
                                if (media_control.playingTxt != null) {
                                    media_control.playingTxt.setText(title);
                                }
                                media_control.playingTXT = title;
                                media_control.thumbnailUrl = "http://" + ForegroundService.url + ":5000" + thumb;
                                System.out.println("playing title from connection " + title);
                            }
                        }

                    } else if (type.compareTo("file_path") == 0) {
                        new downloadFile(createFileURL(data), MainActivity.context);
                    } else if(type.compareTo("file_screenshot") == 0) {
                        System.out.println("screenshot: "+data);
                        screenshot_page.url=createFileURL(data);
                        if(o!=null){
                            new DownloadImageTask((ImageView) o)
                                    .execute(screenshot_page.url);
                        }
                    }else {

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
                if (main_page.connectionStatus != null && mainConnection) {
                    main_page.connectionStatus.setText("Connected");
                }
                //this.send("test");

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onClose(int code, String reason, boolean remote ) {
                System.out.println("closed connection code:" + code + " reason:" + reason);
                ForegroundService.connected = false;
                if (main_page.connectionStatus != null && mainConnection) {
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

    private String createFileURL(String data) {
        String path = data;
        System.out.println("filePath " + path);
        //TODO get flask port from qrcode too
        return  "http://" + ForegroundService.url + ":5000/get?f=" + path;
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
        }else if (type.compareTo("info") == 0) {
            getInfo(data);
        }else if(type.compareTo("keyboard_input")==0){
            sendRemoteControlCommand(type,data);
        }else if(type.compareTo("mouse_input")==0){
            sendRemoteControlCommand(type,data);
        }

    }



    public Connection(ClipboardManager clipBoard, URI uri, String type, Object o) {
        this(clipBoard, uri);
        while (!isConnected()) ;
        if (type.compareTo("get_screenshot") == 0){
            this.o=o;
            getScreenshot();
        }

    }

    private void getScreenshot() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", "get_screenshot");
            obj.put("data", " ");
            Log.d("get_screenshot", "get_screenshot: " + obj.toString());
            String msg = obj.toString();
            mWs.send(msg);
        } catch (JSONException e) {
            e.printStackTrace();
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

    private void sendRemoteControlCommand(String type,String data) {
        if (!isConnected()){
            Toast.makeText(MainActivity.context, "Connection Failed or still connecting", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", type);
            obj.put("data", data);
            Log.d("send", "sendKeyboard_input: " + obj.toString());
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

    public void setAsMain() {
        this.mainConnection = true;
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
