package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class DisplayNewsActivity extends AppCompatActivity {

    private static final String TAG = "Image Url";
    String title, date, description, imgUrl;
    TextView tvDes,tvTitle,tvDate;
    ImageView imgView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_news);

        //
        Intent displayIntent = getIntent();
        Bundle bundle=displayIntent.getExtras();
        //
         title = bundle.getString("title");
         description = bundle.getString("description");
         date =bundle.getString("date");
         imgUrl =  bundle.getString("imgUrl");

        Uri uri=Uri.parse(imgUrl);
        String imgUrl= "\"" +uri + "\"";
        Log.d(TAG, "Image Url: "+imgUrl);
         //
        tvTitle=findViewById(R.id.tvDisplay_title);
        tvDes=findViewById(R.id.tvDisplay_description);
        tvDate=findViewById(R.id.tvDisplay_date);
        imgView=findViewById(R.id.ivNews);

        tvTitle.setText(title);
        tvDes.setText(description);
        tvDate.setText(date);

        imgView.setImageURI(uri);


    }
}
