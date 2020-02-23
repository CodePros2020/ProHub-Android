package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
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

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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
                goLogin(v);
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
    public void goLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        this.startActivity(intent);
    }

    // function handles registration
    private void register(View view) {
        String firstName = firstname.getText().toString();
        String lastName = lastname.getText().toString();
        String phoneNumber = phone.getText().toString();
        String passwordString = password.getText().toString();
        String confirmedPassword = confirmedPass.getText().toString();

        // validate password with confirmed password
        // and need input for first name and last name
        if(!passwordString.equals(confirmedPassword) || firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty()){
            // show error message
            String message = "Sorry, missing information, please try again!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        // validate phone number in correct format
        else if(!PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber) || phoneNumber.length() < 9 || phoneNumber.length() > 13){
            // show error message
            String message = "Sorry, incorrect phone number format, please try again!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        else{
            User newUser = new User(firstName, lastName, phoneNumber, passwordString);
            // need to save to firebase
            myUserRef.child("users").child(phoneNumber).setValue(newUser);
            //boolean test = User.validatePassword("123", newUser.getPassword());
            //Toast.makeText(getApplicationContext(), "authentication: "+test, Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "saved!", Toast.LENGTH_LONG).show();
            // intent to next page
            Intent intent = new Intent(this, RegistrationRoleActivity.class);
            intent.putExtra("phoneNumber", phoneNumber);
            this.startActivity(intent);
        }
    }
}
