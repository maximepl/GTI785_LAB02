package maxandalex.peertopeer;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import maxandalex.peertopeer.QrCode.GeneratedQRCode;
import maxandalex.peertopeer.QrCode.QRCodeReader;
import maxandalex.peertopeer.Server.FilesList;
import maxandalex.peertopeer.Server.Server;

public class MainActivity extends AppCompatActivity {

    private Button btn_CreateQRCode, btn_ReadQRCode, btn_ViewPairedList, btn_GetItemList, btn_TestBluetooth;
    private static Context myContext;
    private final static int REQUEST_ENABLE_BT = 1;
    private InputStream inStream;
    private OutputStream outputStream;
    private static final UUID MY_UUID = UUID.fromString("0000110a-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.myContext = getApplicationContext();
        Contact.contactList.clear();
        CSVParser.ImportCSVToList();
        LocationService.getLocationManager(getAppContext());

        btn_CreateQRCode = (Button) findViewById(R.id.btn_createQRCode);
        btn_ReadQRCode = (Button) findViewById(R.id.btn_receiveQRCode);
        btn_ViewPairedList = (Button) findViewById(R.id.btn_viewPairedList);
        btn_GetItemList = (Button) findViewById(R.id.button6);
        btn_TestBluetooth = (Button) findViewById(R.id.testBlueTooth);

        btn_CreateQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Génération du code QR", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, GeneratedQRCode.class);
                startActivity(intent);
            }
        });

        btn_ReadQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, QRCodeReader.class);
                startActivity(intent);
            }
        });

        btn_ViewPairedList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PairingView.class);
                startActivity(intent);
            }
        });

        btn_GetItemList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*File[] fileList;
                fileList = FilesList.getFileList();
                Log.i("FILELIST", "Voici la file list : " + fileList[10].getName());*/
                Log.i("LocationService", "Ma location" + LocationService.getLocation());
            }
        });


        btn_TestBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter != null) {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                }

                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress(); // MAC address

                        //TODO TESTER EN CHANGEANT LE TRUE PAR LE NOM DE L'AUTRE DEVICE
                        if(deviceHardwareAddress.equals("3C:FA:43:73:16:E0")){
                            Log.i("BluetoothDevice", "DeviceName : " + deviceName);
                            BluetoothConnection bc = new BluetoothConnection(device);

                        }
                    }
                }



            }
        });
        //Server starter
        Server.StartServer();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                Toast.makeText(this, contents,Toast.LENGTH_LONG).show();
                // Handle successful scan
            } else if (resultCode == RESULT_CANCELED) {
                //Handle cancel
            }
        }
    }

    public static Context getAppContext() {
        return MainActivity.myContext;
    }
    private boolean hasPermission(String perm) {
        return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
    }

}
