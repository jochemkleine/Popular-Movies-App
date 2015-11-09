package android.jochemkleine.com.popularmovies.ui;

/**
 * Created by Jochemkleine on 8-11-2015.
 */
public class Review {

    private String author;
    private String comments;

    public Review() {

    }


    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
