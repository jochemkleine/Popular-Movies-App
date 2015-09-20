package android.jochemkleine.com.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Jochemkleine on 4-9-2015.
 */
public class CustomArrayAdapter extends ArrayAdapter {

    private ArrayList<Movie> currentMovies;
    private Context movieOverviewContext;
    private MovieOverview mMovieOverview;

    public CustomArrayAdapter(Context context,MovieOverview mMovieOverview, ArrayList<Movie> currentMovies) {
        super(context, R.layout.list_item_movie_overview);
        this.mMovieOverview = mMovieOverview;
        movieOverviewContext = context;
        this.currentMovies = new ArrayList<>(currentMovies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.list_item_movie_overview, parent, false);

        if (currentMovies.size() != 0) {

            int index = 0;
            if (position == 0) {
                index = 2;
            } else {
                index = (position * 3) + 2;
            }

            final int poster1Index = index - 2;
            final int poster2Index = index - 1;
            final int poster3Index = index;

            loadPosterImage(R.id.posterImage1, customView, poster1Index);
            loadPosterImage(R.id.posterImage2, customView, poster2Index);
            loadPosterImage(R.id.posterImage3, customView, poster3Index);

            ImageView poster1 = (ImageView) customView.findViewById(R.id.posterImage1);

            poster1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMovieOverview.launchMovieDetails(poster1Index);
                }
            });

            ImageView poster2 = (ImageView) customView.findViewById(R.id.posterImage2);
            poster2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMovieOverview.launchMovieDetails(poster2Index);
                }
            });

            ImageView poster3 = (ImageView) customView.findViewById(R.id.posterImage3);
            poster3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMovieOverview.launchMovieDetails(poster3Index);
                }
            });
        }

        return customView;
    }

    /**
     *  Retrieves poster image URL, loads into app with Picasso
     */
    public void loadPosterImage (int image_id, View customView, int index) {
        final String BASE_URL = "http://image.tmdb.org/t/p/";
        final String IMG_SIZE = "w185";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(IMG_SIZE)
                .appendEncodedPath(currentMovies.get(index).getPosterPath())
                .build();

        Picasso.with(movieOverviewContext)
                .load(builtUri)
                .into((ImageView) customView.findViewById(image_id));

    }

    /**
     * Indicates amount of rows in listview.
     */
    @Override
    public int getCount() {
        return (mMovieOverview.getRecentMoviePages()*20)/3;
    }
}

