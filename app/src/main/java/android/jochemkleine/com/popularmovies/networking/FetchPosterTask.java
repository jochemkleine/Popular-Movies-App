package android.jochemkleine.com.popularmovies.networking;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.jochemkleine.com.popularmovies.data.Movie;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Jochemkleine on 7-11-2015.
 */
public class FetchPosterTask  extends AsyncTask<String, Void, Bitmap> {

    final String BASE_URL = "http://image.tmdb.org/t/p/";
    final String IMG_SIZE = "w185";

    @Override
    protected Bitmap doInBackground(String... params) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(IMG_SIZE)
                .appendEncodedPath(params[0])
                .build();

        try {

            URL url = new URL(builtUri.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
