package com.paperflow.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import org.json.JSONObject;
import org.opencv.text.Text;

public class OtpActivity extends Activity implements OnApiResponse {

    private EditText editText1,editText2,editText3,editText4,editText5,editText6;
    private TextView mEnterTextView,mErrorTextView;
    private String phoneNumber;
    private Button mVerifyButton;
    private String fromActivity;
    private LinearLayout mOtpLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        phoneNumber = getIntent().getStringExtra("number");
        fromActivity = getIntent().getStringExtra(Utils.FROM_KEY);

        mOtpLayout = (LinearLayout)findViewById(R.id.otpLayout);

        editText1 = (EditText)findViewById(R.id.num1Edittext);
        editText2 = (EditText)findViewById(R.id.num2Edittext);
        editText3 = (EditText)findViewById(R.id.num3Edittext);
        editText4 = (EditText)findViewById(R.id.num4Edittext);
        editText5 = (EditText)findViewById(R.id.num5Edittext);
        editText6 = (EditText)findViewById(R.id.num6Edittext);

        mEnterTextView= (TextView)findViewById(R.id.enterTextView);
        mErrorTextView = (TextView)findViewById(R.id.errorTextView);

        mVerifyButton = (Button)findViewById(R.id.verifyButton);

        editText1.addTextChangedListener(new GenericTextWatcher(editText1));
        editText2.addTextChangedListener(new GenericTextWatcher(editText2));
        editText3.addTextChangedListener(new GenericTextWatcher(editText3));
        editText4.addTextChangedListener(new GenericTextWatcher(editText4));
        editText5.addTextChangedListener(new GenericTextWatcher(editText5));
        editText6.addTextChangedListener(new GenericTextWatcher(editText6));

        mEnterTextView.setText("Please enter 6 digit OTP sent on \n"+phoneNumber);

        mVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String otp="";
                mErrorTextView.setText("");
                for(int i=0;i<mOtpLayout.getChildCount();i++){
                    otp = otp+""+((EditText)mOtpLayout.getChildAt(i)).getText().toString();
                    mOtpLayout.getChildAt(i).setBackgroundResource(R.drawable.form_number_bg);
                    if(((EditText)mOtpLayout.getChildAt(i)).getText().toString().length() <= 0){

                        mOtpLayout.getChildAt(i).setBackgroundResource(R.drawable.form_number_bg_red);

                    }
                }

