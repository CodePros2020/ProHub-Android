package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.codepros.prohub.utils.FirebaseDataseHelper;
import com.codepros.prohub.model.Unit;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ArrayAdapter<String> filterAdaptor;
    private Spinner spFilter;
    private String[] filter;
    private String filterVal;
    private String propId;
    private AppCompatAutoCompleteTextView atvSearch;

    private ArrayAdapter<String> searchListAdapter;
    private List<Unit> allUnitList;
    private List<Unit> myUnitList;
    private List<String> searchList;

    // firebase database objects
    private DatabaseReference myDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // set variables
        myDatabaseRef = FirebaseDatabase.getInstance().getReference();

        spFilter = findViewById(R.id.search_filter_spinner);
        filter = getResources().getStringArray(R.array.search_filter);
        atvSearch = findViewById(R.id.atv_search);
        allUnitList = new ArrayList<>();
        myUnitList = new ArrayList<>();
        searchList = new ArrayList<>();

        SharedPreferences myPref = getSharedPreferences("myUserSharedPref", MODE_PRIVATE);
        propId = myPref.getString("propId", "");

        // set spinner
        setFilterSpinner();

        // initialize default value
//        Init();

        // read values
        new FirebaseDataseHelper().readUnits(new FirebaseDataseHelper.UnitDataStatus() {
            @Override
            public void DataIsLoad(List<Unit> units, List<String> keys) {
                allUnitList = units;
                for (int i = 0; i < allUnitList.size(); i++) {
                    if (allUnitList.get(i).getPropId() == propId) {
                        myUnitList.add(allUnitList.get(i));
                    }
                }
            }
        });

        // handle click event and set desc on textview
        atvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                String selection = parent.getItemAtPosition(pos).toString();

                for (int i = 0; i < myUnitList.size(); i++) {
                    if (myUnitList.get(i).getUnitName() == selection) {
                        String propId = myUnitList.get(i).getPropId();
                        String tenantId = myUnitList.get(i).getTenantId();
                        String unitName = myUnitList.get(i).getUnitName();

                        Intent intent = new Intent(SearchActivity.this, ChatActivity.class);
                        intent.putExtra("propId", propId);
                        intent.putExtra("tenantId", tenantId);
                        intent.putExtra("unitName", unitName);

                        SearchActivity.this.startActivity(intent);
                    }
                }
            }
        });
    }

    // set filter spinner value
    private void setFilterSpinner() {
        filterAdaptor = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filter);
        filterAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilter.setAdapter(filterAdaptor);
        spFilter.setOnItemSelectedListener(this);
    }

    // spinner selection event handler
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        filterVal = parent.getItemAtPosition(pos).toString();

        switch (filterVal) {
            case "Chat":
                searchList.clear();
                for (int i = 0; i < myUnitList.size(); i++) {
                    searchList.add(myUnitList.get(i).getUnitName());
                }
                break;
            default:
                break;
        }
        searchListAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, searchList);
        atvSearch.setThreshold(1); // will start working from first character
        atvSearch.setAdapter(searchListAdapter);
    }

    // spinner selection event handler
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        searchList.clear();
    }
}
