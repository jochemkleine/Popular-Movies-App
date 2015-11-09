package android.jochemkleine.com.popularmovies.networking;

import android.jochemkleine.com.popularmovies.ui.Review;
import android.jochemkleine.com.popularmovies.ui.Trailer;
import android.net.Uri;
import android.os.AsyncTask;

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
 * Created by Jochemkleine on 8-11-2015.
 */
public class FetchReviewTask extends AsyncTask<String, Void, ArrayList<Review>> {


    private String id;
    private HttpURLConnection connection;
    private BufferedReader reader;
    private ArrayList<Review> reviewList;

    @Override
    protected ArrayList<Review> doInBackground(String... params) {
        reviewList = new ArrayList<>();
        String reviewUrl;
        id = params[0];
        String reviewInformation;

        final String BASE_URL
                = "http://api.themoviedb.org/3/movie?";
        final String REVIEWS = "reviews";
        final String API_KEY_PARAM = "api_key";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(id)
                .appendPath(REVIEWS)
                .appendQueryParameter(API_KEY_PARAM, "fbca96e0df3f8f1c439b24a9af702b75")
                .build();

        try {
            URL url = new URL(builtUri.toString());
            System.out.println("The URL is : " + url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                System.out.println("WARNING: INPUTSTREAM NULL");

            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            reviewInformation = buffer.toString();


            getDataFromJsonString(reviewInformation);
        } catch (IOException e) {
            e.printStackTrace();
            reviewInformation = null;
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return reviewList;
    }

    private void getDataFromJsonString(String trailerInformation) throws JSONException {

        final String TMDB_RESULTS = "results";
        final String TMDB_NAME = "author";
        final String TMDB_CONTENT = "content";


        JSONObject tmdbDataJson = new JSONObject(trailerInformation);
        JSONArray trailerArray = tmdbDataJson.getJSONArray(TMDB_RESULTS);
        try {
            for (int i = 0; i < tmdbDataJson.length(); i++) {
                Review r = new Review();
                JSONObject currentTrailer = trailerArray.getJSONObject(i);
                r.setAuthor(currentTrailer.getString(TMDB_NAME));
                r.setComments(currentTrailer.getString(TMDB_CONTENT));
                this.reviewList.add(r);
            }
        } catch (JSONException j){
            j.printStackTrace();
            System.out.println("JSON EXCEPTION ");
            System.out.println("REVIEW LIST SIZE " + reviewList.size());

        }

    }
}
