package com.codepros.prohub.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepros.prohub.DisplayNewsActivity;
import com.codepros.prohub.EditNewsActivity;
import com.codepros.prohub.R;
import com.codepros.prohub.model.News;

import java.net.URI;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder>{
    private List<News> listItems;
    private Context context;
    public String title;
    public String des;
    public String date;
    public Uri imageUri;
    public NewsAdapter(List<News> newsList,Context context){
        this.listItems=newsList;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_news,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final News news=listItems.get(position);
         title=news.getNewsTitle();
        des=news.getShortDes(news.getContent());
        date=news.getCreateTime();
        //Log.d("Image Url in Adapter", "onBindViewHolder: Before "+news.getImageUrl());
        try{
            imageUri=news.getImageUri(news.getImageUrl());
            holder.imgNews.setImageURI(imageUri);
        }
        catch(NullPointerException e) {
            holder.imgNews.setImageResource(R.drawable.noimg);
        }

        //Log.d("Image Url in Adapter", "onBindViewHolder: "+imageUri);
        holder.tvTitle.setText(title);
        holder.tvDescription.setText(des);
        holder.tvDate.setText(date);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Bundle b = new Bundle();
                Intent newIntent=new Intent(context, DisplayNewsActivity.class);
                b.putString("title",title);
                b.putString("description",news.getContent());
                b.putString("date",date);
                b.putString("imgUrl",imageUri.toString());
                newIntent.putExtras(b);
                context.startActivity(newIntent);
            }
        });
        holder.tvMenuOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Display options menu
                PopupMenu popupMenu=new PopupMenu(context,holder.tvMenuOptions);
                popupMenu.inflate(R.menu.options_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId()==R.id.menu_item_edit){
                            Intent newIntent = new Intent(context, EditNewsActivity.class);
                            context.startActivity(newIntent);
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvTitle;
        public TextView tvDescription;
        public TextView tvMenuOptions;
        public TextView tvDate;
        public ImageView imgNews;
        public RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle= itemView.findViewById(R.id.tvHeadingNews);
            tvDescription=itemView.findViewById(R.id.tvBodyNews);
            tvMenuOptions=itemView.findViewById(R.id.tvOptionDigit);
            tvDate=itemView.findViewById(R.id.tvDate);
            imgNews=itemView.findViewById(R.id.news_img);
            relativeLayout=itemView.findViewById(R.id.layout_Relative);
        }
    }
}
