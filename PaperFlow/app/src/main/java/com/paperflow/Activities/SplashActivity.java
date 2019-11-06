package com.paperflow.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paperflow.R;
import com.paperflow.responses.LoginResponse;
import com.paperflow.responses.RegisterOtpResponse;
import com.paperflow.utils.OnApiResponse;
import com.paperflow.utils.Pref;
import com.paperflow.utils.Utils;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import org.json.JSONObject;

public class SplashActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);


        if(Pref.loadStringPref(SplashActivity.this,Pref.USER_ID_KEY) != null
                && Pref.loadStringPref(SplashActivity.this,Pref.AUTH_TOKEN_KEY )!= null) {

            Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }else{
            Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }

    }



}
