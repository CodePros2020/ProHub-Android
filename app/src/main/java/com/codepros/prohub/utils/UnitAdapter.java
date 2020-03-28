package com.codepros.prohub.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.codepros.prohub.DisplayStaffActivity;
import com.codepros.prohub.DisplayUnitActivity;
import com.codepros.prohub.R;
import com.codepros.prohub.UpdateStaffActivity;
import com.codepros.prohub.UpdateUnitActivity;
import com.codepros.prohub.model.Staff;
import com.codepros.prohub.model.Unit;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UnitAdapter extends RecyclerView.Adapter<UnitAdapter.ViewHolder> {
    private List<Unit> listItems;
    private List<String> listItemKeys;
    private Context context;
    private String  tenantNum, unitName, myRole,unitId;
    public UnitAdapter(List<Unit> unitList,List<String> keys,String myRole,Context context){
        this.listItems=unitList;
        this.myRole = myRole;
        this.context=context;
        this.listItemKeys = keys;
    }

    @NonNull
    @Override
    public UnitAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_unit,parent,false);
        return new UnitAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UnitAdapter.ViewHolder holder, final int position) {

        final Unit unit=listItems.get(position);
        tenantNum=unit.getTenantId();
        unitName=unit.getUnitName();
        unitId=unit.getUnitId();

        holder.tvName.setText(unitName);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                Intent newIntent=new Intent(context, DisplayUnitActivity.class);
                newIntent.putExtra("unitName",unitName);
                newIntent.putExtra("unitId",unitId);
                newIntent.putExtra("tenantNum",tenantNum);
                context.startActivity(newIntent);
            }
        });
        holder.tvMenuOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myRole.equals("Tenant")){
                    Toast.makeText(context,"Sorry! You do not have permission to edit unit.",Toast.LENGTH_LONG).show();
                }else{
                //Display options menu
                PopupMenu popupMenu=new PopupMenu(context,holder.tvMenuOptions);
                popupMenu.inflate(R.menu.staff_options_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId()==R.id.menu_item_update){
                            Intent newIntent = new Intent(context, UpdateUnitActivity.class);
                            newIntent.putExtra("unitName",unitName);
                            newIntent.putExtra("unitId",unitId);
                            newIntent.putExtra("tenantNum",tenantNum);
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
        TextView tvMenuOptions;
        TextView tvName;
        RelativeLayout relativeLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMenuOptions=itemView.findViewById(R.id.tvOption);
            tvName=itemView.findViewById(R.id.tvName);
            relativeLayout=itemView.findViewById(R.id.layout_Relative);
        }
    }
}
