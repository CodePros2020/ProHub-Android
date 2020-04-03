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
import com.codepros.prohub.utils.ToolbarHelper;
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
    private Button toolbarBtnSettings, toolbarBtnChat, toolbarBtnNews, toolbarBtnForms;
    private ImageButton toolbarBtnSearch, btnHome, toolbarBtnMenu;
    private ToolbarHelper toolbar;
    //
    String myRole;
    String propId, propName;

    // views for update staff
    private EditText etUpdateName;
    private EditText etUpdateEmail;
    private EditText etUpdatePhone;
    private EditText etUpdateAddress;
    private EditText etUpdatePostalCode;
    private EditText etUpdateCity;
    private EditText etUpdateRole;
    private Spinner spUpdateProvince;
    ImageView updateStaffImagebtn;
    Button updateStaffCancelBtn;
    Button updateStaffPostBtn;

    // province dropdown list
    ArrayAdapter<String> provinceAdapter;
    String[] provinces;

    // variables for Staff
    String staffId;
    String name, email, phone, address, postalCode, city, province, role, imageUrl;

    // image file upload
    private Uri imguri;
    private static final int REQUEST_IMAGE = 2;

    // firebase database objects
    private DatabaseReference myStaffRef;
    private StorageReference myStorageRef;

    // constant
    private static final String TAG = "UpdateStaffActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_staff);

        SharedPreferences sharedPreferences = getSharedPreferences("myUserSharedPref", Context.MODE_PRIVATE);
        myRole = sharedPreferences.getString("myRole", "");

        propName = sharedPreferences.getString("propName", "");
        propId = sharedPreferences.getString("propId", "");

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

        toolbar = new ToolbarHelper(this, toolbarBtnChat, toolbarBtnNews, toolbarBtnForms,
                toolbarBtnSettings, btnHome, toolbarBtnSearch, toolbarBtnMenu);

        //////////////////////////////////////////////

        // find Views
        etUpdateName = findViewById(R.id.etUpdateName);
        etUpdateEmail = findViewById(R.id.etUpdateEmail);
        etUpdatePhone = findViewById(R.id.etUpdatePhone);
        etUpdateAddress = findViewById(R.id.etUpdateAddress);
        etUpdateCity = findViewById(R.id.etUpdateCity);
        spUpdateProvince = findViewById(R.id.spUpdateProvince);
        etUpdatePostalCode = findViewById(R.id.etUpdatePostalCode);
        etUpdateRole = findViewById(R.id.etUpdateRole);
        // buttons
        updateStaffImagebtn = findViewById(R.id.updateStaffImagebtn);
        updateStaffCancelBtn = findViewById(R.id.updateStaffCancelBtn);
        updateStaffPostBtn = findViewById(R.id.updateStaffPostBtn);

        // button events
        updateStaffImagebtn.setOnClickListener(new View.OnClickListener() {
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
        updateStaffPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStaff(v);
            }
        });

        // get bundled extras
        Intent displayIntent = getIntent();
        Bundle bundle = displayIntent.getExtras();

        staffId = bundle.getString("staffId");
        imageUrl = bundle.getString("imgUrl");
        name = bundle.getString("name");
        email = bundle.getString("email");
        phone = bundle.getString("phone");
        address = bundle.getString("address");
        city = bundle.getString("city");
        province = bundle.getString("province");
        postalCode = bundle.getString("postal");
        role = bundle.getString("role");

        // set text to views
        etUpdateName.setText(name);
        etUpdateEmail.setText(email);
        etUpdatePhone.setText(phone);
        etUpdateAddress.setText(address + ", ");
        etUpdateCity.setText(city);
        etUpdatePostalCode.setText(postalCode);
        etUpdateRole.setText(role);
        Picasso.get().load(imageUrl).placeholder(R.drawable.noimg).into(this.updateStaffImagebtn);

        // set province Spinner
        provinces = provinces = getResources().getStringArray(R.array.provinces);
        SetProvinceAdapter();

    }

    // get index of spinner
    public int getIndex(Spinner spinner, String str) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(str)) {
                index = i;
            }
        }
        return index;
    }

    // Setting list View adapter
    public void SetProvinceAdapter() {
        provinceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, provinces) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }

                return view;
            }
        };

        // set adapter to spinner
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUpdateProvince.setAdapter(provinceAdapter);

        // set province from retrieved record
        spUpdateProvince.setSelection(getIndex(spUpdateProvince, province));

        // set onItemSelected event
        spUpdateProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if (position > 0) {
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

    // get file extention from uri
    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void FileUploader() {
        final StorageReference ref = myStorageRef.child(System.currentTimeMillis() + "." + getExtension(imguri));

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imguri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (bitmap.getByteCount() > 100000) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            } else {
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
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private void fileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // set file type to open
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        // open file explorer
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null) {
            // get uri of local image
            imguri = data.getData();

            // update uri
            updateStaffImagebtn.setImageURI(imguri);

            // upload the image to firebase storage
            FileUploader();
        }
    }

    // update staff
    private void updateStaff(View v) {

        // get input from the form
        String name = etUpdateName.getText().toString();
        String email = etUpdateEmail.getText().toString();
        String phone = etUpdatePhone.getText().toString();
        String address = etUpdateAddress.getText().toString();
        String postal = etUpdatePostalCode.getText().toString();
        String city = etUpdateCity.getText().toString();
        String role = etUpdateRole.getText().toString();
        String regex = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(postal);

        // phone number validation
        // modified from https://www.regexpal.com/17
//        String phoneNumRegex = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$";
        String phoneNumRegex = "^(?:(?:\\+?\\d\\s*(?:[.-]\\s*)?)?(?:\\(\\s*(\\d{3})\\s*\\)|(\\d{3}))\\s*(?:[.-]\\s*)?)?(\\d{3})\\s*(?:[.-]\\s*)?(\\d{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$";
        Pattern phoneNumPattern = Pattern.compile(phoneNumRegex);
        Matcher phoneMatcher = phoneNumPattern.matcher(phone);

        // validations
        if (name.isEmpty() || name == null) {
            String message = "Sorry, name cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } else if (email.isEmpty() || email == null) {
            String message = "Sorry, email cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } else if (phone.isEmpty() || phone == null) {
            String message = "Sorry, phone cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } else if(!phoneMatcher.matches()){
            String message = "Sorry, Phone number is incorrect pattern!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } else if (address.isEmpty() || address == null) {
            String message = "Sorry, address cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } else if (postal.isEmpty() || postal == null) {
            String message = "Sorry, Postal code cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } else if (!matcher.matches()) {
            String message = "Sorry, Postal code is incorrect pattern. A0A 0A0!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } else if (city.isEmpty() || city == null) {
            String message = "Sorry, city cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } else if (role.isEmpty() || role == null) {
            String message = "Sorry, role cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } else if (imageUrl.isEmpty() || imageUrl == null) {
            String message = "Please choose an image!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } else {
            // staff with updated information
            Staff staffToUpdate = new Staff(staffId, propId, name, phone, address, postal, city, province, email, role, imageUrl);

            // update database
            myStaffRef.child(staffId).setValue(staffToUpdate);

            // notification
            Toast.makeText(getApplicationContext(), staffToUpdate.getName() + " is updated!", Toast.LENGTH_LONG).show();

            // redirect to Staff list View
            goBack();
        }

    }

    private void goBack() {
        Intent intent = new Intent(this, ViewStaffActivity.class);
        this.startActivity(intent);
    }
}
