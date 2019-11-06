package com.paperflow.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyLog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.Result;
import com.paperflow.R;
import com.paperflow.adapters.BooksListAdapter;
import com.paperflow.fragments.BooksListFragment;
import com.paperflow.responses.BookDetailResponse;
import com.paperflow.responses.GetUserBooksResponse;
import com.paperflow.utils.OnApiResponse;
import com.paperflow.utils.Pref;
import com.paperflow.utils.Utils;

import org.json.JSONObject;
import org.opencv.text.Text;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.support.constraint.Constraints.TAG;

public class QRBookScannerActivity extends Activity implements ZXingScannerView.ResultHandler,OnApiResponse {
    private ZXingScannerView mScannerView;
    private LinearLayout mScannedDialogLayout;
    private TextView mRetryTextView,mDoneTextView,mBookTitleTextView,mBookDescTextView;
    private ImageView mBookImageView;
    private String mQRCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_book_scanner);

        mScannerView = (ZXingScannerView)findViewById(R.id.scannerView);

        mScannedDialogLayout = (LinearLayout)findViewById(R.id.scannedDialogLl);

        mRetryTextView = (TextView)findViewById(R.id.retryTextView);
        mDoneTextView = (TextView)findViewById(R.id.doneTextView);
        mBookTitleTextView = (TextView)findViewById(R.id.bookTitleTextView);
        mBookDescTextView = (TextView)findViewById(R.id.bookDescTextView);

        mBookImageView = (ImageView)findViewById(R.id.bookImageView);


        mScannerView.setAutoFocus(true);

        mDoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAddBookApi();

            }
        });

        mRetryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScannedDialogLayout.setVisibility(View.GONE);
                mScannerView.resumeCameraPreview(QRBookScannerActivity.this);
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v(TAG, rawResult.getText()); // Prints scan results
        Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        // If you would like to resume scanning, call this method below:
//        mScannerView.resumeCameraPreview(this);

        if(rawResult != null && rawResult.getText() != null && rawResult.getText().toString().length() > 0){
            mScannedDialogLayout.setVisibility(View.VISIBLE);
            mQRCode = rawResult.getText().toString();
            callApi(rawResult.getText());
        }

    }

    private void callApi(String qrCode){
        String URL = Utils.GET_BOOK_DETAIL;
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("bookQRcode",qrCode);
        }catch (Exception e){
            e.printStackTrace();
        }
        new Utils().callApis(QRBookScannerActivity.this,URL,jsonBody,QRBookScannerActivity.this,"Loading...");

    }

    private void callAddBookApi(){
        String URL = Utils.ADD_BOOK_SELF;
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("bookQRcode",mQRCode);
            jsonBody.put("userId",Pref.loadStringPref(QRBookScannerActivity.this,Pref.USER_ID_KEY));
        }catch (Exception e){
            e.printStackTrace();
        }
        new Utils().callApis(QRBookScannerActivity.this,URL,jsonBody,QRBookScannerActivity.this,"Adding book to shelf...");

    }


    @Override
    public void onSuccess(String method, String response) {

        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();

        if(method.equalsIgnoreCase(Utils.GET_BOOK_DETAIL)) {
            BookDetailResponse bookDetailResponse = gson.fromJson(response,
                    BookDetailResponse.class);
            if (bookDetailResponse.getStatus()) {

                mBookTitleTextView.setText(bookDetailResponse.getBook().getBookname());
                mBookDescTextView.setText(bookDetailResponse.getBook().getBookdesc());

                Glide.with(QRBookScannerActivity.this).load(bookDetailResponse.getBook().getThumbnail()).into(mBookImageView);

            } else {
                new Utils().showErrorDialog(QRBookScannerActivity.this, "Something went wrong");
            }
        }else if(method.equalsIgnoreCase(Utils.ADD_BOOK_SELF)){

            finish();
        }

    }

    @Override
    public void onError(String response) {

        new Utils().showErrorDialog(QRBookScannerActivity.this, response);
    }

}
