package sampleapp.com;
/*
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.cloud.text.FirebaseVisionCloudDocumentTextDetector;
import com.google.firebase.ml.vision.cloud.text.FirebaseVisionCloudText;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sampleapp.com.LoginActivity;
public class ScanActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private StorageReference mStorageRef;

    private Button mButton, show_text;
    private Button mCloudButton;
    private Bitmap mSelectedImage;
    ImageView imageView;
    String s = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        imageView = findViewById(R.id.image_view);
        mButton = findViewById(R.id.button_text);
        show_text = findViewById(R.id.show_text);
        mCloudButton = findViewById(R.id.button_cloud_text);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runTextRecognition();
            }
        });
        show_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showText(s);
            }
        });
        Spinner dropdown = findViewById(R.id.spinner);
        String[] items = new String[]{"Image 1", "Image 2", "Image 3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout
                .simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        //dropdown.setOnItemSelectedListener(this);
    }

    private void runTextRecognition() {
        s = "";
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mSelectedImage);
        FirebaseVisionTextDetector detector = FirebaseVision.getInstance()
                .getVisionTextDetector();
        mButton.setEnabled(false);
        detector.detectInImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {
                                processTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                mButton.setEnabled(true);
                                e.printStackTrace();
                            }
                        });
    }

    private String processTextRecognitionResult(FirebaseVisionText texts) {
        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            Toast.makeText(getApplicationContext(), "No text found", Toast.LENGTH_LONG).show();
            return " ";
        }
        s = "";
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    s += elements.get(k).getText() + " ";
                }
            }
        }
        String pattern = "[A-Z]{1,3} [A-Z]{1,2} [0-9]{1,4}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(s);
        //showText(m.toString());
        return s;
    }
}

/*
    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        final String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference uidRef = rootRef.child("Users").child(
                FirebaseAuth.getInstance().getUid());
        uidRef.child("imageUrl");
        ValueEventListener valueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                if (Objects.equals(dataSnapshot.child("type").getValue(String.class), "Valet")) {
                    uidRef.setValue(imageEncoded);

                } else {
                    Toast.makeText(ScanActivity.this, "Failed uploading. Please Try Again", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }*/

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*   public void onItemSelected(AdapterView parent, View v, int position, long id) {
        switch (position) {
            case 0:
                mSelectedImage = ((BitmapDrawable)ResourcesCompat.getDrawable(this.getResources(), R.drawable.np_4, null)).getBitmap();
                break;
            case 1:
                mSelectedImage = ((BitmapDrawable) ResourcesCompat.getDrawable(this.getResources(), R.drawable.np_2, null)).getBitmap();
                break;
            case 2:
                mSelectedImage = ((BitmapDrawable)ResourcesCompat.getDrawable(this.getResources(), R.drawable.tamil_signboard1, null)).getBitmap();
                break;
        }

        imageView.setImageBitmap(mSelectedImage);
    }
 /*   @Override
    public void onNothingSelected(AdapterView parent) {
    }
    private void showText(String text){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(text);
        alert.setIcon(ContextCompat.getDrawable(this, R.mipmap.ic_launcher));
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }

        };
    }
   }
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;

import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;


public class ScanActivity extends AppCompatActivity {

    private Button capture;
    private EditText mPostTitle;
    private EditText mPostDesc;
    private Button mSubmitBtn;
    private ProgressBar mProgress;
    private DatabaseReference mDatabase;

    private Uri mImageUri = null;


    private static final int CAMERA_REQUEST_CODE = 1;

    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        capture = (Button) findViewById(R.id.button_capture);


       // mProgress = new ProgressBar(this);

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                }

            }
        });

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
    }


    private void startPosting() {

        // mProgress.setProgress(10);


        final String title_val = mPostTitle.getText().toString().trim();
        final String desc_val = mPostDesc.getText().toString().trim();
        if (!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && mImageUri != null) {

            // mProgress.
            StorageReference filepath = mStorage.child("NP_Images").child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getUploadSessionUri();

                    DatabaseReference newPost = mDatabase.push();
                    newPost.child("title").setValue(title_val);
                    newPost.child("desc").setValue(desc_val);
                    newPost.child("image").setValue(downloadUrl.toString());


                    // mProgress.dismiss();
                    startActivity(new Intent(ScanActivity.this,CustDashboardActivity.class));
                }
            });
        }
    }
}*/
 /*public class ScanActivity  extends AppCompatActivity {
    String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    String pictureFile = "ZOFTINO_" + timeStamp;
    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    File image;

    {
        try {
            image = File.createTempFile(pictureFile,  ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


     pictureFilePath=image.getAbsolutePath();
return image;
    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
if (cameraIntent.resolveActivity(getPackageManager()) != null) {
        startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);

        File pictureFile = null;
        try {
            pictureFile = getPictureFile();
        } catch (IOException ex) {
            Toast.makeText(this,
                    "Photo file can't be created, please try again",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (pictureFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.zoftino.android.fileprovider",
                    pictureFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);
        }
    }
    private void addToCloudStorage() {
        File f = new File(pictureFilePath);
        Uri picUri = Uri.fromFile(f);
        final String cloudFilePath = deviceIdentifier + picUri.getLastPathSegment();

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReference();
        StorageReference uploadeRef = storageRef.child(cloudFilePath);

        uploadeRef.putFile(picUri).addOnFailureListener(new OnFailureListener(){
            public void onFailure(@NonNull Exception exception){
                Log.e(TAG,"Failed to upload picture to cloud storage");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){
                Toast.makeText(ScanActivity.this,
                        "Image has been uploaded to cloud storage",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}*/

/*
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.net.Uri;

import android.support.annotation.NonNull;

import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;

    public class ScanActivity extends AppCompatActivity implements View.OnClickListener {

        private Bitmap myBitmap;
        private ImageView myImageView;
        private TextView myTextView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_scan);

            myTextView = findViewById(R.id.textView);
            myImageView = findViewById(R.id.imageView);
            findViewById(R.id.checkText).setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.checkText:
                    if (myBitmap != null) {
                        runTextRecog();
                    }
                    break;

            }
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case WRITE_STORAGE:
                        checkPermission(requestCode);
                        break;
                    case SELECT_PHOTO:
                        Uri dataUri = data.getData();
                        String path = MyHelper.getPath(this, dataUri);
                        if (path == null) {
                            myBitmap = MyHelper.resizePhoto(photo, this, dataUri, myImageView);
                        } else {
                            myBitmap = MyHelper.resizePhoto(photo, path, myImageView);
                        }
                        if (myBitmap != null) {
                            myTextView.setText(null);
                            myImageView.setImageBitmap(myBitmap);
                        }
                        break;

                }
            }
        }

        private void runTextRecog() {
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(myBitmap);
            FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();
            detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText texts) {
                    processExtractedText(texts);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure
                        (@NonNull Exception exception) {
                    Toast.makeText(MainActivity.this,
                            "Exception", Toast.LENGTH_LONG).show();
                }
            });
        }

        private void processExtractedText(FirebaseVisionText firebaseVisionText) {
            myTextView.setText(null);
            if (firebaseVisionText.getBlocks().size() == 0) {
                myTextView.setText(R.string.no_text);
                return;
            }
            for (FirebaseVisionText.Block block : firebaseVisionText.getBlocks()) {
                myTextView.append(block.getText());

            }
        }*/

 //   }
 public class ScanActivity extends AppCompatActivity
 {

 }