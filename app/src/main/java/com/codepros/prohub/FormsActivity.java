package com.codepros.prohub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codepros.prohub.model.Form;
import com.codepros.prohub.model.Staff;
import com.codepros.prohub.utils.FirebaseDataseHelper;
import com.codepros.prohub.utils.FormsAdapter;
import com.codepros.prohub.utils.StaffAdapter;
import com.codepros.prohub.utils.ToolbarHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FormsActivity extends AppCompatActivity {

    // Toolbar items
    private Button toolbarBtnChat;
    private Button toolbarBtnNews;
    private Button toolbarBtnForms;
    private Button toolbarBtnSettings;
    private ImageButton btnHome, toolbarBtnSearch;
    private ImageButton toolbarBtnMenu;
    private ToolbarHelper toolbar;

    private static final int REQUEST_IMAGE = 2;
    private static final String TAG = "FormsActivity";
    public static final String FORM_CHILD = "form";

    public List<String> formKeyList = new ArrayList<>();
    public List<Form> formList = new ArrayList<>();
    public FormsAdapter formsAdapter;

    String propId, propName, myRole;

    private RecyclerView formRecyclerView;

    // firebase database objects
    private DatabaseReference myFormRef;
    DatabaseReference drForm;

    //
    String fileNameToUpload = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forms);

        // for pdf testing
        exportChatHistory();

        // get prop id from shared preference
        SharedPreferences sharedPreferences = getSharedPreferences("myUserSharedPref", Context.MODE_PRIVATE);
        myRole = sharedPreferences.getString("myRole", "");
        propName = sharedPreferences.getString("propName", "");
        propId = sharedPreferences.getString("propId", "");

        //Shared Preferences
        myFormRef = FirebaseDatabase.getInstance().getReference("form");

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
        toolbarBtnForms.setBackgroundColor(getResources().getColor(R.color.btnBackground));

        toolbar = new ToolbarHelper(this, toolbarBtnChat, toolbarBtnNews, toolbarBtnForms,
                toolbarBtnSettings, btnHome, toolbarBtnSearch, toolbarBtnMenu);

        //////////////////////////////////////////////

        // Initialize RecyclerView
        formRecyclerView = (RecyclerView) findViewById(R.id.formRecyclerView);
        formRecyclerView.setHasFixedSize(true);
        formRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        new FirebaseDataseHelper().readForm(new FirebaseDataseHelper.FormDataStatus() {
            @Override
            public void DataIsLoad(List<Form> listForm, List<String> keys) {
                formList.clear();
                for (Form form : listForm) {
                    if (form.getPropId().equals(propId)) {
                        formList.add(form);
                    }
                }
                formKeyList = keys;
                setFormsAdapter();
            }

        });

        // upload button
        FloatingActionButton  btnUpload = (FloatingActionButton) findViewById(R.id.btnUpload);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Select image for image message on click.
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                // open image or pdf
                String[] mimeTypes = {"image/*", "application/pdf"};
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

                startActivityForResult(intent, REQUEST_IMAGE);

            }
        });

    }

    private void setFormsAdapter() {
        formsAdapter = new FormsAdapter(formList, formKeyList, myRole, this);
        formRecyclerView.setItemAnimator(new DefaultItemAnimator());
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(formRecyclerView);
        formRecyclerView.setAdapter(formsAdapter);
        System.out.println(formRecyclerView.getItemDecorationCount());
        System.out.println(formsAdapter.getItemCount());
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

            if (myRole.equals("Tenant")) {
                Toast.makeText(getApplicationContext(), "Sorry! You do not have permission to delete form.", Toast.LENGTH_LONG).show();
            } else {

                // get the position of staff to remove
                final int position = viewHolder.getAdapterPosition();
                final Form formToRemove = formList.remove(position);

                // display alert dialog for deletion
                AlertDialog.Builder builder = new AlertDialog.Builder(viewHolder.itemView.getContext());

                builder.setMessage("Are you sure to delete the Form?")

                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // delete
                                drForm = FirebaseDatabase.getInstance().getReference("form").child(formToRemove.getFormId());
                                drForm.removeValue();

                                // reflect the change
                                formsAdapter.notifyDataSetChanged();

                                // toast message
                                Toast.makeText(getBaseContext(), "Form deleted successfully", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // get the staff back
                                formList.add(position, formToRemove);

                                // reflect the change
                                formsAdapter.notifyDataSetChanged();
                            }
                        });
                builder.show();

            }

        }
    };

    private void uploadFormWithFileName(Context context, final Uri uri) {

        // filename and extension
        final String fullFileName = getFileNameFromUri(this, uri);
        String fileNameBody = fullFileName.substring(0, fullFileName.lastIndexOf("."));
        final String extension = fullFileName.substring(fullFileName.lastIndexOf(".") + 1);

        // show dialog box for filename selection
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("New Form Title");
        alertDialog.setMessage("Enter Form Title");

        // hard-coded dialog layout
        final EditText input = new EditText(this);
        input.setText(fileNameBody);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        fileNameToUpload = input.getText().toString();

                        if(fileNameToUpload.isEmpty() || fileNameToUpload == null) {
                            fileNameToUpload = fullFileName;
                        } else {
                            fileNameToUpload += "." + extension;
                        }

                        // get ready to create
                        final String newFormId = myFormRef.push().getKey();

                        Form newForm = new Form();
                        newForm.setFormId(newFormId);
                        newForm.setPropId(propId);
                        newForm.setContentUrl(uri.toString());
                        newForm.setFormTitle(fileNameToUpload);

                        myFormRef.child(newFormId).setValue(newForm, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError,
                                                   DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    String key = databaseReference.getKey();
                                    StorageReference storageReference =
                                            FirebaseStorage.getInstance()
                                                    .getReference("form")
                                                    .child(key)
                                                    .child(uri.getLastPathSegment());

                                    putImageInStorage(storageReference, uri, key, fileNameToUpload);
                                } else {
                                    Log.w(TAG, "Unable to write message to database.",
                                            databaseError.toException());
                                }
                            }
                        });


                    }
                })
                .setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    // get file name
                    final Uri uri = data.getData();

                    // upload form with filename
                    uploadFormWithFileName(this, uri);
                    formsAdapter.notifyDataSetChanged();
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
                                                        formWithFileUrl.setFormId(key);
                                                        formWithFileUrl.setPropId(propId);
                                                        formWithFileUrl.setContentUrl(task.getResult().toString());
                                                        formWithFileUrl.setFormTitle(fileName);

                                                        myFormRef.child(key)
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
    String getFileNameFromUri(Context context, Uri uri) {
        // Get the Uri, file, path of the selected file
        String uriString = uri.toString();
        File myFile = new File(uriString);
        String path = myFile.getAbsolutePath();

        String displayName = null;

        if (uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
            // local file
        } else if (uriString.startsWith("file://")) {
            displayName = myFile.getName();
        }

        return displayName;
    }



    // for pdf testing
    private static final int PERMISSION_REQUEST_CODE = 100;
    private String EXPORT_FILENAME;
    private JSONObject jsonData = new JSONObject(); // tentative output

    // resource:
    // export PDF
    // https://github.com/rakesh2gnit/export-print-pdf-android
    // NOTE:
    // PDF Viewer need to be installed in the device
    // Need to build below the project and install into the emulator
    // https://github.com/barteksc/AndroidPdfViewer
    // export chat history function tentatively put in PropertyHomeActivity

    public void exportChatHistory() {
//    public void exportChatHistory(View view) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                printPdf();
            } else {
                requestPermission(); // Code for permission
            }
        } else {
            printPdf();
        }
    }

    public void printPdf() {
        // get storage directory path
        // PDF Filepath:
        // Settings > Storage&USB > Internal Storage
        // Others > PDF > Name.pdf
        EXPORT_FILENAME = Environment.getExternalStorageDirectory().toString() + "/PDF/" + "Name.pdf";

        // Create New Blank Document
        Document document = new Document(PageSize.A4);

        // create Directory
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/PDF");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }

        // create new document via PDF Writer
        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(EXPORT_FILENAME));

            document.open();

            addTitlePage(document, pdfWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        document.close();

        Toast.makeText(this, "PDF File created! : " + EXPORT_FILENAME, Toast.LENGTH_LONG).show();

        // open default PDF viewer
//        openGeneratedPDF();
    }

    private void openGeneratedPDF() {
        File file = new File(EXPORT_FILENAME);
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(FormsActivity.this, "No Application avaiable to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void addTitlePage(Document document, PdfWriter pdfWriter) throws DocumentException, IOException {

        // create paragraph
        Paragraph prHead = new Paragraph();

        // add a string to the paragraph
        prHead.add(jsonData.toString());

        // add the paragraph to the document
        document.add(prHead);

        // Create new Page in PDF
        document.newPage();
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(FormsActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(FormsActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(FormsActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(FormsActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

}
