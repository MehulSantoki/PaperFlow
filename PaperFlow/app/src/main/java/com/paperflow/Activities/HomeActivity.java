package com.paperflow.Activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.paperflow.BookSearchListner;
import com.paperflow.R;
import com.paperflow.fragments.AccountSettingFragment;
import com.paperflow.fragments.BookDetailsFragment;
import com.paperflow.fragments.BooksListFragment;
import com.paperflow.fragments.DashBoardFragment;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView mProgressImageView,mBookImageView,mBlubImageView,mProfileImageView,mCameraImageView;
    private View mProgressView,mBookView,mBlubView,mProfileView;
    Toolbar mToolbar;
    boolean doubleBackToExitPressedOnce = false;

    private BookSearchListner mBookSearchListner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.dark_gray));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Books");

        mToolbar.setNavigationIcon(R.drawable.group_13);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked

            }
        });


        mProgressImageView = (ImageView)findViewById(R.id.progressImageView);
        mBookImageView = (ImageView)findViewById(R.id.bookImageView);
        mBlubImageView = (ImageView)findViewById(R.id.bulbImageView);
        mProfileImageView = (ImageView)findViewById(R.id.profileImageView);
        mCameraImageView = (ImageView)findViewById(R.id.cameraImageview);


        mProgressView = (View)findViewById(R.id.progressView);
        mBookView = (View)findViewById(R.id.bookView);
        mBlubView = (View)findViewById(R.id.bulbView);
        mProfileView = (View)findViewById(R.id.profileView);

        mProgressImageView.setOnClickListener(this);
        mBookImageView.setOnClickListener(this);
        mBlubImageView.setOnClickListener(this);
        mProfileImageView.setOnClickListener(this);


        loadFragment(new BooksListFragment());
    }


    public void setSearchListner(BookSearchListner searchListner){
        mBookSearchListner = searchListner;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.progressImageView :
                mProgressImageView.setSelected(true);
                mBookImageView.setSelected(false);
                mBlubImageView.setSelected(false);
                mProfileImageView.setSelected(false);

                mProgressView.setVisibility(View.VISIBLE);
                mBookView.setVisibility(View.GONE);
                mBlubView.setVisibility(View.GONE);
                mProfileView.setVisibility(View.GONE);

                break;

            case R.id.bookImageView :
                mProgressImageView.setSelected(false);
                mBookImageView.setSelected(true);
                mBlubImageView.setSelected(false);
                mProfileImageView.setSelected(false);

                mProgressView.setVisibility(View.GONE);
                mBookView.setVisibility(View.VISIBLE);
                mBlubView.setVisibility(View.GONE);
                mProfileView.setVisibility(View.GONE);

                loadFragment(new BooksListFragment());

                break;
            case R.id.bulbImageView :
                mProgressImageView.setSelected(false);
                mBookImageView.setSelected(false);
                mBlubImageView.setSelected(true);
                mProfileImageView.setSelected(false);

                mProgressView.setVisibility(View.GONE);
                mBookView.setVisibility(View.GONE);
                mBlubView.setVisibility(View.VISIBLE);
                mProfileView.setVisibility(View.GONE);

                break;
            case R.id.profileImageView :
                mProgressImageView.setSelected(false);
                mBookImageView.setSelected(false);
                mBlubImageView.setSelected(false);
                mProfileImageView.setSelected(true);

                mProgressView.setVisibility(View.GONE);
                mBookView.setVisibility(View.GONE);
                mBlubView.setVisibility(View.GONE);
                mProfileView.setVisibility(View.VISIBLE);
                break;
        }

    }

    private void loadFragment(Fragment fragment) {
// create a FragmentManager
        FragmentManager fm = getSupportFragmentManager();
// create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
// replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit(); // save the changes
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu, menu);

        MenuItem mSearch = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mBookSearchListner.onSearchQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.e("On close","on scolse");
                mBookSearchListner.onSearchQuery("");
                return false;
            }
        });

        return true;
    }
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search
            Log.e("Query"," == "+query);
        }
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
