package apps.vip.clippy;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Connection {
    private final WebSocketClient mWs;
    public static String lastRecieved = "";
    boolean mainConnection = false;
    Object o;
    public Connection(ClipboardManager clipboardManager, URI uri, boolean main) throws Exception {


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
                        String[] arr = data.trim().split("\\s+");
                        Set<String> links = new HashSet<>();
                        for (String s : arr) {
                            if (Patterns.WEB_URL.matcher(s).matches()) {
                                links.add(s);
                            }
                        }
                        Pattern p = Pattern.compile("(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}");
                        Matcher m = p.matcher(data);
                        while (m.find()) {
                            String found = m.group();
                            System.out.println(found);
                            ForegroundService.createPhoneNumberNotification(found);
                        }
                        System.out.println("Links in connection " + Arrays.toString(links.toArray()));
                        ForegroundService.createLinksNotification(links.toArray(new String[0]));
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
                                ForegroundService.playingTXT = title;
                                ForegroundService.thumbnailUrl = "http://" + ForegroundService.url + ":"+ForegroundService.flask_port + thumb;
                                if(media_control.playingThumb !=null) {
                                    setMediaThumb(ForegroundService.thumbnailUrl);
                                }
                                System.out.println("playing title from connection " + title);
                            }
                        }

                    } else if (type.compareTo("file_path") == 0) {
                        new downloadFile(createFileURL(data), ForegroundService.context);
                    } else if(type.compareTo("file_screenshot") == 0) {
                        System.out.println("screenshot: "+data);
                        screenshot_page.url=createFileURL(data);
                        if(o!=null){
                            new DownloadImageTask((ImageView) o)
                                    .execute(screenshot_page.url);
                        }
                    } else if(type.compareTo("PC_name") == 0) {
                        ForegroundService.PCname = data;
                        Handler h = new Handler(ForegroundService.context.getMainLooper());
                        // Although you need to pass an appropriate context
                        h.post(() -> {
                            main_page.PCname.setText(ForegroundService.PCname);
                        });


                    }else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onOpen( ServerHandshake handshake ) {
                if(mainConnection){
                    System.out.println("opened main connection");
                }else {
                    System.out.println("opened connection");
                }
                ForegroundService.connected = true;
                if (main_page.connectionStatus != null && mainConnection) {
                    main_page.connectionStatus.setText("Connected");
                    try {
                        ForegroundService.getPCName();
                    } catch (Exception e) {
                        System.err.println(e);
                    }
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
                if (mainConnection && ForegroundService.started) {
                    new serviceControl().killService(ForegroundService.context);
                }
            }

            @Override
            public void onError(Exception ex) {
                ForegroundService.connected = false;
                ex.printStackTrace();
                if (mainConnection && ForegroundService.started) {
                    new serviceControl().killService(ForegroundService.context);
                }
            }

        };
        this.mainConnection = main;
        new Handler(ForegroundService.context.getMainLooper()).post(() -> {
            if (main_page.connectionStatus != null && mainConnection) {
                main_page.connectionStatus.setText("Connecting");
            }
        });
        //open websocket
        if(mainConnection) {
            if(main) {
                SharedPreferences sharedPref = ForegroundService.context.getSharedPreferences(ForegroundService.context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("ip",ForegroundService.url);
                editor.putString("port",ForegroundService.port);
                editor.putString("flaskPort",ForegroundService.flask_port);
                editor.apply();
            }
            final Runnable r = () -> {
                try {
                    boolean result=mWs.connectBlocking(1, TimeUnit.MINUTES);
                    if(!result){

                        // Although you need to pass an appropriate context
                        new Handler(ForegroundService.context.getMainLooper()).post(() -> {
                            if (main_page.connectionStatus != null) {
                                main_page.connectionStatus.setText("Failed to connect");
                            }
                            Toast.makeText(ForegroundService.context,"Failed to connect",Toast.LENGTH_LONG).show();
                        });
                        new serviceControl().killService(ForegroundService.context);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
            new Thread(r).start();
        }else{
            mWs.connect();
        }

    }

    public void setMediaThumb(String thumbnailUrl) {
        try {
            // Using an Async solution from SO
            // https://stackoverflow.com/questions/2471935/how-to-load-an-imageview-by-url-in-android
            new DownloadImageTask(media_control.playingThumb)
                    .execute(thumbnailUrl);
        } catch (Exception e) {
            System.err.println("lmao get fukd user");
        }
    }

    private String createFileURL(String data) {
        String path = data;
        System.out.println("filePath " + path);
        return  "http://" + ForegroundService.url + ":"+ForegroundService.flask_port+"/get?f=" + path;
    }

    public boolean isConnected() {
        return mWs.getReadyState() == ReadyState.OPEN;
    }

    public Connection(ClipboardManager clipBoard, URI uri, String type, String data) throws Exception {
        this(clipBoard, uri,false);
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
        }else if(type.compareTo("PC_name")==0){
            getPCName();
        }

    }



    public Connection(ClipboardManager clipBoard, URI uri, String type, Object o) throws Exception {
        this(clipBoard, uri,false);
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

    private void getPCName() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", "PC_name");
            obj.put("data", ForegroundService.PCname);
            Log.d("send", "PC_name: " + obj.toString());
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

    public void sendRemoteControlCommand(String type,String data) {
        if (!isConnected()){
            Toast.makeText(ForegroundService.context, "Connection Failed or still connecting", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", type);
            obj.put("data", data);
            Log.d("send", "sendKeyboard_input: " + obj.toString());
            String msg = obj.toString();
            mWs.send(msg);
//            mWs.close();
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
        if(mWs.getReadyState()!=ReadyState.CLOSED||mWs.getReadyState()!=ReadyState.CLOSING) {
            mWs.close();
        }
    }

    static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
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
