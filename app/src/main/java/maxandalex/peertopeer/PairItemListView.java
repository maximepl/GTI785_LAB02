package maxandalex.peertopeer;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PairItemListView extends AppCompatActivity {

    private Button btn_Back;
    private ListView fileList;
    private TextView txt_PairName;

    private ArrayList<String> fileNames = new ArrayList<>();
    private String pairFilesJSON, pairIpAdress, pairName;
    private File[] pairFileList;
    private final Type FILE_TYPE = new TypeToken<File[]>() {
    }.getType();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pairitemlist);

        btn_Back = findViewById(R.id.btn_back_pairList);
        fileList = findViewById(R.id.pairedFileView);
        txt_PairName = findViewById(R.id.pairName);

        pairName = getIntent().getStringExtra("pairName");
        pairIpAdress = getIntent().getStringExtra("pairIpAdress");
        pairFilesJSON = getIntent().getStringExtra("pairFiles");
        pairFileList = new Gson().fromJson(pairFilesJSON, FILE_TYPE);

        txt_PairName.setText(pairName);

        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(pairFileList.length != 0){
            for(int i = 0; i < pairFileList.length; i++){
                fileNames.add(pairFileList[i].getName());
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    fileNames);

            fileList.setAdapter(arrayAdapter);
            fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                    Log.i("onItemClick", "Click on : " + pairFileList[position].getAbsolutePath());
                    //TODO SAVOIR LE LIENS DES AUTRES EQUIPES

                    Uri uri = Uri.parse("http://" + pairIpAdress + "/getFile/" + pairFileList[position].getPath());
                    //Uri uri = Uri.parse("http://" + pairIpAdress + "/getFile/" + "$path" +  pairFileList[position].getPath());

                    Log.i("DM_REQUEST", "duri: " + uri);
                    /*DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setTitle()
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, pairFileList[position].getName());
                    request.setMimeType()

                    try {
                        dm.enqueue(request);
                    } catch (java.lang.NullPointerException){
                        Log.i("DM_REQUEST", "dm.enqueue(request): " + "Problème lors de l'accès au fichier");
                    }*/
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Le transfert de la liste de fichier a échoué, veuillez essayer de nouveau", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
