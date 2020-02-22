package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepros.prohub.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "RegistrationActivity";
    // user interaction objects
    private EditText firstname, lastname, phone, password, confirmedPass;
    private Button btnSignUp;
    private Button btnLinkLogin;

    // firebase database objects
    private DatabaseReference myUserRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        myUserRef = FirebaseDatabase.getInstance().getReference();

        // reference
        firstname = findViewById(R.id.txtFirstName);
        lastname = findViewById(R.id.txtLastName);
        phone = findViewById(R.id.txtPhone);
        password = findViewById(R.id.txtPassword);
        confirmedPass = findViewById(R.id.txtConfirmPassword);

        btnSignUp = findViewById(R.id.btnConfirm);
        btnLinkLogin = findViewById(R.id.btnBack);

        btnLinkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goMain(v);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(v);
            }
        });

        // Read from the database
        myUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

//                String value = dataSnapshot.getValue(String.class);
//                Log.d(TAG,"Value is: " + value);
                HashMap<String, User> map = (HashMap<String, User>) dataSnapshot.getValue();
                Log.d(TAG, "Value is" + map);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });




    }

    // this may change to go to login page
    public void goMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }

    // function handles registration
    public void register(View view){
        String firstName = firstname.getText().toString();
        String lastName = lastname.getText().toString();
        String phoneNumber = phone.getText().toString();
        String passwordString = password.getText().toString();
        String confirmedPassword = confirmedPass.getText().toString();

        if(!passwordString.equals(confirmedPassword) || firstName.isEmpty() || lastName.isEmpty() || passwordString.isEmpty()){
            // show error message
            String message = "Sorry, something went wrong, please try again!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        else{
            User newUser = new User(firstName, lastName, phoneNumber, passwordString);
            // need to save to firebase
            myUserRef.child("users").child(phoneNumber).setValue(newUser);
            Toast.makeText(getApplicationContext(), "saved!", Toast.LENGTH_LONG).show();
            // intent to next page
        }
    }
}
