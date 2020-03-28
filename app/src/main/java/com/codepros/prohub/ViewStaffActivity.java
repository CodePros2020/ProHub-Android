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
import android.widget.TextView;
import android.widget.Toast;

import com.codepros.prohub.model.News;
import com.codepros.prohub.model.Staff;
import com.codepros.prohub.model.Unit;
import com.codepros.prohub.utils.FirebaseDataseHelper;
import com.codepros.prohub.utils.NewsAdapter;
import com.codepros.prohub.utils.StaffAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewStaffActivity extends AppCompatActivity {
    //Toolbar
    private Button toolbarBtnSettings, toolbarBtnChat,toolbarBtnNews,toolbarBtnForms ;
    private ImageButton toolbarBtnSearch,btnHome,toolbarBtnMenu;
    //

    private TextView tvTitleStaff;
    public RecyclerView staffRecyclerView;
    public StaffAdapter staffAdapter;
    public List<Staff> staffList=new ArrayList<>();
    public List<String> staffKeyList = new ArrayList<>();
    public FloatingActionButton btn_add;
    private DatabaseReference myStaffRef;
    DatabaseReference drStaff;

    String propId,propName;
    // user role
    private String myRole;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_staff);
        //Shared pref
        SharedPreferences sharedPreferences = getSharedPreferences("myUserSharedPref", Context.MODE_PRIVATE);
        myRole= sharedPreferences.getString("myRole", "");
        propName= sharedPreferences.getString("propName", "");
        propId= sharedPreferences.getString("propId", "");
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


        // Menu drop down
        final PopupMenu dropDownMenu = new PopupMenu(this, toolbarBtnMenu);
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
                            finish();
                            return true;
                        }
                    case 1:
                        if(myRole.equals("Tenant")){
                            Toast.makeText(getBaseContext(),"Sorry! You do not have permission to manage staff.",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Intent intent = new Intent(getBaseContext(),ViewStaffActivity.class);
                            startActivity(intent);
                            finish();
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
        //////////////////////////////////////////////

        //
        tvTitleStaff=findViewById(R.id.tvTitleStaff);
        tvTitleStaff.setText(propName);
        //
        myStaffRef = FirebaseDatabase.getInstance().getReference();
        //Add button functionality
        Log.d("Prop: ", "Property Id"+propId);
        if(myRole.equals("Tenant")){
            Toast.makeText(getApplicationContext(),"Sorry! You do not have permission to edit staff.",Toast.LENGTH_LONG).show();
        }
        else{
            btn_add=findViewById(R.id.btn_add_staff);

            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent addIntent=new Intent(getBaseContext(),AddStaffActivity.class);
                    addIntent.putExtra("propId",propId);
                    startActivity(addIntent);
                }
            });
        }

        // Staff recycler view
        staffRecyclerView =findViewById(R.id.staffRecyclerView);
        staffRecyclerView.setHasFixedSize(true);
        staffRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Read News from database
        // read the list of News from Firebase
        new FirebaseDataseHelper().readStaff(new FirebaseDataseHelper.StaffDataStatus() {
            @Override
            public void DataIsLoad(List<Staff> listStaff, List<String> keys) {
                staffList.clear();
                for (Staff staff : listStaff) {
                    if (staff.getPropId().equals(propId)) {
                        staffList.add(staff);
                    }
                }
                // filter the target viewer
                //
                staffKeyList = keys;
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
        Intent intent = new Intent(this, SettingsActivity.class);
        this.startActivity(intent);
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if(myRole.equals("Tenant")){
                Toast.makeText(getApplicationContext(),"Sorry! You do not have permission to delete staff.",Toast.LENGTH_LONG).show();
            }
            else{
                Staff staff=staffList.get(viewHolder.getAdapterPosition());
                staffList.remove(viewHolder.getAdapterPosition());
                drStaff=FirebaseDatabase.getInstance().getReference("staff").child(staff.getStaffId());
                drStaff.removeValue();
                staffAdapter.notifyDataSetChanged();

                Toast.makeText(getBaseContext(),"Staff deleted successfully",Toast.LENGTH_LONG).show();
            }

        }
    };
}
