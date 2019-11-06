package com.paperflow.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paperflow.R;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

public class RecoverPasswodActivity extends Activity {

    private TextView mPhoneButton,mEmailButton;
    private EditText mEmailEditText,mNumberEditText;
    private LinearLayout mPhoneLayout;
    CountryCodePicker ccp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);


        mPhoneButton = (TextView)findViewById(R.id.phoneButton);
        mEmailButton = (TextView)findViewById(R.id.emailButton);

        mEmailEditText = (EditText)findViewById(R.id.emailEdittext);
        mNumberEditText = (EditText)findViewById(R.id.numberEdittext);

        mPhoneLayout = (LinearLayout)findViewById(R.id.phoneNumberLayout);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerPhoneNumberTextView(mNumberEditText);

        mPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mEmailEditText.getVisibility() == View.VISIBLE){
                    mEmailEditText.setVisibility(View.GONE);
                    mPhoneLayout.setVisibility(View.VISIBLE);

                    mPhoneButton.setBackground(getResources().getDrawable(R.drawable.blue_button_bg));
                    mEmailButton.setBackgroundColor(getResources().getColor(R.color.white));
                }

            }
        });
        mEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPhoneLayout.getVisibility() == View.VISIBLE){
                    mPhoneLayout.setVisibility(View.GONE);
                    mEmailEditText.setVisibility(View.VISIBLE);
                    mEmailButton.setBackground(getResources().getDrawable(R.drawable.blue_button_bg));
                    mPhoneButton.setBackgroundColor(getResources().getColor(R.color.white));
                }
            }
        });


    }

    public void onClickSignup(View v){

        Intent intent = new Intent(RecoverPasswodActivity.this,CreateProfileActivity.class);
        startActivity(intent);
        finish();

    }


}
