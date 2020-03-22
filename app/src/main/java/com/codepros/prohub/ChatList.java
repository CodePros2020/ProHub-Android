package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.codepros.prohub.model.ChatMessage;
import com.codepros.prohub.utils.FirebaseDataseHelper;
import com.codepros.prohub.utils.ChatAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatList extends AppCompatActivity {

    ChatAdapter chatAdapter;
    RecyclerView chatRecycler;
    List<ChatMessage> allChatMessages = new ArrayList<>();
    List<ChatMessage> filteredChatMessages = new ArrayList<>();
    private SharedPreferences mSharedPreferences;
    private String mPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        chatRecycler = (RecyclerView) findViewById(R.id.recyclerChatList);
        mSharedPreferences = getSharedPreferences("myUserSharedPref", MODE_PRIVATE);
        mPhoneNumber = mSharedPreferences.getString("phoneNum","0123456789");

        // read the list of ChatMessages from Firebase

        new FirebaseDataseHelper().readChatMessages(new FirebaseDataseHelper.ChatMessageDataStatus() {
            @Override
            public void DataIsLoad(List<ChatMessage> chatMessages, List<String> keys) {
                allChatMessages = chatMessages;
                for (ChatMessage chat : allChatMessages)
                {
                    if (chat.getReceiverNumber().equals(mPhoneNumber))
                    {
                        filteredChatMessages.add(chat);
                    }
                    //Log.d("Receiver Number", chat.getReceiverNumber());
                }
                Log.d("FilteredLENGTH", String.valueOf(filteredChatMessages.size()));
                setChatAdapter();
            }
        });

    }

    private void setChatAdapter() {
        chatAdapter = new ChatAdapter(this, filteredChatMessages);
        chatRecycler.setLayoutManager(new LinearLayoutManager(this));
        chatRecycler.setItemAnimator(new DefaultItemAnimator());
        chatRecycler.setAdapter(chatAdapter);
    }
}
