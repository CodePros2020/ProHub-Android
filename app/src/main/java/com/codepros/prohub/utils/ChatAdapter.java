package com.codepros.prohub.utils;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepros.prohub.ChatActivity;
import com.codepros.prohub.R;
import com.codepros.prohub.model.Chat;
import com.codepros.prohub.model.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {

    List<ChatMessage> chatList;
    Context context;

    public ChatAdapter(Context context, List<ChatMessage> chats)
    {
        this.chatList = chats;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatAdapter.ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_list,
                parent, false);

        ChatHolder chatHolder = new ChatHolder(v);
        return chatHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ChatHolder holder, int position)
    {
        ChatMessage chat = chatList.get(position);
        holder.txtSenderFullName.setText(chat.getSenderName());

        String lastMessage = chat.getSenderNumber();
        if (lastMessage.length() > 20) {
            lastMessage = lastMessage.substring(0,20);
    }
        holder.txtLastMessage.setText(lastMessage);
        holder.txtTimeDateSent.setText(chat.getChatMessageId());
        holder.onChatClickListener = new OnChatClickListener() {
            @Override
            public void onChatClick(View v, int position) {
                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                intent.putExtra("Chat_ID", chatList.get(position).getChatMessageId());
                intent.putExtra("senderName", chatList.get(position).getSenderName());
                intent.putExtra("senderNumber", chatList.get(position).getSenderNumber());
                intent.putExtra("receiverNumber", chatList.get(position).getReceiverNumber());

                v.getContext().startActivity(intent);
            }
        };
    }

    @Override
    public int getItemCount() { return chatList.size(); }

    interface OnChatClickListener
    {
        void onChatClick(View v, int position);
    }

    public static class ChatHolder extends RecyclerView.ViewHolder {

        TextView txtSenderFullName;
        TextView txtTimeDateSent;
        TextView txtLastMessage;
        OnChatClickListener onChatClickListener = null;

        public ChatHolder(View view) {
            super(view);

            txtSenderFullName = view.findViewById(R.id.chatSenderFullName);
            txtTimeDateSent = view.findViewById(R.id.chatSentDate);
            txtLastMessage = view.findViewById(R.id.chatLastMessage);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onChatClickListener != null) {
                        onChatClickListener.onChatClick(v, getLayoutPosition());
                    }
                }
            });
        }
    }
}
