package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.codepros.prohub.model.Staff;
import com.codepros.prohub.model.ChatMessage;
import com.codepros.prohub.model.Unit;
import com.codepros.prohub.model.User;
import com.codepros.prohub.utils.FirebaseDataseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddUnitActivity extends AppCompatActivity {
    //Toolbar
    private Button toolbarBtnSettings, toolbarBtnChat,toolbarBtnNews,toolbarBtnForms ;
    private ImageButton toolbarBtnSearch,btnHome,toolbarBtnMenu;
    //Activity Items
    private static final String TAG = "AddUnitActivity";
    // user interaction objects
    private EditText etUnitName, etTenantNumber;
    private String propId, unitName, tenantNumber, tenantName, landlordName, landlordPhoneNumber,myRole;
    private String chatMessageId;
    private Button btnSaveUnit;
    private List<User> userList;
    public static final String ANONYMOUS = "anonymous";
    SharedPreferences myPref;

    // firebase database objects
    private DatabaseReference myDataRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_unit);

        myDataRef = FirebaseDatabase.getInstance().getReference();
        Log.d(TAG, "onCreate: Units DB"+myDataRef.child("-M3X8yzN8R0Bs41p0PVy"));
        //Shared Preference
        SharedPreferences myPref = getSharedPreferences("myUserSharedPref", MODE_PRIVATE);
        propId = myPref.getString("propId", "");
        myRole = myPref.getString("myRole", "");
        /////////////////////////////////////////////////////
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

        //click CHAT button on toolbar
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

        //click FORMS button on toolbar
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

        // click Settings icon on toolbar
        toolbarBtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSettings(v);
            }
        });
        //click to go to Property page
//        btnHome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(getBaseContext(),PropertyHomeActivity.class);
//                startActivity(intent);
//            }
//        });

        // Menu drop down
        final PopupMenu dropDownMenu = new PopupMenu(getApplicationContext(), toolbarBtnMenu);
        final Menu menu = dropDownMenu.getMenu();
        // list of items for menu:
        menu.add(0, 0, 0, "Manage Unit");
        menu.add(1, 1, 1, "Manage Staff");
        menu.add(2, 2, 2, "Logout");

        // logout item
        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 0:
                        if(myRole.equals("Tenant")){
                            Toast.makeText(getBaseContext(),"Sorry! You do not have permission to manage staff.",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Intent intent = new Intent(getBaseContext(),ViewUnitsActivity.class);
                            startActivity(intent);
                            return true;
                        }
                    case 1:
                        if(myRole.equals("Tenant")){
                            Toast.makeText(getBaseContext(),"Sorry! You do not have permission to manage staff.",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Intent intent = new Intent(getBaseContext(),ViewStaffActivity.class);
                            startActivity(intent);
                            return true;
                        }

                    case 2:
                        // item ID 0 was clicked
                        Intent i = new Intent(getBaseContext(), MainActivity.class);
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
        // click to go to Property page
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),PropertyHomeActivity.class);
                startActivity(intent);
            }
        });
            /////////////////////////////////////
        myDataRef = FirebaseDatabase.getInstance().getReference();

        myPref = getSharedPreferences("myUserSharedPref", MODE_PRIVATE);
        propId = myPref.getString("propId", "");
        landlordName = myPref.getString("username", ANONYMOUS);
        landlordPhoneNumber = myPref.getString("phoneNum","0123456789");

        //
        etUnitName = findViewById(R.id.etUnitName);
        etTenantNumber = findViewById(R.id.etTenantNumber);
        btnSaveUnit = findViewById(R.id.btnSaveUnit);
        userList = new ArrayList<>();

        // read values
        new FirebaseDataseHelper().readUsers(new FirebaseDataseHelper.UserDataStatus() {
            @Override
            public void DataIsLoad(List<User> users, List<String> keys) {
                userList = users;
            }
        });

        btnSaveUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUnit(v);
            }
        });
    }

    private void addUnit(View view) {

        unitName = etUnitName.getText().toString();
        tenantNumber = etTenantNumber.getText().toString();
        boolean tenantExist = false;
        boolean isTenant = false;
        for (User u : userList) {
            if (u.getPhone().equals(tenantNumber)) {
                //Toast.makeText(this, "u.getPhone(): " + u.getPhone(), Toast.LENGTH_LONG).show();
                tenantExist = true;
                if (u.getRole().equals("Tenant")) {
                    isTenant = true;
                    tenantName = u.getFirstname() + " " + u.getLastname();
                    chatMessageId = landlordPhoneNumber + "_" + tenantNumber;
                }
            }
        }
        // validate the input field in the new Property form
        if(unitName.isEmpty()){
            // show error message
            String message = "Sorry, unit name cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        else if(tenantNumber.isEmpty()){
            // show error message
            String message = "Sorry, tenant number cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } else if(tenantNumber.length() != 10){
            // show error message
            String message = "Sorry, tenant number must be 10 digit!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } else if (!tenantExist) {
            // show error message
            String message = "Sorry, tenant does not exist!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } else if (!isTenant) {
            // show error message
            String message = "Sorry, entered number is not a tenant!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } else {
          //  Unit newUnit = new Unit(propId, tenantNumber, unitName);
            ChatMessage newChatMessage1 = new ChatMessage(chatMessageId, tenantNumber, landlordPhoneNumber, landlordName);
            ChatMessage newChatMessage2 = new ChatMessage(chatMessageId, landlordPhoneNumber, tenantNumber, tenantName);

            DatabaseReference chatRef = myDataRef.child("chatMessages");
            DatabaseReference newChatRef1 = chatRef.push();
            DatabaseReference newChatRef2 = chatRef.push();
            newChatRef1.setValue(newChatMessage1);
            newChatRef2.setValue(newChatMessage2);

            String unitId=  myDataRef.push().getKey();
            Unit newUnit = new Unit(unitId,propId, tenantNumber, unitName);
            myDataRef.child("units").child(unitId).setValue(newUnit);
            Toast.makeText(getApplicationContext(), "New Unit Saved!", Toast.LENGTH_LONG).show();

            // intent to next page
            Intent intent = new Intent(this, ViewUnitsActivity.class);
            this.startActivity(intent);
        }
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
    public void goSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        this.startActivity(intent);
    }

}
