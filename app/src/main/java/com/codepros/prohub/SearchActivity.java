package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.codepros.prohub.model.ChatMessage;
import com.codepros.prohub.model.Form;
import com.codepros.prohub.model.News;
import com.codepros.prohub.utils.FirebaseDataseHelper;
import com.codepros.prohub.model.Unit;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ArrayAdapter<String> filterAdaptor;
    private Spinner spFilter;
    private String[] filter;
    private String filterVal;
    private String propId;
    private String phoneNum;
    private AppCompatAutoCompleteTextView atvSearch;

    private ArrayAdapter<String> searchListAdapter;
    private List<ChatMessage> allChatMessageList;
    private List<ChatMessage> myChatMessageList;
    private List<News> allNewsList;
    private List<News> myNewsList;
    private List<Form> allFormList;
    private List<Form> myFormList;
    private List<String> searchList;

    // firebase database objects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // set variables
        spFilter = findViewById(R.id.search_filter_spinner);
        filter = getResources().getStringArray(R.array.search_filter);
        filterVal = "Chat";

        atvSearch = findViewById(R.id.atv_search);

        allChatMessageList = new ArrayList<>();
        myChatMessageList = new ArrayList<>();

        allNewsList = new ArrayList<>();
        myNewsList = new ArrayList<>();

        allFormList = new ArrayList<>();
        myFormList = new ArrayList<>();

        searchList = new ArrayList<>();

        SharedPreferences myPref = getSharedPreferences("myUserSharedPref", MODE_PRIVATE);
        propId = myPref.getString("propId", "");
        phoneNum = myPref.getString("phoneNum", "0123456789");

        // set spinner
        setFilterSpinner();

        // initialize default value
//        Init();

        // read values
        new FirebaseDataseHelper().readChatMessages(new FirebaseDataseHelper.ChatMessageDataStatus() {
            @Override
            public void DataIsLoad(List<ChatMessage> chatMessages, List<String> keys) {
                allChatMessageList = chatMessages;
                for (int i = 0; i < allChatMessageList.size(); i++) {
                    if (allChatMessageList.get(i).getReceiverNumber().equals(phoneNum)) {
                        boolean isExisting = false;
                        for (int a = 0; a < myChatMessageList.size(); a++) {
                            if (myChatMessageList.get(a).getReceiverNumber().equals(phoneNum)) {
                                isExisting = true;
                            }
                        }
                        if (!isExisting) {
                            myChatMessageList.add(allChatMessageList.get(i));
                        }
                    }
                }
            }
        });

        new FirebaseDataseHelper().readNews(new FirebaseDataseHelper.NewsDataStatus() {
            @Override
            public void DataIsLoad(List<News> news, List<String> keys) {
                allNewsList = news;
                for (int i = 0; i < allNewsList.size(); i++) {
                    if (allNewsList.get(i).getPropId().equals(propId)) {
                        myNewsList.add(allNewsList.get(i));
                    }
                }
            }
        });

        new FirebaseDataseHelper().readForm(new FirebaseDataseHelper.FormDataStatus() {
            @Override
            public void DataIsLoad(List<Form> form, List<String> keys) {
                allFormList = form;
                for (int i = 0; i < allFormList.size(); i++) {
                    if (allFormList.get(i).getPropId().equals(propId)) {
                        myFormList.add(allFormList.get(i));
                    }
                }
            }
        });

        // handle click event and set desc on textview
        atvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                String selection = parent.getItemAtPosition(pos).toString();

                switch (filterVal) {
                    case "Chat":
                        for (int i = 0; i < myChatMessageList.size(); i++) {
                            if (myChatMessageList.get(i).getSenderName().equals(selection)) {
                                String chatId = myChatMessageList.get(i).getChatMessageId();
                                String senderName = myChatMessageList.get(i).getSenderName();
                                String senderNumber = myChatMessageList.get(i).getSenderNumber();
                                String receiverNumber = myChatMessageList.get(i).getReceiverNumber();

                                Intent intent = new Intent(SearchActivity.this, ChatActivity.class);
                                intent.putExtra("Chat_ID", chatId);
                                intent.putExtra("senderName", senderName);
                                intent.putExtra("senderNumber", senderNumber);
                                intent.putExtra("receiverNumber", receiverNumber);

                                SearchActivity.this.startActivity(intent);
                            }
                        }
                        break;
                    case "News":
                        for (int i = 0; i < myNewsList.size(); i++) {
                            if (myNewsList.get(i).getNewsTitle().equals(selection)) {
                                String content = myNewsList.get(i).getContent();
                                String createTime = myNewsList.get(i).getCreateTime();
                                String creatorPhoneNumber = myNewsList.get(i).getCreatorPhoneNumber();
                                String hideFlag = Boolean.toString(myNewsList.get(i).getHideFlag());
                                String imageUrl = myNewsList.get(i).getImageUrl();
                                String newsTitle = myNewsList.get(i).getNewsTitle();
                                String targetViewer = myNewsList.get(i).getTargetViewer();

                                Bundle b = new Bundle();
                                Intent intent = new Intent(SearchActivity.this, DisplayNewsActivity.class);
                                b.putString("title", newsTitle);
                                b.putString("description", content);
                                b.putString("date", createTime);
                                b.putString("imgUrl", imageUrl);
                                b.putString("propId", propId);
                                b.putString("creatorPhoneNumber", creatorPhoneNumber);
                                b.putString("hideFlag", hideFlag);
                                b.putString("targetViewer", targetViewer);

                                intent.putExtras(b);

                                SearchActivity.this.startActivity(intent);
                            }
                        }
                        break;
                    case "Form":
                        for (int i = 0; i < myFormList.size(); i++) {
                            if (myFormList.get(i).getFormTitle().equals(selection)) {
                                String formId = myFormList.get(i).getFormTitle();
                                String formTitle = myFormList.get(i).getFormTitle();
                                String contentUrl = myFormList.get(i).getContentUrl();

                                Intent intent = new Intent(SearchActivity.this, FormsActivity.class);

                                SharedPreferences myPreference = getSharedPreferences("myUserSharedPref", 0);
                                SharedPreferences.Editor prefEditor = myPreference.edit();
                                prefEditor.putString("formId", formId);
                                prefEditor.putString("formTitle", formTitle);
                                prefEditor.putString("contentUrl", contentUrl);
                                prefEditor.putString("propId", propId);
                                prefEditor.putString("isSearching", "true");
                                prefEditor.apply();

                                SearchActivity.this.startActivity(intent);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    // set filter spinner value
    private void setFilterSpinner() {
        filterAdaptor = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filter);
        filterAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilter.setAdapter(filterAdaptor);
        spFilter.setOnItemSelectedListener(this);
    }

    // spinner selection event handler
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        filterVal = parent.getItemAtPosition(pos).toString();

        switch (filterVal) {
            case "Chat":
                searchList.clear();
                for (int i = 0; i < myChatMessageList.size(); i++) {
                    searchList.add(myChatMessageList.get(i).getSenderName());
                }
                break;
            case "News":
                searchList.clear();
                for (int i = 0; i < myNewsList.size(); i++) {
                    searchList.add(myNewsList.get(i).getNewsTitle());
                }
                break;
            case "Form":
                searchList.clear();
                for (int i = 0; i < myFormList.size(); i++) {
                    searchList.add(myFormList.get(i).getFormTitle());
                }
                break;
            default:
                searchList.clear();
                break;
        }
        searchListAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, searchList);
        atvSearch.setThreshold(1); // will start working from first character
        atvSearch.setAdapter(searchListAdapter);
    }

    // spinner selection event handler
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        searchList.clear();
    }
}
