package com.jrocaberte.twitterclone;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context context;
    private List<Message> list;

    public MessageAdapter(Context context, List<Message> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.message_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = list.get(position);
        holder.mSenderName.setText(message.getSenderName());
        holder.mMessage.setText(message.getMessage());
        holder.mSendTime.setText(message.getSendTime());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mSenderName, mMessage, mSendTime;

        public ViewHolder(View itemView) {
            super(itemView);

            mSenderName = itemView.findViewById(R.id.sender_name);
            mMessage = itemView.findViewById(R.id.message);
            mSendTime = itemView.findViewById(R.id.send_time);
        }
    }

}
