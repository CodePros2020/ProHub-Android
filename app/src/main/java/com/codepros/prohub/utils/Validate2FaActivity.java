package com.codepros.prohub.utils;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.codepros.prohub.PropertyHomeActivity;
import com.codepros.prohub.R;


public class Validate2FaActivity extends AppCompatActivity {
    private String securityCode;

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
            this.startActivity(new Intent(this, PropertyHomeActivity.class));
            Toast.makeText(this, "successfully pased the 2FA!", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "incorrect code!", Toast.LENGTH_LONG).show();
        }
    }
}
