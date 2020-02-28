package com.codepros.prohub.utils;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.codepros.prohub.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class Enable2FaActivity extends AppCompatActivity {
    private String email, phoneNumber, message;
    private String countryCode = "1";
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable2_fa);
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        email = phoneNumber+"@gmail.com";

        (findViewById(R.id.smsOptionLyt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSecurityCodeTo();
            }
        });
    }

    private void sendSecurityCodeTo() {
        message = Math.round(new Random().nextFloat() * 10000)+"";
        String getCodeSMS = "https://rest.nexmo.com/sms/json?&api_key=fbaf81c7&api_secret=Bq6RG1reiGntHn9K&to=" + countryCode + phoneNumber + "&from=18194830665&text=" + message;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, getCodeSMS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONArray messages = response.getJSONArray("messages");
                            String status = messages.getJSONObject(0).getString("status");
                            goNext(status);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR! ", error.getMessage());
                    }
                });
        (AppSingleton.getInstance(this).getRequestQueue()).add(jsObjRequest);
    }

    private void goNext(String status){
        if(status.equals("0")) {
            Intent intent = new Intent(this, Validate2FaActivity.class);
            intent.putExtra("securityCode", message);
            this.startActivity(intent);
        }
        else{
            Toast.makeText(this, "Invalid phone number!", Toast.LENGTH_LONG).show();
        }
    }
}
