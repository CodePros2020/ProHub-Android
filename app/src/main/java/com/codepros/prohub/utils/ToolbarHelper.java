package com.codepros.prohub.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.codepros.prohub.AddUnitActivity;
import com.codepros.prohub.ChatActivity;
import com.codepros.prohub.ChatList;
import com.codepros.prohub.FormsActivity;
import com.codepros.prohub.MainActivity;
import com.codepros.prohub.NewsViewActivity;
import com.codepros.prohub.PropertyHomeActivity;
import com.codepros.prohub.SearchActivity;
import com.codepros.prohub.SettingsActivity;
import com.codepros.prohub.ViewStaffActivity;
import com.codepros.prohub.model.Chat;
import com.codepros.prohub.model.ChatMessage;
import com.codepros.prohub.model.Form;
import com.codepros.prohub.model.Property;
import com.codepros.prohub.model.Unit;
import com.codepros.prohub.model.User;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.JsonObject;
import com.google.type.Date;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Header;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import io.grpc.internal.JsonParser;

public class ToolbarHelper {
    private Context context;
    private View toolBtnChat, toolBtnNews, toolBtnForms, toolBtnSettings;
    private View toolBtnHome, toolBtnMenu, toolBtnSearch;
    public String propId, myRole, propName, phoneNum, chatMessageId;

    // for export Chat History
    private DatabaseReference myPropRef;
    private Property myProp;
    private Unit myUnit;
    private User myTenant;
    private User myLandlord;


    public ToolbarHelper(Context context) {
        this.context = context;
    }

    public ToolbarHelper(Context context, View toolBtnChat, View toolBtnNews, View toolBtnForms, View toolBtnSettings,
                         View toolBtnHome, View toolBtnSearch, View toolBtnMenu) {
        this.context = context;
        this.toolBtnChat = toolBtnChat;
        this.toolBtnNews = toolBtnNews;
        this.toolBtnForms = toolBtnForms;
        this.toolBtnSettings = toolBtnSettings;
        this.toolBtnHome = toolBtnHome;
        this.toolBtnMenu = toolBtnMenu;
        this.toolBtnSearch = toolBtnSearch;

        SetSharedPrefValues();
        setOnClickFunctions();

    }

    public void SetSharedPrefValues() {
        SharedPreferences myPreference = context.getSharedPreferences("myUserSharedPref", 0);
        myRole = myPreference.getString("myRole", "");
        SharedPreferences propPreference = Objects.requireNonNull(context).getSharedPreferences("myPropSharedPref", Context.MODE_PRIVATE);
        propId = propPreference.getString("phoneNum", "");
        propId = propPreference.getString("propId", "");

    }

