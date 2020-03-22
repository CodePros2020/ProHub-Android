package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.codepros.prohub.model.Property;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class PropertyHomeActivity extends AppCompatActivity {

    private Button toolbarBtnChat, chatButton;
    private Button toolbarBtnNews, newsroomButton;
    private Button toolbarBtnForms, formsButton;
    private Button toolbarBtnSettings, settingsButton;
    private Button btnDashboard;
    private ImageButton toolbarBtnHome, toolbarBtnSearch;
    private FloatingActionButton btnAddUnit;
    private ImageButton toolbarBtnMenu; // menu

    // Firebase database objects
    private static final String TAG = "PropertyHomeActivity";
    private DatabaseReference myPropRef;

    // Chat database ref for export chat history
    private DatabaseReference myChatRef;
    public static final String CHAT_CHILD = "chat";

    // Export chat History
    private static final int PERMISSION_REQUEST_CODE = 100;
    private String EXPORT_FILENAME;
    private JSONObject jsonData = new JSONObject(); // tentative output

    // property ID
    private String propId;

    // user role
    private String myRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_home);

        // get propId from intent, save to shared preference
        propId = getIntent().getStringExtra("propId");
        SharedPreferences myPreference = getSharedPreferences("myUserSharedPref", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = myPreference.edit();
        prefEditor.putString("propId", propId);
        prefEditor.apply();

        myPropRef = FirebaseDatabase.getInstance().getReference();

        myRole = myPreference.getString("myRole", "");

        // references to the buttons on view
        chatButton = findViewById(R.id.chatButton);
        newsroomButton = findViewById(R.id.newsroomButton);
        formsButton = findViewById(R.id.formsButton);
        settingsButton = findViewById(R.id.settingsButton);
        btnDashboard=findViewById(R.id.btnDashboard);
        btnAddUnit = findViewById(R.id.btnAddUnit);

        // Set the name of the Logged in person
        btnDashboard.setText("PROHUB");

        // Button for top toolbar
        toolbarBtnChat = findViewById(R.id.toolbarBtnChat);
        toolbarBtnNews = findViewById(R.id.toolbarBtnNews);
        toolbarBtnForms = findViewById(R.id.toolbarBtnForms);
        toolbarBtnSettings = findViewById(R.id.toolbarBtnSettings);
        toolbarBtnHome = findViewById(R.id.ImageButtonHome);
        toolbarBtnSearch = findViewById(R.id.ImageButtonSearch);
        toolbarBtnMenu = findViewById(R.id.ImageButtonMenu);

        // define the actions for each button

        toolbarBtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goChat(v);
            }
        });
        toolbarBtnNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNews(v);
            }
        });
        toolbarBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSearch(v);
            }
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goChat(v);
            }
        });
        final PopupMenu dropDownMenu = new PopupMenu(PropertyHomeActivity.this, toolbarBtnMenu);
        final Menu menu = dropDownMenu.getMenu();
        // list of items for menu:
        menu.add(0, 0, 0, "Logout");

        // logout item
        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 0:
                        // item ID 0 was clicked
                        Intent i = new Intent(PropertyHomeActivity.this, MainActivity.class);
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

        // NEEDS TO BE CHANGED

        newsroomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNews(v);
            }
        });
        /*
        formsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goForms(v);
            }
        });
        /*
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goAddUnit(v);
            }
        });
        */

        btnAddUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myRole.equals("Landlord")) {
                    goAddUnit(v);
                } else {
                    Toast.makeText(PropertyHomeActivity.this, "You cannot add property as you are not a landlord!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    public void goAddUnit(View view) {
        Intent intent = new Intent(this, AddUnitActivity.class);
        intent.putExtra("propId", propId);
        this.startActivity(intent);
    }

    // resource:
    // export PDF
    // https://github.com/rakesh2gnit/export-print-pdf-android
    // NOTE:
    // PDF Viewer need to be installed in the device
    // Need to build below the project and install into the emulator
    // https://github.com/barteksc/AndroidPdfViewer
    // export chat history function tentatively put in PropertyHomeActivity
    public void exportChatHistory(View view){
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

    public void printPdf(){
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
        if(!myDir.exists()) {
            myDir.mkdirs();
        }

        // create new document via PDF Writer
        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(EXPORT_FILENAME));

            document.open();

            addTitlePage(document, pdfWriter);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(DocumentException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }

        document.close();

        Toast.makeText(this, "PDF File created! : " + EXPORT_FILENAME, Toast.LENGTH_LONG).show();

        // open default PDF viewer
//        openGeneratedPDF();
    }

    private void openGeneratedPDF() {
        File file = new File(EXPORT_FILENAME);
        if(file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(PropertyHomeActivity.this, "No Application avaiable to view pdf", Toast.LENGTH_LONG).show();
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
        int result = ContextCompat.checkSelfPermission(PropertyHomeActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(PropertyHomeActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(PropertyHomeActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(PropertyHomeActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
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