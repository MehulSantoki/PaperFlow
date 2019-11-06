package com.paperflow.fragments;

import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.paperflow.R;
import com.paperflow.utils.SquareImageView;

public class DashBoardFragment extends Fragment {

    private LinearLayout mScoreLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mScoreLayout = (LinearLayout)rootView.findViewById(R.id.scoreLayout);

        getActivity().setTitle("Book Details");


        addScoreViews();
        return rootView;
    }


    private void addScoreViews(){
        for(int i=0;i<10;i++) {
            View child = getLayoutInflater().inflate(R.layout.score_imageview, null);
            final ImageView circleImageView = (ImageView)child.findViewById(R.id.pOneImageView);

            circleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)circleImageView.getLayoutParams();
                    lp.height = (int)getResources().getDimension(R.dimen.dp_55);
                    lp.width = (int)getResources().getDimension(R.dimen.dp_55);
                    circleImageView.setLayoutParams(lp);
                    circleImageView.setBackgroundResource(R.drawable.green_circle_border_bg);
                }
            });

            mScoreLayout.addView(child);
        }
    }
}