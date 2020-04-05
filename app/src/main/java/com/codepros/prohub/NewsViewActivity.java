package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.codepros.prohub.model.News;
import com.codepros.prohub.model.User;
import com.codepros.prohub.utils.ChatAdapter;
import com.codepros.prohub.utils.FirebaseDataseHelper;
import com.codepros.prohub.utils.NewsAdapter;
import com.codepros.prohub.utils.ToolbarHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class NewsViewActivity extends AppCompatActivity {

    //Toolbar
    private Button toolbarBtnSettings, toolbarBtnChat, toolbarBtnNews, toolbarBtnForms;
    private ImageButton toolbarBtnSearch, btnHome, toolbarBtnMenu;
    private ToolbarHelper toolbar;
    //
    public RecyclerView newsRecyclerView;
    public NewsAdapter newsAdapter;
    public List<News> newsList = new ArrayList<>();
    public List<String> newsKeyList = new ArrayList<>();
    public FloatingActionButton btn_add;
    TextView tvPropertyName;
    private DatabaseReference myPropRef;
    // user role
    private String myRole, propName;

     List<Chat> allMessages = new ArrayList<>();
    private String userPhoneNum;
    private String newsTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_view);
        //
        SharedPreferences sharedPreferences = getSharedPreferences("myUserSharedPref", Context.MODE_PRIVATE);
        myRole = sharedPreferences.getString("myRole", "");
        propName = sharedPreferences.getString("propName", "");
        newsTitle = sharedPreferences.getString("title", "");
        tvPropertyName = findViewById(R.id.tvPropertyName);
        tvPropertyName.setText(propName);
        //////////////////////////////////////////////
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
        toolbarBtnNews.setBackgroundColor(getResources().getColor(R.color.btnBackground));

        toolbar = new ToolbarHelper(this, toolbarBtnChat, toolbarBtnNews, toolbarBtnForms,
                toolbarBtnSettings, btnHome, toolbarBtnSearch, toolbarBtnMenu);

        //////////////////////////////////////////////

        /////////////////////////////////////////////////////////////////////////

        // for unread chat messages counter in the toolbar
         SharedPreferences myPreference = getSharedPreferences("myUserSharedPref", MODE_PRIVATE);
        userPhoneNum = myPreference.getString("phoneNum", "");
        new FirebaseDataseHelper().readChats(new FirebaseDataseHelper.ChatDataStatus() {
            @Override
            public void DataIsLoad(List<Chat> chats, List<String> keys) {
                allMessages = chats;
                int count = 0;
                if (allMessages != null) {
                    for (Chat chat : allMessages)
                    {
                        if (!chat.getPhoneNumber().equals(userPhoneNum) && chat.getChatSeen().equals("false")
                                && chat.getChatMessageId().contains(userPhoneNum))
                        {
                            count++;
                        }
                    }
                }

                if (count > 0) {
                    toolbarBtnChat.setText("CHAT (" + count + ")");
                    toolbarBtnChat.setTextColor(Color.parseColor("#FF0000"));
                } else if (count <= 0) {
                    toolbarBtnChat.setText("CHAT");
                    toolbarBtnChat.setTextColor(Color.parseColor("#000000"));
                }
            }
        });

        ////////////////////////////////////////////////////////////////////////

        //
        myPropRef = FirebaseDatabase.getInstance().getReference();

        btn_add = findViewById(R.id.btn_add_news);
        switch (myRole) {
            case "Landlord":
                btn_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent addIntent = new Intent(getBaseContext(), AddNewsActivity.class);
                        startActivity(addIntent);
                    }
                });
                break;
            case "Tenant":
                btn_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getBaseContext(), "Sorry! You do not have permission to create news.", Toast.LENGTH_LONG).show();
                    }
                });
        }

        newsRecyclerView = findViewById(R.id.newsRecyclerview);
        newsRecyclerView.setHasFixedSize(true);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Read News from database
        // read the list of News from Firebase
        new FirebaseDataseHelper().readNews(new FirebaseDataseHelper.NewsDataStatus() {
            @Override
            public void DataIsLoad(List<News> listNews, List<String> keys) {
                // filter the target viewer
                for (int i = 0; i < listNews.size(); i++) {
                    if (!listNews.get(i).getHideFlag()) {
                        if ((myRole.equals("Tenant"))) {
                            if ((listNews.get(i).getTargetViewer().equals("all"))) {
                                newsList.add(listNews.get(i));
                                newsKeyList.add(keys.get(i));
                            }
                        } else {
                            newsList.add(listNews.get(i));
                            newsKeyList.add(keys.get(i));
                        }
                    }

                }
                setNewsAdapter();
            }
        });

        SharedPreferences.Editor prefEditor = myPreference.edit();
        prefEditor.putString("isSearching", "false");
        prefEditor.apply();
    }

    private void setNewsAdapter() {
        newsAdapter = new NewsAdapter(newsList, newsKeyList, myRole, this);
        newsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        newsRecyclerView.setAdapter(newsAdapter);
    }
}
