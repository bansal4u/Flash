package com.example.manish.flash;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mEmail,mPass;
    private Button mCreateAcc;
    private TextView SignIN;

    private ProgressDialog mRegProgress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRegProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mEmail = (TextInputLayout) findViewById(R.id.reg_email);
        mPass = (TextInputLayout) findViewById(R.id.reg_pass);
        mCreateAcc = (Button) findViewById(R.id.register_btn);
        SignIN = (TextView)findViewById(R.id.SignIN);

        SignIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
            }
        });

        mCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Email = mEmail.getEditText().getText().toString();
                String Password = mPass.getEditText().getText().toString();

                if(!TextUtils.isEmpty(Email) || !TextUtils.isEmpty(Password)){

                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Please wait while your Account is Set up");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();

                    register_user(Email,Password);

                }

            }
        });
    }

    private void register_user(final  String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            mRegProgress.dismiss();
                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = current_user.getUid();

                            Intent profileIntent = new Intent(RegisterActivity.this,ProfileSettings.class);
                            profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(profileIntent);


                        }else{

                            mRegProgress.hide();

                            Toast.makeText(RegisterActivity.this, "Cannot SIGN UP",
                                    Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });
    }
}
