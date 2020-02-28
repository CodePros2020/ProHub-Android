package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.codepros.prohub.model.FirebaseDataseHelper;
import com.codepros.prohub.model.User;
import com.codepros.prohub.utils.Enable2FaActivity;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    // user interaction objects
    private EditText phone, password;
    private ImageButton btnLogin;

    private List<User> myUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phone = findViewById(R.id.txtLoginPhoneNumber);
        password = findViewById(R.id.txtLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });

        new FirebaseDataseHelper().readUsers(new FirebaseDataseHelper.DataStatus() {
            @Override
            public void DataIsLoad(List<User> users, List<String> keys) {
                myUsers = users;
            }
        });
    }

    private void login(View view){
        String phoneNumber = phone.getText().toString();
        String passwordString = password.getText().toString();

        if(myUsers.size()>0){
            for (User user: myUsers) {
                if(user.getPhone().equals(phoneNumber)){
                    if(user.authentication(passwordString)){
                        Toast.makeText(getApplicationContext(), "login successful!", Toast.LENGTH_LONG).show();
                        // intent to next page which make the 2-factor
                        Intent intent = new Intent(this, Enable2FaActivity.class);
                        intent.putExtra("phoneNumber", phoneNumber);
                        this.startActivity(intent);
                        return;
                    }
                    Toast.makeText(getApplicationContext(), "sorry, incorrect password!", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
        Toast.makeText(getApplicationContext(), "sorry, phone number not found!", Toast.LENGTH_LONG).show();

    }
}
