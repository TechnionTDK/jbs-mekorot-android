package technion.com.testapplication.models;

/**
 * Created by tomerlevinson on 18/12/2017.
 */
public class MakorModel {

    private String mMakorName;
    private String mMakorAuthor;
    private String mMakorText;

    public MakorModel(String name, String author, String text) {
        mMakorName = name;
        mMakorAuthor = author;
        mMakorText = text;
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
}
