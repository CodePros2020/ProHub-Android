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

import com.codepros.prohub.model.Staff;
import com.codepros.prohub.model.Unit;
import com.codepros.prohub.utils.FirebaseDataseHelper;
import com.codepros.prohub.utils.StaffAdapter;
import com.codepros.prohub.utils.ToolbarHelper;
import com.codepros.prohub.utils.UnitAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ViewUnitsActivity extends AppCompatActivity {
    //Toolbar
    private Button toolbarBtnSettings, toolbarBtnChat, toolbarBtnNews, toolbarBtnForms;
    private ImageButton toolbarBtnSearch, btnHome, toolbarBtnMenu;
    private ToolbarHelper toolbar;
    //
    private TextView tvTitleUnit;
    // Activity Items
    public RecyclerView unitsRecyclerView;
    public UnitAdapter unitAdapter;
    public List<Unit> unitList = new ArrayList<>();
    public List<String> unitKeyList = new ArrayList<>();
    public FloatingActionButton btn_add;
    private DatabaseReference myUnitsRef;
    DatabaseReference drUnits;

    String propId, propName;
    // user role
    private String myRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_units);

        //
        //Shared pref
        SharedPreferences sharedPreferences = getSharedPreferences("myUserSharedPref", Context.MODE_PRIVATE);
        myRole = sharedPreferences.getString("myRole", "");
        propId = sharedPreferences.getString("propId", "");
        propName = sharedPreferences.getString("propName", "");
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

        btn_add = findViewById(R.id.btn_add_unit);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myRole.equals("Tenant")) {
                    Toast.makeText(getApplicationContext(), "Sorry! You do not have permission to add unit.", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(getBaseContext(), AddUnitActivity.class);
                    startActivity(intent);
                }
            }
        });

        //////////////////////////////////////////////

        tvTitleUnit = findViewById(R.id.tvTitleUnit);
        tvTitleUnit.setText(propName);
        //
        myUnitsRef = FirebaseDatabase.getInstance().getReference();
        //Add button functionality
        Log.d("Prop: ", "Property Id" + propId);

        // Staff recycler view
        unitsRecyclerView = findViewById(R.id.unitsRecyclerView);
        unitsRecyclerView.setHasFixedSize(true);
        unitsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Read News from database
        // read the list of News from Firebase
        new FirebaseDataseHelper().readUnits(new FirebaseDataseHelper.UnitDataStatus() {

            @Override
            public void DataIsLoad(List<Unit> listUnits, List<String> keys) {
                unitList.clear();
                for (Unit unit : listUnits) {
                    if (unit.getPropId().equals(propId)) {
                        unitList.add(unit);
                    }
                }
                // filter the target viewer
                //unitList=listUnits;
                unitKeyList = keys;
                setUnitsAdapter();
            }
        });

    }

    private void setUnitsAdapter() {
        unitAdapter = new UnitAdapter(unitList, unitKeyList, myRole, this);
        unitsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(unitsRecyclerView);
        unitsRecyclerView.setAdapter(unitAdapter);
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            if (myRole.equals("Tenant")) {
                Toast.makeText(getApplicationContext(), "Sorry! You do not have permission to delete unit.", Toast.LENGTH_LONG).show();
            } else {
                Unit unit = unitList.get(viewHolder.getAdapterPosition());
                unitList.remove(viewHolder.getAdapterPosition());
                drUnits = FirebaseDatabase.getInstance().getReference("units").child(unit.getUnitId());
                drUnits.removeValue();
                unitAdapter.notifyDataSetChanged();

                Toast.makeText(getBaseContext(), "Unit deleted successfully", Toast.LENGTH_LONG).show();
            }


        }
    };
}

