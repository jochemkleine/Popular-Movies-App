package android.jochemkleine.com.popularmovies.adapters;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.jochemkleine.com.popularmovies.R;
import android.jochemkleine.com.popularmovies.data.Movie;
import android.jochemkleine.com.popularmovies.ui.MovieOverview;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Jochemkleine on 4-9-2015.
 */
public class CustomArrayAdapter extends ArrayAdapter {

    private ArrayList<Movie> currentMovies;
    private Context movieOverviewContext;
    private MovieOverview mMovieOverview;
    private boolean favouriteOverview;

    public CustomArrayAdapter(Context context, MovieOverview mMovieOverview, ArrayList<Movie> currentMovies, boolean favouriteOverview) {
        super(context, R.layout.list_item_movie_overview);
        this.favouriteOverview = favouriteOverview;
        this.mMovieOverview = mMovieOverview;
        movieOverviewContext = context;
        this.currentMovies = (currentMovies);

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

            if (favouriteOverview) {
                loadPosterImageBitmap(R.id.posterImage1, customView, poster1Index);
                loadPosterImageBitmap(R.id.posterImage2, customView, poster2Index);
                loadPosterImageBitmap(R.id.posterImage3, customView, poster3Index);
            } else {
                loadPosterImagePicasso(R.id.posterImage1, customView, poster1Index);
                loadPosterImagePicasso(R.id.posterImage2, customView, poster2Index);
                loadPosterImagePicasso(R.id.posterImage3, customView, poster3Index);

            }

            ImageView poster1 = (ImageView) customView.findViewById(R.id.posterImage1);

            poster1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (favouriteOverview) {
                        mMovieOverview.launchMovieDetails(poster1Index, true);
                    } else {
                        mMovieOverview.launchMovieDetails(poster1Index, false);
                    }
                }
            });

            ImageView poster2 = (ImageView) customView.findViewById(R.id.posterImage2);
            poster2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (favouriteOverview) {
                        mMovieOverview.launchMovieDetails(poster2Index, true);
                    } else {
                        mMovieOverview.launchMovieDetails(poster2Index, false);
                    }
                }
            });

            ImageView poster3 = (ImageView) customView.findViewById(R.id.posterImage3);
            poster3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (favouriteOverview) {
                        mMovieOverview.launchMovieDetails(poster3Index, true);
                    } else {
                        mMovieOverview.launchMovieDetails(poster3Index, false);
                    }
                }
            });
        }

        return customView;
    }

    /**
     * Retrieves poster image URL, loads into app with Picasso
     */
    public void loadPosterImagePicasso(int image_id, View customView, int index) {
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

    public void loadPosterImageBitmap (int image_id, View customView, int index) {

        if (currentMovies.size () > index) {
            try {
                byte[] imgByte = currentMovies.get(index).getPosterByteArr();

                Bitmap imgBitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
                final ImageView imageView = (ImageView) customView.findViewById(image_id);
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
    }


    /**
     * Indicates amount of rows in listview.
     */
    @Override
    public int getCount() {
        if (favouriteOverview) {
            if (currentMovies.size() < 12) {
                return 4;
            } else {
                if (currentMovies.size() %3 > 0){
                    return currentMovies.size() /3 + 1;
                } else {
                    return currentMovies.size() / 3;
                }
            }
        } else {
            return (mMovieOverview.getRecentMoviePages() * 20) / 3;

        }
    }
}

