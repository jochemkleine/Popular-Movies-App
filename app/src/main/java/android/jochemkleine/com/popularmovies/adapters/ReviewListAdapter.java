package android.jochemkleine.com.popularmovies.adapters;

import android.content.Context;
import android.jochemkleine.com.popularmovies.R;
import android.jochemkleine.com.popularmovies.data.Movie;
import android.jochemkleine.com.popularmovies.ui.MovieOverview;
import android.jochemkleine.com.popularmovies.ui.Review;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jochemkleine on 8-11-2015.
 */
public class ReviewListAdapter extends ArrayAdapter {

    private ArrayList<Review> mReviewArrayList;

    public ReviewListAdapter(Context context, ArrayList<Review> reviewArrayList) {
        super(context, R.layout.list_item_movie_overview);
        this.mReviewArrayList = reviewArrayList;
        for (int i =0; i < mReviewArrayList.size(); i++){
            System.out.println("INSIDE REVIEWLISTADAPTER :  + "  + mReviewArrayList.get(i).getComments());
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.list_item_review, parent, false);

        TextView content = (TextView) customView.findViewById(R.id.reviewContent);
        content.setText(mReviewArrayList.get(position).getComments());
        content.setMovementMethod(new ScrollingMovementMethod());
        TextView author = (TextView) customView.findViewById(R.id.reviewAuthor);
        author.setText(mReviewArrayList.get(position).getAuthor());
        return customView;
    }

    @Override
    public int getCount (){
        return mReviewArrayList.size();
    }
}


