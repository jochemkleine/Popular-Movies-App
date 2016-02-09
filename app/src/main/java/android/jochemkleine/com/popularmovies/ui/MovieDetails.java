package android.jochemkleine.com.popularmovies.ui;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.jochemkleine.com.popularmovies.R;
import android.jochemkleine.com.popularmovies.adapters.ReviewListAdapter;
import android.jochemkleine.com.popularmovies.adapters.TrailerListAdapter;
import android.jochemkleine.com.popularmovies.data.Movie;
import android.jochemkleine.com.popularmovies.networking.FetchReviewTask;
import android.jochemkleine.com.popularmovies.networking.FetchTrailerTask;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;



import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;


import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.support.v4.view.MenuItemCompat.getActionProvider;


public class MovieDetails extends Fragment {

    private Movie selectedMovie;
    private MovieOverview mMovieOverview;
    private int SORT_FAVOURITE = 2;
    private ArrayList<Trailer> trailerList;
    private Context mContext;
    private ShareActionProvider mShareActionProvider;
    private View mRootView;
    private MovieDetailActivity mMovieDetailActivity;
    private ArrayList<Review> mReviewArrayList;
    private NestedScrollView scrollView;
    private ListView reviewListView;
    private ListView trailerListView;

    public MovieDetails (){


    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        Bundle b = getArguments();
        selectedMovie = (Movie) b.getSerializable("selectedMovie");
        this.mMovieOverview = (MovieOverview) b.get("movieOverview");
        //mMovieDetailActivity = (MovieDetailActivity) b.get("movieDetailActivity");
        mMovieDetailActivity = (MovieDetailActivity) this.getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
     //   super.onCreate(savedInstanceState);
      //  setHasOptionsMenu(true);


        View rootView = inflater.inflate(R.layout.movie_details, container, false);

      //  selectedMovie = (Movie) gegetIntent().getSerializableExtra("selectedMovie");
      //  mMovieOverview = (MovieOverview) getIntent().getSerializableExtra("movieOverview");
        this.mContext = getActivity().getBaseContext();
        this.mRootView = rootView;
        getActivity().setTitle(selectedMovie.getTitle());

        if (selectedMovie.isFavourite() && mMovieOverview.getSortCriteria() == SORT_FAVOURITE) {
            mMovieDetailActivity.loadPosterImageBitmap(selectedMovie.getPosterByteArr());
        } else {
            mMovieDetailActivity.loadPosterImage(selectedMovie.getPosterPath());
        }

        // MAY IMPLEMENT
         mMovieDetailActivity.setToolbarTitle(selectedMovie.getTitle());


        TextView releaseDate = (TextView) rootView.findViewById(R.id.releaseDate);

        int monthIndex = Integer.parseInt(selectedMovie.getReleaseDate().substring(5,7));
        releaseDate.setText(getMonthName(monthIndex) + " " + selectedMovie.getReleaseDate().substring(0, 4));

        TextView rating = (TextView) rootView.findViewById(R.id.rating);
        rating.setText(Double.toString(Math.round(selectedMovie.getVoteAverage()*10d) /10d) + "/10");

        TextView movieSynopsis = (TextView) rootView.findViewById(R.id.movie_plot);
        movieSynopsis.setText(selectedMovie.getOverview());

        final TextView movieTitle = (TextView) rootView.findViewById(R.id.movieTitle);
        movieTitle.setText(selectedMovie.getTitle());



        mMovieDetailActivity.setFavouriteButtonSkin(selectedMovie.isFavourite());

        trailerListView = (ListView) mRootView.findViewById(R.id.trailerList);
        trailerListView.setFocusable(false);
        fetchTrailers();
        setTrailerList();

        reviewListView = (ListView) mRootView.findViewById(R.id.reviewList);
        reviewListView.setFocusable(false);
        fetchReviews();
        setReviewList();

        String shareUrl = "";
        if ( trailerList == null) {
            shareUrl = "unavailable";
        } else {
             shareUrl = trailerList.get(0).getURL();
        }
        mMovieDetailActivity.initShareButton(shareUrl);

     /*   final NestedScrollView  mScrollView = (NestedScrollView) rootView.findViewById(R.id.nestedScrollView);
        //scrollView.scrollTo(0, scrollView.getBottom());
        final LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.parentLinearLayout);
        mScrollView.post(new Runnable() {
            public void run() {
                mScrollView.scrollTo(0, linearLayout.getTop());
            }
        });
        */

        return rootView;
    }

    public void fetchReviews () {
        FetchReviewTask reviewTask = new FetchReviewTask();
        mReviewArrayList = new ArrayList<>();
        try {
            mReviewArrayList = reviewTask.execute(Integer.toString(selectedMovie.getId())).get();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setReviewList () {
        if ( mReviewArrayList != null) {

            ListAdapter listAdapter = null;
            listAdapter = new ReviewListAdapter(getActivity(), mReviewArrayList);
            reviewListView.setAdapter(listAdapter);
            reviewListView.setVisibility(View.VISIBLE);
        }
    }

    private void fetchTrailers() {
        FetchTrailerTask fetchTrailerTask = new FetchTrailerTask();
        try {
            this.trailerList = fetchTrailerTask.execute(Integer.toString(selectedMovie.getId())).get();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("FETCH TRAILER TASK FAILED.");
        }
    }

    public String getMonthName (int monthIndex) {
        String[] months = {"January",
                "February",
                "March",
                "April",
                "May",
                "June",
                "July",
                "August",
                "September",
                "October",
                "November",
                "December"};

        return (months[monthIndex-1]);
    }


    public void setTrailerList () {
        if ( trailerList != null) {
            ListAdapter listAdapter = null;
            listAdapter = new TrailerListAdapter(mContext, selectedMovie, this , this.trailerList);
            trailerListView.setAdapter(listAdapter);
            trailerListView.setVisibility(View.VISIBLE);

        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().invalidateOptionsMenu();
        inflater.inflate(R.menu.menu_movie_details, menu);

        if (trailerList == null) {
            System.out.println("TRAILERLIST IS NULL" ) ;
            MenuItem menuItem = menu.findItem(R.id.action_share).setEnabled(false);
            disableShareIntent();

        } else {

            MenuItem menuItem = menu.findItem(R.id.action_share);

            ShareActionProvider mShareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareIntent());
            } else {
                System.out.println("MSHAREACTIONPROVIDER IS NULL");
            }
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
            getActivity().finish();
            return true;
        }

         //item = menu.findItem(R.id.menu_item_share);


        return super.onOptionsItemSelected(item);
    }




    public void launchTrailer(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        //      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }


    public Intent createShareIntent () {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        //  shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        // shareIntent.putExtra(Intent.)
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this trailer for the movie " + selectedMovie.getTitle() +"!");
        shareIntent.putExtra(Intent.EXTRA_TEXT, trailerList.get(0).getURL());
        return shareIntent;
        //startActivity(Intent.createChooser(shareIntent, "Share URL"));
    }

    public void disableShareIntent() {
        this.mShareActionProvider = null;
    }



}