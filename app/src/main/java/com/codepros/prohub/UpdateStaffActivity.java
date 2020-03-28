package com.codepros.prohub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codepros.prohub.model.Staff;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateStaffActivity extends AppCompatActivity {
    //Toolbar
    private Button toolbarBtnSettings, toolbarBtnChat,toolbarBtnNews,toolbarBtnForms ;
    private ImageButton toolbarBtnSearch,btnHome,toolbarBtnMenu;
    //
    String myRole;
    String propId,propName;

    private TextView tvUpdateStaff;
    private EditText etUpdateName;
    private EditText etUpdateEmail;
    private EditText etUpdatePhone;
    private EditText etUpdateAddress;
    private EditText etUpdatePostalCode;
    private EditText etUpdateCity;
    ArrayAdapter<String> provinceAdapter;
    String[] provinces;

    private Spinner spUpdateProvince;
    private EditText etUpdateRole;
    ImageView updateStaffbtn;
    Button updateStaffCancelBtn, updateStaffPostBtn;

    String name, email, phone, address, postalCode, city, province, role, imageUrl;
    private Uri imguri;

    private static final String TAG = "UpdateStaffActivity";
    private static final int REQUEST_IMAGE = 2;

    // firebase database objects
    private DatabaseReference myStaffRef;
    private StorageReference myStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_staff);

        SharedPreferences sharedPreferences = getSharedPreferences("myUserSharedPref", Context.MODE_PRIVATE);
        myRole= sharedPreferences.getString("myRole", "");

        propName= sharedPreferences.getString("propName", "");
        propId= sharedPreferences.getString("propId", "");

        //Shared Preferences
        myStaffRef = FirebaseDatabase.getInstance().getReference("staff");
        myStorageRef = FirebaseStorage.getInstance().getReference("Images");

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

        //click CHAT button on toolbar
        toolbarBtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goChat(v);
            }
        });

        // click NEWS button on toolbar
        toolbarBtnNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNews(v);
            }
        });

        //click FORMS button on toolbar
        toolbarBtnForms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goForms(v);
            }
        });

        // click SEARCH icon on toolbar
        toolbarBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSearch(v);
            }
        });

        // click Settings icon on toolbar
        toolbarBtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSettings(v);
            }
        });
        //click to go to Property page
        // click to go to Property page
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),PropertyHomeActivity.class);
                startActivity(intent);
            }
        });
        // Menu drop down
        final PopupMenu dropDownMenu = new PopupMenu(this, toolbarBtnMenu);
        final Menu menu = dropDownMenu.getMenu();
        // list of items for menu:
        menu.add(0, 0, 0, "Manage Unit");
        menu.add(1, 1, 1, "Manage Staff");
        menu.add(2, 2, 2, "Logout");

        // logout item
        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 0:
                        if(myRole.equals("Tenant")){
                            Toast.makeText(getBaseContext(),"Sorry! You do not have permission to manage staff.",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Intent intent = new Intent(getBaseContext(),ViewUnitsActivity.class);
                            startActivity(intent);
                            return true;
                        }
                    case 1:
                        if(myRole.equals("Tenant")){
                            Toast.makeText(getBaseContext(),"Sorry! You do not have permission to manage staff.",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Intent intent = new Intent(getBaseContext(),ViewStaffActivity.class);
                            startActivity(intent);
                            return true;
                        }

                    case 2:
                        // item ID 0 was clicked
                        Intent i = new Intent(getBaseContext(), MainActivity.class);
                        i.putExtra("finish", true);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clean all activities
                        startActivity(i);
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        return true;
                }
                return false;
            }
        });

        // Menu button click
        toolbarBtnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropDownMenu.show();
            }
        });
        //////////////////////////////////////////////

        etUpdateName=findViewById(R.id.etUpdateName);
        etUpdateEmail=findViewById(R.id.etUpdateEmail);
        etUpdatePhone=findViewById(R.id.etUpdatePhone);
        etUpdateAddress=findViewById(R.id.etUpdateAddress);
        etUpdateCity=findViewById(R.id.etUpdateCity);
        spUpdateProvince=findViewById(R.id.spUpdateProvince);
        etUpdatePostalCode=findViewById(R.id.etUpdatePostalCode);
        etUpdateRole=findViewById(R.id.etUpdateRole);
        updateStaffbtn =findViewById(R.id.updateStaffbtn);
        updateStaffCancelBtn = findViewById(R.id.updateStaffCancelBtn);
        updateStaffPostBtn = findViewById(R.id.updateStaffCancelBtn);


        updateStaffbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileChooser();
            }
        });
        updateStaffCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        //Province Spinner
        updateStaffPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStaff(v);
            }
        });


        // get bundled extras
        Intent displayIntent = getIntent();
        Bundle bundle=displayIntent.getExtras();

        imageUrl = bundle.getString("imgUrl");
        name = bundle.getString("name");
        email = bundle.getString("email");
        phone = bundle.getString("phone");
        address = bundle.getString("address");
        city = bundle.getString("city");
        province = bundle.getString("province");
        postalCode = bundle.getString("postal");
        role = bundle.getString("role");

        etUpdateName.setText(name);
        etUpdateEmail.setText(email);
        etUpdatePhone.setText(phone);
        etUpdateAddress.setText(address+", ");
        etUpdateCity.setText(city);
        //
        //Province Spinner
        provinces= provinces=getResources().getStringArray(R.array.provinces);
        SetProvinceAdapter();

