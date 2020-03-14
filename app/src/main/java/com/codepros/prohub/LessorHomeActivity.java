package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.codepros.prohub.utils.FirebaseDataseHelper;
import com.codepros.prohub.model.Property;
import com.codepros.prohub.utils.PropertyAdapter;

import java.util.ArrayList;
import java.util.List;

public class LessorHomeActivity extends AppCompatActivity {
    private TextView textWeclomeLessor;
    private Button btnCreateProp;
    private ListView list_property;
    private String userPhoneNum;

    // list of properties
    List<Property> allProperties = new ArrayList<>();
    List<Property> myProperties = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessor_home);

        // get the reference
        textWeclomeLessor = findViewById(R.id.textWeclomeLessor);
        btnCreateProp = findViewById(R.id.btnCreateProp);
        list_property = findViewById(R.id.list_property);

        // set the weclome message
        SharedPreferences myPref = getSharedPreferences("myUserSharedPref", MODE_PRIVATE);
        String username = myPref.getString("username", "");
        userPhoneNum = myPref.getString("phoneNum", "");
        textWeclomeLessor.setText("Weclome "+username);

        // read the list of property from firebase
        new FirebaseDataseHelper().readProperty(new FirebaseDataseHelper.PropDataStatus() {
            @Override
            public void DataIsLoad(List<Property> properties, List<String> keys) {
                allProperties = properties;
                for(int i = 0; i<allProperties.size(); i++){
                    if(allProperties.get(i).getPhone().equals(userPhoneNum)){
                        myProperties.add(allProperties.get(i));
                    }
                }
                setAdpater();
            }
        });

        // set the onClick event
        btnCreateProp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewProperty();
            }
        });
    }

    private void addNewProperty(){
        // TODO: going to the create property page
        Intent intent = new Intent(this, AddPropertyActivity.class);
        startActivity(intent);
    }

    private void setAdpater(){
        // set the property adapter to the list view
        PropertyAdapter propertyAdapter = new PropertyAdapter(this, myProperties);
        list_property.setAdapter(propertyAdapter);
    }
}
