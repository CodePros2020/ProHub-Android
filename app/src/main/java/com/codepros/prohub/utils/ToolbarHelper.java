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
            public String propId,myRole,propName,phoneNum;

        public ToolbarHelper(Context context){
            this.context=context;
        }
    public void SetValues(){
        SharedPreferences myPreference = context.getSharedPreferences("myUserSharedPref", 0);
        myRole=myPreference.getString("myRole","");
        SharedPreferences propPreference = Objects.requireNonNull(context).getSharedPreferences("myPropSharedPref", Context.MODE_PRIVATE);
        propId = propPreference.getString("phoneNum", "");
        propId = propPreference.getString("propId", "");

    }

        // Chat buttons
        public void getChatButtons(View btnChat, View toolBtnChat){

            btnChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goChat(v);
                }
            });
            toolBtnChat.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    goChat(v);
                }
            });

        }

        //News Buttons
    public void getNewsButtons(View btnNews, View toolBtnNews){

        btnNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNews(v);
            }
        });
        toolBtnNews.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                goNews(v);
            }
        });

    }
    //Forms  Buttons
    public void getFormsButtons(View btnForms, View toolBtnForms){

        btnForms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goForms(v);
            }
        });
        toolBtnForms.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                goForms(v);
            }
        });

    }
    //Settings Buttons
    public void getSettingsButtons(View btnSettings, View toolBtnSettings){

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSettings(v);
            }
        });
        toolBtnSettings.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                goSettings(v);
            }
        });

    }
    //Search  Buttons
    public void getSearchButtons(View btnSettings){

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSearch(v);
            }
        });
    }
    //Add unit  Buttons
    public void getAddUnitButtons(View btnAddUnit){

        btnAddUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myRole.equals("Landlord")) {
                    goAddUnit(v);
                } else {
                    Toast.makeText(context, "\"Sorry! You do not have permission to add property.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //Home button action
    public void getHomeBtn(View btnHome){
            btnHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context,PropertyHomeActivity.class);
                    context.startActivity(intent);
                }
            });
    }
    // Menu button Action
public void getMenuBtn(View btnMenu){
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
                    if(myRole.equals("Tenant")){
                        Toast.makeText(context,"Sorry! You do not have permission to manage staff.",Toast.LENGTH_LONG).show();
                    }
                    else{
                        Intent intent = new Intent(context, ViewStaffActivity.class);
                        intent.putExtra("propId", propId);
                        intent.putExtra("propName",propName);
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



    public void getValues(String propID,String myRole,String propName){
            this.propId=propID;
            this.myRole=myRole;
            this.propName=propName;
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
    public void goAddUnit(View view) {
        Intent intent = new Intent(context, AddUnitActivity.class);
        intent.putExtra("propId", propId);
        context.startActivity(intent);
    }






}
