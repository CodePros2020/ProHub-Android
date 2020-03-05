package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.codepros.prohub.model.Property;
import com.codepros.prohub.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddPropertyActivity extends AppCompatActivity {
    private static final String TAG = "NewPropertyActivity";
    // user interaction objects
    private EditText etName, etStreetLine1, etStreetLine2, etCity, etPostalCode;
    String[] provinces;
    ArrayAdapter<String>  provinceAdapter;
    Spinner spProvince;

    // firebase database objects
    private DatabaseReference myPropertyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);

    myPropertyRef = FirebaseDatabase.getInstance().getReference();

        // reference
        etName = findViewById(R.id.etName);
        etStreetLine1 = findViewById(R.id.etStreetLine1);
        etStreetLine2 = findViewById(R.id.etStreetLine2);
        etCity = findViewById(R.id.etCity);
        etPostalCode= findViewById(R.id.etPostalCode);

        provinces=getResources().getStringArray(R.array.provinces);
        spProvince = findViewById(R.id.spProvince);
        SetCityAdapter();
        Button btnSaveProperty = findViewById(R.id.btnSaveProperty);


        btnSaveProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProperty(v);
            }
        });

    }

    // Setting list View adapter
    public void SetCityAdapter() {
        provinceAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, provinces);
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProvince.setAdapter(provinceAdapter);

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
        String province=spProvince.getSelectedItem().toString();

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
        else if(streetLine2.isEmpty()){
            // show error message
            String message = "Sorry, street line 2 cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
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
            Property newProperty = new Property(name, streetLine1, streetLine2, city,province,postalCode);
            // need to save to firebase
            myPropertyRef.child("properties").setValue(newProperty);
            Toast.makeText(getApplicationContext(), "property saved!", Toast.LENGTH_LONG).show();
            // intent to next page
           Intent intent = new Intent(this, LessorHomeActivity.class);
            //intent.putExtra("phoneNumber", phoneNumber);
            this.startActivity(intent);
        }
    }
}
