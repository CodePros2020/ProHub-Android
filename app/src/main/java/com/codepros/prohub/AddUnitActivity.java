package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepros.prohub.model.Unit;
import com.codepros.prohub.model.User;
import com.codepros.prohub.utils.FirebaseDataseHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddUnitActivity extends AppCompatActivity {

    private static final String TAG = "AddUnitActivity";
    // user interaction objects
    private EditText etUnitName, etTenantNumber;
    private String propId, unitName, tenantNumber;
    private Button btnSaveUnit;
    private List<User> userList;

    // firebase database objects
    private DatabaseReference myDataRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_unit);

        myDataRef = FirebaseDatabase.getInstance().getReference();

        SharedPreferences myPref = getSharedPreferences("myUserSharedPref", MODE_PRIVATE);
        propId = myPref.getString("propId", "");

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
                Toast.makeText(this, "u.getPhone(): " + u.getPhone(), Toast.LENGTH_LONG).show();
                tenantExist = true;
                if (u.getRole().equals("Tenant")) {
                    isTenant = true;
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
            Unit newUnit = new Unit(propId, tenantNumber, unitName);

            DatabaseReference postsRef = myDataRef.child("units");
            DatabaseReference newPostRef = postsRef.push();
            newPostRef.setValue(newUnit);
            Toast.makeText(getApplicationContext(), "New Unit Saved!", Toast.LENGTH_LONG).show();

            // intent to next page
            Intent intent = new Intent(this, PropertyHomeActivity.class);
            intent.putExtra("propId", propId);
            this.startActivity(intent);
        }
    }
}
