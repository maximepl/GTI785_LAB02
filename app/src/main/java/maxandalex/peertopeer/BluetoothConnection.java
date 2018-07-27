package maxandalex.peertopeer;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothConnection extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    byte[] buffer;

    // Unique UUID for this application, you may use different
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public BluetoothConnection(BluetoothDevice device) {

        BluetoothSocket tmp = null;

        // Get a BluetoothSocket for a connection with the given BluetoothDevice
        try {
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mmSocket = tmp;

        //now make the socket connection in separate thread to avoid FC
        Thread connectionThread  = new Thread(new Runnable() {

            @Override
            public void run() {

                // Make a connection to the BluetoothSocket
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    mmSocket.connect();
                } catch (IOException e) {
                    Log.i("BluetoothConnection", "IOException : " + e);
                    //connection to device failed so close the socket
                    try {
                        mmSocket.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        });

        connectionThread.start();
        Log.i("BluetoothConnection", "Socket : " + mmSocket);

        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the BluetoothSocket input and output streams
        try {
            tmpIn = mmSocket.getInputStream();
            tmpOut = mmSocket.getOutputStream();
            buffer = new byte[1024];
        } catch (IOException e) {
            e.printStackTrace();
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {

        // Keep listening to the InputStream while connected
        while (true) {
            try {
                //read the data from socket stream
                mmInStream.read(buffer);
                // Send the obtained bytes to the UI Activity
            } catch (IOException e) {
                //an exception here marks connection loss
                //send message to UI Activity
                break;
            }
        }
    }

    public void write(byte[] buffer) {
        try {
            //write the data to socket stream
            mmOutStream.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
