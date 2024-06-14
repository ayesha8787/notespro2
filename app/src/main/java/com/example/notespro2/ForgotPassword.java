package com.example.notespro2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
 private EditText mforgotpassword;
 private Button mpasswordrecoverbutton;
 private TextView mgobacktologin;

 FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        getSupportActionBar().hide();
        mforgotpassword = findViewById(R.id.forgotpassword);
        mpasswordrecoverbutton = findViewById(R.id.password_recover_btn);
        mgobacktologin = findViewById(R.id.gobacktologin);
        firebaseAuth = FirebaseAuth.getInstance();

        mgobacktologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPassword.this,MainActivity.class);
                startActivity(intent);

            }
        });

        mpasswordrecoverbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = mforgotpassword.getText().toString().trim();
                if (mail.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Enter your email first", Toast.LENGTH_SHORT).show();
                }else {
                    //we have to send password recover email
                    firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                              if (task.isSuccessful()){
                                  Toast.makeText(getApplicationContext(), "Mail sent,You can recover your password using mail", Toast.LENGTH_SHORT).show();
                                  finish();
                                  startActivity(new Intent(ForgotPassword.this, MainActivity.class));
                              }else {
                                  Toast.makeText(getApplicationContext(), "Email is wrong or Account not Exist", Toast.LENGTH_SHORT).show();
                              }
                        }
                    });

                }
            }
        });


    }
}