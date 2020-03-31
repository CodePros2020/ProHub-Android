package com.codepros.prohub.utils;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepros.prohub.DisplayStaffActivity;
import com.codepros.prohub.FormsActivity;
import com.codepros.prohub.PropertyHomeActivity;
import com.codepros.prohub.R;
import com.codepros.prohub.UpdateStaffActivity;
import com.codepros.prohub.model.Form;
import com.codepros.prohub.model.Staff;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URL;
import java.util.List;

public class FormsAdapter extends RecyclerView.Adapter<FormsAdapter.ViewHolder> {
    // PRIVATE FIELDS
    private List<Form> listItems;
    private List<String> listItemKeys;
    private Context context;
    private String formTitle, formContentUrl;
    private String  name, imageUrl, myRole;

    String fileNameToUpdate = "";

    // firebase database objects
    private DatabaseReference myFormRef;

    // CONSTRUCTOR
    public FormsAdapter(List<Form> formsList, List<String> keys, String myRole, Context context){
        // set listitems
        this.listItems = formsList;
        this.listItemKeys = keys;

        //Shared Preferences
        myFormRef = FirebaseDatabase.getInstance().getReference("form");

        this.myRole = myRole;
        this.context=context;
    }

    @NonNull
    @Override
    public FormsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // set layout view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_forms,parent,false);

        return new FormsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FormsAdapter.ViewHolder holder, final int position) {

        final Form form = listItems.get(position);

        formTitle = form.getFormTitle();
        formContentUrl = form.getContentUrl();

        holder.tvFormTitle.setText(formTitle);

        // open file on click
        holder.relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                // get file uri
                Uri uri = Uri.parse(form.getContentUrl());

                // get extension from formTitle
                String extension = form.getFormTitle().substring(form.getFormTitle().lastIndexOf(".") + 1).trim();

                //
                if(extension.equals("pdf")) {
                    // set intent to view the file in action view
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    try {
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "No Application avaiable to view the pdf", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // set intent to view the file in action view
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "image/*");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    try {
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "No Application avaiable to view the image", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        holder.tvMenuOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myRole.equals("Tenant")){
                    Toast.makeText(context,"Sorry! You do not have permission to edit document.",Toast.LENGTH_LONG).show();
                }else{

                    //Display options menu
                    PopupMenu popupMenu=new PopupMenu(context,holder.tvMenuOptions);
                    popupMenu.inflate(R.menu.form_options_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getItemId()==R.id.menu_formitem_update){
//                                Uri uri = Uri.parse(form.getContentUrl());

                                // filename and extension
//                                final String fullFileName = getFileNameFromUri(context, uri);
                                final String fullFileName = form.getFormTitle();

                                String fileNameBody = fullFileName.substring(0, fullFileName.lastIndexOf("."));
                                final String extension = fullFileName.substring(fullFileName.lastIndexOf(".") + 1);

                                // show dialog box for filename selection
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                                alertDialog.setTitle("Edit Form Title");
                                alertDialog.setMessage("Enter Form Title");

                                // hard-coded dialog layout
                                final EditText input = new EditText(context);
                                input.setText(fileNameBody);
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT);
                                input.setLayoutParams(lp);
                                alertDialog.setView(input);

                                alertDialog.setPositiveButton("Edit",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                                fileNameToUpdate = input.getText().toString();

                                                if(fileNameToUpdate.isEmpty() || fileNameToUpdate == null) {
                                                    fileNameToUpdate = fullFileName;
                                                } else {
                                                    fileNameToUpdate += "." + extension;
                                                }

                                                // get ready to create

                                                Form formToUpdate = new Form();
                                                formToUpdate.setFormId(form.getFormId());
                                                formToUpdate.setPropId(form.getPropId());
                                                formToUpdate.setContentUrl(form.getContentUrl());
                                                formToUpdate.setFormTitle(fileNameToUpdate);

                                                myFormRef.child(form.getFormId()).setValue(formToUpdate);

                                                // notification
                                                Toast.makeText(context, "Form is updated!", Toast.LENGTH_LONG).show();

                                            }
                                        })
                                        .setNegativeButton("Cancel",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                });

                                alertDialog.show();



//                                Intent newIntent=new Intent(context, UpdateStaffActivity.class);
//
//                                // bundle and put extras
//                                Bundle b = new Bundle();
//
//                                b.putString("formId", form.getFormId()); // for update
//                                b.putString("formTitle", form.getFormTitle()); // for update
//                                b.putString("contentUrl", form.getContentUrl()); // for update
//                                b.putString("propId", form.getPropId()); // for update
//                                newIntent.putExtras(b);
//
//                                context.startActivity(newIntent);
                            }
                            return false;

                        }
                    });
                    popupMenu.show();
                }

            }
        });

    }
//    // get file name from uri
//    String getFileNameFromUri(Context context, Uri uri) {
//        // Get the Uri, file, path of the selected file
//        String uriString = uri.toString();
//        File myFile = new File(uriString);
//        String path = myFile.getAbsolutePath();
//
//        String displayName = null;
//
//        if (uriString.startsWith("content://")) {
//            Cursor cursor = null;
//            try {
//                cursor = context.getContentResolver().query(uri, null, null, null, null);
//                if (cursor != null && cursor.moveToFirst()) {
//                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                }
//            } finally {
//                cursor.close();
//            }
//            // local file
//        } else if (uriString.startsWith("file://")) {
//            displayName = myFile.getName();
//        }
//
//        return displayName;
//    }


    @Override
    public int getItemCount() {
        return listItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvMenuOptions;
        TextView tvFormTitle;
        RelativeLayout relativeLayout;

        // constructor
        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMenuOptions=itemView.findViewById(R.id.tvOption);
            tvFormTitle = itemView.findViewById(R.id.tvFormTitle);

            relativeLayout=itemView.findViewById(R.id.layout_FormsRelative);
        }
    }

}
