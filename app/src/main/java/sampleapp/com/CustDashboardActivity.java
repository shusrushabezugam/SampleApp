package sampleapp.com;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import static sampleapp.com.ValetDashboardActivity.payment;
import java.text.ParseException;

public class CustDashboardActivity extends AppCompatActivity {

    ImageView decode,imageView;
    ImageButton pay;
    Button generate;private Bitmap imageBitmap1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust_dashboard);
        generate = (Button) findViewById(R.id.code);
        pay = (ImageButton) findViewById(R.id.pay);
        decode = (ImageView) findViewById(R.id.decode);
        imageView=(ImageView) findViewById(R.id.imageView);
        //mEditText=(EditText)view.findViewById(R.id.editText);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustDashboardActivity.this, PaymentActivity.class));
            }
            });
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadPhoto();
                /*String text = "Hello";
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    decode.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }*/

            }
        });
    }
   private void downloadPhoto()
    {
        /*StorageReference storageReference = FirebaseStorage.getInstance().getReference("Customer1.jpg") ;
        String url=storageReference.getDownloadUrl().toString();
        Glide.with(this).load(url).into(decode);*/
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference qrReference= storageReference.child("Customer1/3ca42127-8ddc-428d-a569-400bb680b106");

        final long ONE_MEGABYTE = 1024 * 1024;
        qrReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                decode.setImageBitmap(bmp);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
            }
        });
    }
}