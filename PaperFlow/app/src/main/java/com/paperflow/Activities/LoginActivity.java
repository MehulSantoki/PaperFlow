package com.paperflow.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class LoginActivity extends Activity  implements OnApiResponse {

    private TextView mPhoneButton,mEmailButton,mErrorTextView;
    private EditText mEmailEditText,mPassEditText,mNumberEditText;
    private LinearLayout mPhoneLayout;
    CountryCodePicker ccp;
    private Button mLoginButton;
    private boolean isLoginWithOTP = true;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mPhoneButton = (TextView)findViewById(R.id.phoneButton);
        mEmailButton = (TextView)findViewById(R.id.emailButton);
        mErrorTextView = (TextView)findViewById(R.id.errorTextView);

        mEmailEditText = (EditText)findViewById(R.id.emailEdittext);
        mPassEditText = (EditText)findViewById(R.id.passEdittext);
        mNumberEditText = (EditText)findViewById(R.id.numberEdittext);

        mLoginButton = (Button)findViewById(R.id.loginButton);



        mPhoneLayout = (LinearLayout)findViewById(R.id.phoneNumberLayout);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        //ccp.registerPhoneNumberTextView(mNumberEditText);

        mPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isLoginWithOTP = true;
                if(mEmailEditText.getVisibility() == View.VISIBLE){
                    mEmailEditText.setVisibility(View.GONE);
                    mPhoneLayout.setVisibility(View.VISIBLE);
                    mPassEditText.setVisibility(View.GONE);

                    mPhoneButton.setBackground(getResources().getDrawable(R.drawable.blue_button_bg));
                    mEmailButton.setBackgroundColor(getResources().getColor(R.color.white));
                }

            }
        });
        mEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLoginWithOTP = false;
                if(mPhoneLayout.getVisibility() == View.VISIBLE){
                    mPassEditText.setVisibility(View.VISIBLE);
                    mPhoneLayout.setVisibility(View.GONE);
                    mEmailEditText.setVisibility(View.VISIBLE);
                    mEmailButton.setBackground(getResources().getDrawable(R.drawable.blue_button_bg));
                    mPhoneButton.setBackgroundColor(getResources().getColor(R.color.white));
                }
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPhoneLayout.setBackgroundResource(R.drawable.form_number_bg);
                mEmailEditText.setBackgroundResource(R.drawable.form_number_bg);
                mPassEditText.setBackgroundResource(R.drawable.form_number_bg);
                mErrorTextView.setText("");

                if(isLoginWithOTP){
                    if(mNumberEditText.getText().toString().trim().length() != 10){
                        mErrorTextView.setText("Please enter correct number.");
                        mPhoneLayout.setBackgroundResource(R.drawable.form_number_bg_red);
                    }else{
                        callLoginWithOtp();
                    }

                }else{
                    if(mEmailEditText.getText().toString().length() <= 0){
                        mErrorTextView.setText("Please enter email.");
                        mEmailEditText.setBackgroundResource(R.drawable.form_number_bg_red);
                    }else if(!isValidEmail(mEmailEditText.getText().toString())){
                        mErrorTextView.setText("Please enter correct email address.");
                        mEmailEditText.setBackgroundResource(R.drawable.form_number_bg_red);
                    }else if(mPassEditText.getText().toString().length() < 6){
                        mErrorTextView.setText("Minimum 6 characters required for password.");
                        mPassEditText.setBackgroundResource(R.drawable.form_number_bg_red);
                    }else{
                        callLoginWithUserName();
                    }

                }


            }
        });
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    private void callLoginWithOtp(){
        String URL = Utils.OTP_SIGNUP;
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("mobile","+"+ccp.getSelectedCountryCode()+""+mNumberEditText.getText().toString());
            jsonBody.put("type","LOGIN");

        }catch (Exception e){
            e.printStackTrace();
        }
        new Utils().callApis(LoginActivity.this,URL,jsonBody,LoginActivity.this,"Loading...");

    }
    private void callLoginWithUserName(){
        String URL = Utils.USER_LOGIN;
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("username",mEmailEditText.getText().toString());
            jsonBody.put("password",mPassEditText.getText().toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        new Utils().callApis(LoginActivity.this,URL,jsonBody,LoginActivity.this,"Loading...");

    }



    public void onClickSignup(View v){

        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
        finish();

    }


    @Override
    public void onSuccess(String method, String response) {
        if(method.equalsIgnoreCase(Utils.OTP_SIGNUP)){

            GsonBuilder gsonb = new GsonBuilder();
            Gson gson = gsonb.create();

            RegisterOtpResponse otpResponse = gson.fromJson(response,
                    RegisterOtpResponse.class);

            if(otpResponse.getStatus()){
                Intent intent = new Intent(LoginActivity.this,OtpActivity.class);
                intent.putExtra("number","+"+ccp.getSelectedCountryCode()+""+mNumberEditText.getText().toString());
                intent.putExtra(Utils.FROM_KEY,Utils.FROM_LOGIN);
                startActivity(intent);
            }

        }else if(method.equalsIgnoreCase(Utils.USER_LOGIN)){
            GsonBuilder gsonb = new GsonBuilder();
            Gson gson = gsonb.create();

            LoginResponse loginResponse = gson.fromJson(response,
                    LoginResponse.class);

            if(loginResponse.getStatus()){
                Pref.saveStringPref(LoginActivity.this,Pref.AUTH_TOKEN_KEY,loginResponse.getToken());
                Pref.saveStringPref(LoginActivity.this,Pref.USER_ID_KEY,loginResponse.getUser().getId());

                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }


        }

    }

    @Override
    public void onError(String response) {
        mErrorTextView.setText(response);
        mPhoneLayout.setBackgroundResource(R.drawable.form_number_bg_red);
        mEmailEditText.setBackgroundResource(R.drawable.form_number_bg_red);
        mPassEditText.setBackgroundResource(R.drawable.form_number_bg_red);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
