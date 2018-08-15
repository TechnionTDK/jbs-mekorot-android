package technion.com.testapplication.models;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by tomerlevinson on 14/01/2018.
 * Used to store categories.
 */
public class CategoryModel implements Serializable {
    private String mCategoryName;
    private String mCategoryRefernceNum;
    private HashSet<String> mMekorotUris;

    public CategoryModel(String categoryName, String referenceNumber) {
        mCategoryName = categoryName;
        mCategoryRefernceNum = referenceNumber;
        mMekorotUris = new HashSet<>();
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public String getCategoryRefernceNum() {
        return mCategoryRefernceNum;
    }

    public HashSet<String> getmMekorotUris() {
        return mMekorotUris;
    }

    public boolean addToMekorotUris(String makorUri) {
        boolean result = mMekorotUris.add(makorUri);
        mCategoryRefernceNum = String.valueOf(mMekorotUris.size());
        return result;
    }

}
