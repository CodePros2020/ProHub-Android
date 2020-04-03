package com.codepros.prohub.utils;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.codepros.prohub.LessorHomeActivity;
import com.codepros.prohub.PropertyHomeActivity;
import com.codepros.prohub.R;
import com.codepros.prohub.model.Unit;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class Validate2FaActivity extends AppCompatActivity {
    private String securityCode;
    // firebase database objects
    private DatabaseReference myUserRef;
    private List<Unit> allUnitList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate2_fa);

        securityCode = getIntent().getStringExtra("securityCode");
        allUnitList = new ArrayList<>();

        findViewById(R.id.validateBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNext();
            }
        });

        new FirebaseDataseHelper().readUnits(new FirebaseDataseHelper.UnitDataStatus() {
            @Override
            public void DataIsLoad(List<Unit> unit, List<String> keys) {
                allUnitList = unit;
            }
        });
    }

    // go to next page which is the property home page
    private void goNext(){
        if(((EditText) findViewById(R.id.codeEdtx)).getText().toString().equals(securityCode)) {
            // need to set the user's is2FA property to true
            SharedPreferences myPref = getSharedPreferences("myUserSharedPref", MODE_PRIVATE);
            String userPhone = myPref.getString("phoneNum", "");
            String userRole = myPref.getString("userRole", "");
            myUserRef = FirebaseDatabase.getInstance().getReference();
            myUserRef.child("users").child(userPhone).child("is2FA").setValue(true);

            Toast.makeText(this, "successfully passed the 2FA!", Toast.LENGTH_LONG).show();

            // go to next page
            if(userRole.equals("Tenant")){
                String propId = "";
                for (int i = 0; i < allUnitList.size(); i++) {
                    if (allUnitList.get(i).getTenantId().equals(userPhone)) {
                        propId = allUnitList.get(i).getPropId();
                    }
                }

                Intent intent = new Intent(this, PropertyHomeActivity.class);

                SharedPreferences myPreference = getSharedPreferences("myUserSharedPref", 0);
                SharedPreferences.Editor prefEditor = myPreference.edit();
                prefEditor.putString("phoneNum", userPhone);
                prefEditor.putString("myRole", userRole);
                prefEditor.putString("propId", propId);
                prefEditor.apply();

                this.startActivity(intent);
                return;
            }
            else{
                Intent intent = new Intent(this, LessorHomeActivity.class);

                SharedPreferences myPreference = getSharedPreferences("myUserSharedPref", 0);
                SharedPreferences.Editor prefEditor = myPreference.edit();
                prefEditor.putString("phoneNum", userPhone);
                prefEditor.putString("myRole", userRole);
                prefEditor.apply();

                this.startActivity(intent);
                return;
            }
        }
        else {
            Toast.makeText(this, "incorrect code!", Toast.LENGTH_LONG).show();
        }
    }
}
