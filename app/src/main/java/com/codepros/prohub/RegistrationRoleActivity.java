package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codepros.prohub.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegistrationRoleActivity extends AppCompatActivity {

    private static final String TAG = "RegistrationRoleAct";

    private Button btnLandlord;
    private Button btnTenant;
    private String phoneNumber;

    // firebase database objects
    private DatabaseReference myUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_role);

        myUserRef = FirebaseDatabase.getInstance().getReference();

        phoneNumber = getIntent().getStringExtra("phoneNumber");

        btnLandlord = findViewById(R.id.btnLandlord);
        btnTenant = findViewById(R.id.btnTenant);

        // Read from the database
        myUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                HashMap<String, User> map = (HashMap<String, User>) dataSnapshot.getValue();
                Log.d(TAG, "Value is" + map);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        btnLandlord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String role = "Landlord";
                myUserRef.child("users").child(phoneNumber).child("role").setValue(role);
                goMain(v);
            }
        });

        btnTenant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String role = "Tenant";
                myUserRef.child("users").child(phoneNumber).child("role").setValue(role);
                goMain(v);
            }
        });
    }

    // this may change to go to login page
    public void goMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }
}
