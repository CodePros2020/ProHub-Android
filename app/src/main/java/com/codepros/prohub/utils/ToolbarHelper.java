package com.codepros.prohub.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.codepros.prohub.AddUnitActivity;
import com.codepros.prohub.ChatList;
import com.codepros.prohub.FormsActivity;
import com.codepros.prohub.MainActivity;
import com.codepros.prohub.NewsViewActivity;
import com.codepros.prohub.PropertyHomeActivity;
import com.codepros.prohub.SearchActivity;
import com.codepros.prohub.SettingsActivity;
import com.codepros.prohub.ViewStaffActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ToolbarHelper {
    private Context context;
    private View toolBtnChat, toolBtnNews, toolBtnForms, toolBtnSettings;
    private View toolBtnHome, toolBtnMenu, toolBtnSearch;
    public String propId, myRole, propName, phoneNum;

    public ToolbarHelper(Context context) {
        this.context = context;
    }

    public ToolbarHelper(Context context, View toolBtnChat, View toolBtnNews, View toolBtnForms, View toolBtnSettings,
                         View toolBtnHome, View toolBtnSearch, View toolBtnMenu) {
        this.context = context;
        this.toolBtnChat = toolBtnChat;
        this.toolBtnNews = toolBtnNews;
        this.toolBtnForms = toolBtnForms;
        this.toolBtnSettings = toolBtnSettings;
        this.toolBtnHome = toolBtnHome;
        this.toolBtnMenu = toolBtnMenu;
        this.toolBtnSearch = toolBtnSearch;

        SetSharedPrefValues();
        setOnClickFunctions();
    }

    public void SetSharedPrefValues() {
        SharedPreferences myPreference = context.getSharedPreferences("myUserSharedPref", 0);
        myRole = myPreference.getString("myRole", "");
        SharedPreferences propPreference = Objects.requireNonNull(context).getSharedPreferences("myPropSharedPref", Context.MODE_PRIVATE);
        propId = propPreference.getString("phoneNum", "");
        propId = propPreference.getString("propId", "");
    }

    public void setOnClickFunctions() {
        toolBtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goChat(v);
            }
        });

        toolBtnNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNews(v);
            }
        });

        toolBtnForms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goForms(v);
            }
        });

        toolBtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSettings(v);
            }
        });

        toolBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSearch(v);
            }
        });

        toolBtnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome(v);
            }
        });

        getMenuBtn(toolBtnMenu);
    }

    // Menu button Action
    public void getMenuBtn(View btnMenu) {
        // Menu drop down
        final PopupMenu dropDownMenu = new PopupMenu(context, btnMenu);
        final Menu menu = dropDownMenu.getMenu();
        // list of items for menu:
        menu.add(0, 0, 0, "Manage Staff");
        menu.add(1, 1, 1, "Logout");

        // logout item
        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 0:
                        if (myRole.equals("Tenant")) {
                            Toast.makeText(context, "Sorry! You do not have permission to manage staff.", Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(context, ViewStaffActivity.class);
                            intent.putExtra("propId", propId);
                            intent.putExtra("propName", propName);
                            context.startActivity(intent);
                            return true;
                        }

                    case 1:
                        FirebaseAuth.getInstance().signOut();
                        // item ID 0 was clicked
                        Intent i = new Intent(context, MainActivity.class);
                        i.putExtra("finish", true);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clean all activities
                        context.startActivity(i);
                        //finish();
                        return true;
                }
                return false;
            }
        });

        // Menu button click
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropDownMenu.show();
            }
        });

    }

    public void goChat(View view) {
        Intent intent = new Intent(context, ChatList.class);
        context.startActivity(intent);
    }

    public void goNews(View view) {
        Intent intent = new Intent(context, NewsViewActivity.class);
        context.startActivity(intent);
    }

    public void goForms(View view) {
        Intent intent = new Intent(context, FormsActivity.class);
        context.startActivity(intent);
    }

    public void goSettings(View view) {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra("propId", propId);
        context.startActivity(intent);
    }

    public void goSearch(View view) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    public void goHome(View view) {
        Intent intent = new Intent(context, PropertyHomeActivity.class);
        context.startActivity(intent);
    }

}
