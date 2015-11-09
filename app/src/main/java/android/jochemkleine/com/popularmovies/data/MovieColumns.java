package android.jochemkleine.com.popularmovies.data;

import android.graphics.Bitmap;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by Jochemkleine on 1-11-2015.
 */
public interface MovieColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey String _ID = "_id";

    @DataType(DataType.Type.TEXT) @NotNull String NAME  = "name";

    @DataType(DataType.Type.TEXT) @NotNull String OVERVIEW = "overview";

    @DataType(DataType.Type.TEXT) @NotNull String RELEASE_DATE = "releaseDate";

    @DataType(DataType.Type.TEXT) @NotNull  String VOTE_AVERAGE = "voteAverage";

    @DataType(DataType.Type.TEXT) @NotNull  String POPULARITY = "popularity";

    @DataType(DataType.Type.BLOB) @NotNull String POSTER_IMAGE = "posterImageBlob";
}

