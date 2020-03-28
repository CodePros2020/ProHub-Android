package com.codepros.prohub.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepros.prohub.MainActivity;
import com.codepros.prohub.PropertyHomeActivity;
import com.codepros.prohub.R;
import com.codepros.prohub.model.Property;


import java.util.ArrayList;
import java.util.List;

public class PropertyAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Property> properties;
    private final List<String> keys;

    public PropertyAdapter(Context mContext, List<Property> properties, List<String> keys)
    {
        this.mContext = mContext;
        this.properties = properties;
        this.keys = keys;
    }

    @Override
    public int getCount() {
        return this.properties.size();
    }

    @Override
    public Object getItem(int position) {
        return this.properties.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // set the view for each item in item list
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Property property = this.properties.get(position);

       if(convertView == null){
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.linearlayout_property, null);
        }

        final TextView propertyNameTextView = convertView.findViewById(R.id.txtVPropertyName);
        final TextView propertyAddressTextView = convertView.findViewById(R.id.txtVPropertyAddress);
        final TextView propertyPostalTextView = convertView.findViewById(R.id.tvPostalCode);
        //final Button propertyDetailButton = convertView.findViewById(R.id.btnPropertyDetail);
        String address=property.getStreetLine1()+", "+property.getCity()+", "+property.getProvince()+", ";
        propertyNameTextView.setText(property.getName());
        propertyAddressTextView.setText(address);
        if(property.getPostalCode() != null){
            propertyPostalTextView.setText(property.getPostalCode().toUpperCase());
        }

//        propertyDetailButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle b=new Bundle();
//                Toast.makeText(mContext, "redirect to selected peoperty", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(mContext, PropertyHomeActivity.class);
//                intent.putExtra("propId", keys.get(position));
//                intent.putExtra("propName", property.getName());
//
//                mContext.startActivity(intent);
//            }
//        });

        return convertView;
    }

}
