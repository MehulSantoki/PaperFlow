package com.paperflow.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paperflow.R;
import com.paperflow.responses.RegisterOtpResponse;
import com.paperflow.utils.OnApiResponse;
import com.paperflow.utils.Utils;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import org.json.JSONObject;

public class RegisterActivity  extends AppCompatActivity  implements OnApiResponse {

    CountryCodePicker ccp;

    private EditText mPhoneEditText;
    private TextView mLoginTextView,mErrorTextView;
    private Button mLoginButton;
    private LinearLayout mNumberLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ccp = (CountryCodePicker) findViewById(R.id.ccp);
//        ccp.registerPhoneNumberTextView(mNumberEditText);

        mPhoneEditText = (EditText)findViewById(R.id.phoneEdittext);

        mLoginTextView = (TextView)findViewById(R.id.loginTextView);
        mErrorTextView = (TextView)findViewById(R.id.errorTextView);

        mNumberLayout = (LinearLayout)findViewById(R.id.phoneNumberLayout);

        mLoginButton = (Button)findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mErrorTextView.setText("");
                mNumberLayout.setBackgroundResource(R.drawable.form_number_bg);

                if(mPhoneEditText.getText().toString().trim().length() == 10) {
                    callApi();
                }else{
                    mNumberLayout.setBackgroundResource(R.drawable.form_number_bg_red);
                    mErrorTextView.setText("Please enter number.");
                }
            }
        });

        mLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }

    private void callApi(){

        String URL = Utils.OTP_SIGNUP;
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("mobile","+"+ccp.getSelectedCountryCode()+""+mPhoneEditText.getText().toString());

        }catch (Exception e){
            e.printStackTrace();
        }
        new Utils().callApis(RegisterActivity.this,URL,jsonBody,RegisterActivity.this,"Loading...");


    }


    @Override
    public void onSuccess(String method, String response) {

        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();

        RegisterOtpResponse otpResponse = gson.fromJson(response,
                RegisterOtpResponse.class);

        if(otpResponse.getStatus()){
            mNumberLayout.setBackgroundResource(R.drawable.form_number_bg);
            Intent intent = new Intent(RegisterActivity.this,OtpActivity.class);
            intent.putExtra("number","+"+ccp.getSelectedCountryCode()+""+mPhoneEditText.getText().toString());
            intent.putExtra(Utils.FROM_KEY,Utils.FROM_SIGNUP);
            startActivity(intent);
        }else{
            mErrorTextView.setText(otpResponse.getMessage());
            mNumberLayout.setBackgroundResource(R.drawable.form_number_bg_red);
        }

    }

    @Override
    public void onError(String response) {
        mErrorTextView.setText(response);
        mNumberLayout.setBackgroundResource(R.drawable.form_number_bg_red);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