    public void setOnClickFunctions() {
        toolBtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goChat(v);
            }
        });

        toolBtnNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNews(v);
            }
        });

        toolBtnForms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goForms(v);
            }
        });

        toolBtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSettings(v);
            }
        });

        toolBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSearch(v);
            }
        });

        toolBtnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome(v);
            }
        });

        getMenuBtn(toolBtnMenu);
    }

    // for export chat history
    public void setChatHistoryExportInfo(String chatMessageId, String propId){
        this.chatMessageId = chatMessageId;
        this.propId = propId;
        // read chat for export chat history
        setUpForExportChatHistory();
    }

    // Menu button Action
    public void getMenuBtn(View btnMenu) {
        // Menu drop down
        final PopupMenu dropDownMenu = new PopupMenu(context, btnMenu);
        final Menu menu = dropDownMenu.getMenu();
        // list of items for menu:
        menu.add(0, 0, 0, "Manage Staff");
        menu.add(1, 1, 1, "Logout");

        if(context instanceof ChatActivity) {
            menu.add(2, 2, 2, "Export Chat History");
        }


        // logout item
        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 0:
                        if (myRole.equals("Tenant")) {
                            Toast.makeText(context, "Sorry! You do not have permission to manage staff.", Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(context, ViewStaffActivity.class);
                            intent.putExtra("propId", propId);
                            intent.putExtra("propName", propName);
                            context.startActivity(intent);
                            return true;
                        }

                    case 1:
                        FirebaseAuth.getInstance().signOut();
                        // item ID 0 was clicked
                        Intent i = new Intent(context, MainActivity.class);
                        i.putExtra("finish", true);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clean all activities
                        context.startActivity(i);
                        //finish();
                        return true;
                    case 2:
                        exportChatHistory();

                        return true;
                }
                return false;
            }
        });

        // Menu button click
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropDownMenu.show();
            }
        });

    }

    private void setUpForExportChatHistory(){
        System.out.println(chatMessageId);
        final String phoneNum = (chatMessageId.split("_"))[0];
        final String tenantId = (chatMessageId.split("_"))[1];

        new FirebaseDataseHelper().readChats(new FirebaseDataseHelper.ChatDataStatus() {
            @Override
            public void DataIsLoad(List<Chat> chats, List<String> keys) {
                chatList.clear();
                for(Chat c : chats) {
                    if(c.getChatMessageId().equals(chatMessageId)) {
                        chatList.add(c);
                    }
                }
            }
        });
        new FirebaseDataseHelper().readProperty(new FirebaseDataseHelper.PropDataStatus() {
            @Override
            public void DataIsLoad(List<Property> properties, List<String> keys) {
                for(Property p : properties) {
                    if(p.getPropId().equals(propId)) {
                        myProp = p;
                    }
                }
            }
        });
        new FirebaseDataseHelper().readUnits(new FirebaseDataseHelper.UnitDataStatus() {
            @Override
            public void DataIsLoad(List<Unit> units, List<String> keys) {
                for(Unit u : units) {
                    if(u.getTenantId().equals(tenantId) && u.getPropId().equals(propId)) {
                        myUnit = u;
                    }
                }
            }
        });
        new FirebaseDataseHelper().readUsers(new FirebaseDataseHelper.UserDataStatus() {
            @Override
            public void DataIsLoad(List<User> users, List<String> keys) {
                for(User u : users) {
                    if(u.getPhone().equals(tenantId) && u.getRole().equals("Tenant")) {
                        myTenant = u;
                    }
                }
            }
        });
        new FirebaseDataseHelper().readUsers(new FirebaseDataseHelper.UserDataStatus() {
            @Override
            public void DataIsLoad(List<User> users, List<String> keys) {
                for(User u : users) {
                    if(u.getPhone().equals(phoneNum) && u.getRole().equals("Landlord")) {
                        myLandlord = u;
                    }
                }
            }
        });


    }

    public void goChat(View view) {
        Intent intent = new Intent(context, ChatList.class);
        context.startActivity(intent);
    }

    public void goNews(View view) {
        Intent intent = new Intent(context, NewsViewActivity.class);
        context.startActivity(intent);
    }

    public void goForms(View view) {
        Intent intent = new Intent(context, FormsActivity.class);
        context.startActivity(intent);
    }

    public void goSettings(View view) {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra("propId", propId);
        context.startActivity(intent);
    }

    public void goSearch(View view) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    public void goHome(View view) {
        Intent intent = new Intent(context, PropertyHomeActivity.class);
        context.startActivity(intent);
    }

    // Export chat History
    private static final int PERMISSION_REQUEST_CODE = 100;
    private String EXPORT_FILENAME;

    List<Chat> chatList = new ArrayList<>();

    // resource:
    // export PDF
    // https://github.com/rakesh2gnit/export-print-pdf-android
    // NOTE:
    // PDF Viewer need to be installed in the device
    // Need to build below the project and install into the emulator
    // https://github.com/barteksc/AndroidPdfViewer
    // export chat history function tentatively put in PropertyHomeActivity
    String exportingFileName = "";
    public void exportChatHistory() {

        // get current date time
        SimpleDateFormat formatter= new SimpleDateFormat("yyyyMMdd");
        java.util.Date date = new java.util.Date(System.currentTimeMillis());
        String dateString = formatter.format(date);

        // filename and extension
        String fileNameBody = dateString + "_" + myTenant.getFirstname() + "_" + myTenant.getLastname();

        final String extension = "pdf";
        final String fullFileName = fileNameBody + "." + extension;

        // show dialog box for filename selection
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Export Chat History");
        alertDialog.setMessage("Enter PDF File Name");

        // hard-coded dialog layout
        final EditText input = new EditText(context);
        input.setText(fileNameBody);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Export",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        exportingFileName = input.getText().toString();

                        if(exportingFileName.isEmpty() || exportingFileName == null) {
                            exportingFileName = fullFileName;
                        } else {
                            exportingFileName += "." + extension;
                        }
                        //print
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
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

        alertDialog.show();

    }

    public void printPdf() {
//        EXPORT_FILENAME = Environment.getExternalStorageDirectory().toString() + "/PDF/" + "Name.pdf";
        EXPORT_FILENAME = Environment.getExternalStorageDirectory().toString() + "/PDF/" + exportingFileName;

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
            addChatHistoryPage(document, pdfWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        document.close();

        Toast.makeText(context, "PDF File created! : " + EXPORT_FILENAME, Toast.LENGTH_LONG).show();

    }


    public void addTitlePage(Document document, PdfWriter pdfWriter) throws DocumentException, IOException {
        // Font Style for Document
        Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.BOLD
                | Font.UNDERLINE);
        Font subTitleFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD
                , BaseColor.GRAY);

        Font bodyFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.NORMAL);

        Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

        // DOCUMENT TITLE
