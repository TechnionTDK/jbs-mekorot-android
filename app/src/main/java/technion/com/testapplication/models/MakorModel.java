package technion.com.testapplication.models;

import java.io.Serializable;

/**
 * Created by tomerlevinson on 18/12/2017.
 * Used to store mekorot.
 */
public class MakorModel implements Serializable {

    private String mMakorName;
    private String mMakorAuthor;
    private String mMakorText;
    private String mMakorBook;
    private String mNumOfPsukimMentions;
    private String mMakorUri;
    private boolean mIsClicked = false;

    public MakorModel(String name, String text, String uri, String book, String numOfPsukimMentions) {
        mMakorName = name;
        mMakorText = text;
        mMakorUri = uri;
        mMakorBook = book;
        mNumOfPsukimMentions = numOfPsukimMentions;
    }

    public String getMakorAuthor() {
        return mMakorAuthor;
    }

    public String getMakorName() {
        return mMakorName;
    }

    public String getMakorText() {
        return mMakorText;
    }

    public String getMakorBook() {
        return mMakorBook;
    }

    public String getNumOfPsukimMentions() {
        return mNumOfPsukimMentions;
    }

    public String getMakorUri() {
        return mMakorUri;
    }

    public boolean getIsClicked() {
        return mIsClicked;
    }

    public void setIsClicked(boolean isClicked) {
        mIsClicked = isClicked;
    }

    public void setMakorAuthor(String makorAuthor) {
        mMakorAuthor = makorAuthor;
    }
}
