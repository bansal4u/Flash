package com.example.manish.flash;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mPatientList;
    private FirebaseDatabase myDb;
    private DatabaseReference myDb_Ref;

    private DownloadManager downloadManager;
    private long downloadReference;

    private FirebaseAuth mAuth;
    FloatingActionButton fab;

    Dialog dialog;
    Uri internalImg_Uri;
    String date_toString;

    int MY_PERMISSIONS_REQUEST_READ_CONTACTS=121, MY_PERMISSIONS_REQUEST_CAMERA=122;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent form_intent = new Intent(MainActivity.this,FormActivity.class);
                startActivity(form_intent);

            }
        });

        LinearLayoutManager mlayoutManager = new LinearLayoutManager(this);
        mlayoutManager.setReverseLayout(true);
        mlayoutManager.setStackFromEnd(true);

       //Recycler View
        mPatientList = (RecyclerView)findViewById(R.id.ll);
        mPatientList.setHasFixedSize(true);
        mPatientList.setLayoutManager(mlayoutManager);

        myDb = FirebaseDatabase.getInstance();
        myDb_Ref = myDb.getReference().child("Data");

        mAuth = FirebaseAuth.getInstance();

        checkPermission();
    }

    public void checkPermission(){

        //Write External Storage Permission
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.INTERNET)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.INTERNET}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        //Camera Permission
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
        }

        mPatientList.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (dy > 0 ||dy<0 && fab.isShown())
                {
                    fab.hide();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    fab.show();
                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });


        FirebaseRecyclerAdapter<ModelClass,CardViewHolder> firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<ModelClass, CardViewHolder>(
                        ModelClass.class,
                        R.layout.card_layout,
                        CardViewHolder.class,
                        myDb_Ref) {

            @Override
            protected void populateViewHolder(final CardViewHolder vHolder, final ModelClass model, final int pos) {

                        final String post_key = getRef(pos).getKey();

                        vHolder.setName(model.getName());
                        vHolder.setDate(model.getDate());
                        vHolder.setAge(model.getAge());
                        vHolder.setGender(model.getGender());
                        vHolder.setEye(model.getEye());
                        vHolder.setDetails(model.getDetails());

                        final String imgURL = model.getImage();
                        final String position = String.valueOf(pos);

                vHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if(vHolder.hidden_ll.getVisibility()==View.GONE){
                                    vHolder.hidden_ll.setVisibility(View.VISIBLE);
                                    vHolder.details.setVisibility(View.VISIBLE);

                                }
                                else {
                                    vHolder.hidden_ll.setVisibility(View.GONE);
                                    vHolder.details.setVisibility(View.GONE);
                                }
                            }
                        });

                        vHolder.tv_download.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String name = model.getName() +"_"+ model.getAge() +"_"+ model.getGender() +"_"+ model.getEye() +"_"+ position +".jpg";

                                downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                                Uri Download_Uri = Uri.parse(imgURL);
                                DownloadManager.Request request = new DownloadManager.Request(Download_Uri);

                                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                                request.setAllowedOverRoaming(false);
                                request.setTitle("Downloading Image");
                                request.setDescription("");

                                //request.setDestinationInExternalPublicDir(DIRECTORY_PICTURES,  "Flash/" + name);
                                request.setDestinationInExternalPublicDir("Flash",name);
                                request.allowScanningByMediaScanner();

                                //Enqueue a new download and same the referenceId
                                downloadReference = downloadManager.enqueue(request);

                                /*DownloadManager.Query query = new DownloadManager.Query();
                                query.setFilterById(downloadReference);
                                Cursor cursor = downloadManager.query(query);
                                if (cursor.moveToFirst()) {
                                    filePath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                                }*/


                            }
                        });

                        vHolder.tv_Send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String fileName = model.getName() +"_"+ model.getAge() +"_"+ model.getGender() +"_"+ model.getEye() + "_" + position + ".jpg";
                                String myDir = Environment.getExternalStorageDirectory().toString()+ "/Flash/";
                                internalImg_Uri = Uri.parse("file:///" + myDir + fileName);

                                File file = new File(myDir+fileName);
                                if (file.exists()) {
                                    //Do something
                                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                    shareIntent.setType("image/*");

                                    shareIntent.putExtra(Intent.EXTRA_STREAM, internalImg_Uri);
                                    startActivity(Intent.createChooser(shareIntent, "Share Image"));
                                }else {

                                    String Image = imgURL;
                                    Intent imageIntent = new Intent(Intent.ACTION_SEND);
                                    //imageIntent.
                                    Snackbar.make(v, "File not Available! Please try Downloading First ", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                    //Toast.makeText(MainActivity.this,"Please Download File first",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        vHolder.tv_View.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String fileName = model.getName() +"_"+ model.getAge() +"_"+ model.getGender() +"_"+ model.getEye() +"_" + position + ".jpg";
                                String myDir = Environment.getExternalStorageDirectory().toString()+ "/Flash/";
                                internalImg_Uri = Uri.parse("file:///" + myDir + fileName);

                                File file = new File(myDir+fileName);
                                if (file.exists()) {

                                    showImageDialog();

                                }else {

                                    Snackbar.make(v, "File not Available! Please try Downloading First ", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                                    //Toast.makeText(MainActivity.this,"Please Download File first",Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                        vHolder.delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View vi) {
                                myDb_Ref.child(post_key).removeValue();
                                final StorageReference delete_photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(imgURL);
                                delete_photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Snackbar.make(vi, "Post Removed", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                                    }
                                });
                            }
                        });

            }
        };
        mPatientList.setAdapter(firebaseRecyclerAdapter);
    }


   public static class CardViewHolder extends RecyclerView.ViewHolder{
        View mView;
        LinearLayout hidden_ll;
        TextView tv_download,tv_Send,tv_View;
        ImageView delete;
        TextView details;

        public CardViewHolder(View itemView){
            super(itemView);
            mView = itemView;
            hidden_ll = (LinearLayout)mView.findViewById(R.id.hidden_ll);
            delete = (ImageView)mView.findViewById(R.id.delete_post);
            tv_download = (TextView)mView.findViewById(R.id.tv_hidden);
            tv_Send = (TextView)mView.findViewById(R.id.tv_Send);
            tv_View = (TextView)mView.findViewById(R.id.tv_View);
            details = (TextView) mView.findViewById(R.id.details);

        }

        public void setDate(Long timestamp){


            /*SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            sfd.format(new Date(timestamp));*/
            TextView date_view = (TextView) mView.findViewById(R.id.date);

            try {
                SimpleDateFormat sfd = new SimpleDateFormat("EEE, d MMM yyyy");
                String date_toString = sfd.format(timestamp);
                date_view.setText(date_toString);
            }catch (IllegalArgumentException e){

            }

        }

        public void setName(String Name){
            TextView name_view = (TextView) mView.findViewById(R.id.name);
            name_view.setText(Name);
        }

        public void setAge(String Age){
            TextView age_view = (TextView) mView.findViewById(R.id.age);
            age_view.setText(Age+"  | ");
        }

        public void setGender(String Gender){
            TextView gender_view = (TextView) mView.findViewById(R.id.gender);
            gender_view.setText(Gender+"  | ");
        }

        public void setEye(String Eye){
            TextView eye_view = (TextView) mView.findViewById(R.id.eye);
            eye_view.setText(Eye+ " Eye");
        }

        public void setDetails(String Details){
            details.setText(Details);
        }
    }

    private void showImageDialog() {
        // custom dialog
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.image_layout);

        // set the custom dialog components - text, image and button
        ImageButton close = (ImageButton) dialog.findViewById(R.id.btnClose);
        ImageView eye_image = (ImageView)dialog.findViewById(R.id.eye_img);

        eye_image.setImageURI(internalImg_Uri);

        // Close Button
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
    }

    private void aboutDialog(){

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.about_layout);

        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setTitle("About");

        dialog.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this,ProfileSettings.class);
            startActivity(i);
            return true;
        }
        else if(id == R.id.action_logout){
            FirebaseAuth.getInstance().signOut();
            sendToStart();

        }
        else if(id == R.id.action_about){
            aboutDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendToStart(){
        Intent startIntent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(startIntent);
        finish();
    }
}
