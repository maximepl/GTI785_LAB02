package maxandalex.peertopeer.Server;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Tasker {

    // Inspirer du vidéo https://www.youtube.com/watch?v=_7r_vdwmW0o
    public static class GetFiles extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params){

            Log.i("TASKER", "Je go dans le tasker bébé : " +params[0] );

            HttpURLConnection connection = null;
            BufferedReader reader = null ;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();

                String line = "";
                while((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                return buffer.toString();

            }catch(MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                if(connection != null)
                    connection.disconnect();
                try {
                    if(reader != null)
                        reader.close();
                }catch(IOException e){
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            Log.i("SERVER","Server result" + result);
        }
    }
}
