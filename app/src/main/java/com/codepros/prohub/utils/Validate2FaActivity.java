package com.codepros.prohub.utils;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.codepros.prohub.MainActivity;
import com.codepros.prohub.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Validate2FaActivity extends AppCompatActivity {

    private static final String API_KEY = "SKead4f3c9c43ba5de5e1bdaf32e7ed163";
    private static final String CHEAT_API = "CCb8fPiHfTdFp332cefjTuRjgMNprVOx";
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate2_fa);

        userId = getIntent().getStringExtra("userId");

        findViewById(R.id.validateBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** call authy api to validate code provided by the user**/
                validateSecurityCode(((EditText) findViewById(R.id.codeEdtx)).getText().toString(), userId,
                        ((EditText) findViewById(R.id.codeEdtx)), ((TextView) findViewById(R.id.errorTxt)));

            }
        });
    }

    private void validateSecurityCode(String code, final String userId, final EditText codeTxt, final TextView errorTxt){
        String codeValidationUrl="https://api.authy.com/protected/json/verify/"+code+"/"+userId+"?api_key="+CHEAT_API;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,codeValidationUrl,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if((response.getString("token")).equals("is valid")){
                                goNext();
                            }
                            else
                                codeTxt.setError("You typed a wrong code!");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        /*Toast.makeText(context,
                                "You typed a wrong code!",
                                Toast.LENGTH_LONG).show();*/
                        codeTxt.setText("");
                        errorTxt.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void goNext(){
        this.startActivity(new Intent(this, MainActivity.class));
        Toast.makeText(this, "successfully pased the 2FA!", Toast.LENGTH_LONG).show();
    }
}
