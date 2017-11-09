package com.example.manish.flash;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileSettings extends AppCompatActivity {

    private TextInputLayout name, contact,age;
    private Button update_profile_btn;
    private FirebaseAuth mAuth;
    private DatabaseReference db_ref;

    private RadioGroup radioGender;
    private RadioButton radioGenderButton;

    String currentUser_UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        getSupportActionBar().setTitle("Profile Settings");

        name = (TextInputLayout) findViewById(R.id.name);
        age = (TextInputLayout) findViewById(R.id.age);
        contact = (TextInputLayout) findViewById(R.id.phone);
        radioGender = (RadioGroup)findViewById(R.id.radioGender);

        mAuth = FirebaseAuth.getInstance();

        db_ref = FirebaseDatabase.getInstance().getReference("Users");

        update_profile_btn = (Button)findViewById(R.id.update_profile);
        update_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateProfile();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUser_UID = currentUser.getUid();

        db_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(currentUser_UID)){

                    db_ref.child(currentUser_UID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String xname = dataSnapshot.child("Name").getValue().toString();
                            String xage = dataSnapshot.child("Age").getValue().toString();
                            String xcontact = dataSnapshot.child("Contact").getValue().toString();

                            name.getEditText().setText(xname);
                            age.getEditText().setText(xage);
                            contact.getEditText().setText(xcontact);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void updateProfile() {

        int selectedId = radioGender.getCheckedRadioButtonId();
        radioGenderButton = (RadioButton) findViewById(selectedId);
        String Sex = radioGenderButton.getText().toString();

        final String mName = name.getEditText().getText().toString();
        final String mAge = age.getEditText().getText().toString();
        final String mContact = contact.getEditText().getText().toString();
        final String mGender = Sex.substring(0,1);

        DatabaseReference newPost = db_ref.child(currentUser_UID);
        newPost.child("Name").setValue(mName);
        newPost.child("Age").setValue(mAge);
        newPost.child("Contact").setValue(mContact);
        newPost.child("Gender").setValue(mGender);

        Toast.makeText(getApplicationContext(), " Profile Updated ", Toast.LENGTH_SHORT).show();

        Intent main_Intent = new Intent(getApplicationContext(),MainActivity.class);
        main_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(main_Intent);
    }
}
