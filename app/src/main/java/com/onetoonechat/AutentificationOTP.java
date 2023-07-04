package com.onetoonechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class AutentificationOTP extends AppCompatActivity {

    TextView changeNumber;
    EditText inputCode;
    ProgressBar progressBar;
    android.widget.Button verifyBtn;
    String otp_code;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autentification_otp);

        changeNumber = findViewById(R.id.changenumbertext);
        inputCode = findViewById(R.id.inputOTP);
        progressBar = findViewById(R.id.progresOPTVerify);
        verifyBtn = findViewById(R.id.sentOTPBtn);
        firebaseAuth = FirebaseAuth.getInstance();

        changeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AutentificationOTP.this, MainActivity.class);
                startActivity(intent);
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otp_code = inputCode.getText().toString();
                if(otp_code.isEmpty()) inputCode.setError(getString(R.string.please_enter_code));
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    String codeFirebase = getIntent().getStringExtra("otp_code");
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeFirebase, otp_code);
                    signInCredential(credential);
                }
            }
        });
    }

    private void signInCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), getString(R.string.login_succ), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AutentificationOTP.this, SetProfile.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    if(task.getException()instanceof FirebaseAuthInvalidCredentialsException){
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}