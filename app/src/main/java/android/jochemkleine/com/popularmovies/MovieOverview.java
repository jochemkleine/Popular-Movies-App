package android.jochemkleine.com.popularmovies;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MovieOverview extends ActionBarActivity  {

    private ArrayList<Movie> currentMovies;
    /**
     * recentMoviePages variable determines how many pages (each containing 20 movies)
     * will be imported. Standard amount of pages is 5 (100 movies). Rest of code builds on
     * value indicated by this variable; no other code changes needed in order to change amount
     * of displayed movies.
     */
    private int recentMoviePages = 5;
    private final int SORT_POPULARITY = 0;
    private final int SORT_RATING = 1;
    private int sortCriteria = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_overview);

        if (savedInstanceState == null || !savedInstanceState.containsKey("currentMovies")
                || !savedInstanceState.containsKey("sortCriteria")
                || savedInstanceState.get("currentMovies") == null) {
            new FetchMovieTask().execute(recentMoviePages, this);
        } else {
            currentMovies = new ArrayList<>((ArrayList<Movie>)savedInstanceState.get("currentMovies"));
            sortCriteria = (Integer) savedInstanceState.get("sortCriteria");
            updateAdapter();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("currentMovies", currentMovies);
        outState.putInt("sortCriteria", sortCriteria);
        super.onSaveInstanceState(outState);
    }
    public void updateCurrentMovies (ArrayList currentMovies){
        this.currentMovies = new ArrayList<Movie>(currentMovies);
        sortMovies();

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

    public void loadingErrorToast (){
        Toast.makeText(this, "Error loading movie data.", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void sortMovies (){
        if (currentMovies == null) {
            // If currentMovies is null, that means an error has occurred earlier in the process.
            // In this case, sorting by a criteria will also attempt te import movies to sort.
            new FetchMovieTask().execute(recentMoviePages, this);
        } else {
            switch (sortCriteria) {
                case SORT_POPULARITY:
                    sortMoviesByPopularity();
                    break;
                case SORT_RATING:
                    sortMoviesByRating();
                    break;
            }
            updateAdapter();
        }
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
                setSortCriteria(SORT_POPULARITY);
                break;
            case R.id.action_sort_rating:
                setSortCriteria(SORT_RATING);
                break;
        }
        sortMovies();
        return super.onOptionsItemSelected(item);
    }

    public int getRecentMoviePages (){
        return recentMoviePages;
    }

    public void setSortCriteria (int sortCriteria){
        this.sortCriteria = sortCriteria;
    }
}
