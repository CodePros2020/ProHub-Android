package com.codepros.prohub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.codepros.prohub.model.News;
import com.codepros.prohub.model.Staff;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddStaffActivity extends AppCompatActivity {

    private ImageView addStaffBtn, addStaffImageView;
    private EditText etName, etEmail,etPhone,etAddress,etPostal,etCity,etRole;
//    private RadioButton radioBtnAll, radioBtnManage, radioBtnTrue, radioBtnFalse;
    private Button addStaffCancelBtn, addStaffPostBtn;

    private String userPhoneNum, propId, imageUrl;
    private Uri imguri;
    private static final int REQUEST_IMAGE = 2;
    private static final String TAG = "AddStaffActivity";

    // firebase database objects
    private DatabaseReference myStaffRef;
    private StorageReference myStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);

        //
        Intent intent=getIntent();
        propId=intent.getStringExtra("propId");
        //
        myStaffRef = FirebaseDatabase.getInstance().getReference("staff");
        myStorageRef = FirebaseStorage.getInstance().getReference("Images");

        SharedPreferences myPref = getSharedPreferences("myUserSharedPref", MODE_PRIVATE);
        userPhoneNum = myPref.getString("phoneNum", "");
        //propId = myPref.getString("propId", "");

        addStaffBtn = findViewById(R.id.addStaffbtn);
        addStaffImageView = findViewById(R.id.addStaffImageView);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etPostal = findViewById(R.id.etPostalCode);
        etCity = findViewById(R.id.etCity);
        etRole = findViewById(R.id.etRole);
        addStaffCancelBtn = findViewById(R.id.addStaffCancelBtn);
        addStaffPostBtn = findViewById(R.id.addStaffPostBtn);

        addStaffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileChooser();
            }
        });

        addStaffCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        addStaffPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStaff(v);
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
            addStaffImageView.setImageURI(imguri);
            // upload the image to firebase storage
            FileUploader();
        }
    }

    private void goBack(){
        Intent intent = new Intent(this, ViewStaffActivity.class);
        this.startActivity(intent);
    }

    private void addStaff(View v){
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

        if(name.isEmpty()){
            String message = "Sorry, name cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        else if(email.isEmpty()){
            String message = "Sorry, email cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        else if(phone.isEmpty()){
            String message = "Sorry, phone cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        else if(address.isEmpty()){
            String message = "Sorry, address cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        else if(postal.isEmpty()){
            String message = "Sorry, Postal code cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        else if(!matcher.matches()){
            String message = "Sorry, Postal code is incorrect pattern. A0A 0A0!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        else if(city.isEmpty()){
            String message = "Sorry, city cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        else if(role.isEmpty()){
            String message = "Sorry, role cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        else if(imageUrl.isEmpty()){
            String message = "Please choose an image!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        else{
            Log.d("Imahe Url", "addStaff: Image Url"+imageUrl);
            String staffId = myStaffRef.push().getKey();
            Staff staff = new Staff(staffId,propId,name,phone,address,postal,city,email,role,imageUrl);
            // need to save to firebase
            myStaffRef.child(staffId).setValue(staff);

//            DatabaseReference postsRef = myStaffRef.child("staff");
//            DatabaseReference newPostRef = postsRef.push();
//            newPostRef.setValue(staff);
            Toast.makeText(getApplicationContext(), staff.getName()+" is saved!", Toast.LENGTH_LONG).show();

            // redirect to Staff list View
            goBack();
        }
    }
    }

