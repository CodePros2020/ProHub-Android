package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codepros.prohub.model.ChatMessage;
import com.codepros.prohub.model.News;
import com.codepros.prohub.model.User;
import com.codepros.prohub.utils.ChatAdapter;
import com.codepros.prohub.utils.FirebaseDataseHelper;
import com.codepros.prohub.utils.NewsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class NewsViewActivity extends AppCompatActivity {

    public RecyclerView newsRecyclerView;
    public NewsAdapter newsAdapter;
    public List<News> newsList=new ArrayList<News>();
    private List<User> myUsers = new ArrayList<>();
    public FloatingActionButton btn_add;
    private DatabaseReference myPropRef;
    // user role
    private String myRole;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_view);

        //Shared pref
        SharedPreferences sharedPreferences = getSharedPreferences("myUserSharedPref", Context.MODE_PRIVATE);
         myRole= sharedPreferences.getString("myRole", "");
        Log.d("Role in preference: ", "onCreate: "+myRole);
        //
        myPropRef = FirebaseDatabase.getInstance().getReference();
        //
        new FirebaseDataseHelper().readUsers(new FirebaseDataseHelper.UserDataStatus() {
            @Override
            public void DataIsLoad(List<User> users, List<String> keys) {
                myUsers = users;
            }
        });
        //

        btn_add=findViewById(R.id.btn_add_news);
        switch(myRole){
            case "Landlord":
                btn_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent addIntent=new Intent(getBaseContext(),AddNewsActivity.class);
                        startActivity(addIntent);
                    }
                });
                break;
            case "Tenant":
                btn_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getBaseContext(),"Sorry! You do not have permission to create news.",Toast.LENGTH_LONG).show();
                    }
                });

        }


        newsRecyclerView =findViewById(R.id.newsRecyclerview);
        newsRecyclerView.setHasFixedSize(true);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Read News from database
        // read the list of News from Firebase

        new FirebaseDataseHelper().readNews(new FirebaseDataseHelper.NewsDataStatus() {
            @Override
            public void DataIsLoad(List<News> listNews, List<String> keys) {

                for(int i=0;i<listNews.size();i++){
                    if((myRole.equals("Tenant")) && (listNews.get(i).getTargetViewer().equals("all"))){

                            newsList.add(listNews.get(i));
                    }
                    else if((myRole.equals("Landlord")) && (listNews.get(i).getTargetViewer().equals("management only"))){

                            newsList.add(listNews.get(i));
                    }
                }
                setNewsAdapter();
            }
        });

    }
    private void setNewsAdapter() {
        newsAdapter = new NewsAdapter( newsList,this);
        newsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        newsRecyclerView.setAdapter(newsAdapter);
    }
}
