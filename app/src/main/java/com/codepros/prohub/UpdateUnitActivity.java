package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.codepros.prohub.model.Chat;
import com.codepros.prohub.model.Unit;
import com.codepros.prohub.utils.FirebaseDataseHelper;
import com.codepros.prohub.utils.ToolbarHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class UpdateUnitActivity extends AppCompatActivity {
    //Toolbar
    private Button toolbarBtnSettings, toolbarBtnChat, toolbarBtnNews, toolbarBtnForms;
    private ImageButton toolbarBtnSearch, btnHome, toolbarBtnMenu;
    private ToolbarHelper toolbar;
    //
    String unitName, unitId, tenantNum, myRole,propId;
    EditText etUnitName, etUnitId, etTenantNum;
    Button btnUpdate,btnCancel;
    DatabaseReference   myUnitRef;

     List<Chat> allMessages = new ArrayList<>();
     private String userPhoneNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_unit);
        myUnitRef=FirebaseDatabase.getInstance().getReference("units");
        SharedPreferences sharedPreferences = getSharedPreferences("myUserSharedPref", Context.MODE_PRIVATE);
        myRole = sharedPreferences.getString("myRole", "");
        propId = sharedPreferences.getString("propId", "");

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
        etTenantNum = findViewById(R.id.etTenantNumber);
        etUnitId = findViewById(R.id.etUnitId);
        etUnitName = findViewById(R.id.etUnitName);
        btnCancel=findViewById(R.id.updateCancelBtn);
        btnUpdate=findViewById(R.id.updateUnitBtn);
        //
        Intent intent = getIntent();
        unitName = intent.getStringExtra("unitName");
        unitId = intent.getStringExtra("unitId");
        tenantNum = intent.getStringExtra("tenantNum");

        etUnitName.setText(unitName);
        etUnitId.setText(unitId);
        etTenantNum.setText(tenantNum);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUnit(v);

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               goBack();;
            }
        });
    }
    public void updateUnit(View v){
            String unitName=etUnitName.getText().toString();
            String tenantNo=etTenantNum.getText().toString();
            String unitId=etUnitId.getText().toString();
        // validations
        if (unitName.isEmpty()) {
            String message = "Sorry, unit name cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
       else if (tenantNo.isEmpty()) {
            String message = "Please add tenant number!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        else {
            // staff with updated information
            Unit updateUnit = new Unit(unitId,propId,tenantNo,unitName);

            // update database
            myUnitRef.child(unitId).setValue(updateUnit);

            // notification
            Toast.makeText(getApplicationContext(), updateUnit.getUnitName() + " is updated!", Toast.LENGTH_LONG).show();

            // redirect to Staff list View
            goBack();
        }

    }
    public void goBack(){
        Intent intent=new Intent(getApplicationContext(),ViewUnitsActivity.class);
        startActivity(intent);
    }
}
