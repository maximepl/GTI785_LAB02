package maxandalex.peertopeer.QrCode;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.Result;
import com.google.zxing.qrcode.encoder.QRCode;

import java.io.IOException;

import maxandalex.peertopeer.CSVParser;
import maxandalex.peertopeer.Contact;
import maxandalex.peertopeer.R;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeReader extends AppCompatActivity {

    private Button btn_Back;
    SurfaceView cameraPreview;
    TextView txtResult;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraPreview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcodereader);

        cameraPreview = (SurfaceView) findViewById(R.id.QRCode);
        txtResult = (TextView) findViewById(R.id.txt_Result);
        btn_Back = (Button) findViewById(R.id.btn_backk);

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640,480)
                .setAutoFocusEnabled(true)
                .build();

        //AddEvent
        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                //Request permission
                ActivityCompat.requestPermissions(QRCodeReader.this,
                            new String[] {Manifest.permission.CAMERA}, RequestCameraPermissionID);
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();
                if (qrCodes.size() != 0) {
                    txtResult.post(new Runnable() {
                        @Override
                        public void run() {
                            cameraSource.stop();
                            cameraSource.release();
                            finish();
                            Vibrator vibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            //vibrator.vibrate(500);
                            String[] qrCodeResult;
                            qrCodeResult = qrCodes.valueAt(0).displayValue.split(",");
                            qrCodeResult[0] = qrCodeResult[0].substring(3);
                            qrCodeResult[1] = qrCodeResult[1].substring(5);
                            qrCodeResult[2] = qrCodeResult[2].substring(3);
                            Toast.makeText(getApplicationContext(), qrCodeResult[1] + " a été ajouté à votre liste de pairs", Toast.LENGTH_SHORT).show();
                            Contact receivedContact = new Contact(qrCodeResult[0], qrCodeResult[1], qrCodeResult[2]);

                            if(Contact.contactList.size() == 0){
                                Contact.contactList.add(receivedContact);
                                CSVParser.ExportListToCSV(Contact.contactList, false, true);
                            } else {
                                for(int i = 0; i < Contact.contactList.size(); i++){
                                    if(Contact.contactList.get(i).getId() == qrCodeResult[0]){
                                        Toast.makeText(getApplicationContext(), qrCodeResult[1] + " est déjà dans votre liste de pairs", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Contact.contactList.add(receivedContact);
                                        CSVParser.ExportListToCSV(Contact.contactList, false, true);
                                    }

                                }
                            }
                        }
                    });
                }
            }
        });

        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
