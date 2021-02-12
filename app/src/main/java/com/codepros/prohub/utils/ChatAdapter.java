package com.codepros.prohub.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepros.prohub.ChatActivity;
import com.codepros.prohub.ChatList;
import com.codepros.prohub.R;
import com.codepros.prohub.model.Chat;
import com.codepros.prohub.model.ChatMessage;
import com.codepros.prohub.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {

    List<ChatMessage> chatList;
    Context context;
    private DatabaseReference myDataRef;
    private List<Chat> latestChat;
    String theLastMessage;
    String theLastTimeStamp;
    String chatMessageTimestamp;
    String num_unread;
    List<Chat> allMessages = new ArrayList<>();
    private String imageUrl;

    public ChatAdapter(Context context, List<ChatMessage> chats)
    {
        this.chatList = chats;
        this.context = context;
        //this.latestChat = latestChat;
    }

    @NonNull
    @Override
    public ChatAdapter.ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        ChatHolder chatHolder;

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_list,
                parent, false);

         chatHolder = new ChatHolder(v);
        return chatHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatAdapter.ChatHolder holder, int position)
    {
        final ChatMessage chat = chatList.get(position);

        holder.txtSenderFullName.setText(chat.getSenderName());

        lastMessage(chat.getChatMessageId(), holder.txtLastMessage, holder.txtTimeDateSent);
        numUnreadMsg(chat.getChatMessageId(), chat.getReceiverNumber(), holder.txtNumUnread);
        //setChatMessageTimestamp(chat.getChatMessageId(), chat.getTimestamp());

//        if (chat.getSenderPhotoUrl() == null || chat.getSenderPhotoUrl().equals("")) {
//            holder.chatImageView.setImageDrawable(ContextCompat.getDrawable(holder.chatImageView.getContext(),
//                    R.drawable.ic_account_circle_black_36dp));
//        } else {
//            Glide.with(holder.chatImageView.getContext())
//                    .load(chat.getSenderPhotoUrl())
//                    .into(holder.chatImageView);
//        }

        new FirebaseDataseHelper().readUsers(new FirebaseDataseHelper.UserDataStatus() {
            @Override
            public void DataIsLoad(List<User> listUsers, List<String> keys) {
                for (User user : listUsers) {
                    if (user.getPhone().equals(chat.getSenderNumber())) {
                        loadUserImage(user.getImageUrl(), holder.chatImageView);
                    }
                }
            }
        });

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
        TextView txtNumUnread;
        CircleImageView chatImageView;
        OnChatClickListener onChatClickListener = null;

        public ChatHolder(View view) {
            super(view);

            txtSenderFullName = view.findViewById(R.id.chatSenderFullName);
            txtTimeDateSent = view.findViewById(R.id.chatSentDate);
            txtLastMessage = view.findViewById(R.id.chatLastMessage);
            chatImageView = view.findViewById(R.id.chatImageView);
            txtNumUnread = view.findViewById(R.id.txtNumUnread);
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

    private void lastMessage(final String chatMessageId, final TextView last_msg, final TextView last_timestamp) {
        theLastMessage = "default";
        theLastTimeStamp = "defaultTimeStamp";
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chat");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getChatMessageId().equals(chatMessageId)) {
                        theLastMessage = chat.getMessage();
                        theLastTimeStamp = chat.getTimestamp();
                    }
                }
                if(theLastMessage == null || theLastMessage.isEmpty()){
                    theLastMessage = "[image]";
                }

                if (theLastMessage.length() > 20) {
                    theLastMessage = theLastMessage.substring(0,20);
                }

                switch (theLastMessage) {
                    case "default":
                        last_msg.setText("No message");
                        last_timestamp.setText(" ");
                        break;

                    default:
                        last_msg.setText(theLastMessage + "...");
                        last_timestamp.setText(theLastTimeStamp);
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void numUnreadMsg(final String chatMessageId, final String phoneNumber, final TextView numUnread) {
        num_unread = "1";

        new FirebaseDataseHelper().readChats(new FirebaseDataseHelper.ChatDataStatus() {
            @Override
            public void DataIsLoad(List<Chat> chats, List<String> keys) {
                allMessages = chats;
                int count = 0;
                if (allMessages != null) {
                    for (Chat chat : allMessages)
                    {
                        if (!chat.getPhoneNumber().equals(phoneNumber) && chat.getChatSeen().equals("false")
                                && chat.getChatMessageId().equals(chatMessageId))
                        {
                            count++;
                        }
                    }
                }

                if (count > 0)
                {
                    num_unread = String.valueOf(count);
                    numUnread.setText(num_unread);
                } else {
                    numUnread.setVisibility(View.GONE);
                }

            }
        });
    }

    private void setChatMessageTimestamp(final String chatMsgId, final String lastMsgTimestamp) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chatMessages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
                    if (chatMessage.getChatMessageId().equals(chatMsgId)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("timestamp", lastMsgTimestamp);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // loads the user image according to imageURL
    private void loadUserImage(String imageUrl, ImageView profileImage){
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(profileImage);
        }
        else {
            profileImage.setImageDrawable(ContextCompat.getDrawable(profileImage.getContext(),
                    R.drawable.ic_account_circle_black_36dp));
        }
    }
}
