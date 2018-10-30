package technion.com.testapplication.models;

import java.io.Serializable;

/**
 * Created by tomerlevinson on 14/01/2018.
 * Used to store categories.
 */
public class CategoryModel implements Serializable {
    private String mCategoryName;
    private String mCategoryRefernceNum;
    private String mCategoryUri;

    public CategoryModel(String categoryName, String referenceNumber, String uri) {
        mCategoryName = categoryName;
        mCategoryRefernceNum = referenceNumber;
        mCategoryUri = uri;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public String getCategoryRefernceNum() {
        return mCategoryRefernceNum;
    }

    public String getCategoryUri() {
        return mCategoryUri;
    }
}
