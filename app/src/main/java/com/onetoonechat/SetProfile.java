package com.onetoonechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SetProfile extends AppCompatActivity {

    private CardView cardUserImage;
    private ImageView imgUserImage;
    private static int IMAGE_ = 123;
    private Uri uriImgPath;

    private EditText editInputUserName;
    private  android.widget.Button saveProfileBtn;
    ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private String name;
    private String imageAccessToken;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private  StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);

        editInputUserName = findViewById(R.id.getUserName);
        progressBar = findViewById(R.id.progresSaveProfile);
        saveProfileBtn = findViewById(R.id.saveProfileBtn);
        imgUserImage = findViewById(R.id.getUserImageImg);
        cardUserImage = findViewById(R.id.getUserImage);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        imgUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_);
            }
        });

        saveProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = editInputUserName.getText().toString();
                if(name.isEmpty()) Toast.makeText(getApplicationContext(), getString(R.string.enter_name), Toast.LENGTH_SHORT).show();
                else if(uriImgPath == null) Toast.makeText(getApplicationContext(), getString(R.string.enter_image), Toast.LENGTH_SHORT).show();
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    saveDataUser();
                    progressBar.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(SetProfile.this, ChatActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void saveDataUser() {
        sendDataRealtime();
        sendDataStorage();
    }

    private void sendDataStorage() {
        StorageReference reference = storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Image");

        //Image compression
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriImgPath);
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
                        imageAccessToken = uri.toString();
                        Toast.makeText(getApplicationContext(), getString(R.string.uri_succ), Toast.LENGTH_SHORT).show();
                        sendDataCloudFirestore();
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

    private void sendDataCloudFirestore() {
        DocumentReference reference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("image", imageAccessToken);
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

    private void sendDataRealtime() {
        name = editInputUserName.getText().toString();
        DatabaseReference reference = firebaseDatabase.getReference(firebaseAuth.getUid());
        UserInfo userInfo = new UserInfo(name, firebaseAuth.getUid());
        reference.setValue(userInfo);
        Toast.makeText(getApplicationContext(), getString(R.string.profile_saved), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == IMAGE_ && resultCode == RESULT_OK){
            uriImgPath = data.getData();
            imgUserImage.setImageURI(uriImgPath);
        }
        else{
            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}