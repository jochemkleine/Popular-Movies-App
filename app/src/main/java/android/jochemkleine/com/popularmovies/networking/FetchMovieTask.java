package android.jochemkleine.com.popularmovies.networking;

import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.jochemkleine.com.popularmovies.data.Movie;
import android.jochemkleine.com.popularmovies.ui.MovieOverview;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Jochemkleine on 4-9-2015.
 */
public class FetchMovieTask extends AsyncTask<Object, Void, ArrayList<Movie>> {

    private MovieOverview mMovieOverview;
    private ArrayList<Movie> currentMovies;

    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String recentMoviesJsonStr = null;

    @Override
    protected ArrayList<Movie> doInBackground(Object... params) {

        // Params contain nr movie pages, movieOverview class instance.
        int recentMoviePages = (int) params[0];
        mMovieOverview = (MovieOverview) params[1];
        currentMovies = new ArrayList<>();
        try {

            // complete url:
            // http://api.themoviedb.org/3/discover/movie?now_playing&page=1&api_key=api_key

            final String MOVIE_DATA_BASE_URL
                    = "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_PATH = "movie";
            final String PAGE = "page";
            final String API_KEY_PARAM = "api_key";

            for (int i = 1; i <= recentMoviePages; i++) {
                /**
                 * Recommended sort paths: 'popular' or 'now_playing'.
                 */
                Uri builtUri = Uri.parse(MOVIE_DATA_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PATH, "popular")
                        .appendQueryParameter(PAGE, Integer.toString(i))
                        .appendQueryParameter(API_KEY_PARAM, "fbca96e0df3f8f1c439b24a9af702b75")
                        .build();

                URL movieURL = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) movieURL.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    System.out.println("WARNING: INPUTSTREAM NULL");

                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                recentMoviesJsonStr = buffer.toString();
                getMovieDataFromJson(recentMoviesJsonStr);
            }
        } catch (IOException e) {
            e.printStackTrace();
            recentMoviesJsonStr = null;
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return currentMovies;
    }

    @Override
    protected void onPostExecute(ArrayList<Movie> currentMovies) {
        if (currentMovies != null) {

            mMovieOverview.updateCurrentMovies(currentMovies);
            mMovieOverview.syncFetchedMoviesWithDb();
            mMovieOverview.updateAdapter();
        } else {
            mMovieOverview.loadingErrorToast();
        }

    }

    private void getMovieDataFromJson(String recentMoviesJsonStr)
            throws JSONException {


        final String TMDB_RESULTS = "results";
        final String TMDB_ID = "id";
        final String TMDB_TITLE = "title";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_VOTE_AVERAGE = "vote_average";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_POPULARITY = "popularity";

        JSONObject tmdbDataJson = new JSONObject(recentMoviesJsonStr);
        JSONArray moviesArray = tmdbDataJson.getJSONArray(TMDB_RESULTS);

        for (int i = 0; i < 20; i++) {
            Movie movie = new Movie();
            JSONObject currentMovie = moviesArray.getJSONObject(i);
            movie.setId(Integer.parseInt(currentMovie.getString(TMDB_ID)));
            movie.setTitle(currentMovie.getString(TMDB_TITLE));
            movie.setOverview(currentMovie.getString(TMDB_OVERVIEW));
            movie.setPosterPath(currentMovie.getString(TMDB_POSTER_PATH));
            movie.setReleaseDate(currentMovie.getString(TMDB_RELEASE_DATE));
            movie.setVoteAverage(Double.parseDouble(currentMovie.getString(TMDB_VOTE_AVERAGE)));
            movie.setPopularity(Double.parseDouble(currentMovie.getString(TMDB_POPULARITY)));
            currentMovies.add(movie);
        }
    }

}
