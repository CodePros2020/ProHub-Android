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

        toolbarBtnChat.setBackgroundColor(getResources().getColor(R.color.btnBackground));
        //click CHAT button on toolbar
        toolbarBtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goChat(v);
            }
        });

        // click NEWS button on toolbar
        toolbarBtnNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNews(v);
            }
        });

        //click FORMS button on toolbar
        toolbarBtnForms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goForms(v);
            }
        });

        // click SEARCH icon on toolbar
        toolbarBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSearch(v);
            }
        });

        // click Settings icon on toolbar
        toolbarBtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSettings(v);
            }
        });

        // Menu drop down
        final PopupMenu dropDownMenu = new PopupMenu(getApplicationContext(), toolbarBtnMenu);
        final Menu menu = dropDownMenu.getMenu();
        // list of items for menu:
        menu.add(0, 0, 0, "Manage Unit");
        menu.add(1, 1, 1, "Manage Staff");
        menu.add(2, 2, 2, "Logout");

        // logout item
        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 0:
                        if(myRole.equals("Tenant")){
                            Toast.makeText(getBaseContext(),"Sorry! You do not have permission to manage staff.",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Intent intent = new Intent(getBaseContext(),ViewUnitsActivity.class);
                            startActivity(intent);
                            return true;
                        }
                    case 1:
                        if(myRole.equals("Tenant")){
                            Toast.makeText(getBaseContext(),"Sorry! You do not have permission to manage staff.",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Intent intent = new Intent(getBaseContext(),ViewStaffActivity.class);
                            startActivity(intent);
                            return true;
                        }

                    case 2:
                        // item ID 0 was clicked
                        Intent i = new Intent(getBaseContext(), MainActivity.class);
                        i.putExtra("finish", true);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clean all activities
                        startActivity(i);
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        return true;
                }
                return false;
            }
        });

        // Menu button click
        toolbarBtnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropDownMenu.show();
            }
        });
        // click to go to Property page
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),PropertyHomeActivity.class);
                startActivity(intent);
            }
        });

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
                for (ChatMessage chat : allChatMessages)
                {
                    if (chat.getReceiverNumber().equals(mPhoneNumber))
                    {
                        filteredChatMessages.add(chat);
                    }
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

    public void goNews(View view) {
        Intent intent = new Intent(this, NewsViewActivity.class);
        this.startActivity(intent);
    }

    public void goChat(View view) {
        Intent intent = new Intent(this, ChatList.class);
        this.startActivity(intent);
    }

    public void goForms(View view) {
        Intent intent = new Intent(this, FormsActivity.class);
        this.startActivity(intent);
    }

    public void goSearch(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        this.startActivity(intent);
    }

    public void goSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        this.startActivity(intent);
    }

}
