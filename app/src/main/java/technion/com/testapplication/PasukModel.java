package technion.com.testapplication;

/**
 * Created by tomerlevinson on 16/12/2017.
 */
public class PasukModel {
    private String mText;
    private boolean mIsSelected = false;
    private String mUri = null;

    public PasukModel(String text) {
        this.mText = text;
    }

    public String getText() {
        return mText;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }

    public void setUri(String uri) {
        mUri = uri;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

}
