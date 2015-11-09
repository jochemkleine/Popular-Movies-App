package android.jochemkleine.com.popularmovies.networking;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
public class FetchTrailerTask  extends AsyncTask<String, Void, ArrayList<Trailer>> {


    private String id;
    private HttpURLConnection connection;
    private BufferedReader reader;
    private ArrayList<Trailer> trailerList;

    @Override
    protected ArrayList<Trailer> doInBackground(String... params) {
        trailerList = new ArrayList<>();
        String trailerUrl;
        id = params[0];
        String trailerInformation;

        final String BASE_URL
                = "http://api.themoviedb.org/3/movie?";
        final String VIDEOS = "videos";
        final String API_KEY_PARAM = "api_key";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(id)
                .appendPath(VIDEOS)
                .appendQueryParameter(API_KEY_PARAM, "fbca96e0df3f8f1c439b24a9af702b75")
                .build();

        try {
            URL url = new URL(builtUri.toString());
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

            trailerInformation = buffer.toString();

            getDataFromJsonString(trailerInformation);

        } catch (IOException e) {
            e.printStackTrace();
            trailerInformation = null;
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
        return trailerList;
    }

    private void getDataFromJsonString(String trailerInformation) throws JSONException {
        final String TMDB_RESULTS = "results";
        final String TMDB_KEY = "key";
        final String TMDB_NAME = "name";


        JSONObject tmdbDataJson = new JSONObject(trailerInformation);
        JSONArray trailerArray = tmdbDataJson.getJSONArray(TMDB_RESULTS);

        for (int i = 0; i < tmdbDataJson.length(); i++) {
            Trailer t = new Trailer ();
            JSONObject currentTrailer = trailerArray.getJSONObject(i);
            t.setTrailerName(currentTrailer.getString(TMDB_NAME));
            String key = (currentTrailer.getString(TMDB_KEY));
            t.setURL("https://www.youtube.com/watch?v="+key);
            this.trailerList.add(t);
        }

    }
}

