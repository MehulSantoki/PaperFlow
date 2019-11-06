package com.paperflow.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.paperflow.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.text.Text;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static String BASE_URL = "https://vwz05o7ny7.execute-api.ap-south-1.amazonaws.com/dev/";
    public static String BASE_URL_AUTH = "https://e29fmiu4kg.execute-api.ap-south-1.amazonaws.com/dev/";

    public static String OTP_SIGNUP = BASE_URL_AUTH+"user/otp";
    public static String CREATE_USER = BASE_URL_AUTH+"user/create";

    public static String VERIFY_OTP = BASE_URL_AUTH+"user/verifyOtp";
    public static String UPDATE_PROFILE = BASE_URL_AUTH+"user/update";
    public static String LOGIN_WITH_OTP = BASE_URL_AUTH+"user/loginWithOtp";
    public static String USER_LOGIN = BASE_URL_AUTH+"user/login";


    public static String FROM_KEY = "from_activity";
    public static String FROM_SIGNUP = "from_signup";
    public static String FROM_LOGIN = "from_login";
    public static String FROM_RESET_PASS= "from_reset_pass";

    public static String GET_USER_BOOKS = BASE_URL+"shelf/get";
    public static String GET_BOOK_DETAIL = BASE_URL+"book/get";
    public static String ADD_BOOK_SELF = BASE_URL+"shelf/add";


    Dialog mDialog;

    public void callApis(final Context context, final String URL, final JSONObject jsonBody,
                         final OnApiResponse onApiResponse,String dialogMsg){

        mDialog = new Dialog(context);
        // no tile for the dialog
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mDialog.setContentView(R.layout.custom_progress_dialog);
        TextView message = (TextView)mDialog.findViewById(R.id.msgTextView);
        message.setText(dialogMsg);
        mDialog.setCancelable(false);
        mDialog.show();


        RequestQueue requestQueue = Volley.newRequestQueue(context);

        final String mRequestBody = jsonBody.toString();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("LOG_VOLLEY_RESPONEe", response);

                onApiResponse.onSuccess(URL,response);
                mDialog.dismiss();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LOG_VOLLEY_ERROR", error.toString());
                mDialog.dismiss();
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data

                        JSONObject obj = new JSONObject(res);
                        Log.e("Error OBJ",""+obj.toString());
                        if(obj.has("error")){
                            //showErrorDialog(context,obj.getString("error"));
                            onApiResponse.onError(obj.getString("error"));

                        }else if(obj.has("message")){
                           // showErrorDialog(context,obj.getString("message"));
                            onApiResponse.onError(obj.getString("message"));
                        }


                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
                mDialog.dismiss();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return mRequestBody == null ? null : mRequestBody
                            .getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog
                            .wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                    mRequestBody, "utf-8");
                    return null;
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
//                if(Pref.loadStringPref(context,Pref.AUTH_TOKEN_KEY) != null) {
//                    params.put("Authorization", "Bearer "+Pref.loadStringPref(context,Pref.AUTH_TOKEN_KEY));
//                }
                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(
                    NetworkResponse response) {
                String responseString = "";
                if (response != null) {

                    responseString = String.valueOf(response.statusCode);
                    Log.e("REsponse"," == "+responseString);

                }
                return super.parseNetworkResponse(response);
                // return Response.success(responseString,
                // HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        requestQueue.add(stringRequest);

    }

    public void showErrorDialog(Context context,String message){

        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setMessage(message);
        adb.setPositiveButton("OK",null);
        adb.show();
    }

}
