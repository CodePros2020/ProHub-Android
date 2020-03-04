package com.codepros.prohub.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codepros.prohub.R;


import java.util.ArrayList;
import java.util.List;

public class PropertyAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Property> properties;

    public PropertyAdapter(Context mContext, List<Property> properties)
    {
        this.mContext = mContext;
        this.properties = properties;
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
        return this.properties.get(position).getPropID();
    }

    // set the view for each item in item list
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Property property = this.properties.get(position);

        if(convertView == null){
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.linearlayout_property, null);
        }

        final TextView propertyNameTextView = convertView.findViewById(R.id.txtVPropertyName);
        final TextView propertyAddressTextView = convertView.findViewById(R.id.txtVPropertyAddress);
        final Button propertyDetailButton = convertView.findViewById(R.id.btnPropertyDetail);

        propertyNameTextView.setText(property.getName());
        propertyAddressTextView.setText(property.getAddress());

        propertyDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: need link to the property Home page with property ID
                Toast.makeText(mContext, "redirect to peoperty: "+property.getPropID(), Toast.LENGTH_LONG).show();
            }
        });

        return convertView;
    }
}
