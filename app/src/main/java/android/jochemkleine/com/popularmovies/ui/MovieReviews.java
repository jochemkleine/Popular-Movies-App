package android.jochemkleine.com.popularmovies.ui;

import android.jochemkleine.com.popularmovies.adapters.ReviewListAdapter;
import android.jochemkleine.com.popularmovies.adapters.TrailerListAdapter;
import android.jochemkleine.com.popularmovies.data.Movie;
import android.jochemkleine.com.popularmovies.networking.FetchReviewTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.jochemkleine.com.popularmovies.R;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MovieReviews extends ActionBarActivity {

    private ArrayList<Review> mReviewArrayList;
    private Movie selectedMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_reviews);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        selectedMovie = (Movie) getIntent().getSerializableExtra("selectedMovie");
        actionBar.setTitle("Reviews for " + selectedMovie.getTitle());
        FetchReviewTask reviewTask = new FetchReviewTask();
        mReviewArrayList = new ArrayList<>();
        try {
            mReviewArrayList = reviewTask.execute(Integer.toString(selectedMovie.getId())).get();
        }catch (Exception e){
            e.printStackTrace();
        }

        if ( mReviewArrayList != null) {
            ListView reviewList = (ListView) findViewById(R.id.reviewList);
            ListAdapter listAdapter = null;
            listAdapter = new ReviewListAdapter(this, mReviewArrayList);
            reviewList.setAdapter(listAdapter);
            reviewList.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
