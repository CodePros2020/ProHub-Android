package com.codepros.prohub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import com.codepros.prohub.model.Chat;
import com.codepros.prohub.model.News;
import com.codepros.prohub.model.Staff;
import com.codepros.prohub.model.Unit;
import com.codepros.prohub.utils.FirebaseDataseHelper;
import com.codepros.prohub.utils.NewsAdapter;
import com.codepros.prohub.utils.StaffAdapter;
import com.codepros.prohub.utils.ToolbarHelper;
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
    private Button toolbarBtnSettings, toolbarBtnChat, toolbarBtnNews, toolbarBtnForms;
    private ImageButton toolbarBtnSearch, btnHome, toolbarBtnMenu;
    private ToolbarHelper toolbar;
    //

    private TextView tvTitleStaff;
    public RecyclerView staffRecyclerView;
    public StaffAdapter staffAdapter;
    public List<Staff> staffList = new ArrayList<>();
    public List<String> staffKeyList = new ArrayList<>();
    public FloatingActionButton btn_add;
    private DatabaseReference myStaffRef;
    DatabaseReference drStaff;

    String propId, propName;
    // user role
    private String myRole;

     List<Chat> allMessages = new ArrayList<>();
     private String userPhoneNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_staff);
        //Shared pref
        SharedPreferences sharedPreferences = getSharedPreferences("myUserSharedPref", Context.MODE_PRIVATE);
        myRole = sharedPreferences.getString("myRole", "");
        propName = sharedPreferences.getString("propName", "");
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

        tvTitleStaff = findViewById(R.id.tvTitleStaff);
        tvTitleStaff.setText(propName);
        //
        myStaffRef = FirebaseDatabase.getInstance().getReference();

        //Add button functionality
        Log.d("Prop: ", "Property Id" + propId);
        if (myRole.equals("Tenant")) {
            Toast.makeText(getApplicationContext(), "Sorry! You do not have permission to edit staff.", Toast.LENGTH_LONG).show();
        } else {
            btn_add = findViewById(R.id.btn_add_staff);

            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent addIntent = new Intent(getBaseContext(), AddStaffActivity.class);
                    addIntent.putExtra("propId", propId);
                    startActivity(addIntent);
                }
            });
        }

        // Staff recycler view
        staffRecyclerView = findViewById(R.id.staffRecyclerView);
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
        staffAdapter = new StaffAdapter(staffList, staffKeyList, myRole, this);
        staffRecyclerView.setItemAnimator(new DefaultItemAnimator());
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(staffRecyclerView);
        staffRecyclerView.setAdapter(staffAdapter);
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }



        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

            if (myRole.equals("Tenant")) {
                Toast.makeText(getApplicationContext(), "Sorry! You do not have permission to delete staff.", Toast.LENGTH_LONG).show();
            } else {

                // get the position of staff to remove
                final int position = viewHolder.getAdapterPosition();
                final Staff staffToRemove = staffList.remove(position);

                // display alert dialog for deletion
                AlertDialog.Builder builder = new AlertDialog.Builder(viewHolder.itemView.getContext());

                builder.setMessage("Are you sure to delete the staff?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // delete
                                drStaff = FirebaseDatabase.getInstance().getReference("staff").child(staffToRemove.getStaffId());
                                drStaff.removeValue();

                                // reflect the change
                                staffAdapter.notifyDataSetChanged();

                                // toast message
                                Toast.makeText(getBaseContext(), "Staff deleted successfully", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // get the staff back
                                staffList.add(position, staffToRemove);

                                // reflect the change
                                staffAdapter.notifyDataSetChanged();
                            }
                        });
                builder.show();

            }

        }
    };
}
