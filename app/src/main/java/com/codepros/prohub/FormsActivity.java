package com.codepros.prohub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepros.prohub.model.Chat;
import com.codepros.prohub.model.Form;
import com.codepros.prohub.model.News;
import com.codepros.prohub.utils.FirebaseDataseHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FormsActivity extends AppCompatActivity {
    // nested class
    public static class FormViewHolder extends RecyclerView.ViewHolder {
        TextView formFileNameView;

        public FormViewHolder(View v) {
            super(v);

            formFileNameView = (TextView) itemView.findViewById(R.id.formFileNameView);
        }
    }

    private static final int REQUEST_IMAGE = 2;
    private static final String TAG = "FormsActivity";
    public static final String FORM_CHILD = "form";

    private String mPropId;
    public static final String ANONYMOUS = "anonymous";
    private RecyclerView mFormListRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    // Firebase instance variables
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Form, FormViewHolder>
            mFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forms);

        // get prop id from shared preference
        SharedPreferences myPref = getSharedPreferences("myUserSharedPref", MODE_PRIVATE);
        mPropId = myPref.getString("propId", ANONYMOUS);

        // Initialize RecyclerView.
        mFormListRecyclerView = (RecyclerView) findViewById(R.id.mFormListRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mFormListRecyclerView.setLayoutManager(mLinearLayoutManager);

        // get ref to firebase
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        final SnapshotParser<Form> parser = new SnapshotParser<Form>() {
            @NonNull
            @Override
            public Form parseSnapshot(@NonNull DataSnapshot snapshot) {
                Form form = snapshot.getValue(Form.class);
                if (form != null) {
                    form.setFormId(snapshot.getKey());
                }
                return form;
            }
        };

        Query formRef = mFirebaseDatabaseReference.child(FORM_CHILD);

        final FirebaseRecyclerOptions<Form> options =
                new FirebaseRecyclerOptions.Builder<Form>()
                        .setQuery(formRef, parser)
                        .build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Form, FormViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final FormViewHolder holder, int position, @NonNull Form model) {
                holder.formFileNameView.setText(model.getFormTitle());
                holder.formFileNameView.setVisibility(TextView.VISIBLE);
                holder.formFileNameView.setTextColor(Color.BLACK);
            }

            @NonNull
            @Override
            public FormsActivity.FormViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_form, parent, false);
                return new FormsActivity.FormViewHolder(view) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };

            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int chatCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (chatCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mFormListRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        Log.d("TAG", "!!!" + mFirebaseAdapter.getItemCount());

        mFormListRecyclerView.setAdapter(mFirebaseAdapter);



        // upload button
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

    @Override
    public void onPause() {
        mFirebaseAdapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAdapter.startListening();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    // get file name
                    final Uri uri = data.getData();
                    final String fileName = getFileNameFromUri(this, uri);

                    Toast.makeText(FormsActivity.this, "??????" + fileName, Toast.LENGTH_LONG).show();

                    // creat Model
                    Form newForm = new Form();
                    newForm.setPropId(mPropId);
                    newForm.setContentUrl(uri.toString());
                    newForm.setFormTitle(fileName);

                    //
                                        mFirebaseDatabaseReference.child(FORM_CHILD).push()
                            .setValue(newForm, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError,
                                                       DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        String key = databaseReference.getKey();
                                        StorageReference storageReference =
                                                FirebaseStorage.getInstance()
                                                        .getReference(mPropId)
                                                        .child(key)
                                                        .child(uri.getLastPathSegment());

                                        putImageInStorage(storageReference, uri, key, fileName);
                                    } else {
                                        Log.w(TAG, "Unable to write message to database.",
                                                databaseError.toException());
                                    }
                                }
                            });

                }
            }
        }
    }

    // upload file to firebase storage
    private void putImageInStorage(StorageReference storageReference, Uri uri, final String key, final String fileName) {
        storageReference.putFile(uri).addOnCompleteListener(FormsActivity.this,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            task.getResult().getMetadata().getReference().getDownloadUrl()
                                    .addOnCompleteListener(FormsActivity.this,
                                            new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    if (task.isSuccessful()) {
                                                        //
                                                        Form formWithFileUrl = new Form();
                                                        formWithFileUrl.setPropId(mPropId);
                                                        formWithFileUrl.setContentUrl(task.getResult().toString());
                                                        formWithFileUrl.setFormTitle(fileName);

                                                        mFirebaseDatabaseReference.child(FORM_CHILD).child(key)
                                                                .setValue(formWithFileUrl);
                                                    }
                                                }
                                            });
                        } else {
                            Log.w(TAG, "Image upload task was not successful.",
                                    task.getException());
                        }
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

}
