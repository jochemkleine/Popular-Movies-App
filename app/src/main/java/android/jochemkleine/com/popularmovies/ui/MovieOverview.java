package android.jochemkleine.com.popularmovies.ui;



import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.database.Cursor;

import android.graphics.Bitmap;
import android.jochemkleine.com.popularmovies.adapters.CustomArrayAdapter;
import android.jochemkleine.com.popularmovies.networking.FetchMovieTask;
import android.jochemkleine.com.popularmovies.networking.FetchPosterTask;
import android.net.Uri;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.content.Intent;


import android.jochemkleine.com.popularmovies.R;
import android.jochemkleine.com.popularmovies.data.FavMovieProvider;
import android.jochemkleine.com.popularmovies.data.Movie;
import android.jochemkleine.com.popularmovies.data.MovieColumns;

import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;


public class MovieOverview extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>, Serializable {

    /**
     * recentMoviePages variable determines how many pages (each containing 20 movies)
     * will be imported. Standard amount of pages is 5 (100 movies). Rest of code builds on
     * value indicated by this variable; no other code changes needed in order to change amount
     * of displayed movies.
     */
    private int recentMoviePages = 5;
    private final int SORT_POPULARITY = 0;
    private final int SORT_RATING = 1;
    private final int SORT_FAVOURITE = 2;
    private int sortCriteria = 0;

    private static ArrayList<Movie> allMovieList;
    private static ContentResolver mContentResolver;
    private static ArrayList<Movie> favMovieList;