                if(otp.length() < 6){
                    mErrorTextView.setText("Please enter correct OTP.");
                }else{
                    if(fromActivity.equalsIgnoreCase(Utils.FROM_LOGIN)){
                        callLoginApi();
                    }else{
                        callApi();
                    }
                }


            }
        });

    }

    private void callLoginApi(){

        String URL = Utils.LOGIN_WITH_OTP;
        JSONObject jsonBody = new JSONObject();
        Log.e("Mobile",""+phoneNumber);


        try {
            jsonBody.put("mobile",phoneNumber);
            jsonBody.put("otp",editText1.getText().toString()+""+editText2.getText().toString()+""+
                    editText3.getText().toString()+""+editText4.getText().toString()+""+editText5.getText().toString()+""+
                    editText6.getText().toString());

        }catch (Exception e){
            e.printStackTrace();
        }
        new Utils().callApis(OtpActivity.this,URL,jsonBody,OtpActivity.this,"Loading...");


    }
    private void callApi(){

        String URL = Utils.CREATE_USER;
        JSONObject jsonBody = new JSONObject();


        try {
            jsonBody.put("mobile",phoneNumber);
            jsonBody.put("otp",editText1.getText().toString()+""+editText2.getText().toString()+""+
            editText3.getText().toString()+""+editText4.getText().toString()+""+editText5.getText().toString()+""+
            editText6.getText().toString());

        }catch (Exception e){
            e.printStackTrace();
        }
        new Utils().callApis(OtpActivity.this,URL,jsonBody,OtpActivity.this,"Loading...");


    }


    @Override
    public void onSuccess(String method, String response) {

        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();

        RegisterOtpResponse otpResponse = gson.fromJson(response,
                RegisterOtpResponse.class);

        if(otpResponse.getStatus()){
            Pref.saveStringPref(OtpActivity.this,Pref.MOBILE_NO,phoneNumber);
            if(fromActivity.equalsIgnoreCase(Utils.FROM_LOGIN)){
                LoginResponse loginResponse = gson.fromJson(response,
                        LoginResponse.class);
                Pref.saveStringPref(OtpActivity.this,Pref.AUTH_TOKEN_KEY,loginResponse.getToken());
                Pref.saveStringPref(OtpActivity.this,Pref.USER_ID_KEY,loginResponse.getUser().getId());
                Intent intent = new Intent(OtpActivity.this,HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }else if(fromActivity.equalsIgnoreCase(Utils.FROM_SIGNUP)){
                Intent intent = new Intent(OtpActivity.this,CreateProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }else if(fromActivity.equalsIgnoreCase(Utils.FROM_RESET_PASS)){

            }


        }else {
            new Utils().showErrorDialog(OtpActivity.this,otpResponse.getMessage());
        }


    }

    @Override
    public void onError(String response) {

        for(int i=0;i<mOtpLayout.getChildCount();i++){

            mOtpLayout.getChildAt(i).setBackgroundResource(R.drawable.form_number_bg_red);
        }

        mErrorTextView.setText(response);
    }


    public class GenericTextWatcher implements TextWatcher {
        private View view;

        public GenericTextWatcher(View view) {
            this.view = view;

        }

        @Override
        public void afterTextChanged(Editable s) {
            boolean allOtherFilled = false;
            EditText nextEdit = null;
            EditText previousEdit = null;
            switch (view.getId()) {
                case R.id.num1Edittext:
                    allOtherFilled = editText2.getText().length() == 1
                            && editText3.getText().length() == 1
                            && editText4.getText().length() == 1
                            && editText5.getText().length() == 1
                            && editText6.getText().length() == 1;
                    nextEdit = editText2;
                    break;
                case R.id.num2Edittext:
                    allOtherFilled = editText1.getText().length() == 1
                            && editText3.getText().length() == 1
                            && editText4.getText().length() == 1
                            && editText5.getText().length() == 1
                            && editText6.getText().length() == 1;
                    nextEdit = editText3;
                    previousEdit = editText1;
                    break;
                case R.id.num3Edittext:
                    allOtherFilled = editText1.getText().length() == 1
                            && editText2.getText().length() == 1
                            && editText4.getText().length() == 1
                            && editText5.getText().length() == 1
                            && editText6.getText().length() == 1;
                    nextEdit = editText4;
                    previousEdit = editText2;
                    break;
                case R.id.num4Edittext:
                    allOtherFilled = editText1.getText().length() == 1
                            && editText2.getText().length() == 1
                            && editText3.getText().length() == 1
                            && editText5.getText().length() == 1
                            && editText6.getText().length() == 1;
                    nextEdit = editText5;
                    previousEdit = editText3;
                    break;
                case R.id.num5Edittext:
                    allOtherFilled = editText1.getText().length() == 1
                            && editText2.getText().length() == 1
                            && editText3.getText().length() == 1
                            && editText4.getText().length() == 1
                            && editText6.getText().length() == 1;
                    nextEdit = editText6;
                    previousEdit = editText4;
                    break;
                case R.id.num6Edittext:
                    allOtherFilled = editText1.getText().length() == 1
                            && editText2.getText().length() == 1
                            && editText3.getText().length() == 1
                            && editText4.getText().length() == 1
                            && editText5.getText().length() == 1;
                    previousEdit = editText5;
                    break;
            }

            if (s.length() == 1) {
                if (allOtherFilled) {
//                //if next 2 edit texts are filled , enable the pay button
//                enableDisableButton(continueButton, true);
//                KeyboardUtils.hideKeyboard(LoginActivity.this, (EditText) view);
                }
            } else if (s.length() > 1) {
                if (allOtherFilled) {
                    //if all next edit texts are filled , enable the pay button
//                enableDisableButton(continueButton, true);
//                KeyboardUtils.hideKeyboard(LoginActivity.this, (EditText) view);

                } else if (nextEdit != null) {
                    if (nextEdit.getText().length() == 0) {
                        //if next edit is not filled, move to next edit and set the second digit
                        moveToNextEdit(nextEdit, (EditText) view);
                    } else {
                        //if any other edit is not filled, stay in current edit
//                    enableDisableButton(continueButton, false);
                        stayOnCurrentEdit((EditText) view);
                    }
                }
            } else if (s.length() < 1) {
                if (null != previousEdit)
                    moveToPreviousEdit(previousEdit);
//            enableDisableButton(continueButton, false);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        private void stayOnCurrentEdit(EditText editText) {
            editText.setText(editText.getText().toString().substring(0, 1));
            editText.setSelection(editText.getText().length());
        }

        private void moveToPreviousEdit(EditText editText) {
            editText.setSelection(editText.getText().length());
            editText.requestFocus();
        }

        private void moveToNextEdit(EditText editText2, EditText editText1) {
            editText2.setText(editText1.getText().toString().substring(1, 2));
            editText2.requestFocus();
            editText2.setSelection(editText2.getText().length());
            editText1.setText(editText1.getText().toString().substring(0, 1));
        }
    }
}
