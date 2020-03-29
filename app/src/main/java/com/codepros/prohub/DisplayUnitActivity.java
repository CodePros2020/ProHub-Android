package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.codepros.prohub.utils.ToolbarHelper;
import com.google.firebase.auth.FirebaseAuth;

public class DisplayUnitActivity extends AppCompatActivity {

    //Toolbar
    private Button toolbarBtnSettings, toolbarBtnChat, toolbarBtnNews, toolbarBtnForms;
    private ImageButton toolbarBtnSearch, btnHome, toolbarBtnMenu;
    private ToolbarHelper toolbar;
    //

    String unitName, unitId, tenantNum, myRole;
    EditText etUnitName, etUnitId, etTenantNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_unit);
        SharedPreferences myPreference = getSharedPreferences("myUserSharedPref", MODE_PRIVATE);
        myRole = myPreference.getString("myRole", "");
        //
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

        toolbar = new ToolbarHelper(this, toolbarBtnChat, toolbarBtnNews, toolbarBtnForms,
                toolbarBtnSettings, btnHome, toolbarBtnSearch, toolbarBtnMenu);

        ////////////////////////////////////////////////
        etTenantNum = findViewById(R.id.etTenantNumber);
        etUnitId = findViewById(R.id.etUnitId);
        etUnitName = findViewById(R.id.etUnitName);
        //
        Intent intent = getIntent();
        unitName = intent.getStringExtra("unitName");
        unitId = intent.getStringExtra("unitId");
        tenantNum = intent.getStringExtra("tenantNum");

        etUnitName.setText(unitName);
        etUnitId.setText(unitId);
        etTenantNum.setText(tenantNum);
    }

}
