package android.jochemkleine.com.popularmovies.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;


/**
 * Created by Jochemkleine on 1-11-2015.
 */

@ContentProvider(authority = FavMovieProvider.AUTHORITY, database = FavMovieDatabase.class)
public final class FavMovieProvider {

    public static final String AUTHORITY =
            "android.jochemkleine.com.popularmovies.data";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String MOVIES = "movies";
    }

    // Takes arguments for making a CONTENT_URI.
    public static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path: paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = FavMovieDatabase.FAVOURITE_MOVIES)
    public static class FavMovies {
        @ContentUri(
                path = Path.MOVIES,
                type = "vnd.android.cursor.dir/favMovies",
                defaultSort = MovieColumns._ID + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.MOVIES);
        //  public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/lists");


        @InexactContentUri(
                name = "TESTS_ID",
                path = Path.MOVIES + "/#",
                type = "vnd.android.cursor.dir/tests",
                whereColumn = MovieColumns._ID,
                pathSegment = 1
        )

        public static Uri withId (long id) {
            return buildUri(Path.MOVIES, String.valueOf(id));
        }
    }
}

