package android.jochemkleine.com.popularmovies;

import java.io.Serializable;

/**
 * Created by Jochemkleine on 10-9-2015.
 */

/**
 * Serializable implemented in order to putExtra Movie Object
 * with Intents.
 */
public class Movie implements Serializable {

    private String title;
    private String overview;
    private String releaseDate;
    private String posterPath;
    private double voteAverage;
    private double popularity;


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

    public Movie () {

    }
}
