package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.codepros.prohub.utils.ToolbarHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class DisplayStaffActivity extends AppCompatActivity {

    //Toolbar
    private Button toolbarBtnSettings, toolbarBtnChat, toolbarBtnNews, toolbarBtnForms;
    private ImageButton toolbarBtnSearch, btnHome, toolbarBtnMenu;
    private ToolbarHelper toolbar;
    //
    private static final String TAG = "Image Url";
    String name, email, phone, address, city, province, postal, imgUrl, role, myRole;
    TextView tvName, tvEmail, tvPhone, tvAddress, tvCity, tvProvince, tvPostal, tvRole;
    ImageView ivStaff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_staff);
        SharedPreferences myPreference = getSharedPreferences("myUserSharedPref", MODE_PRIVATE);
        myRole = myPreference.getString("myRole", "");
        //
        //////////////////////////////////////////////
        // declaring the buttons

        // define the actions for each button
        // Button for top toolbar
        toolbarBtnChat = findViewById(R.id.toolbarBtnChat);
        toolbarBtnNews = findViewById(R.id.toolbarBtnNews);
        toolbarBtnForms = findViewById(R.id.toolbarBtnForms);
        toolbarBtnSettings = findViewById(R.id.toolbarBtnSettings);
        btnHome = findViewById(R.id.ImageButtonHome);
        toolbarBtnSearch = findViewById(R.id.ImageButtonSearch);
        toolbarBtnMenu = findViewById(R.id.ImageButtonMenu);

        toolbar = new ToolbarHelper(this, toolbarBtnChat, toolbarBtnNews, toolbarBtnForms,
                toolbarBtnSettings, btnHome, toolbarBtnSearch, toolbarBtnMenu);

        ////////////////////////////////////////////////

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvCity = findViewById(R.id.tvCity);
        tvProvince = findViewById(R.id.tvProvince);
        tvPostal = findViewById(R.id.tvPostal);
        tvRole = findViewById(R.id.tvRole);
        ivStaff = findViewById(R.id.ivStaff);
        //
        Intent displayIntent = getIntent();
        Bundle bundle = displayIntent.getExtras();

        imgUrl = bundle.getString("imgUrl");
        name = bundle.getString("name");
        email = bundle.getString("email");
        phone = bundle.getString("phone");
        address = bundle.getString("address");
        city = bundle.getString("city");
        province = bundle.getString("province");
        postal = bundle.getString("postal");
        role = bundle.getString("role");
        //
        tvName.setText(name);
        tvEmail.setText(email);
        tvPhone.setText(phone);
        tvAddress.setText(address + ", ");
        tvCity.setText(city);
        tvProvince.setText(province + ", ");
        tvPostal.setText(postal);
        tvRole.setText(role);
        Picasso.get().load(imgUrl).placeholder(R.drawable.noimg).into(this.ivStaff);

    }

}
