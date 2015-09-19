package android.jochemkleine.com.popularmovies;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MovieOverview extends ActionBarActivity  {

    private ArrayList<Movie> currentMovies;
    /**
     * recentMoviePages variable determines how many pages (each containing 20 movies)
     * will be imported. Standard amount of pages is 5 (100 movies). Rest of code builds on
     * value indicated by this variable; no other code changes needed in order to change amount
     * of displaye movies.
     */
    private int recentMoviePages = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_overview);
        new FetchMovieTask().execute(recentMoviePages, this);

    }
    public void updateCurrentMovies (ArrayList currentMovies){
        this.currentMovies = new ArrayList<Movie>(currentMovies);
        sortMoviesByPopularity();

    }
    public void updateAdapter (){
        ListView lv;
        lv = (ListView) findViewById(R.id.movie_list);
        ListAdapter ca = new CustomArrayAdapter(this, this, currentMovies);
        lv.setAdapter(ca);
        lv.setVisibility(View.VISIBLE);
    }

    public void launchMovieDetails (int movieIndex){
        Intent i = new Intent(this, MovieDetails.class);
        i.putExtra("selectedMovie", currentMovies.get(movieIndex));
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    public void sortMoviesByPopularity(){
        Collections.sort(currentMovies, new Comparator<Movie>(){
            @Override
            public int compare(Movie movie1, Movie movie2){
                return Double.compare(movie2.getPopularity(), movie1.getPopularity());
            }
        });
    }

    public void sortMoviesByRating(){
        Collections.sort(currentMovies, new Comparator<Movie>(){
            @Override
            public int compare(Movie movie1, Movie movie2){
                return Double.compare(movie2.getVoteAverage(), movie1.getVoteAverage());
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_sort_popular:
                sortMoviesByPopularity();
                updateAdapter();
                break;
            case R.id.action_sort_rating:
                sortMoviesByRating();
                updateAdapter();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public int getRecentMoviePages (){
        return recentMoviePages;
    }





}
