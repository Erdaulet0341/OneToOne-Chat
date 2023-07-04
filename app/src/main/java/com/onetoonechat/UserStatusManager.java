package com.onetoonechat;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserStatusManager {
    private static final String TAG = "UserStatusManager";

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private static UserStatusManager instance;

    private UserStatusManager() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public static synchronized UserStatusManager getInstance() {
        if (instance == null) {
            instance = new UserStatusManager();
        }
        return instance;
    }

    public void updateUserStatus(String status) {
        DocumentReference documentReference = firebaseFirestore.collection("Users")
                .document(firebaseAuth.getUid());

        documentReference.update("status", status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User status updated successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to update user status", e);
                    }
                });
    }
}
