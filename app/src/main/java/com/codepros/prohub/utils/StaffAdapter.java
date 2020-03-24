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
import com.codepros.prohub.DisplayStaffActivity;
import com.codepros.prohub.EditNewsActivity;
import com.codepros.prohub.R;
import com.codepros.prohub.UpdateStaffActivity;
import com.codepros.prohub.model.News;
import com.codepros.prohub.model.Staff;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.List;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.ViewHolder>{
    private List<Staff> listItems;
    private List<String> listItemKeys;
    private Context context;
    private String  name, imageUrl, myRole;
    public StaffAdapter(List<Staff> newsList,List<String> keys,String myRole,Context context){
        this.listItems=newsList;
        this.myRole = myRole;
        this.context=context;
        this.listItemKeys = keys;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_staff,parent,false);
        return new ViewHolder(view);
    }
    public void deleteItem(int position){
       // getSnapshots.getSnapshot(position).getReference().detele();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Staff staff=listItems.get(position);
        name=staff.getName();
        imageUrl=staff.getImgUrl();
        holder.tvName.setText(name);
        Picasso.get().load(imageUrl).placeholder(R.drawable.ic_menu_report_image).into(holder.imgStaff);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Bundle b = new Bundle();
                Intent newIntent=new Intent(context, DisplayStaffActivity.class);
                b.putString("name",staff.getName());
                b.putString("imgUrl",staff.getImgUrl());
                newIntent.putExtras(b);
                context.startActivity(newIntent);
            }
        });
        holder.tvMenuOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(myRole.equals("Tenant")){
//                    Toast.makeText(context,"Sorry! You do not have permission to edit news.",Toast.LENGTH_LONG).show();
//                }else{
                    //Display options menu
                    PopupMenu popupMenu=new PopupMenu(context,holder.tvMenuOptions);
                    popupMenu.inflate(R.menu.staff_options_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getItemId()==R.id.menu_item_update){
                                Intent newIntent = new Intent(context, UpdateStaffActivity.class);
                                context.startActivity(newIntent);
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }

//            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvMenuOptions;
        TextView tvName;
        ImageView imgStaff;
        RelativeLayout relativeLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMenuOptions=itemView.findViewById(R.id.tvOption);
            tvName=itemView.findViewById(R.id.tvName);
            imgStaff=itemView.findViewById(R.id.staffImg);
            relativeLayout=itemView.findViewById(R.id.layout_Relative);
        }
    }
}
