package com.codepros.prohub.utils;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.codepros.prohub.PropertyHomeActivity;
import com.codepros.prohub.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Validate2FaActivity extends AppCompatActivity {
    private String securityCode;
    // firebase database objects
    private DatabaseReference myUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate2_fa);

        securityCode = getIntent().getStringExtra("securityCode");

        findViewById(R.id.validateBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNext();
            }
        });
    }

    // go to next page which is the property home page
    private void goNext(){
        if(((EditText) findViewById(R.id.codeEdtx)).getText().toString().equals(securityCode)) {
            // need to set the user's is2FA property to true
            SharedPreferences myPref = getSharedPreferences("myUserSharedPref", MODE_PRIVATE);
            String userPhone = myPref.getString("phoneNum", "");
            myUserRef = FirebaseDatabase.getInstance().getReference();
            myUserRef.child("users").child(userPhone).child("is2FA").setValue(true);

            // go to next page
            this.startActivity(new Intent(this, PropertyHomeActivity.class));
            Toast.makeText(this, "successfully passed the 2FA!", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "incorrect code!", Toast.LENGTH_LONG).show();
        }
    }
}
