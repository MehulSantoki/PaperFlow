package com.paperflow.fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.VolleyLog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paperflow.Activities.HomeActivity;
import com.paperflow.Activities.LoginActivity;
import com.paperflow.Activities.QRBookScannerActivity;
import com.paperflow.BookSearchListner;
import com.paperflow.R;
import com.paperflow.adapters.BooksListAdapter;
import com.paperflow.responses.GetUserBooksResponse;
import com.paperflow.responses.RegisterOtpResponse;
import com.paperflow.utils.OnApiResponse;
import com.paperflow.utils.Pref;
import com.paperflow.utils.Utils;

import org.json.JSONObject;
import org.opencv.text.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class BooksListFragment  extends Fragment implements OnApiResponse,BookSearchListner {

    private BookSearchListner bookSearchListner;
    private GridView mGridView;
    private BooksListAdapter mAdapter;
    private Button mAddBookButton;
    private TextView mSortTextView;
    GetUserBooksResponse getUserBooksResponse;
    Dialog dialog;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_book_list, container, false);

        ((HomeActivity)getActivity()).setSearchListner(this);
        mGridView = (GridView)rootView.findViewById(R.id.gridview);

        mAddBookButton = (Button)rootView.findViewById(R.id.addBookButton);

        mSortTextView = (TextView)rootView.findViewById(R.id.sortTextView);

        mAddBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isReadStoragePermissionGranted()){
                    Intent intent = new Intent(getActivity(),QRBookScannerActivity.class);
                    startActivity(intent);
                }
            }
        });


        mSortTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();
            }
        });
        initSortDialog();

        return rootView;
    }


    private void initSortDialog(){
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.bok_sort_dialog);

        Button applyButton = (Button)dialog.findViewById(R.id.applyButton);
        TextView cancelTextView = (TextView)dialog.findViewById(R.id.cancelTextView);
        final RadioGroup radioGroup = (RadioGroup)dialog.findViewById(R.id.radioSort);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedId = radioGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                RadioButton radioSortButton = (RadioButton)dialog.findViewById(selectedId);
                mSortTextView.setText(radioSortButton.getText().toString());
                filterBookList(selectedId);
                dialog.dismiss();
            }
        });

        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }


    @Override
    public void onSearchQuery(String text) {

        mAdapter.getFilter().filter(text);
    }

    private void filterBookList(int id){

        ArrayList<GetUserBooksResponse.UserBook> bookArrayList = getUserBooksResponse.getUserBooks();
        switch (id){
            case R.id.lastRadio:

                break;
            case R.id.descRadio:
            case R.id.ascRadio:

                Collections.sort(bookArrayList, new Comparator<GetUserBooksResponse.UserBook>() {
                    @Override
                    public int compare(GetUserBooksResponse.UserBook lhs, GetUserBooksResponse.UserBook rhs) {
                        return lhs.getBookname().compareTo(rhs.getBookname());
                    }
                });

                if(id == R.id.descRadio){
                    Collections.reverse(bookArrayList);
                }
                if(getUserBooksResponse.getStatus()){

                    mAdapter = new BooksListAdapter(getActivity(),bookArrayList);
                    mGridView.setAdapter(mAdapter);
                }

                break;

            case R.id.dateRadio:

                Collections.sort(bookArrayList, new Comparator<GetUserBooksResponse.UserBook>() {
                    @Override
                    public int compare(GetUserBooksResponse.UserBook lhs, GetUserBooksResponse.UserBook rhs) {
                        SimpleDateFormat format = new SimpleDateFormat(
                                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                        format.setTimeZone(TimeZone.getTimeZone("UTC"));
                        try {
                            Date lhsDate = format.parse(lhs.getCreatedAt());
                            Date rhsDate = format.parse(rhs.getCreatedAt());
                            return lhsDate.compareTo(rhsDate);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        return 0;

                    }
                });

                if(getUserBooksResponse.getStatus()){

                    mAdapter = new BooksListAdapter(getActivity(),bookArrayList);
                    mGridView.setAdapter(mAdapter);
                }

                break;



        }

    }
    private void callApi(){
        String URL = Utils.GET_USER_BOOKS;
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("userId",Pref.loadStringPref(getActivity(),Pref.USER_ID_KEY));
        }catch (Exception e){
            e.printStackTrace();
        }
        new Utils().callApis(getActivity(),URL,jsonBody,BooksListFragment.this,"Loading...");

    }

    @Override
    public void onResume() {
        super.onResume();
        callApi();
    }

    @Override
    public void onSuccess(String method, String response) {

        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();

        getUserBooksResponse = gson.fromJson(response,
                GetUserBooksResponse.class);
        if(getUserBooksResponse.getStatus()){

            mAdapter = new BooksListAdapter(getActivity(),getUserBooksResponse.getUserBooks());
            mGridView.setAdapter(mAdapter);
        }else{
            new Utils().showErrorDialog(getActivity(),"Something went wrong");
        }

    }

    @Override
    public void onError(String response) {

    }


    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {


                return true;
            } else {

                Log.v(VolleyLog.TAG,"Permission is revoked1");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation

            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(VolleyLog.TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission

        }else{

        }
    }

}