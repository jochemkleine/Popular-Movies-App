package android.jochemkleine.com.popularmovies.data;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Jochemkleine on 10-9-2015.
 */

/**
 * Serializable implemented in order to putExtra Movie Object
 * with Intents.
 */
public class Movie implements Serializable {

    private int id;
    private String title;
    private String overview;
    private String releaseDate;
    private String posterPath;
    private double voteAverage;
    private double popularity;
    private boolean isFavourite;
    private byte [] posterByteArr;
    private int entry = -1;

    public Movie () {
        this.isFavourite = false;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public boolean isFavourite () {
        return isFavourite;
    }

    public void setFavourite(Boolean b){
        isFavourite = b;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public byte[] getPosterByteArr() {
        return posterByteArr;
    }

    public void setPosterByteArr(byte[] posterByteArr) {
        this.posterByteArr = posterByteArr;
    }

    public int getEntry() {
        return entry;
    }

    public void setEntry(int entry) {
        this.entry = entry;
    }
}
