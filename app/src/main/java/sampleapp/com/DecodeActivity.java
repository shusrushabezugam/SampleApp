package sampleapp.com;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.Result;
import java.text.ParseException;
import java.util.Date;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;
import static sampleapp.com.ValetDashboardActivity.duration;

public class DecodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

String time;
    private static final int REQUEST_CAMERA = 1;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        DatabaseReference mDatabse = FirebaseDatabase.getInstance().getReferenceFromUrl("https://valetticketapp.firebaseio.com/Users");
        StorageReference mStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://valetticketapp.appspot.com/Number_Plates");
        private ZXingScannerView scannerView;
        private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;
        TextView t;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            t=(TextView)findViewById(R.id.text2);
            scannerView = new ZXingScannerView(this);
            setContentView(scannerView);
            int currentApiVersion = Build.VERSION.SDK_INT;

            if(currentApiVersion >=  Build.VERSION_CODES.M)
            {
                if(checkPermission())
                {
                    Toast.makeText(getApplicationContext(), "Permission already granted!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    requestPermission();
                }
            }
        }

        private boolean checkPermission()
        {
            return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
        }

        private void requestPermission()
        {
            ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
        }

        @Override
        public void onResume() {
            super.onResume();

            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
                if (checkPermission()) {
                    if(scannerView == null) {
                        scannerView = new ZXingScannerView(this);
                        setContentView(scannerView);
                    }
                    scannerView.setResultHandler(this);
                    scannerView.startCamera();
                } else {
                    requestPermission();
                }
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            scannerView.stopCamera();
        }

        public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
            switch (requestCode) {
                case REQUEST_CAMERA:
                    if (grantResults.length > 0) {

                        boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                        if (cameraAccepted){
                            Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (shouldShowRequestPermissionRationale(CAMERA)) {
                                    showMessageOKCancel("You need to allow access to both the permissions",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                        requestPermissions(new String[]{CAMERA},
                                                                REQUEST_CAMERA);
                                                    }
                                                }
                                            });
                                    return;
                                }
                            }
                        }
                    }
                    break;
            }
        }

        private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
            new android.support.v7.app.AlertDialog.Builder(DecodeActivity.this)
                    .setMessage(message)
                    .setPositiveButton("OK", okListener)
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        }

        @Override
        public void handleResult(Result result) {
            final String myResult = result.getText();
            Log.d("QRCodeScanner", result.getText());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder.setTitle("Scan Result");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    scannerView.resumeCameraPreview(DecodeActivity.this);
                }
            });
            builder.setMessage(result.getText()+" "+decodeTime());
            AlertDialog alert1 = builder.create();
            alert1.show();
            StringBuilder t=new StringBuilder(40);
            t.append(result.getText());
            t.append(" ");
            t.append(decodeTime());
            time=t.toString();
            String[] time_ed=time.split(" ");
            int l=time_ed.length;
            String en=time_ed[l-1];
            String de=time_ed[l-2];
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            Date date1 = null;
            try {
                date1 = format.parse(en);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.println(date1);
            Date date2 = null;
            try {
                date2 = format.parse(de);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.println(date2);
            builder1.setTitle("Time Notification");

            long difference = date1.getTime() - date2.getTime();
            int hours=(int) ((difference/ (60 * 60 * 1000) % 24));
            int amount=0;
            if(hours>0&& hours<=1)amount+=10;else if(hours>1&&hours<=3)amount+=20;else if(hours>3&&hours<=4)amount+=30;

            builder1.setMessage("You have spent "+Integer.toString(hours)+ " hours"+"\n\n"+"Amount to be paid "+Integer.toString(amount));
            System.out.println(hours);


            builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    scannerView.resumeCameraPreview(DecodeActivity.this);
                }
            });
            AlertDialog alert2=builder1.create();
            alert2.show();
        }

      public String getTime(Result result)
      {
          StringBuilder t=new StringBuilder(40);
          t.append(result.getText());
          t.append(" ");
          t.append(decodeTime());
          time=t.toString();
          return time;
      }

        public final String decodeTime(){

           android.icu.util.Calendar cal = Calendar. getInstance();
            Date date=cal. getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            String formattedDate=dateFormat. format(date);
            return formattedDate;
        }

    }
