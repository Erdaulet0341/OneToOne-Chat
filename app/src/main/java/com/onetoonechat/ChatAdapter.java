package com.onetoonechat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.Holder> {

    Context context;
    ArrayList<FirebaseModel> firebaseModels;

    public ChatAdapter(Context context, ArrayList<FirebaseModel> firebaseModels) {
        this.context = context;
        this.firebaseModels = firebaseModels;

        Log.d("work22", String.valueOf(firebaseModels.size()));
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        Log.d("work33", "yes");
        return new Holder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        String name = firebaseModels.get(position).getName();
        String status = firebaseModels.get(position).getStatus();
        String uri = firebaseModels.get(position).getUid();
        Log.d("work88",name);
        holder.bind(name, status, uri);
    }

    @Override
    public int getItemCount() {
        return firebaseModels.size();
    }

//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);
//        Log.d("work33", "yes");
//        return new Holder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        FirebaseModel model = firebaseModels.get(position);
//        ChatAdapter.Holder holder1 =  (ChatAdapter.Holder) holder;
//        Log.d("work77", "yes");
//
//        holder1.name.setText(model.getName());
//        String uri = model.getImage();
//
//        Picasso.get().load(uri).into(holder1.imgUser);
//        if(model.getStatus().equals("Online")){
//            holder1.status.setText(model.getStatus());
//            holder1.status.setTextColor(Color.GREEN);
//        }
//        else{
//            holder1.status.setText(model.getStatus());
//        }
//
//        holder1.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, OneToOneChat.class);
//                intent.putExtra("username", model.getName());
//                intent.putExtra("useruid", model.getUri());
//                intent.putExtra("imageUser", model.getImage());
//                context.startActivity(intent);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return firebaseModels.size();
//    }

    class Holder extends RecyclerView.ViewHolder{
        private Context contextt;
        TextView name;
        TextView status;
        ImageView imgUser;

        public Holder(@NonNull View itemView, Context context) {
            super(itemView);
            Log.d("work66", "yes");
            status = itemView.findViewById(R.id.statusOfUser);
            name = itemView.findViewById(R.id.nameOfChat);
            contextt = context;
            imgUser = itemView.findViewById(R.id.userImageChat);
        }


        public void bind(String namee, String statuss, String urii) {
            name.setText(namee);
            status.setText(statuss);
        }
    }
}
