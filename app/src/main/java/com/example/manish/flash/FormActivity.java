package com.example.manish.flash;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

public class FormActivity extends AppCompatActivity {

    private TextInputLayout name,age,gender,eye,details;
    private ImageView imageview;
    private Button post_btn;
    private TextView selectImgText;

    private RadioGroup radioGender, radioEye;
    private RadioButton radioGenderButton,RadioEyeButton;
    String Sex;


    private int PICK_IMAGE_CAMERA = 1;
    private int PICK_IMAGE_GALLERY = 2;
    private static final String IMAGE_DIRECTORY = "/Flash";
    private Uri imageURI;
    private Uri Cropped_ImageURI;

    private StorageReference mStorageRef;
    FirebaseDatabase db;
    DatabaseReference db_ref;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        getSupportActionBar().setTitle("Details");

        mProgressDialog = new ProgressDialog(this);

        name = (TextInputLayout) findViewById(R.id.name);
        age = (TextInputLayout) findViewById(R.id.age);
        //eye = (TextInputLayout) findViewById(R.id.eye);
        //gender = (TextInputLayout) findViewById(R.id.gender);
        details = (TextInputLayout) findViewById(R.id.details);

        selectImgText  =(TextView)findViewById(R.id.selectImgText);

        imageview = (ImageView)findViewById(R.id.image);
        post_btn = (Button)findViewById(R.id.send);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseDatabase.getInstance();
        db_ref = db.getReference("Data");

        radioGender = (RadioGroup)findViewById(R.id.radioGender);
        radioEye = (RadioGroup)findViewById(R.id.radioEye);


        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startPosting();
            }
        });

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

                        startActivityForResult(takePictureIntent, PICK_IMAGE_CAMERA);
                    }

                }

            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == PICK_IMAGE_GALLERY) {
            if (data != null) {
                imageURI = data.getData();

                CropImage.activity(imageURI).start(this);

                /*try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Cropped_ImageURI);
                    //String path = saveImage(bitmap);
                    imageview.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(FormActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }*/
            }

        } else if (requestCode == PICK_IMAGE_CAMERA) {
            imageURI = data.getData();
            CropImage.activity(imageURI).start(this);

            //Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            //imageview.setImageBitmap(thumbnail);
            //saveImage(thumbnail);
            //Toast.makeText(FormActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
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
   /*public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }*/

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


        StorageReference filePath = mStorageRef.child("Images").child(ServerValue.TIMESTAMP + ".jpg");
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


