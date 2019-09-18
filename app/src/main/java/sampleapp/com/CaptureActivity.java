package sampleapp.com;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.drawable.BitmapDrawable;
        import android.icu.text.SimpleDateFormat;
        import android.icu.util.Calendar;
        import android.net.Uri;
        import android.provider.MediaStore;
        import android.support.annotation.NonNull;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.firebase.FirebaseApp;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.ml.vision.FirebaseVision;
        import com.google.firebase.ml.vision.common.FirebaseVisionImage;
        import com.google.firebase.ml.vision.text.FirebaseVisionText;
        import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
        import com.google.firebase.storage.FirebaseStorage;
        import com.google.firebase.storage.StorageMetadata;
        import com.google.firebase.storage.StorageReference;
        import com.google.firebase.storage.UploadTask;
        import com.google.zxing.BarcodeFormat;
        import com.google.zxing.MultiFormatWriter;
        import com.google.zxing.WriterException;
        import com.google.zxing.common.BitMatrix;
        import com.journeyapps.barcodescanner.BarcodeEncoder;

        import java.io.ByteArrayOutputStream;
        import java.text.ParseException;
        import java.util.Date;
        import java.util.List;
        import java.util.UUID;

        import id.zelory.compressor.Compressor;

public class CaptureActivity extends AppCompatActivity {

            private Button snapBtn;
            private Button detectBtn;
            protected ImageView imageView;
            protected TextView txtView;
            private Bitmap imageBitmap;
            FirebaseStorage storage;
            StorageReference mStorageReference;
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                FirebaseApp.initializeApp(this);
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_capture);
                snapBtn = findViewById(R.id.snapBtn);
                detectBtn = findViewById(R.id.detectBtn);
              //  QRbtn = findViewById(R.id.qrcode);
                imageView = findViewById(R.id.imageView);
                txtView = findViewById(R.id.txtView);
                String url= FirebaseAuth.getInstance().getCurrentUser().getUid();
                mStorageReference = FirebaseStorage.getInstance().getReference("Customer1/"+UUID.randomUUID().toString());
                snapBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dispatchTakePictureIntent();
                    }
                });
                detectBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        detectTxt();
                    }
                });

        }

            static final int REQUEST_IMAGE_CAPTURE = 1;

            protected void dispatchTakePictureIntent() {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }

            @Override
            protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    imageBitmap = (Bitmap) extras.get("data");

                    imageView.setImageBitmap(imageBitmap);
                }
            }

            protected void detectTxt() {
                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
                FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
                detector.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText result) {
                        processTxt(result);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    }
                });
            }

            protected void processTxt(FirebaseVisionText text) {
                Date present = new Date();
                present.getTime();

              List<FirebaseVisionText.TextBlock> blocks = text.getTextBlocks();
              if (blocks.size() == 0) {
                   Toast.makeText(CaptureActivity.this, "No Text :(", Toast.LENGTH_LONG).show();
                    return;
                }
                for (int i = 0; i < blocks.size(); i++) {
                    List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
                    for (int j = 0; j < lines.size(); j++) {
                        txtView.setTextSize(12);
                        txtView.setText(lines.get(j).getText());
                              /* List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                               for (int k = 0; k < elements.size(); k++) {
                                    txtView.setTextSize(12);
                                    txtView.setText(elements.get(k).getText());
                                }*/

                    }

                }

                encodeText();
                uploadImage();
                encodeTime();

            }



            protected void encodeText()
            {

                String text=txtView.getText().toString();
                StringBuilder s=new StringBuilder(40);
                s.append(text);
                s.append(" ");
                s.append(encodeTime());
                String new_txt=s.toString();
                System.out.print(new_txt);
                // txtView.setText(new_txt);
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(new_txt, BarcodeFormat.QR_CODE, 200, 200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                    Bitmap resized = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                    resized = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*0.8), (int)(bitmap.getHeight()*0.8), true);
                    imageView.setImageBitmap(resized);
                    /*Bundle b=new Bundle();
                    b.putParcelable("img",bitmap);//put your bitmap in
                    Intent intent = new Intent();
                    intent.setClass(CaptureActivity.this, CustDashboardActivity.class);
                    intent.putExtras(b);*/
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
            protected final String encodeTime(){
                android.icu.util.Calendar cal = Calendar.getInstance();
                Date date=cal. getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                String formattedDate=dateFormat.format(date);
                return formattedDate;
            }

    public void uploadImage()
    {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mStorageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });
        }
        public void getDownloadLink()
        {
            mStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
        public void getUploadTime()
        {
            mStorageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    System.out.println(storageMetadata.getCreationTimeMillis());
                }
            });
        }
        }