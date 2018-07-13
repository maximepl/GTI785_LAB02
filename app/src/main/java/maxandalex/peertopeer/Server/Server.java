package maxandalex.peertopeer.Server;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import fi.iki.elonen.NanoHTTPD;
import maxandalex.peertopeer.QrCode.GeneratedQRCode;

import static android.content.Context.WIFI_SERVICE;

public class Server extends NanoHTTPD {

    private Activity mainActivity;
    //extrait de JSON trouver sur https://www.w3schools.com/js/js_json_intro.asp
    private String JSONSample = "{ \"name\":\"John\", \"age\":31, \"city\":\"New York\" }";

    public Server(String hostname, int port) {
        super(hostname, port);
        init(mainActivity);
    }

    private void init(Activity mainActivity){
        this.mainActivity = mainActivity;
    }

    @Override
    public Response serve(IHTTPSession session){
        if(session.getUri().contains("/files/")){
            return new Response(Response.Status.OK, MIME_PLAINTEXT, JSONSample);
        }
        return new Response(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "NOT FOUND");
    }

    public static void StartServer(){
        try {
            String ip = GeneratedQRCode.getLocalIpAddress();
            Server serveur = new Server(ip, 8080);
            serveur.start();
            Log.i("SERVER","Server running at: " + ip + ":" );
            Log.i("SERVER","Server is up? " + serveur.isAlive()  );

        }
        catch(Exception e){
            Log.i("SERVER","The server can't be started : " + e);
        }
    }
}
