package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ArrayAdapter<String> filterAdaptor;
    private Spinner spFilter;
    private String[] filter;
    private String filterVal;

    // firebase database objects
    private DatabaseReference myUnitRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // set variables
        spFilter = findViewById(R.id.search_filter_spinner);
        filter = getResources().getStringArray(R.array.search_filter);
        myUnitRef = FirebaseDatabase.getInstance().getReference();

        // set spinner
        setFilterSpinner();
    }

    private void setFilterSpinner() {
        filterAdaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, filter);
        filterAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilter.setAdapter(filterAdaptor);
        spFilter.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        filterVal = parent.getItemAtPosition(pos).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        filterVal = "Chat";
    }
}
