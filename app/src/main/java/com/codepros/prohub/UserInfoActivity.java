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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.codepros.prohub.model.User;
import com.codepros.prohub.utils.FirebaseDataseHelper;
import com.codepros.prohub.utils.ToolbarHelper;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class UserInfoActivity extends AppCompatActivity {
    //Toolbar
    private Button toolbarBtnSettings, toolbarBtnChat, toolbarBtnNews, toolbarBtnForms;
    private ImageButton toolbarBtnSearch, btnHome, toolbarBtnMenu;
    private ToolbarHelper toolbar;

    private String userPhoneNum, imageUrl, myRole, userName;
    private User myUser;
    private Uri imguri;
    //
    private ImageButton ivUserIconBtn;
    private Button btnChangePass;
    private TextView tvUserName, tbUserPhone, tvUserRole;
    private EditText etCurrentPass, etNewPass, etConfirmPass;

    // database reference
    private DatabaseReference myUserRef;
    private StorageReference myStorageRef;
    //
    private static final int REQUEST_IMAGE = 2;
    private static final String TAG = "AddNewsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        SharedPreferences myPreference = getSharedPreferences("myUserSharedPref", MODE_PRIVATE);
        myRole = myPreference.getString("myRole", "");
        userPhoneNum = myPreference.getString("phoneNum", "");
        userName = myPreference.getString("username", "");
        //////////////////////////////////////////////
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
        myUserRef = FirebaseDatabase.getInstance().getReference();
        myStorageRef = FirebaseStorage.getInstance().getReference("userIcons");

        new FirebaseDataseHelper().readUsers(new FirebaseDataseHelper.UserDataStatus() {
            @Override
            public void DataIsLoad(List<User> users, List<String> keys) {
                for (User user : users) {
                    if (user.getPhone().equals(userPhoneNum)) {
                        myUser = user;
                        imageUrl = user.getImageUrl();
                        loadUserImage();
                        break;
                    }
                }
            }
        });
        //////////////////////////////////////////////
        tvUserName = findViewById(R.id.tvUserName);
        tvUserName.setText(userName);
        tbUserPhone = findViewById(R.id.tbUserPhone);
        tbUserPhone.setText(userPhoneNum);
        tvUserRole = findViewById(R.id.tvUserRole);
        tvUserRole.setText(myRole);

        etCurrentPass = findViewById(R.id.etCurrentPass);
        etNewPass = findViewById(R.id.etNewPass);
        etConfirmPass = findViewById(R.id.etConfirmPass);

        ivUserIconBtn = findViewById(R.id.ivUserIconBtn);
        btnChangePass = findViewById(R.id.btnChangePass);

        ivUserIconBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileChooser();
            }
        });

        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    private void loadUserImage(){
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(this.ivUserIconBtn);
        }
    }

    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void saveImageUrlToDatabase() {
        myUserRef.child("users").child(userPhoneNum).child("imageUrl").setValue(imageUrl);
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
                        saveImageUrlToDatabase();
                    }
                }
            });
        } catch (Exception e) {
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
            ivUserIconBtn.setImageURI(imguri);
            // upload the image to firebase storage
            FileUploader();
        }
    }

    private void changePassword() {
        String currentPass = etCurrentPass.getText().toString();
        String newPass = etNewPass.getText().toString();
        String confirmPass = etConfirmPass.getText().toString();

        if (myUser != null) {
            if (myUser.authentication(currentPass)) {
                if (newPass.equals(confirmPass)) {
                    // hash password
                    String password = myUser.hashPassword(newPass);
                    // update password in database
                    myUserRef.child("users").child(userPhoneNum).child("password").setValue(password);
                    Toast.makeText(getApplicationContext(), "Password saved!", Toast.LENGTH_LONG).show();
                    goBack();
                } else {
                    Toast.makeText(getApplicationContext(), "Password doesn't match, please type again", Toast.LENGTH_LONG).show();
                    return;
                }
            } else {
                Toast.makeText(getApplicationContext(), "Incorrect password input, please type again", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }

    private void goBack(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
