package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.google.firebase.auth.FirebaseAuth;

public class FormActivity extends AppCompatActivity {

    // Toolbar items
    private Button toolbarBtnChat;
    private Button toolbarBtnNews;
    private Button toolbarBtnForms;
    private Button toolbarBtnSettings;
    private ImageButton toolbarBtnHome, toolbarBtnSearch;
    private ImageButton toolbarBtnMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

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
        final PopupMenu dropDownMenu = new PopupMenu(FormActivity.this, toolbarBtnMenu);
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
                        Intent i = new Intent(FormActivity.this, MainActivity.class);
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
