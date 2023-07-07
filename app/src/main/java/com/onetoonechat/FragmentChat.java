package com.onetoonechat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class FragmentChat extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;

    ImageView imgUser;
    FirestoreRecyclerAdapter<FirebaseModel,ViewHolderFirebase> chatAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerView);

        Query query = firebaseFirestore.collection("Users").whereNotEqualTo("uid", firebaseAuth.getUid());

        FirestoreRecyclerOptions<FirebaseModel> allUsers = new FirestoreRecyclerOptions.Builder<FirebaseModel>().setQuery(query, FirebaseModel.class).build();

        chatAdapter = new FirestoreRecyclerAdapter<FirebaseModel, ViewHolderFirebase>(allUsers) {
            @SuppressLint("ResourceAsColor")
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderFirebase holder, int position, @NonNull FirebaseModel model) {
                holder.name.setText(model.getName());
                String uri = model.getImage();

                Picasso.get().load(uri).into(imgUser);
                if(model.getStatus().equals("Online")){
                    holder.status.setText(getString(R.string.online));
                    int color = ContextCompat.getColor(getContext(), R.color.online_off);
                    holder.status.setTextColor(color);
                }
                else{
                    int color = ContextCompat.getColor(getContext(), R.color.grey);
                    holder.status.setTextColor(color);
                    holder.status.setText(getString(R.string.offline2));
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), OneToOneChat.class);
                        intent.putExtra("username", model.getName());
                        intent.putExtra("useruid", model.getUid());
                        intent.putExtra("imageUser", model.getImage());
                        intent.putExtra("token_notification", model.getToken_notification());
                        startActivity(intent);

                    }
                });

            }

            @NonNull
            @Override
            public ViewHolderFirebase onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
                return new ViewHolderFirebase(v);
            }
        };
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(chatAdapter);

        return  view;
    }

    public  class ViewHolderFirebase extends RecyclerView.ViewHolder{

        private TextView name, status;

        public ViewHolderFirebase(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameOfChat);
            status = itemView.findViewById(R.id.statusOfUser);
            imgUser = itemView.findViewById(R.id.userImageChat);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        chatAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(chatAdapter != null) chatAdapter.stopListening();

    }
}
