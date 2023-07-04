package com.onetoonechat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<OneMessage> oneMessages;

    int ITEM_SEND = 1;
    int ITEM_REC = 2;

    public MessagesAdapter(Context context, ArrayList<OneMessage> oneMessages) {
        this.context = context;
        this.oneMessages = oneMessages;
    }

    @NonNull
    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            return new SenderHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout, parent, false);
            return new ReceiverHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        OneMessage oneMessage = oneMessages.get(position);

        if(holder.getClass() == SenderHolder.class){
            SenderHolder senderHolder =  (SenderHolder) holder;
            senderHolder.message.setText(oneMessage.getMessage());
            senderHolder.time.setText(oneMessage.getTimeMessage());
        }
        else{
            ReceiverHolder receiverHolder =  (ReceiverHolder) holder;
            receiverHolder.message.setText(oneMessage.getMessage());
            receiverHolder.time.setText(oneMessage.getTimeMessage());
        }
    }

    @Override
    public int getItemCount() {
        return oneMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        OneMessage oneMessage = oneMessages.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(oneMessage.getId())) return ITEM_SEND;
        else return ITEM_REC;
    }

    class SenderHolder extends RecyclerView.ViewHolder{

        TextView message;
        TextView time;

        public SenderHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.senderMessage);
            time = itemView.findViewById(R.id.timeSender);
        }
    }

    class ReceiverHolder extends RecyclerView.ViewHolder{

        TextView message;
        TextView time;

        public ReceiverHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.senderMessage);
            time = itemView.findViewById(R.id.timeSender);
        }
    }
}
