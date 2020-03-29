package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import com.codepros.prohub.utils.ToolbarHelper;

public class UserInfoActivity extends AppCompatActivity {
    //Toolbar
    private Button toolbarBtnSettings, toolbarBtnChat, toolbarBtnNews, toolbarBtnForms;
    private ImageButton toolbarBtnSearch, btnHome, toolbarBtnMenu;
    private ToolbarHelper toolbar;

    private String userPhoneNum, propId, imageUrl, myRole, userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        SharedPreferences myPreference = getSharedPreferences("myUserSharedPref", MODE_PRIVATE);
        myRole = myPreference.getString("myRole", "");
        userPhoneNum = myPreference.getString("phoneNum", "");
        propId = myPreference.getString("propId", "");
        userName = myPreference.getString("username", "");
        //////////////////////////////////////////////
        // declaring the buttons

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
    }
}
