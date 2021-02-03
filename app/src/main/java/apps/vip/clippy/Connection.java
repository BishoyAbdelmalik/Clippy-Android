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
    private  WebSocketClient mWs;
    public  static String lastRecieved="";
    Connection( ClipboardManager clipboardManager,String url, String port, String path){

        initializeDiscoveryListener();


        URI uri= null;
        try {
            uri = new URI( "ws://"+url+":"+port+"/"+path+"" );
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mWs = new WebSocketClient(uri)
        {
            @Override
            public void onMessage( String message ) {
                System.out.println(message);
                Log.d("testing connection", "onMessage: "+message);
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

    public Connection(ClipboardManager clipBoard, String url, String port, String send, String valueOf) {
        this(clipBoard,url,port,send);
        while (mWs.getReadyState() != ReadyState.OPEN);
        sendClipboard(valueOf);

    }
    public void sendClipboard(String text) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("type","clipboard");
            obj.put("data",text);
            Log.d("send", "sendClipboard: "+obj.toString());
            String msg=obj.toString();
            mWs.send(msg);
            mWs.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        mWs.close();
    }
    public void initializeDiscoveryListener() {
        String TAG="debug:";
        // Instantiate a new DiscoveryListener
        NsdManager.DiscoveryListener discoveryListener = new NsdManager.DiscoveryListener() {

            // Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found! Do something with it.
                Log.d(TAG, "Service discovery success" + service);
//                if (!service.getServiceType().equals(SERVICE_TYPE)) {
//                    // Service type is the string containing the protocol and
//                    // transport layer for this service.
//                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
//                } else if (service.getServiceName().equals(serviceName)) {
//                    // The name of the service tells the user what they'd be
//                    // connecting to. It could be "Bob's Chat App".
//                    Log.d(TAG, "Same machine: " + serviceName);
//                } else if (service.getServiceName().contains("NsdChat")){
//                    nsdManager.resolveService(service, resolveListener);
//                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(TAG, "service lost: " + service);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
//                nsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
//                nsdManager.stopServiceDiscovery(this);
            }
        };
    }
}
