package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        chatRecycler = (RecyclerView) findViewById(R.id.recyclerChatList);
        TextView test = findViewById(R.id.txtTest);

        // read the list of ChatMessages from Firebase

        new FirebaseDataseHelper().readChatMessages(new FirebaseDataseHelper.ChatMessageDataStatus() {
            @Override
            public void DataIsLoad(List<ChatMessage> chatMessages, List<String> keys) {
                allChatMessages = chatMessages;
                setChatAdapter();
            }
        });

    }

    private void setChatAdapter() {
        chatAdapter = new ChatAdapter(this, allChatMessages);
        chatRecycler.setLayoutManager(new LinearLayoutManager(this));
        chatRecycler.setItemAnimator(new DefaultItemAnimator());
        chatRecycler.setAdapter(chatAdapter);
    }
}
