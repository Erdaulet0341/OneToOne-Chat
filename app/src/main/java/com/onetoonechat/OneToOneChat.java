package com.onetoonechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class OneToOneChat extends AppCompatActivity {

    EditText message;
    ImageButton sendMessage;
    CardView sendCard;
    androidx.appcompat.widget.Toolbar toolbar;
    ImageView imgChat;
    TextView usernameChat;
    ImageButton back;
    ProgressBar progressBar;

    private String enteredMessage, token_notification;
    Intent intent;
    String nameSender, uidSender, nameReceiver, uidReceiver;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseFirestore firebaseFirestore;
    String senderRoom, receiverRoom;
    RecyclerView recyclerView;

    String currentTime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    ArrayList<OneMessage> oneMessageArrayList;
    MessagesAdapter messagesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_to_one_chat);

        message = findViewById(R.id.getUserMessageOne);
        sendMessage = findViewById(R.id.sendMessageBtn);
        progressBar = findViewById(R.id.progressChatOne);
        sendCard = findViewById(R.id.cardViewOne);
        toolbar = findViewById(R.id.toolbarOne);
        imgChat = findViewById(R.id.userImageOne);
        recyclerView = findViewById(R.id.recyclerViewOne);
        usernameChat = findViewById(R.id.nameOfUserOne);
        back = findViewById(R.id.backFragmentChatOne);
        intent = getIntent();
        oneMessageArrayList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        messagesAdapter = new MessagesAdapter(OneToOneChat.this, oneMessageArrayList);
        recyclerView.setAdapter(messagesAdapter);

        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm a");

        uidSender = firebaseAuth.getUid();
        token_notification = getIntent().getStringExtra("token_notification");
        uidReceiver = getIntent().getStringExtra("useruid");
        nameReceiver = getIntent().getStringExtra("username");
        senderRoom = uidSender + uidReceiver;
        receiverRoom = uidReceiver + uidSender;

        progressBar.setVisibility(View.VISIBLE);

        boolean isInternetAvailable = NetworkUtils.isNetworkAvailable(this);
        checkInternet(isInternetAvailable);
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("chats").child(senderRoom).child("messages");
        messagesAdapter = new MessagesAdapter(OneToOneChat.this, oneMessageArrayList);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                oneMessageArrayList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    OneMessage oneMessage = dataSnapshot.getValue(OneMessage.class);
                    oneMessageArrayList.add(oneMessage);
                }
                progressBar.setVisibility(View.INVISIBLE);
                messagesAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(oneMessageArrayList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        usernameChat.setText(nameReceiver);
        String uirImg = intent.getStringExtra("imageUser");
        if (uirImg != null && !uirImg.isEmpty()) {
            Picasso.get().load(uirImg).into(imgChat);
        }

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredMessage = message.getText().toString();
                if(!enteredMessage.isEmpty()){
                    enteredMessage = enteredMessage.replaceAll("\\s+$", "");
                    currentTime = simpleDateFormat.format(calendar.getTime());
                    Date date = new Date();
                    OneMessage oneMessage = new OneMessage(enteredMessage, firebaseAuth.getUid(), date.getTime(), currentTime);


                    DatabaseReference senderRef = firebaseDatabase.getReference().child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .push();
                    DatabaseReference receiverRef = firebaseDatabase.getReference().child("chats")
                            .child(receiverRoom)
                            .child("messages")
                            .push();

                    senderRef.setValue(oneMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    receiverRef.setValue(oneMessage)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });

                                }
                            });

                    BackendNotification backendNotification = new BackendNotification(nameSender, enteredMessage,token_notification);
                    backendNotification.execute();

                    message.setText(null);
                }
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        messagesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(messagesAdapter != null) messagesAdapter.notifyDataSetChanged();
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

    private void checkInternet(boolean isInternetAvailable) {
        if(!isInternetAvailable)  {
            Toast.makeText(this, getString(R.string.check_interne), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}