//        spUpdateProvince.setSelection(getIndex(spUpdateProvince, province));

        etUpdatePostalCode.setText(postalCode);
        etUpdateRole.setText(role);
        Picasso.get().load(imageUrl).placeholder(R.drawable.noimg).into(this.updateStaffbtn);


    }

    public int getIndex(Spinner spinner, String str){
        int index = 0;

        for (int i = 0; i<spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).equals(str)){
                index = i;
            }
        }
        return index;

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
        spUpdateProvince.setAdapter(provinceAdapter);

        // set province from retrieved record
        spUpdateProvince.setSelection(getIndex(spUpdateProvince, province));

        spUpdateProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                        // Notify the selected item text
                        province = spUpdateProvince.getSelectedItem().toString();
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

    private String getExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void FileUploader() {
        final StorageReference ref = myStorageRef.child(System.currentTimeMillis()+"."+getExtension(imguri));
        try{
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imguri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if(bitmap.getByteCount() > 100000){
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            }
            else{
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            }

            byte[] data = baos.toByteArray();

            UploadTask uploadTask = ref.putBytes(data);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "something went wrong when uploading image", Toast.LENGTH_LONG).show();
                        throw task.getException();
                    }
                    Toast.makeText(getApplicationContext(), "Image uploaded successfully", Toast.LENGTH_LONG).show();
                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        imageUrl = task.getResult().toString();
                    }
                }
            });
        }catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
    }

    private void fileChooser(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null){
            imguri = data.getData();
            updateStaffbtn.setImageURI(imguri);
            // upload the image to firebase storage
            FileUploader();
        }
    }



    private void goBack(){
        Intent intent = new Intent(this, ViewStaffActivity.class);
        this.startActivity(intent);
    }

    private void updateStaff(View v){
        Toast.makeText(getApplicationContext(), "clicked!", Toast.LENGTH_LONG).show();

        //        String name = etName.getText().toString();
//        String email = etEmail.getText().toString();
//        String phone = etPhone.getText().toString();
//        String address = etAddress.getText().toString();
//        String postal = etPostal.getText().toString();
//        String city = etCity.getText().toString();
//        String role = etRole.getText().toString();
//        String regex = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(postal);
//
//        if(name.isEmpty()|| name == null){
//            String message = "Sorry, name cannot be empty!";
//            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//        }
//        else if(email.isEmpty()|| email == null){
//            String message = "Sorry, email cannot be empty!";
//            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//        }
//        else if(phone.isEmpty() || phone == null){
//            String message = "Sorry, phone cannot be empty!";
//            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//        }
//        else if(address.isEmpty() || address == null){
//            String message = "Sorry, address cannot be empty!";
//            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//        }
//        else if(postal.isEmpty() || postal == null){
//            String message = "Sorry, Postal code cannot be empty!";
//            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//        }
//        else if(!matcher.matches()){
//            String message = "Sorry, Postal code is incorrect pattern. A0A 0A0!";
//            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//        }
//        else if(city.isEmpty() || city == null){
//            String message = "Sorry, city cannot be empty!";
//            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//        }
//        else if(role.isEmpty() || imageUrl == null){
//            String message = "Sorry, role cannot be empty!";
//            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//        }
//        else if(imageUrl.isEmpty() || imageUrl == null){
//            String message = "Please choose an image!";
//            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//        }
//        else{
//
//            String staffId = myStaffRef.push().getKey();
//            Staff staff = new Staff(staffId,propId,name,phone,address,postal,city,province,email,role,imageUrl);
//            // need to save to firebase
//            myStaffRef.child(staffId).setValue(staff);
//            Toast.makeText(getApplicationContext(), staff.getName()+" is saved!", Toast.LENGTH_LONG).show();
//            // redirect to Staff list View
//            goBack();
//        }
    }



    public void goNews(View view) {
        Intent intent = new Intent(this, NewsViewActivity.class);
        this.startActivity(intent);
    }

    public void goChat(View view) {
        Intent intent = new Intent(this, ChatList.class);
        this.startActivity(intent);
    }

    public void goForms(View view) {
        Intent intent = new Intent(this, FormsActivity.class);
        this.startActivity(intent);
    }

    public void goSearch(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        this.startActivity(intent);
    }
    public void goSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        this.startActivity(intent);
    }
}
