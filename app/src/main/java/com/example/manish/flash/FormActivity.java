package com.example.manish.flash;

import android.*;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormActivity extends AppCompatActivity {

    private TextInputLayout name,age,details;
    private ImageView imageview;
    private Button post_btn;
    private TextView selectImgText;

    private RadioGroup radioGender, radioEye;
    private RadioButton radioGenderButton,RadioEyeButton;
    String Sex;


    private int PICK_IMAGE_CAMERA = 1;
    private int PICK_IMAGE_GALLERY = 2;
    public static final int MEDIA_TYPE_IMAGE = 3;
    private static final String IMAGE_DIRECTORY = "/Flash";
    private Uri imageURI;
    private Uri Cropped_ImageURI;

    private StorageReference mStorageRef;
    FirebaseDatabase db;
    DatabaseReference db_ref;

    private ProgressDialog mProgressDialog;
    int MY_PERMISSIONS_REQUEST_READ_CONTACTS=121, MY_PERMISSIONS_REQUEST_CAMERA=122;



    Uri mCropImageUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        getSupportActionBar().setTitle("Details");

        mProgressDialog = new ProgressDialog(this);

        name = (TextInputLayout) findViewById(R.id.name);
        age = (TextInputLayout) findViewById(R.id.age);
        details = (TextInputLayout) findViewById(R.id.details);

        selectImgText  =(TextView)findViewById(R.id.selectImgText);

        imageview = (ImageView)findViewById(R.id.image);
        post_btn = (Button)findViewById(R.id.send);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseDatabase.getInstance();
        db_ref = db.getReference("Data");

        radioGender = (RadioGroup)findViewById(R.id.radioGender);
        radioEye = (RadioGroup)findViewById(R.id.radioEye);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(FormActivity.this)     ;
                //selectImage();
            }
        });

        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startPosting();
            }
        });
        checkPermission();

    }

    public void checkPermission(){

        //Write External Storage Permission
        if (ContextCompat.checkSelfPermission(FormActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(FormActivity.this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(FormActivity.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }


        //Camera Permission
        if (ContextCompat.checkSelfPermission(FormActivity.this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(FormActivity.this,
                    android.Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(FormActivity.this,
                        new String[]{android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

    }

    private void selectImage() {

        CharSequence option[] = new CharSequence[]{"From Gallery", "From Camera"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Options");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Click Event for Items
                if(which == 0){
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), PICK_IMAGE_GALLERY);

                }
                if(which == 1) {
                    Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                    }
                    startActivityForResult(takePictureIntent, PICK_IMAGE_CAMERA);

                }

            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Cropped_ImageURI = result.getUri();
                imageview.setImageURI(Cropped_ImageURI);

                selectImgText.setText("Image Ready to Send");
                selectImgText.setTextColor(getResources().getColor(R.color.colorAccent));
            }

        }
    }

   /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {return;}
        if (requestCode == PICK_IMAGE_GALLERY) {
            if (data != null) {
                imageURI = data.getData();
                CropImage.activity(imageURI).start(this);}
        } else if (requestCode == PICK_IMAGE_CAMERA) {
            imageURI = data.getData();
            CropImage.activity(imageURI).start(this);}
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Cropped_ImageURI = result.getUri();
                imageview.setImageURI(Cropped_ImageURI);
                selectImgText.setText("Image Ready to Send");
                selectImgText.setTextColor(getResources().getColor(R.color.colorAccent));}
        }
    }*/

    private static String setFileName() {

        // Creating a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        String mediaFile = "Flash_" + timeStamp ;

        return mediaFile;
    }


    //------- POSTING DATA --------------
    private void startPosting() {

        //GENDER RADIO
        int selectedId = radioGender.getCheckedRadioButtonId();
        radioGenderButton = (RadioButton) findViewById(selectedId);
        Sex = radioGenderButton.getText().toString();
        //EYE RADIO
        int selectedId2 = radioEye.getCheckedRadioButtonId();
        RadioEyeButton = (RadioButton) findViewById(selectedId2);

        final String mName = name.getEditText().getText().toString();
        final String mAge = age.getEditText().getText().toString();
        final String mGender = Sex.substring(0,1);
        final String mEye= RadioEyeButton.getText().toString();
        final String mDetails = details.getEditText().getText().toString();


        mProgressDialog.setTitle("Uploading Details");
        mProgressDialog.setMessage("PLease wait while we upload the data");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();


        StorageReference filePath = mStorageRef.child("Images").child(setFileName() + ".jpg");
        filePath.putFile(Cropped_ImageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                @SuppressWarnings("VisibleForTests")Uri ImageUrl = taskSnapshot.getDownloadUrl();

                DatabaseReference newPost = db_ref.push();
                newPost.child("Name").setValue(mName);
                newPost.child("Age").setValue(mAge);
                newPost.child("Gender").setValue(mGender);
                newPost.child("Eye").setValue(mEye);
                newPost.child("Details").setValue(mDetails);
                newPost.child("Date").setValue(ServerValue.TIMESTAMP);
                newPost.child("Image").setValue(ImageUrl.toString());

                Toast.makeText(getApplicationContext(), " Date Sent ", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();

                Intent main_Intent = new Intent(getApplicationContext(),MainActivity.class);
                main_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main_Intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads

                Toast.makeText(getApplicationContext(), "Error: failed",
                        Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }



}


