package android.jochemkleine.com.popularmovies.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.jochemkleine.com.popularmovies.R;
import android.jochemkleine.com.popularmovies.adapters.TrailerListAdapter;
import android.jochemkleine.com.popularmovies.data.Movie;
import android.jochemkleine.com.popularmovies.networking.FetchTrailerTask;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;


import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;


import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.support.v4.view.MenuItemCompat.getActionProvider;


public class MovieDetails extends ActionBarActivity {

    private Movie selectedMovie;
    private ToggleButton favouriteButton;
    private MovieOverview mMovieOverview;
    private int movieIndex;
    private int SORT_FAVOURITE = 2;
    private ArrayList<Trailer> trailerList;
    private Context mContext;
    private ShareActionProvider mShareActionProvider;

    public MovieDetails (){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_details);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        selectedMovie = (Movie) getIntent().getSerializableExtra("selectedMovie");
        movieIndex = (int) getIntent().getIntExtra("movieIndex", 0);
        mMovieOverview = (MovieOverview) getIntent().getSerializableExtra("movieOverview");
        this.mContext = getBaseContext();

        setTitle(selectedMovie.getTitle());
        final int movieThumbnail = R.id.poster_thumbnail;
        if (selectedMovie.isFavourite() && mMovieOverview.getSortCriteria() == SORT_FAVOURITE) {
            loadPosterImageBitmap(movieThumbnail);
        } else {
            loadPosterImage(movieThumbnail);
        }

        TextView releaseDate = (TextView) findViewById(R.id.release_date);

        int monthIndex = Integer.parseInt(selectedMovie.getReleaseDate().substring(5,7));
        releaseDate.setText(getMonthName(monthIndex) + " " + selectedMovie.getReleaseDate().substring(0, 4));

        TextView rating = (TextView) findViewById(R.id.rating);
        rating.setText(Double.toString(selectedMovie.getVoteAverage()) + "/10");

        TextView movieSynopsis = (TextView) findViewById(R.id.movie_plot);
        movieSynopsis.setText(selectedMovie.getOverview());

        final TextView movieTitle = (TextView) findViewById(R.id.movieTitle);
        movieTitle.setText(selectedMovie.getTitle());



        favouriteButton = (ToggleButton) findViewById(R.id.favouriteButton);
      //  favouriteButton.setTextOff("Favourite");
      //  favouriteButton.setTextOn("Unfavourite");
        favouriteButton.setTextOn("");
        favouriteButton.setTextOff("");
        if (selectedMovie.isFavourite()) {
            favouriteButton.setChecked(true);

           // favouriteButton.setBackgroundColor(Color.RED);

        } else {
            favouriteButton.setChecked(false);
          //  favouriteButton.setBackgroundColor(Color.GREEN);
        }
        movieTitle.post(new Runnable() {
            @Override
            public void run() {
                int lineCnt;
                //  do {
                lineCnt = movieTitle.getLineCount();
                System.out.println("Lines : '" + lineCnt);
                if (lineCnt > 2) {
                    movieTitle.setTextAppearance(mContext, android.R.style.TextAppearance_Medium);
                    movieTitle.setTextColor(Color.BLACK);

                } else if (lineCnt == 1){
                    favouriteButton.setPadding(favouriteButton.getPaddingLeft(),
                            favouriteButton.getPaddingTop() + 12,
                            favouriteButton.getPaddingRight(),
                            favouriteButton.getPaddingBottom() + 12);

                }
                //   } while (lineCnt > 2);
                // Perform any actions you want based on the line count here.
            }
        });

        fetchTrailers();
        setTrailerList();

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

    public void loadPosterImage (int viewId){
        final String BASE_URL = "http://image.tmdb.org/t/p/";
        final String IMG_SIZE = "w185";
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(IMG_SIZE)
                .appendEncodedPath(selectedMovie.getPosterPath())
                .build();

        Picasso.with(this)
                .load(builtUri)
                .into((ImageView) findViewById(viewId));

    }

    public void loadPosterImageBitmap (int viewId) {


        try {
            byte[] imgByte = selectedMovie.getPosterByteArr();

            Bitmap imgBitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
            final ImageView imageView = (ImageView) findViewById(viewId);
            imageView.setImageBitmap(imgBitmap);

        } catch (NullPointerException e) {
            // dont fill image.
            e.printStackTrace();
            System.out.println("NULL POINTER EXCEPTION IMGBYTE. ");
        } catch (ArrayIndexOutOfBoundsException a) {
            a.printStackTrace();
            System.out.println("INDEX OAB EXCEPTION IMGBYTE. ");
        }

    }

    public void setTrailerList () {
        if ( trailerList != null) {
            ListView trailerList = (ListView) findViewById(R.id.trailerList);
            ListAdapter listAdapter = null;
            listAdapter = new TrailerListAdapter(this, selectedMovie, this , this.trailerList);
            trailerList.setAdapter(listAdapter);
            trailerList.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_details, menu);

        if (trailerList == null) {
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
        return true;
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

         //item = menu.findItem(R.id.menu_item_share);


        return super.onOptionsItemSelected(item);
    }

    public void favouriteButtonPressed(View view) {
        if (favouriteButton.isChecked()) {
         //   favouriteButton.setBackgroundColor(Color.RED);
            mMovieOverview.favouriteMovie(selectedMovie, true);
            Toast.makeText(this, "Added to favourites", Toast.LENGTH_SHORT).show();
        } else {
            mMovieOverview.favouriteMovie(selectedMovie, false);
        //    favouriteButton.setBackgroundColor(Color.GREEN);
            Toast.makeText(this, "Removed from favourites", Toast.LENGTH_SHORT).show();
        }
    }


    public void launchTrailer(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        //      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    public void launchMovieReviews(View v) {
        Intent i = new Intent(this, MovieReviews.class);
        i.putExtra("selectedMovie", selectedMovie);
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