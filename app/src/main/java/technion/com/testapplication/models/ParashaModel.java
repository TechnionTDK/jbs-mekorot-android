package technion.com.testapplication.models;

/**
 * Created by tomerlevinson on 16/12/2017.
 */
public class ParashaModel {
    private String mLabel;
    private String mUri = null;

    public ParashaModel(String label, String uri) {
        this.mLabel = label;
        this.mUri = uri;
    }

    public String getLabel() {
        return mLabel;
    }

    public String getFullUri() {
        return mUri;
    }

    public String getParashaJBSName() {
        return mUri.substring(mUri.lastIndexOf("/") + 1);
    }

}
