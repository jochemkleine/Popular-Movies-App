package android.jochemkleine.com.popularmovies;

import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class MovieDetails extends ActionBarActivity {

    private Movie selectedMovie;
    public MovieDetails (){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        selectedMovie = new Movie();
        selectedMovie = (Movie) getIntent().getSerializableExtra("selectedMovie");
        setTitle(selectedMovie.getTitle());
        int movieThumbnail = R.id.poster_thumbnail;
        loadPosterImage(movieThumbnail);

        TextView releaseDate = (TextView) findViewById(R.id.release_date);

        int monthIndex = Integer.parseInt(selectedMovie.getReleaseDate().substring(5,7));
        releaseDate.setText(getMonthName(monthIndex) + " " + selectedMovie.getReleaseDate().substring(0,4));

        TextView rating = (TextView) findViewById(R.id.rating);
        rating.setText(Double.toString(selectedMovie.getVoteAverage()) + "/10");

        TextView movieSynopsis = (TextView) findViewById(R.id.movie_plot);
        movieSynopsis.setText(selectedMovie.getOverview());

        TextView movieTitle = (TextView) findViewById(R.id.movieTitle);
        movieTitle.setText(selectedMovie.getTitle());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_details, menu);
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
        return super.onOptionsItemSelected(item);
    }
}
