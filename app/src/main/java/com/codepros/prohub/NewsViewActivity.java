package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
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
import android.widget.Toast;

import com.codepros.prohub.model.ChatMessage;
import com.codepros.prohub.model.News;
import com.codepros.prohub.model.User;
import com.codepros.prohub.utils.ChatAdapter;
import com.codepros.prohub.utils.FirebaseDataseHelper;
import com.codepros.prohub.utils.NewsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class NewsViewActivity extends AppCompatActivity {

    // Toolbar items
    private Button toolbarBtnChat;
    private Button toolbarBtnNews;
    private Button toolbarBtnForms;
    private Button toolbarBtnSettings;
    private ImageButton toolbarBtnHome, toolbarBtnSearch;
    private ImageButton toolbarBtnMenu;

    public RecyclerView newsRecyclerView;
    public NewsAdapter newsAdapter;
    public List<News> newsList=new ArrayList<>();
    public List<String> newsKeyList = new ArrayList<>();
    public FloatingActionButton btn_add;
    private DatabaseReference myPropRef;
    // user role
    private String myRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_view);

        ////////////////////////////////////////////////////////////////////
        // TOOLBAR
        // Button for top toolbar
        toolbarBtnChat = findViewById(R.id.toolbarBtnChat);
        toolbarBtnNews = findViewById(R.id.toolbarBtnNews);
        toolbarBtnForms = findViewById(R.id.toolbarBtnForms);
        toolbarBtnSettings = findViewById(R.id.toolbarBtnSettings);
        toolbarBtnHome = findViewById(R.id.ImageButtonHome);
        toolbarBtnSearch = findViewById(R.id.ImageButtonSearch);
        toolbarBtnMenu = findViewById(R.id.ImageButtonMenu);

        // click CHAT button on toolbar
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

        // click FORMS button on toolbar
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


        // Menu drop down
        final PopupMenu dropDownMenu = new PopupMenu(NewsViewActivity.this, toolbarBtnMenu);
        final Menu menu = dropDownMenu.getMenu();
        // list of items for menu:
        menu.add(0, 0, 0, "Logout");

        // logout item
        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 0:
                        // item ID 0 was clicked
                        Intent i = new Intent(NewsViewActivity.this, MainActivity.class);
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


        //////////////////////////////////////////////////////////////////////////

        //Shared pref
        SharedPreferences sharedPreferences = getSharedPreferences("myUserSharedPref", Context.MODE_PRIVATE);
        myRole= sharedPreferences.getString("myRole", "");
        Log.d("Role in preference: ", "onCreate: "+myRole);
        //
        myPropRef = FirebaseDatabase.getInstance().getReference();

        btn_add=findViewById(R.id.btn_add_news);
        switch(myRole){
            case "Landlord":
                btn_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent addIntent=new Intent(getBaseContext(),AddNewsActivity.class);
                        startActivity(addIntent);
                    }
                });
                break;
            case "Tenant":
                btn_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getBaseContext(),"Sorry! You do not have permission to create news.",Toast.LENGTH_LONG).show();
                    }
                });
        }


        newsRecyclerView =findViewById(R.id.newsRecyclerview);
        newsRecyclerView.setHasFixedSize(true);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Read News from database
        // read the list of News from Firebase
        new FirebaseDataseHelper().readNews(new FirebaseDataseHelper.NewsDataStatus() {
            @Override
            public void DataIsLoad(List<News> listNews, List<String> keys) {
                // filter the target viewer
                for(int i=0;i<listNews.size();i++){
                    if(!listNews.get(i).getHideFlag()){
                        if((myRole.equals("Tenant"))){
                            if((listNews.get(i).getTargetViewer().equals("all"))){
                                newsList.add(listNews.get(i));
                                newsKeyList.add(keys.get(i));
                            }
                        }
                        else{
                            newsList.add(listNews.get(i));
                            newsKeyList.add(keys.get(i));
                        }
                    }

                }
                setNewsAdapter();
            }
        });

    }
    private void setNewsAdapter() {
        newsAdapter = new NewsAdapter( newsList, newsKeyList, myRole,this);
        newsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        newsRecyclerView.setAdapter(newsAdapter);
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
}
