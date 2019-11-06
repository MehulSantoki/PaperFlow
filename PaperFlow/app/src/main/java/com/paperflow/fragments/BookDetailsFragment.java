package com.paperflow.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paperflow.R;
import com.paperflow.utils.CircularProgressbar;

public class BookDetailsFragment extends Fragment {

   // CircularProgressbar progressbar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_book_details, container, false);

        getActivity().setTitle("Book Details");
//        progressbar = (CircularProgressbar)rootView.findViewById(R.id.progresssBar);
//        //bydefault it is indeterminate
//        progressbar.setValues(10,50,200,100,"8/12");

        return rootView;
    }

}