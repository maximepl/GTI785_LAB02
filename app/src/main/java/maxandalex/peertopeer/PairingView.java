package maxandalex.peertopeer;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import maxandalex.peertopeer.Server.FilesList;
import maxandalex.peertopeer.Server.Tasker;

public class PairingView extends AppCompatActivity {

    private Button btn_Back, btn_filter_name, btn_filter_ip, btn_filter_distance;
    private ListView txt_PairedList;
    private ImageButton btn_Refresh;

    private Double distance;

    private boolean isNameFilter = false;
    private boolean isIpFilter = false;
    private boolean isDistanceFilter = false;
    private boolean isResponseReturned = false;

    private ArrayList<String> pairingNames = new ArrayList<>();
    private ArrayList<Contact> pv_ContactList = new ArrayList<>();
    private String pairFiles;
    private String pairIpAdress;
    private String isOnline = "\u25CF"; //"\u25CF"=big bullet "\u25A0"=big square
    //private Drawable isOnline; //CRISS DE MARDE
    //TODO FAIRE EN SORTE QUE LE BULLET SOIT COLORÉ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pairingview);

        pv_ContactList = Contact.contactList;

        btn_Back = findViewById(R.id.btn_back_pairingview);
        btn_Refresh = findViewById(R.id.btn_refresh);
        btn_filter_name = findViewById(R.id.btn_filter_name);
        btn_filter_ip = findViewById(R.id.btn_filter_ip);
        btn_filter_distance = findViewById(R.id.btn_filter_distance);
        txt_PairedList = findViewById(R.id.pairedTextView);
        //isOnline = getResources().getDrawable(R.drawable.connectioncircle);

        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_Refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO FAIRE LE PING DE TOUS LES CONTACTS!
                showList();
            }
        });

        btn_filter_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNameFilter){
                    Collections.sort(pairingNames, Collections.<String>reverseOrder());
                    refreshAdapter();
                    isNameFilter = false;
                } else {
                    Collections.sort(pairingNames);
                    refreshAdapter();
                    isNameFilter = true;
                }
            }
        });

        btn_filter_ip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isIpFilter){
                    Collections.sort(pv_ContactList, new SortByIpDesc());
                    showList();
                    isIpFilter = false;
                } else {
                    Collections.sort(pv_ContactList, new SortByIpAsc());
                    showList();
                    isIpFilter = true;
                }
            }
        });

        btn_filter_distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDistanceFilter){
                    Collections.sort(pv_ContactList, new SortByDistDesc());
                    showList();
                    isDistanceFilter = false;
                } else {
                    Collections.sort(pv_ContactList, new SortByDistAsc());
                    showList();
                    isDistanceFilter = true;
                }
            }
        });


        //TODO TESTING PURPOSE
        //Contact.contactList.add(new Contact("Asselin", "Asselinonino", "160.105.123.23"));

        showList();
    }

    private void showList(){
        pairingNames.clear();
        if(pv_ContactList.size() != 0){
            for(int i = 0; i < pv_ContactList.size(); i++){
                if(!pv_ContactList.get(i).isOnline()){
                    isOnline = "\u25A0";
                }
                try{
                    new Tasker.GetFiles().execute("http://" + pv_ContactList.get(i).getIp() + "/getPosition/").get();
                } catch (java.lang.InterruptedException e){
                } catch (java.util.concurrent.ExecutionException e){
                }
                String[] taskerResonse = Tasker.response.split(",");
                Double longitude = Double.parseDouble(taskerResonse[0]);
                Double latitude = Double.parseDouble(taskerResonse[1]);
                Location loc = new Location("pairLoc");
                loc.setLongitude(longitude);
                loc.setLatitude(latitude);
                distance = Double.parseDouble(new DecimalFormat("##.##").format(LocationService.getLocation().distanceTo(loc)));
                Log.i("distance", "distance: " + distance);

                pairingNames.add(pv_ContactList.get(i).getName() + " | "
                        + pv_ContactList.get(i).getIp() + " | "
                        + distance + " | "
                        + isOnline); //TODO - FIX POUR VOIR LES CERCLES ROUGE OU VERT

                refreshAdapter();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Votre liste de contact est vide, veuillez en ajouter", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void refreshAdapter(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                pairingNames);

        txt_PairedList.setAdapter(arrayAdapter);
        txt_PairedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if(pairingNames.get(position).contains("\u25CF")){
                    PopupMenu popMenu = new PopupMenu(PairingView.this,view);
                    popMenu.getMenuInflater().inflate(R.menu.popup_menu, popMenu.getMenu());

                    popMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getTitle().charAt(0) == 'A'){

                                pairIpAdress = pv_ContactList.get(position).getIp();

                                //TODO TESTING PURPOSE
                                //pairFiles =  FilesList.listToJson();
                                try{
                                    new Tasker.GetFiles().execute("http://" + pairIpAdress + "/getFileList/").get();
                                } catch (java.lang.InterruptedException e){
                                } catch (java.util.concurrent.ExecutionException e){
                                }
                                pairFiles = Tasker.response;

                                Intent intent = new Intent(PairingView.this, PairItemListView.class);
                                intent.putExtra("pairName", pv_ContactList.get(position).getName());
                                intent.putExtra("pairFiles", pairFiles);
                                intent.putExtra("pairIpAdress", pairIpAdress);
                                startActivity(intent);
                            } else {
                                //TODO FAIRE LE HANDLER DE PARTAGE DE LISTE DE CONTACT AVEC BLUETOOTH

                                    /*BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                    if (mBluetoothAdapter != null) {
                                        if (!mBluetoothAdapter.isEnabled()) {
                                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                                        }
                                    }

                                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                                    if (pairedDevices.size() > 0) {
                                        // There are paired devices. Get the name and address of each paired device.
                                        for (BluetoothDevice device : pairedDevices) {
                                            String deviceName = device.getName();
                                            String deviceHardwareAddress = device.getAddress(); // MAC address
                                            Log.i("BLUETOOTH", "Get paired device name: " + deviceName + " " + deviceHardwareAddress);

                                            //TODO TESTER EN CHANGEANT LE TRUE PAR LE NOM DE L'AUTRE DEVICE
                                            if(true){
                                                ParcelUuid[] uuids = device.getUuids(); //(uuids[0].getUuid())
                                                try {
                                                    BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                                                    socket.connect();

                                                    outputStream = socket.getOutputStream();
                                                    inStream = socket.getInputStream();

                                                } catch (IOException e) {
                                                    Log.e("FuckMeSideWays", "Error occurred when creating input stream", e);
                                                }
                                            }
                                        }
                                    }*/
                            }
                            return true;
                        }
                    });
                    popMenu.show();
                } else {
                    Toast.makeText(getApplicationContext(), "Le pair n'est pas en ligne", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Double getComaparedDistance(String ip){
        try{
            new Tasker.GetFiles().execute("http://" + ip + "/getPosition/").get();
        } catch (java.lang.InterruptedException e){
        } catch (java.util.concurrent.ExecutionException e){
        }
        String[] taskerResonse = Tasker.response.split(",");
        Double longitude = Double.parseDouble(taskerResonse[0]);
        Double latitude = Double.parseDouble(taskerResonse[1]);
        Location loc = new Location("pairLoc");
        loc.setLongitude(longitude);
        loc.setLatitude(latitude);
        distance = Double.parseDouble(new DecimalFormat("##.##").format(LocationService.getLocation().distanceTo(loc)));
        Log.i("distance", "distance: " + distance);
        return distance;
    }

    class SortByIpAsc implements Comparator<Contact>{
        public int compare(Contact a, Contact b){
            return String.valueOf(a.getIp()).compareTo(String.valueOf(b.getIp()));
        }
    }
    class SortByIpDesc implements Comparator<Contact>{
        public int compare(Contact a, Contact b){
            return String.valueOf(b.getIp()).compareTo(String.valueOf(a.getIp()));
        }
    }

    class SortByDistAsc implements Comparator<Contact>{
        public int compare(Contact a, Contact b){
            return Double.valueOf(a.getDistance()).compareTo(Double.valueOf(b.getDistance()));
        }
    }
    class SortByDistDesc implements Comparator<Contact>{
        public int compare(Contact a, Contact b){
            return Double.valueOf(b.getDistance()).compareTo(Double.valueOf(a.getDistance()));
        }
    }
}