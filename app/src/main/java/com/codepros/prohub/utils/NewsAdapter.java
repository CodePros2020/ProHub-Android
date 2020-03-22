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
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder>{
    private List<News> listItems;
    private List<String> listItemKeys;
    private Context context;
    private String title, des, date, imageUrl, myRole;
    public NewsAdapter(List<News> newsList,List<String> listItemKeys,String myRole,Context context){
        this.listItems=newsList;
        this.listItemKeys = listItemKeys;
        this.myRole = myRole;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_news,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final News news=listItems.get(position);
        title=news.getNewsTitle();
        des=news.getShortDes(news.getContent());
        date=news.getCreateTime();
        imageUrl=news.getImageUrl();

        //Log.d("Image Url in Adapter", "onBindViewHolder: "+imageUri);
        holder.tvTitle.setText(title);
        holder.tvDescription.setText(des);
        holder.tvDate.setText(date);
        Picasso.get().load(imageUrl).placeholder(R.drawable.ic_menu_report_image).into(holder.imgNews);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Bundle b = new Bundle();
                Intent newIntent=new Intent(context, DisplayNewsActivity.class);
                b.putString("title",news.getNewsTitle());
                b.putString("description",news.getContent());
                b.putString("date",news.getCreateTime());
                b.putString("imgUrl",news.getImageUrl());
                newIntent.putExtras(b);
                context.startActivity(newIntent);
            }
        });
        holder.tvMenuOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myRole.equals("Tenant")){
                    Toast.makeText(context,"Sorry! You do not have permission to edit news.",Toast.LENGTH_LONG).show();
                }else{
                    //Display options menu
                    PopupMenu popupMenu=new PopupMenu(context,holder.tvMenuOptions);
                    popupMenu.inflate(R.menu.options_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getItemId()==R.id.menu_item_edit){
                                Intent newIntent = new Intent(context, EditNewsActivity.class);
                                newIntent.putExtra("newsKey", listItemKeys.get(position));
                                context.startActivity(newIntent);
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle;
        TextView tvDescription;
        TextView tvMenuOptions;
        TextView tvDate;
        ImageView imgNews;
        RelativeLayout relativeLayout;

        ViewHolder(@NonNull View itemView) {
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
