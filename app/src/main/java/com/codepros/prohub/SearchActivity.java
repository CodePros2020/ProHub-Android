package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.codepros.prohub.model.FirebaseDataseHelper;
import com.codepros.prohub.model.Unit;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "SearchActivity";
    private ArrayAdapter<String> filterAdaptor;
    private Spinner spFilter;
    private String[] filter;
    private String filterVal;

    private AppCompatAutoCompleteTextView atvSearch;
    private ListView lvSearch;

    private ArrayAdapter<String> searchListAdapter;
    private List<Unit> unitList;
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
        lvSearch = findViewById(R.id.lv_search);
        unitList = new ArrayList<>();
        searchList = new ArrayList<>();

        // initialize default value
//        Init();

        // read values
        new FirebaseDataseHelper().readUnits(new FirebaseDataseHelper.UnitDataStatus() {
            @Override
            public void DataIsLoad(List<Unit> units, List<String> keys) {
                unitList = units;
            }
        });

        for (int i = 0; i < unitList.size(); i++) {
            searchList.add(unitList.get(i).getUnitName());
        }

        // set spinner
        setFilterSpinner();

        searchListAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, searchList);
        atvSearch.setThreshold(1); // will start working from first character
        atvSearch.setAdapter(searchListAdapter);
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
                for (int i = 0; i < unitList.size(); i++) {
                    searchList.add(unitList.get(i).getUnitName());
                }
                break;
            default:
                break;
        }
    }

    // spinner selection event handler
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        filterVal = "Chat";
    }

    // initialize default factory values for the Unit model (one time use)
    public void Init() {
        for (int i = 0; i < 5; i++) {
            int propId = i;
            String tenantId = Integer.toString(i);
            String unitName = "Unit " + i;

            final Unit newUnit = new Unit(propId, tenantId, unitName);

            Log.v(TAG, (i + 1) + ": Unit = " + newUnit);

            myDatabaseRef.child("units").child(unitName).setValue(newUnit);
        }
    }
}
