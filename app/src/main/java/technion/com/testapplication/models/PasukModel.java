package technion.com.testapplication.models;

import java.io.Serializable;

/**
 * Created by tomerlevinson on 16/12/2017.
 * Used to store psukim.
 */
public class PasukModel implements Serializable {
    private String mText;
    private String mLabel;
    private boolean mIsSelected = false;
    private String mUri = null;

    public PasukModel(String text, String label) {
        this.mText = text;
        this.mLabel = label;
    }

    public String getText() {
        return mText;
    }

    public String getLabel() {
        return mLabel;
    }
    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }

    public void setUri(String uri) {
        mUri = uri;
    }

    public String getUri() {
        return mUri;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

}