    private static int CURSOR_LOADER_ID = 1;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_overview);

        getSupportLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        mContentResolver = this.getContentResolver();


        System.out.println("CHECKING FOR TWO PANES") ;
        if (findViewById(R.id.item_detail_container) != null){
            mTwoPane = true;
            System.out.println("TWO PANE VIEW DETECTED");
        }
        if (savedInstanceState == null || !savedInstanceState.containsKey("allMovieList")
                || !savedInstanceState.containsKey("sortCriteria")
                || savedInstanceState.get("allMovieList") == null) {
            new FetchMovieTask().execute(recentMoviePages, this);

        } else {
            allMovieList = ((ArrayList<Movie>) savedInstanceState.get("allMovieList"));
            sortCriteria = (Integer) savedInstanceState.get("sortCriteria");
            updateAdapter();
        }
        // mContentResolver.delete(FavMovieProvider.FavMovies.CONTENT_URI, null, null);
        Cursor cursor = mContentResolver.
                query(FavMovieProvider.FavMovies.CONTENT_URI, null, null, null, null);

        favMovieList = new ArrayList<Movie>();


        if (cursor.moveToFirst()) {
            do {
                Movie m = new Movie();
                m.setId(cursor.getInt(cursor.getColumnIndex((MovieColumns._ID))));
                m.setTitle(cursor.getString(cursor.getColumnIndex((MovieColumns.NAME))));
                m.setOverview(cursor.getString(cursor.getColumnIndex(MovieColumns.OVERVIEW)));
                m.setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieColumns.RELEASE_DATE)));
                m.setVoteAverage((cursor.getDouble(cursor.getColumnIndex((MovieColumns.VOTE_AVERAGE)))));
                m.setPopularity(cursor.getDouble(cursor.getColumnIndex((MovieColumns.POPULARITY))));
                m.setPosterByteArr(cursor.getBlob(cursor.getColumnIndex(MovieColumns.POSTER_IMAGE)));
                m.setFavourite(true);
                favMovieList.add(m);
            } while (cursor.moveToNext());

        }
        cursor.close();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("allMovieList", allMovieList);
        outState.putInt("sortCriteria", sortCriteria);
        super.onSaveInstanceState(outState);
    }

    public void updateCurrentMovies(ArrayList currentMovies) {
        this.allMovieList = currentMovies;
        sortMovies();

    }

    public void updateAdapter() {
        ListView lv;
        lv = (ListView) findViewById(R.id.movie_list);
        ListAdapter listAdapter = null;
        if (sortCriteria == SORT_FAVOURITE) {

            listAdapter = new CustomArrayAdapter(this, this, favMovieList, true);
        } else {
            listAdapter = new CustomArrayAdapter(this, this, allMovieList, false);
        }
        lv.setAdapter(listAdapter);
        lv.setVisibility(View.VISIBLE);
    }

    public void launchMovieDetails(int movieIndex, boolean favourite) {
        Movie m = null;
        if (favourite) {
            m = favMovieList.get(movieIndex);
        } else {
            m = allMovieList.get(movieIndex);
        }

        // DETERMINE WHETHER THIS IS A 2 PANE LAYOUT

        if (mTwoPane) {

            System.out.println("Constructing second pane.");
            Bundle gg = new Bundle();
            gg.putSerializable("selectedMovie", m);
            gg.putSerializable("movieOverview", this);
            MovieDetails fragment = new MovieDetails();
            fragment.setArguments(gg);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        } else {
            Intent i = new Intent(this, MovieDetailActivity.class);
            i.putExtra("selectedMovie", m);
            i.putExtra("movieIndex", movieIndex);
            i.putExtra("movieOverview", this);
            startActivity(i);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sortCriteria == SORT_FAVOURITE) {
            updateAdapter();
        }
    }

    public void loadingErrorToast() {
        Toast.makeText(this, "Error loading movie data.", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void sortMovies() {
        if (allMovieList == null && sortCriteria != SORT_FAVOURITE) {
            // If allMovieList is null, that means an error has occurred earlier in the process.
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
                case SORT_FAVOURITE:
                    sortMoviesByFavourites();
                    break;
            }
            updateAdapter();
        }
    }

    public void sortMoviesByFavourites() {
        Collections.sort(favMovieList, new Comparator<Movie>() {
            @Override
            public int compare(Movie movie1, Movie movie2) {
                return Integer.compare(movie2.getId(), movie1.getId());
            }
        });

    }

    public void sortMoviesByPopularity() {
        Collections.sort(allMovieList, new Comparator<Movie>() {
            @Override
            public int compare(Movie movie1, Movie movie2) {
                return Double.compare(movie2.getPopularity(), movie1.getPopularity());
            }
        });
        if (allMovieList != null) {
            syncFetchedMoviesWithDb();
        }
    }

    public void sortMoviesByRating() {
        Collections.sort(allMovieList, new Comparator<Movie>() {
            @Override
            public int compare(Movie movie1, Movie movie2) {
                return Double.compare(movie2.getVoteAverage(), movie1.getVoteAverage());
            }
        });
        if (allMovieList != null) {
            syncFetchedMoviesWithDb();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_sort_popular:
                setSortCriteria(SORT_POPULARITY);
                break;
            case R.id.action_sort_rating:
                setSortCriteria(SORT_RATING);
                break;
            case (R.id.action_sort_favourite):
                System.out.println("SORT BY FAVOURITE CALLED");
                setSortCriteria(SORT_FAVOURITE);
                break;
        }
        sortMovies();
        return super.onOptionsItemSelected(item);
    }

    public int getRecentMoviePages() {
        return recentMoviePages;
    }

    public void setSortCriteria(int sortCriteria) {
        this.sortCriteria = sortCriteria;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, FavMovieProvider.FavMovies.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void syncFetchedMoviesWithDb() {
        Cursor c2 = mContentResolver.
                query(FavMovieProvider.FavMovies.CONTENT_URI, null, null, null, null);
        if (c2.moveToFirst()) {
            do {
                int movieId = c2.getInt(c2.getColumnIndex(MovieColumns._ID));
                for (int i = 0; i < allMovieList.size(); i++) {
                    if (allMovieList.get(i).getId() == movieId) {
                        allMovieList.get(i).setFavourite(true);

                        break;
                    }
                }
            } while (c2.moveToNext());
        }
        c2.close();
    }

    public void favouriteMovie(Movie movie, boolean b) {


        for (int i=0; i < allMovieList.size(); i++) {
            if (movie.getTitle().equals(allMovieList.get(i).getTitle())) {
                allMovieList.get(i).setFavourite(b);
                break;
            }
        }

        if (b) {

       /*     ContentValues cv = new ContentValues();
            cv.put("_id", m.getId());
            cv.put("name",  m.getTitle());
            cv.put("overview", m.getOverview());
            cv.put("releaseDate", m.getReleaseDate());
            getContentResolver().insert(FavMovieProvider.FavMovies.CONTENT_URI, cv); */
            insertData(movie);
            favMovieList.add(movie);

        } else {
            System.out.println(" DELETEING MOVIE " + movie.getTitle());
            mContentResolver.delete(FavMovieProvider.FavMovies.CONTENT_URI,
                    MovieColumns.NAME + " = ?", new String[]{movie.getTitle()});
            for (int i = 0; i < favMovieList.size(); i++) {
                if (favMovieList.get(i).getTitle().equals(movie.getTitle())) {
                    favMovieList.remove(i);
                    break;
                }
            }

        }


        /*
        Cursor c2 = mContentResolver.
                query(FavMovieProvider.FavMovies.CONTENT_URI, null, null, null, null);
        if (c2.moveToFirst()){
            System.out.println("DB entries  ------------------------" ) ;
            do{
                String data = c2.getString(c2.getColumnIndex(MovieColumns.NAME));
                System.out.println( "name:: " + data);
            }while(c2.moveToNext());
        }
        c2.close();
        System.out.println("------------------"); */
    }

    public void insertData(Movie m) {

        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);

        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                FavMovieProvider.FavMovies.CONTENT_URI);
        builder.withValue(MovieColumns._ID, String.valueOf(m.getId()));
        builder.withValue(MovieColumns.NAME, m.getTitle());
        builder.withValue(MovieColumns.OVERVIEW, m.getOverview());
        builder.withValue(MovieColumns.RELEASE_DATE, m.getReleaseDate());
        builder.withValue(MovieColumns.VOTE_AVERAGE, m.getVoteAverage());
        builder.withValue(MovieColumns.POPULARITY, m.getPopularity());

        if (sortCriteria == SORT_FAVOURITE) {
            builder.withValue(MovieColumns.POSTER_IMAGE, m.getPosterByteArr());

        } else {
            Bitmap mPoster = null;
            try {
                mPoster = new FetchPosterTask().execute(m.getPosterPath()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            byte[] imageArr = getBitmapAsByteArray(mPoster);
            m.setPosterByteArr(imageArr);
            builder.withValue(MovieColumns.POSTER_IMAGE, imageArr);
        }
        batchOperations.add(builder.build());

        try {
            mContentResolver.applyBatch(FavMovieProvider.AUTHORITY, batchOperations);
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public int getSortCriteria() {
        return sortCriteria;
    }
}
