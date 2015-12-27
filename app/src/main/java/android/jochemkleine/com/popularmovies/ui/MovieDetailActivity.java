package android.jochemkleine.com.popularmovies.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.jochemkleine.com.popularmovies.R;
import android.jochemkleine.com.popularmovies.data.Movie;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.Serializable;

/**
 * Created by Jochemkleine on 10-11-2015.
 */
public class MovieDetailActivity  extends AppCompatActivity {

    private Movie selectedMovie;
    private int movieIndex;
    private MovieOverview mMovieOverview;
    private Toolbar toolbar;
    private ImageView toolbarImage;
    private int SORT_FAVOURITE = 2;
    private CollapsingToolbarLayout toolbarLayout;
    private FloatingActionButton favouriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        selectedMovie = (Movie) getIntent().getSerializableExtra("selectedMovie");
        movieIndex = (int) getIntent().getIntExtra("movieIndex", 0);
        mMovieOverview = (MovieOverview) getIntent().getSerializableExtra("movieOverview");
        toolbarImage = (ImageView) findViewById(R.id.toolbarImage);
        favouriteButton = (FloatingActionButton) findViewById(R.id.favouriteButton);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);


        setFavouriteButtonSkin(selectedMovie.isFavourite());
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favouriteButtonPressed();
            }
        });


        if (savedInstanceState == null) {
            System.out.println("Movie  BEING TRANSMITTED: " + selectedMovie.getTitle());
            Bundle gg = new Bundle();
            gg.putSerializable("selectedMovie", selectedMovie);
            gg.putSerializable("movieOverview", mMovieOverview);
            MovieDetails fragment = new MovieDetails();
            fragment.setArguments(gg);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();

        }

    }

    public void loadPosterImageBitmap(byte[] imgByte) {
        try {
            ;

            Bitmap imgBitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
            BitmapDrawable ob = new BitmapDrawable(getResources(), imgBitmap);
            toolbarImage.setBackground(ob);

        } catch (NullPointerException e) {
            // dont fill image.
            e.printStackTrace();
            System.out.println("NULL POINTER EXCEPTION IMGBYTE. ");
        } catch (ArrayIndexOutOfBoundsException a) {
            a.printStackTrace();
            System.out.println("INDEX OAB EXCEPTION IMGBYTE. ");
        }

    }

    public void loadPosterImage(String posterPath) {
        final String BASE_URL = "http://image.tmdb.org/t/p/";
        final String IMG_SIZE = "w500";
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(IMG_SIZE)
                .appendEncodedPath(posterPath)
                .build();

        Picasso.with(this)
                .load(builtUri)
                .into(toolbarImage);
    }

    public void setToolbarTitle(String movieTitle) {
        toolbarLayout.setTitle(movieTitle);
    }

    public void setFavouriteButtonSkin(boolean isFavourite) {
        if (isFavourite) {
            favouriteButton.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            favouriteButton.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }

    public void favouriteButtonPressed() {
        if (!selectedMovie.isFavourite()) {
            selectedMovie.setFavourite(true);
            mMovieOverview.favouriteMovie(selectedMovie, true);
            Toast.makeText(this, "Added to favourites", Toast.LENGTH_SHORT).show();
        } else {
            selectedMovie.setFavourite(false);
            mMovieOverview.favouriteMovie(selectedMovie, false);
            Toast.makeText(this, "Removed from favourites", Toast.LENGTH_SHORT).show();
        }
        setFavouriteButtonSkin(selectedMovie.isFavourite());
    }

    public void initShareButton(String trailerURL) {
        FloatingActionButton shareButton = (FloatingActionButton) findViewById(R.id.shareButton);
        final String url = trailerURL;
        if (trailerURL.equals("unavailable")) {
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    noTrailersAvailableToast();
                }
            });
        } else {
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, "Check out this trailer for the movie " + selectedMovie.getTitle() + "! " +url);
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));


                }
            });
        }
    }

    public void noTrailersAvailableToast (){
        Toast.makeText(this, "No trailers available for sharing", Toast.LENGTH_SHORT).show();
    }
}
