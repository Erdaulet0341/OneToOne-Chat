package com.onetoonechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfile extends AppCompatActivity {

    private   EditText username;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseFirestore firebaseFirestore;
    private ImageView userProfileImage;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private String imgToken;
    private androidx.appcompat.widget.Toolbar toolbar;
    private android.widget.Button saveBtn;
    private ImageButton backBtn ;
    private ProgressBar progressBar;
    private Uri imgPath;
    Intent intent;
    private static int IMAGE_ = 123;
    String newName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        progressBar = findViewById(R.id.progressUpdateProfile);
        backBtn = findViewById(R.id.backUpdate);
        toolbar = findViewById(R.id.toolbarUpdate);
        username = findViewById(R.id.usernameUpdate);
        userProfileImage = findViewById(R.id.userImageUpdate);
        saveBtn = findViewById(R.id.saveUpdateBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        intent = getIntent();
        setSupportActionBar(toolbar);

        username.setText( intent.getStringExtra("username"));

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        DatabaseReference reference = firebaseDatabase.getReference(firebaseAuth.getUid());

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                newName = username.getText().toString();
                if(newName.isEmpty()){
                    Toast.makeText(getApplicationContext(), getString(R.string.name_is_empty), Toast.LENGTH_SHORT).show();
                }
                else if(imgPath != null){
                    progressBar.setVisibility(View.VISIBLE);
                    UserInfo userInfo = new UserInfo(newName, firebaseAuth.getUid());
                    reference.setValue(userInfo);
                    updateImageStorage();

                    Toast.makeText(getApplicationContext(), getString(R.string.updating_saved), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    Intent i = new Intent(UpdateProfile.this, ChatActivity.class);
                    startActivity(i);
                    finish();
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    UserInfo userInfo = new UserInfo(newName, firebaseAuth.getUid());
                    reference.setValue(userInfo);

                    updateNameFirestore();
                    Toast.makeText(getApplicationContext(), getString(R.string.updating_saved), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    Intent i = new Intent(UpdateProfile.this, ChatActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_);
            }
        });

        storageReference = firebaseStorage.getReference();
        storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Image").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imgToken = uri.toString();
                Picasso.get().load(uri).into(userProfileImage);
            }
        });

    }

    private void updateNameFirestore() {

        DocumentReference reference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", newName);
        userData.put("image", imgToken);
        userData.put("uid", firebaseAuth.getUid());
        userData.put("status", "Online");

        reference.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), getString(R.string.cloud_succ), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.cloud_fail), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void updateImageStorage() {
        StorageReference reference = storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Image");

        //Image compression
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgPath);
        }
        catch (IOException e){
            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        UploadTask uploadTask = reference.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imgToken = uri.toString();
                        Toast.makeText(getApplicationContext(), getString(R.string.uri_succ), Toast.LENGTH_SHORT).show();
                        updateNameFirestore();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.uri_fail), Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(getApplicationContext(), getString(R.string.image_succ), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.image_failed), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == IMAGE_ && resultCode == RESULT_OK){
            imgPath  = data.getData();
            userProfileImage.setImageURI(imgPath);
        }
        else{
            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();

        }

        super.onActivityResult(requestCode, resultCode, data);
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