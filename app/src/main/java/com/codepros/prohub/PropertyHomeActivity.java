package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.codepros.prohub.model.Property;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class PropertyHomeActivity extends AppCompatActivity {

    private Button chatButton, chatButton2;
    private Button newsroomButton, newsroomButton2;
    private Button formsButton, formsButton2;
    private Button settingsButton, settingsButton2;

    // Firebase database objects
    private static final String TAG = "PropertyHomeActivity";
    private DatabaseReference myPropRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_home);

        myPropRef = FirebaseDatabase.getInstance().getReference();

        // references to the buttons on view
        chatButton = findViewById(R.id.chatButton);
        chatButton2 = findViewById(R.id.chatButton2);
        newsroomButton = findViewById(R.id.newsroomButton);
        newsroomButton2 = findViewById(R.id.newsroomButton2);
        formsButton = findViewById(R.id.formsButton);
        formsButton2 = findViewById(R.id.formsButton2);
        settingsButton = findViewById(R.id.settingsButton);
        settingsButton2 = findViewById(R.id.settingsButton2);

        // Button for top toolbar
        // NEEDS TO BE CHANGED
        chatButton.setOnClickListener(new View.OnClickListener() {
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



    }

    // CHANGE THIS TO THE ACTUAL PAGE LATER
    public void goPage(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }
}
