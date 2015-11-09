package android.jochemkleine.com.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.jochemkleine.com.popularmovies.R;
import android.jochemkleine.com.popularmovies.data.Movie;
import android.jochemkleine.com.popularmovies.networking.FetchTrailerTask;
import android.jochemkleine.com.popularmovies.ui.MovieDetails;
import android.jochemkleine.com.popularmovies.ui.Trailer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

/**
 * Created by Jochemkleine on 8-11-2015.
 */
public class TrailerListAdapter extends ArrayAdapter{

    private Movie selectedMovie;
    private Context mContext;
    private MovieDetails mMovieDetails;
    private ArrayList<Trailer> trailerList;

    public TrailerListAdapter (Context context, Movie m, MovieDetails movieDetails, ArrayList<Trailer> trailerList) {
        super(context, R.layout.list_item_trailer);
        this.selectedMovie = m;
        this.mContext = context;
        this.mMovieDetails = movieDetails;
        this.trailerList = trailerList;
    }

    @Override
    public View getView (final int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.list_item_trailer, parent, false);



        TextView trailerName = (TextView) customView.findViewById(R.id.trailerNameTextView);
        trailerName.setText(trailerList.get(position).getTrailerName());


        Button trailerButton = (Button) customView.findViewById(R.id.trailerButton);
        trailerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMovieDetails.launchTrailer(trailerList.get(position).getURL());
            }
        });



        return customView;

    }

    @Override
    public int getCount() {
        return trailerList.size();
    }
}
