package com.codepros.prohub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.codepros.prohub.model.Chat;
import com.codepros.prohub.model.ChatMessage;
import com.codepros.prohub.utils.FirebaseDataseHelper;
import com.codepros.prohub.utils.ChatAdapter;
import com.codepros.prohub.utils.ToolbarHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatList extends AppCompatActivity {

    //Toolbar
    private Button toolbarBtnSettings, toolbarBtnChat,toolbarBtnNews,toolbarBtnForms ;
    private ImageButton toolbarBtnSearch,btnHome,toolbarBtnMenu;
    private ToolbarHelper toolbar;
    //Activity Items

    ChatAdapter chatAdapter;
    RecyclerView chatRecycler;
    List<ChatMessage> allChatMessages = new ArrayList<>();
    List<ChatMessage> filteredChatMessages = new ArrayList<>();
    List<Chat> allMessages = new ArrayList<>();
    List<Chat> recentMessage = new ArrayList<>();
    private SharedPreferences mSharedPreferences;
    private String myRole;
    private String mPhoneNumber;
    String lastMessage;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        mSharedPreferences = getSharedPreferences("myUserSharedPref", MODE_PRIVATE);
        mPhoneNumber = mSharedPreferences.getString("phoneNum","0123456789");
        myRole=mSharedPreferences.getString("myRole","");

        /////////////////////////////////////////////////////
        // declaring the buttons

        // define the actions for each button
        // Button for top toolbar
        toolbarBtnChat = findViewById(R.id.toolbarBtnChat);
        toolbarBtnNews = findViewById(R.id.toolbarBtnNews);
        toolbarBtnForms = findViewById(R.id.toolbarBtnForms);
        toolbarBtnSettings = findViewById(R.id.toolbarBtnSettings);
        btnHome = findViewById(R.id.ImageButtonHome);
        toolbarBtnSearch = findViewById(R.id.ImageButtonSearch);
        toolbarBtnMenu = findViewById(R.id.ImageButtonMenu);
        toolbar = new ToolbarHelper(this, toolbarBtnChat, toolbarBtnNews, toolbarBtnForms,
                toolbarBtnSettings, btnHome, toolbarBtnSearch, toolbarBtnMenu);

        toolbarBtnChat.setBackgroundColor(getResources().getColor(R.color.btnBackground));

        /////////////////////////////////////////////

        chatRecycler = (RecyclerView) findViewById(R.id.recyclerChatList);
        chatRecycler = findViewById(R.id.recyclerChatList);
        mSharedPreferences = getSharedPreferences("myUserSharedPref", MODE_PRIVATE);
        mPhoneNumber = mSharedPreferences.getString("phoneNum","0123456789");

        // read the list of ChatMessages from Firebase

        new FirebaseDataseHelper().readChatMessages(new FirebaseDataseHelper.ChatMessageDataStatus() {
            @Override
            public void DataIsLoad(List<ChatMessage> chatMessages, List<String> keys) {
                allChatMessages = chatMessages;

                if (allChatMessages != null) {
                    for (ChatMessage chat : allChatMessages)
                    {
                        if (chat.getReceiverNumber().equals(mPhoneNumber))
                        {
                            filteredChatMessages.add(chat);
                        }
                    }
                }
                Log.d("FilteredLENGTH", String.valueOf(filteredChatMessages.size()));
                setChatAdapter();
            }
        });


        /////////////////////////////////////////////////////////////////////////

//        reference = FirebaseDatabase.getInstance().getReference("chat");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                int unread = 0;
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Chat chat = snapshot.getValue(Chat.class);
//                    if (chat.getPhoneNumber().equals(mPhoneNumber) && chat.getChatSeen().equals("false")) {
//                        unread++;
//                    }
//                }
//
//                Log.d("UNREAD_MESSAGE", "Unread#: " + unread);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        ////////////////////////////////////////////////////////////////////////
    }

    private void setChatAdapter() {
        chatAdapter = new ChatAdapter(this, filteredChatMessages);
        chatRecycler.setLayoutManager(new LinearLayoutManager(this));
        chatRecycler.setItemAnimator(new DefaultItemAnimator());
        chatRecycler.setAdapter(chatAdapter);
    }

}
