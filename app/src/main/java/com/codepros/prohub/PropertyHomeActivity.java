package com.codepros.prohub;


import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.codepros.prohub.model.Property;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class PropertyHomeActivity extends AppCompatActivity {


    private Button chatButton, chatButton2;
    private Button newsroomButton, newsroomButton2;
    private Button formsButton, formsButton2;
    private Button settingsButton, settingsButton2;
    private ImageButton searchIBtn, menuIBtn;

    // Firebase database objects
    private static final String TAG = "PropertyHomeActivity";
    private DatabaseReference myPropRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_home);
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        myPropRef = FirebaseDatabase.getInstance().getReference();

        // references to the buttons on view
        chatButton2 = findViewById(R.id.chatButton2);
        newsroomButton2 = findViewById(R.id.newsroomButton2);
        formsButton2 = findViewById(R.id.formsButton2);
        settingsButton2 = findViewById(R.id.settingsButton2);

        // references to buttons on toolbar
        chatButton = tb.findViewById(R.id.btnChat);
        newsroomButton = tb.findViewById(R.id.btnNews);
        formsButton = tb.findViewById(R.id.btnForms);
        settingsButton = tb.findViewById(R.id.btnSettings);

        //references to image buttons
        searchIBtn = tb.findViewById(R.id.ImageButtonSearch);
        menuIBtn = tb.findViewById(R.id.ImageButtonMenu);

        // Button for top toolbar
        // NEEDS TO BE CHANGED
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPage(v);
            }
        });
        newsroomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPage(v);
            }
        });
        formsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPage(v);
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPage(v);
            }
        });


        // buttons going back to Main Activity
        // NEEDS TO BE CHANGED
        chatButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPage(v);
            }
        });
        newsroomButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPage(v);
            }
        });
        formsButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPage(v);
            }
        });
        settingsButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPage(v);
            }
        });

        // image buttons
        // NEEDS TO BE CHANGED
        searchIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPage(v);
            }
        });
        menuIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPage(v);
            }
        });


    }

    // CHANGE THIS TO THE ACTUAL PAGE LATER
    public void goPage(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }
}