//        Paragraph prHead = new Paragraph();
//        prHead.setFont(titleFont);
//        prHead.add("ProHub\n");
//        prHead.setFont(catFont);
//        prHead.add("\nChat History\n");
//        prHead.add("\n");
//        prHead.setAlignment(Element.ALIGN_CENTER);
//
//        document.add(prHead); // Add all above details into Document

        Paragraph prHead = new Paragraph();
        prHead.setFont(titleFont);
        prHead.add(myProp.getName() + "\n");

        prHead.setFont(subTitleFont);

        String fullAddress = myProp.getStreetLine1() + ", " + myProp.getCity() + ", " + myProp.getProvince();
        prHead.add("\n" + fullAddress);
        if(myProp.getPostalCode() != null){
            prHead.add(" " + myProp.getPostalCode().toUpperCase());
        }
        prHead.add("\n\n\n");
        prHead.setAlignment(Element.ALIGN_CENTER);

        document.add(prHead); // Add all above details into Document




        // DOCUMENT

        Paragraph prTenantNameLabel = new Paragraph();
        prTenantNameLabel.setFont(catFont);
        prTenantNameLabel.add("Tenant Name\n\n");
        document.add(prTenantNameLabel);

        Paragraph prTenantName = new Paragraph();
        prTenantName.setFont(bodyFont);
        prTenantName.add(myTenant.getFirstname() + " " + myTenant.getLastname() + "\n\n");
        document.add(prTenantName);

        Paragraph prUnitNameLabel = new Paragraph();
        prUnitNameLabel.setFont(catFont);
        prUnitNameLabel.add("Unit Name\n\n");
        document.add(prUnitNameLabel);

        Paragraph prUnitName = new Paragraph();
        prUnitName.setFont(bodyFont);
        prUnitName.add(myUnit.getUnitName() + "\n\n\n");
        document.add(prUnitName);


        Paragraph prTenantContactLabel = new Paragraph();
        prTenantContactLabel.setFont(catFont);
        prTenantContactLabel.add("Contact Information\n\n");
        document.add(prTenantContactLabel);

        Paragraph prTenantPhoneNum = new Paragraph();
        prTenantPhoneNum.setFont(bodyFont);
        prTenantPhoneNum.add(myTenant.getPhone() + "\n\n\n");
        document.add(prTenantPhoneNum);

        Paragraph prLandlordLabel = new Paragraph();
        prLandlordLabel.setFont(catFont);
        prLandlordLabel.add("Landlord Name\n\n");
        document.add(prLandlordLabel);

        Paragraph prLandlordName = new Paragraph();
        prLandlordName.setFont(bodyFont);
        prLandlordName.add(myLandlord.getFirstname() + " " + myLandlord.getLastname() + "\n\n\n");
        document.add(prLandlordName);

//        Paragraph prTenantInfo = new Paragraph();
//        prTenantInfo.setFont(bodyFont);
//        prTenantInfo.add(myProp.getName() + "\n\n");
//        if(myUnit != null) {
//            prTenantInfo.add(myUnit.getUnitName() + "\n");
//        }
//        prTenantInfo.add(myProp.getStreetLine1() + "\n");
//        prTenantInfo.add(myProp.getCity() + ", " + myProp.getProvince() + "\n");
//        if(myProp.getPostalCode() != null){
//            prTenantInfo.add(myProp.getPostalCode().toUpperCase() + "\n\n");
//        }
//        document.add(prTenantInfo); // Add all above details into Document


        // Create new Page in PDF
//        document.newPage();
    }

    public void addChatHistoryPage(Document document, PdfWriter pdfWriter) throws DocumentException, IOException {
        // Font Style for Document
        Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.BOLD
                | Font.UNDERLINE, BaseColor.GRAY);

        Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

        // chat history header
        Paragraph prChatHistoryCat = new Paragraph();
        prChatHistoryCat.setFont(catFont);
        prChatHistoryCat.add("Chat History\n\n");
        document.add(prChatHistoryCat);

        // chat history
        Paragraph prChatHistory = new Paragraph();
        prChatHistory.setFont(normal);
        for(Chat c: chatList ){
            String line = "[" + c.getTimestamp() + "] " + c.getFullName() + ": " + c.getMessage() + "\n";
            prChatHistory.add(line);
        }
        document.add(prChatHistory); // add the paragraph to the document

        // Create new Page in PDF
        document.newPage();
    }

        // for export chat history file permission
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText((Activity) context, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }




}
