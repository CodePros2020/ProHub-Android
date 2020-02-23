package com.codepros.prohub.utils;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.codepros.prohub.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class Enable2FaActivity extends AppCompatActivity {
    private String email, phoneNumber;
    private String addUserUrl, addedUserId;
    private static final String API_KEY = "SKead4f3c9c43ba5de5e1bdaf32e7ed163";
    private static final String SECRET = "T7TKbzTrOEFcSewnm4NrzrbEJULwBexE";
    private static final String CHEAT_API = "CCb8fPiHfTdFp332cefjTuRjgMNprVOx";
    private String countryCode = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable2_fa);
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        email = phoneNumber+"@gmail.com";

        (findViewById(R.id.smsOptionLyt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //connectToAuthy();
                goNext();
            }
        });

    }


    private void connectToAuthy(){
        /** 1.create the create user url with Authy API **/
        addUserUrl = "https://api.authy.com/protected/json/users/new?user[email]=" + email
                + "&user[cellphone]=" + phoneNumber
                + "&user[country_code]=" + countryCode + "&api_key="+CHEAT_API;
        /** 2.Add the user to the Authy API **/
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, addUserUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                try {
                    /** get the returned id **/
                    Toast.makeText(getApplicationContext(), "Res: "+response.getString("success"), Toast.LENGTH_LONG).show();
                    JsonObject addedUser = gson.fromJson(response.getString("user"), JsonObject.class);
                    addedUserId = (addedUser.get("id")).getAsString();

                    /** 3.call the Authy API to send a code through sms **/
                    /** 4.call the Authy API to validate code provided by user [embedded in sendSecurityCodeTo method **/
                    sendSecurityCodeTo(addedUserId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR! ", "ee: " + error.getMessage());
            }
        });
    }

    private void sendSecurityCodeTo(final String userId) {
        String getCodeSMS = "https://api.authy.com/protected/json/sms/" + userId + "?api_key=" + CHEAT_API + "&force=true";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, getCodeSMS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        goNext();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR! ", error.getMessage());
                    }
                });

    }

    private void goNext(){
        Intent intent = new Intent(this, Validate2FaActivity.class);
        intent.putExtra("userId", addedUserId);
        this.startActivity(intent);
    }
}
