package com.onetoonechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Profile extends AppCompatActivity {

    private  EditText username;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private TextView goUpdate;
    private FirebaseFirestore firebaseFirestore;
    private ImageView userProfileImage;
    private  StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private String imgToken;
    private  androidx.appcompat.widget.Toolbar toolbar;
    private  ImageButton backChat;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        backChat = findViewById(R.id.backFragmentChat);
        toolbar = findViewById(R.id.toolbarFragmentChat);
        goUpdate = findViewById(R.id.goUpdateProfile);
        username = findViewById(R.id.usernameFragment);
        userProfileImage = findViewById(R.id.userImageFragment);
        progressBar = findViewById(R.id.progressProfile);

        setSupportActionBar(toolbar);

        backChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        progressBar.setVisibility(View.VISIBLE);

        storageReference = firebaseStorage.getReference();
        storageReference.child("Images")
                .child(firebaseAuth.getUid())
                .child("Profile Image")
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imgToken = uri.toString();
                        Picasso.get().load(uri).into(userProfileImage);
                    }});

        DatabaseReference reference = firebaseDatabase.getReference(firebaseAuth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserInfo user = snapshot.getValue(UserInfo.class);
                username.setText(user.getUsername());
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Something wrong", Toast.LENGTH_SHORT).show();
            }
        });

        goUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, UpdateProfile.class);
                intent.putExtra("username", username.getText().toString());
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        UserStatusManager.getInstance().updateUserStatus("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        UserStatusManager.getInstance().updateUserStatus("Offline");
    }

}