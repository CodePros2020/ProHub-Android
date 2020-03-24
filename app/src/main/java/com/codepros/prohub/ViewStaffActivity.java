package com.codepros.prohub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
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

import com.codepros.prohub.model.News;
import com.codepros.prohub.model.Staff;
import com.codepros.prohub.utils.FirebaseDataseHelper;
import com.codepros.prohub.utils.NewsAdapter;
import com.codepros.prohub.utils.StaffAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ViewStaffActivity extends AppCompatActivity {
    // Toolbar items
    private Button toolbarBtnChat;
    private Button toolbarBtnNews;
    private Button toolbarBtnForms;
    private Button toolbarBtnSettings;
    private ImageButton toolbarBtnHome, toolbarBtnSearch;
    private ImageButton toolbarBtnMenu;

    public RecyclerView staffRecyclerView;
    public StaffAdapter staffAdapter;
    public List<Staff> staffList=new ArrayList<>();
    public List<String> staffKeyList = new ArrayList<>();
    public FloatingActionButton btn_add;
    private DatabaseReference myStaffRef;

    String propId;
    // user role
    private String myRole;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_staff);



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
//
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

        // click Settings button on toolbar
        toolbarBtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSettings(v);
            }
        });
        // Menu drop down
        final PopupMenu dropDownMenu = new PopupMenu(ViewStaffActivity.this, toolbarBtnMenu);
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
                        Intent i = new Intent(ViewStaffActivity.this, MainActivity.class);
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
        Intent intent=getIntent();
        propId= intent.getStringExtra("propId");
        //Shared pref
        SharedPreferences sharedPreferences = getSharedPreferences("myUserSharedPref", Context.MODE_PRIVATE);
        myRole= sharedPreferences.getString("myRole", "");
        Log.d("Role in preference: ", "onCreate: "+myRole);
        //
        myStaffRef = FirebaseDatabase.getInstance().getReference("staff");
        //Add button functionality
        btn_add=findViewById(R.id.btn_add_staff);

                btn_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent addIntent=new Intent(getBaseContext(),AddStaffActivity.class);
                        addIntent.putExtra("propId",propId);
                        startActivity(addIntent);
                    }
                });
        // Staff recycler view
        staffRecyclerView =findViewById(R.id.staffRecyclerView);
        staffRecyclerView.setHasFixedSize(true);
        staffRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Read News from database
        // read the list of News from Firebase
        new FirebaseDataseHelper().readStaff(new FirebaseDataseHelper.StaffDataStatus() {
            @Override
            public void DataIsLoad(List<Staff> listStaff, List<String> keys) {
                // filter the target viewer
                staffList=listStaff;
                staffKeyList=keys;
                setNewsAdapter();
            }
        });

    }
    private void setNewsAdapter() {
        staffAdapter = new StaffAdapter( staffList,staffKeyList, myRole,this);
        staffRecyclerView.setItemAnimator(new DefaultItemAnimator());
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(staffRecyclerView);
        staffRecyclerView.setAdapter(staffAdapter);
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
        Intent intent = new Intent(this, ViewStaffActivity.class);
        this.startActivity(intent);
    }


    ItemTouchHelper.SimpleCallback itemTouchHelperCallback=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Log.d("Item Postion", "onSwiped iTE POSITION: "+viewHolder.getItemId());
            staffList.remove(viewHolder.getAdapterPosition());
           // myStaffRef.child(viewHolder.getAdapterPosition());
            staffAdapter.notifyDataSetChanged();
            Toast.makeText(getBaseContext(),"Staff deleted successfully",Toast.LENGTH_LONG).show();
        }
    };
}
