package android.jochemkleine.com.popularmovies.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by Jochemkleine on 1-11-2015.
 */
@Database(version = FavMovieDatabase.VERSION)
public final class FavMovieDatabase {

    public static final int VERSION = 10;

    @Table(MovieColumns.class) public static final String FAVOURITE_MOVIES = "favouriteMovies";




}
