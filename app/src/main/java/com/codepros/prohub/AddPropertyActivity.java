package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codepros.prohub.model.Property;
import com.codepros.prohub.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddPropertyActivity extends AppCompatActivity {
    //Toolbar

    private ImageButton toolbarBtnSearch,btnHome,toolbarBtnMenu;
    //Activity Items
    private static final String TAG = "NewPropertyActivity";
    // user interaction objects
    private EditText etName, etStreetLine1, etStreetLine2, etCity, etPostalCode;
    String[] provinces;
    ArrayAdapter<String>  provinceAdapter;
    Spinner spProvince;
    private String userPhoneNum,province,myRole;

    // firebase database objects
    private DatabaseReference myPropertyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);
        //
        SharedPreferences myPreference = getSharedPreferences("myUserSharedPref", MODE_PRIVATE);
        myRole=myPreference.getString("myRole","");
        userPhoneNum = myPreference.getString("phoneNum", "");
        myPropertyRef = FirebaseDatabase.getInstance().getReference("properties");

        // reference
        etName = findViewById(R.id.etName);
        etStreetLine1 = findViewById(R.id.etStreetLine1);
        etStreetLine2 = findViewById(R.id.etStreetLine2);
        etCity = findViewById(R.id.etCity);
        etPostalCode= findViewById(R.id.etPostalCode);

        provinces=getResources().getStringArray(R.array.provinces);
        spProvince = findViewById(R.id.spProvince);
        SetProvinceAdapter();
        Button btnSaveProperty = findViewById(R.id.btnSaveProperty);

        myPropertyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                HashMap<String, Property> map = (HashMap<String, Property>) dataSnapshot.getValue();
                Log.d(TAG, "Value is" + map);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        btnSaveProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProperty(v);
            }
        });

    }

    // Setting list View adapter
    public void SetProvinceAdapter() {
        provinceAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, provinces){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };


        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProvince.setAdapter(provinceAdapter);
        spProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    // Notify the selected item text
                    province=spProvince.getSelectedItem().toString();
                    Toast.makeText
                            (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    // function handles registration
    private void addProperty(View view) {
        String name = etName.getText().toString();
        String streetLine1 = etStreetLine1.getText().toString();
        String streetLine2 = etStreetLine2.getText().toString();
        String city = etCity.getText().toString();
        String postalCode = etPostalCode.getText().toString();
        String regex = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(postalCode);


        // validate the input field in the new Property form
        if(name.isEmpty()){
            // show error message
            String message = "Sorry, name cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        else if(streetLine1.isEmpty()){
            // show error message
            String message = "Sorry, street address cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        /*
        else if(streetLine2.isEmpty()){
            // show error message
            String message = "Sorry, street line 2 cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }*/
        else if(city.isEmpty()){
            // show error message
            String message = "Sorry, city cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        else if(postalCode.isEmpty()) {
            // show error message
            String message = "Sorry, postal code cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        else if(!(matcher.matches())) {
            // show error message
            String message = "Sorry, Please add correct postal code!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        // validate phone number in correct format
        else if(province.isEmpty() || province.equals("Choose one!")){
            // show error message
            String message = "Please choose one!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        else{
            String propId = myPropertyRef.push().getKey();
            Property newProperty = new Property(propId,name, streetLine1, streetLine2, city,province,postalCode);
            newProperty.setPhone(userPhoneNum);
            // need to save to firebase
           // DatabaseReference postsRef = myPropertyRef.child("properties");
           // DatabaseReference newPostRef = postsRef.push();
            myPropertyRef.child(propId).setValue(newProperty);
            Toast.makeText(getApplicationContext(), "property saved!", Toast.LENGTH_LONG).show();
            // intent to next page
           Intent intent = new Intent(this, LessorHomeActivity.class);
            this.startActivity(intent);
        }
    }

}
