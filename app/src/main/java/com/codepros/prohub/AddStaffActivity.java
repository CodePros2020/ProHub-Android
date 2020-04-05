package com.codepros.prohub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
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
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codepros.prohub.model.Chat;
import com.codepros.prohub.model.News;
import com.codepros.prohub.model.Staff;
import com.codepros.prohub.utils.FirebaseDataseHelper;
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

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddStaffActivity extends AppCompatActivity {
    //Toolbar
    private Button toolbarBtnSettings, toolbarBtnChat, toolbarBtnNews, toolbarBtnForms;
    private ImageButton toolbarBtnSearch, btnHome, toolbarBtnMenu;
    private ToolbarHelper toolbar;
    //Activity Items
    private ImageView addStaffBtn;
    private EditText etName, etEmail, etPhone, etAddress, etPostal, etCity, etRole;
    private Spinner spProvince;
    ArrayAdapter<String> provinceAdapter;
    String[] provinces;
    //    private RadioButton radioBtnAll, radioBtnManage, radioBtnTrue, radioBtnFalse;
    private Button addStaffCancelBtn, addStaffPostBtn;

    private String userPhoneNum, propId, imageUrl, province, myRole;
    private Uri imguri;
    private static final int REQUEST_IMAGE = 2;
    private static final String TAG = "AddStaffActivity";

    // firebase database objects
    private DatabaseReference myStaffRef;
    private StorageReference myStorageRef;
    private boolean clicked=false;
    List<Chat> allMessages = new ArrayList<>();
    List<Staff> staffList=new ArrayList<Staff>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);

        //Shared Preferences
        myStaffRef = FirebaseDatabase.getInstance().getReference("staff");
        myStorageRef = FirebaseStorage.getInstance().getReference("Images");

        SharedPreferences myPref = getSharedPreferences("myUserSharedPref", MODE_PRIVATE);
        userPhoneNum = myPref.getString("phoneNum", "");
        propId = myPref.getString("propId", "");
        myRole = myPref.getString("myRole", "");
        /////////////////////////////////////////////////////
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

        /////////////////////////////////////////////

        /////////////////////////////////////////////////////////////////////////

        // for unread chat messages counter
        new FirebaseDataseHelper().readChats(new FirebaseDataseHelper.ChatDataStatus() {
            @Override
            public void DataIsLoad(List<Chat> chats, List<String> keys) {
                allMessages = chats;
                int count = 0;
                if (allMessages != null) {
                    for (Chat chat : allMessages)
                    {
                        if (!chat.getPhoneNumber().equals(userPhoneNum) && chat.getChatSeen().equals("false")
                                && chat.getChatMessageId().contains(userPhoneNum))
                        {
                            count++;
                        }
                    }
                }

                if (count > 0) {
                    toolbarBtnChat.setText("CHAT (" + count + ")");
                    toolbarBtnChat.setTextColor(Color.parseColor("#FF0000"));
                } else if (count <= 0) {
                    toolbarBtnChat.setText("CHAT");
                    toolbarBtnChat.setTextColor(Color.parseColor("#000000"));
                }
            }
        });

        ////////////////////////////////////////////////////////////////////////

        addStaffBtn = findViewById(R.id.addStaffbtn);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etPostal = findViewById(R.id.etPostalCode);
        etCity = findViewById(R.id.etCity);
        etRole = findViewById(R.id.etRole);
        spProvince = findViewById(R.id.spProvince);
        addStaffCancelBtn = findViewById(R.id.addStaffCancelBtn);
        addStaffPostBtn = findViewById(R.id.addStaffPostBtn);

        addStaffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked=true;
                fileChooser();
            }
        });

        addStaffCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        //Province Spinner
        provinces = provinces = getResources().getStringArray(R.array.provinces);
        SetProvinceAdapter();
        addStaffPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStaff(v);
            }
        });
        new FirebaseDataseHelper().readStaff(new FirebaseDataseHelper.StaffDataStatus() {
            @Override
            public void DataIsLoad(List<Staff> listStaff, List<String> keys) {
               staffList=listStaff;
            }

        });

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


        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProvince.setAdapter(provinceAdapter);
        spProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if (position > 0) {
                    // Notify the selected item text
                    province = spProvince.getSelectedItem().toString();
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
            imageUrl="";
            Log.d(TAG, e.getMessage());
        }
    }

    private void fileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null) {
            imguri = data.getData();
            addStaffBtn.setImageURI(imguri);
            // upload the image to firebase storage
            FileUploader();
        }
    }

    private void goBack() {
        Intent intent = new Intent(this, ViewStaffActivity.class);
        this.startActivity(intent);
    }

    private void addStaff(View v) {
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String phone = etPhone.getText().toString();
        String address = etAddress.getText().toString();
        String postal = etPostal.getText().toString();
        String city = etCity.getText().toString();
        String role = etRole.getText().toString();
        String regex = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(postal);

        // phone number validation
        // modified from https://www.regexpal.com/17
//        String phoneNumRegex = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$";
        String phoneNumRegex = "^(?:(?:\\+?\\d\\s*(?:[.-]\\s*)?)?(?:\\(\\s*(\\d{3})\\s*\\)|(\\d{3}))\\s*(?:[.-]\\s*)?)?(\\d{3})\\s*(?:[.-]\\s*)?(\\d{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$";
        Pattern phoneNumPattern = Pattern.compile(phoneNumRegex);
        Matcher phoneMatcher = phoneNumPattern.matcher(phone);

        if (name.isEmpty() || name == null) {
            String message = "Sorry, name cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } else if (email.isEmpty() || email == null) {
            String message = "Sorry, email cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        else if (phone.length() > 15)  {
            String message = "Please add correct phone number";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }else if (phone.isEmpty() || phone == null )  {
            String message = "Sorry, phone cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }else if(!phoneMatcher.matches()){
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
        }else if (!clicked) {
            String message = "Please choose an image!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
           else {
                boolean exist=false;
            for(Staff staff:staffList){
                if(staff.getPhoneNum().equals(phone) && staff.getPropId().equals(propId)){
                    exist=true;
                    Toast.makeText(getApplicationContext(), staff.getName() + " already exist!", Toast.LENGTH_LONG).show();
                }
                else{
                    exist=false;
                }
            }
            String staffId = myStaffRef.push().getKey();
            Staff newStaff = new Staff(staffId, propId, name, phone, address, postal, city, province, email, role, imageUrl);
            // need to save to firebase

            if(!exist){
                myStaffRef.child(staffId).setValue(newStaff);
                Toast.makeText(getApplicationContext(), newStaff.getName() + " is saved!", Toast.LENGTH_LONG).show();
                // redirect to Staff list View
                goBack();
            }

        }
    }

}

