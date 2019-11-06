package com.paperflow.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paperflow.R;
import com.paperflow.responses.RegisterOtpResponse;
import com.paperflow.utils.OnApiResponse;
import com.paperflow.utils.Pref;
import com.paperflow.utils.Utils;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import org.json.JSONObject;
import org.opencv.text.Text;

import java.util.Calendar;

public class CreateProfileActivity extends AppCompatActivity implements OnApiResponse {

    private EditText mNameEditText,mEmailEditText,mPasswordEditText,mConfirmPassEditText;
    private TextView mDobTextView;

    private Button mCreateButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        mNameEditText = (EditText)findViewById(R.id.nameEdittext);
        mEmailEditText = (EditText)findViewById(R.id.emailEdittext);
        mPasswordEditText = (EditText)findViewById(R.id.passEdittext);
        mConfirmPassEditText = (EditText)findViewById(R.id.conPassEdittext);

        mDobTextView = (TextView)findViewById(R.id.bdTextView);

        mCreateButton = (Button)findViewById(R.id.createButton);

        mDobTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDobDialog();
            }
        });

        mCreateButton .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mNameEditText.getText().toString().trim().length() <= 0){
                    new Utils().showErrorDialog(CreateProfileActivity.this,"Please enter Name");
                }else if(mDobTextView.getText().toString().trim().length() <= 0){
                    new Utils().showErrorDialog(CreateProfileActivity.this,"Please enter Birth Date");
                }else if(mEmailEditText.getText().toString().trim().length() <= 0){
                    new Utils().showErrorDialog(CreateProfileActivity.this,"Please enter email address");
                }else if(!isValidEmail(mEmailEditText.getText().toString().trim())){
                    new Utils().showErrorDialog(CreateProfileActivity.this,"Please enter valid email address");
                }else if(mPasswordEditText.getText().toString().trim().length() <= 0){
                    new Utils().showErrorDialog(CreateProfileActivity.this,"Please enter Password");
                }else if(mConfirmPassEditText.getText().toString().trim().length() <= 0){
                    new Utils().showErrorDialog(CreateProfileActivity.this,"Please enter Confirm Password");
                }else if(!mPasswordEditText.getText().toString().equals(mConfirmPassEditText.getText().toString())){
                    new Utils().showErrorDialog(CreateProfileActivity.this,"Password does not match");
                }else{
                    callApi();
                }
            }
        });

    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    private void callApi(){

        String URL = Utils.UPDATE_PROFILE;
        JSONObject jsonBody = new JSONObject();


        try {
            jsonBody.put("mobile",Pref.loadStringPref(CreateProfileActivity.this,Pref.MOBILE_NO));
            jsonBody.put("name",mNameEditText.getText().toString());
            jsonBody.put("dob",mDobTextView.getText().toString());
            jsonBody.put("email",mEmailEditText.getText().toString());
            jsonBody.put("password",mPasswordEditText.getText().toString());



        }catch (Exception e){
            e.printStackTrace();
        }
        new Utils().callApis(CreateProfileActivity.this,URL,jsonBody,CreateProfileActivity.this,"Loading...");


    }

    private void showDobDialog(){

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateProfileActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        mDobTextView.setText((monthOfYear + 1) + "/" +dayOfMonth + "/"  + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    @Override
    public void onSuccess(String method, String response) {

        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();

        RegisterOtpResponse otpResponse = gson.fromJson(response,
                RegisterOtpResponse.class);

        if(otpResponse.getStatus()){

            Intent intent = new Intent(CreateProfileActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();

        }else{
            new Utils().showErrorDialog(CreateProfileActivity.this,otpResponse.getMessage());
        }
    }

    @Override
    public void onError(String response) {

    }

    public void onClickSignup(View v){
        Intent intent = new Intent(CreateProfileActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
