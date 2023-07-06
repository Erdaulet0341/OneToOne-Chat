package com.onetoonechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    EditText phoneNumberInput;
    android.widget.Button sendBtn;
    CountryCodePicker countryCodePicker;
    String countryCode;
    String phoneNumber;
    String sendCode;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_main);

        phoneNumberInput = findViewById(R.id.inputPhoneNumber);
        sendBtn = findViewById(R.id.sendOPTBtn);
        progressBar = findViewById(R.id.progresOPT);
        countryCodePicker = findViewById(R.id.countrycodepicker);

        firebaseAuth = FirebaseAuth.getInstance();
        countryCode = countryCodePicker.getSelectedCountryCodeWithPlus();

        boolean isInternetAvailable = NetworkUtils.isNetworkAvailable(this);
        checkInternet(isInternetAvailable);

        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryCode = countryCodePicker.getSelectedCountryCodeWithPlus();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = phoneNumberInput.getText().toString();
                if(number.isEmpty()) phoneNumberInput.setError(getString(R.string.please_enter_number));
                else if (number.length() < 10) phoneNumberInput.setError(getString(R.string.enter_correct_phone_number));
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    phoneNumber = countryCode+number;

                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                            .setPhoneNumber(phoneNumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(MainActivity.this)
                            .setCallbacks(callbacks)
                            .build();

                    PhoneAuthProvider.verifyPhoneNumber(options) ;
                }
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {}
            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {}

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(getApplicationContext(), "Please, try again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(getApplicationContext(), getString(R.string.otp_sent), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                sendCode = s;

                Intent intent = new Intent(MainActivity.this, AutentificationOTP.class);
                intent.putExtra("otp_code", sendCode);
                startActivity(intent);
            }
        };
    }

    void setLocale(String lan){
        Locale locale = new Locale(lan);
        Configuration configuration = new Configuration();
        Locale.setDefault(locale);
        configuration.setLocale(locale);
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Language_set", MODE_PRIVATE).edit();
        editor.putString("app_language", lan).apply();
    }

    void loadLocale(){
        SharedPreferences pre = getSharedPreferences("Language_set", Activity.MODE_PRIVATE);
        setLocale(pre.getString("app_language", Locale.getDefault().getLanguage()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void checkInternet(boolean isInternetAvailable) {
        if(!isInternetAvailable)  Toast.makeText(this, getString(R.string.check_interne), Toast.LENGTH_SHORT).show();
    }
}