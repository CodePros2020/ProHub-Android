package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.codepros.prohub.model.Chat;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class FormsActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE = 2;
    private static final String TAG = "FormsActivity";

//    // Firebase instance variables
//    private FirebaseUser mFirebaseUser;
//    private DatabaseReference mFirebaseDatabaseReference;
//    private FirebaseRecyclerAdapter<Chat, ChatActivity.MessageViewHolder>
//            mFirebaseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forms);

        Button btnUpload =  (Button)findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Select image for image message on click.
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                // open image or pdf
                String[] mimeTypes = {"image/*","application/pdf"};
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

                startActivityForResult(intent, REQUEST_IMAGE);

            }
        });

    }

    // get file name from uri
    String getFileNameFromUri(Context cntx, Uri uri) {
        // Get the Uri of the selected file
        String uriString = uri.toString();
        File myFile = new File(uriString);
        String path = myFile.getAbsolutePath();
        String displayName = null;

        if (uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = cntx.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        } else if (uriString.startsWith("file://")) {
            displayName = myFile.getName();
        }
        return displayName;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    // get file name
                    final Uri uri = data.getData();
                    String filepath = getFileNameFromUri(this, uri);

                    Log.d(TAG, "Uri: " + uri.toString());
                    Toast.makeText(FormsActivity.this, "??????" + filepath, Toast.LENGTH_LONG).show();

                    //
//                    Formfile tempForm = new Form(id, fileName, );

//                    Chat tempMessage = new Chat(chatMessageId, mUsername, null, mPhoneNumber,
//                            mPhotoUrl,
//                            LOADING_IMAGE_URL,
//                            timestamp);
//
//                    mFirebaseDatabaseReference.child(CHAT_CHILD).push()
//                            .setValue(tempMessage, new DatabaseReference.CompletionListener() {
//                                @Override
//                                public void onComplete(DatabaseError databaseError,
//                                                       DatabaseReference databaseReference) {
//                                    if (databaseError == null) {
//                                        String key = databaseReference.getKey();
//                                        StorageReference storageReference =
//                                                FirebaseStorage.getInstance()
//                                                        //.getReference(mFirebaseUser.getUid())
//                                                        .getReference(mPhoneNumber)
//                                                        .child(key)
//                                                        .child(uri.getLastPathSegment());
//
//                                        putImageInStorage(storageReference, uri, key);
//                                    } else {
//                                        Log.w(TAG, "Unable to write message to database.",
//                                                databaseError.toException());
//                                    }
//                                }
//                            });




                }
            }
        }
    }


}
