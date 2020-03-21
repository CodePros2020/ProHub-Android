package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepros.prohub.model.ChatMessage;
import com.codepros.prohub.model.News;
import com.codepros.prohub.utils.ChatAdapter;
import com.codepros.prohub.utils.FirebaseDataseHelper;
import com.codepros.prohub.utils.NewsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class NewsViewActivity extends AppCompatActivity {

    public RecyclerView newsRecyclerView;
    public NewsAdapter newsAdapter;
    public List<News> newsList=new ArrayList<News>();
    public FloatingActionButton btn_add;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_view);

        //
        btn_add=findViewById(R.id.btn_add_news);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent=new Intent(getBaseContext(),AddNewsActivity.class);
                startActivity(addIntent);
            }
        });

        newsRecyclerView =findViewById(R.id.newsRecyclerview);
        newsRecyclerView.setHasFixedSize(true);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Read News from database
        // read the list of News from Firebase

        new FirebaseDataseHelper().readNews(new FirebaseDataseHelper.NewsDataStatus() {
            @Override
            public void DataIsLoad(List<News> listNews, List<String> keys) {

                for(int i=0;i<listNews.size();i++){
                    if(!listNews.get(i).getHideFlag()){
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
